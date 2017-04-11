package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.HttpUtils;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(_isExe){
			task.cancel(true);
		}
	}

	private PullToRefreshListView listView = null;
	ImageView iv_return;
	ImageView iv_goin;
	List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	List<Map<String, Object>> list_url = new ArrayList<Map<String,Object>>();
	TextView main_tv;
	
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	MyAdapter adapter;
	private String news_kind = null;
	private String str;
	private downloadImageTask task;
	private boolean _isExe = false;

	private boolean isRefreshing = false;
	private int n = 1;
	boolean flag = false;
	private int i = 0;
	private LinearLayout ll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		httpClient = new DefaultHttpClient();
		listView = (PullToRefreshListView) findViewById(R.id.list);
		listView.setMode(Mode.BOTH);
		adapter = new MyAdapter(list_url, HomeActivity.this);
		listView.setAdapter(adapter);
		ll = (LinearLayout) findViewById(R.id.ll);
		
		main_tv = (TextView) findViewById(R.id.main_tv);
		Intent it = getIntent();
		str = it.getStringExtra("extra");
		if(str!=null){
			main_tv.setText(str);
			if(str.equals("教学通知")){
				news_kind = "Get_Json_TeachNoticeContentSearchList";
			}else if(str.equals("新闻动态")){
				news_kind = "Get_Json_NewsContentSearchList";
			}else if(str.equals("成果展示")){
				news_kind = "Get_Json_RevealContentSearchList";
			}else if(str.equals("党建工作")){
				news_kind = "Get_Json_WorkContentSearchList";
			}
		}
		
		sendRequest(news_kind,"1");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					
					if(flag){
						list_url.clear();
						flag = false;
					}
					System.out.println("888888888888"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String Rstr = Goodo.getString("R");
						char[] Rchar = Rstr.toCharArray();
						if(Rchar[0]=='['){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject r = RArray.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("Contents_ID", r.get("Contents_ID"));
								map.put("ContentTitle", r.get("ContentTitle"));
								map.put("UserName", r.get("UserName"));
								map.put("SubmitDate", r.get("SubmitDate"));
								map.put("PicUrl", r.get("PicUrl"));
								map.put("SubjectName", r.get("SubjectName"));
//								map.put("Pic", Myconfig.local_pic);
								list_url.add(map);
							}
							for(int a = i;a<list_url.size();a++){
								if(!_isExe){
									task = new downloadImageTask();
									task.execute(Myconfig.IP+list_url.get(a).get("PicUrl"));
								}
							}
							_isExe = true;
						}else{
							JSONObject r = Goodo.getJSONObject("R");
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Contents_ID", r.get("Contents_ID"));
							map.put("ContentTitle", r.get("ContentTitle"));
							map.put("BPUnitName", r.get("BPUnitName"));
							map.put("SubmitDate", r.get("SubmitDate"));
							map.put("PicUrl", r.get("PicUrl"));
//							map.put("Pic", Myconfig.local_pic);
							list_url.add(map);
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			}
		};
		//返回键
		
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		//进入系统键
		iv_goin = (ImageView) findViewById(R.id.iv_goin);
		iv_goin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				if(Myconfig.login_flag){
					it.setClass(HomeActivity.this, Select2Activity.class);
				}else{
					it.setClass(HomeActivity.this, MainActivity.class);
				}
				startActivity(it);
			}
		});
		
		
	}
	private void sendRequest(final String news_kind,final String page){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/"+news_kind);
				builder.appendQueryParameter("Page", page);
				builder.appendQueryParameter("PageSize", "10");
				builder.appendQueryParameter("Keyword", "");
				String Url = builder.toString();
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					
					if(entity!=null){
						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						StringBuilder response = new StringBuilder();
						String line;
						while((line = reader.readLine())!=null){
							response.append(line);
						}
						if(listView!=null){
							listView.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									listView.onRefreshComplete();
								}
							}, 1000);
							isRefreshing = false;
						}
						
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = response.toString();
						handler.sendMessage(message);
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	class downloadImageTask extends AsyncTask<String, Integer, Boolean>{
		Bitmap mDownloadImage = null;

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mDownloadImage = HttpUtils.getHttpGetBitmap(params[0]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(mDownloadImage!=null){
				list_url.get(i).put("Pic", mDownloadImage);
			}
			i++;
			if(i==list_url.size()){
				ll.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				
				listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						if(!isRefreshing){
							isRefreshing = true;
							if(listView.isHeaderShown()){
								i = 0;
								_isExe = false;
								n = 1;
								flag = true;
								sendRequest(news_kind,"1");
								Toast.makeText(getApplicationContext(), "刷新",
									     Toast.LENGTH_SHORT).show();
								
							}else if(listView.isFooterShown()){
								_isExe = false;
								n++;
								sendRequest(news_kind, ""+n);
								Toast.makeText(getApplicationContext(), "加载",
									     Toast.LENGTH_SHORT).show();
							}
						}else{
							listView.onRefreshComplete();
							isRefreshing = false;
							_isExe = false;
						}
					}
				});
				
				
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
							Intent it = new Intent();
							it.putExtra("extra", (String)(list_url.get(position-1).get("Contents_ID")));
							it.putExtra("kind", str);
							it.setClass(HomeActivity.this, MoreHomeActivity.class);
							startActivity(it);
					}
				});
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}	
	}
}
