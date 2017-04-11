package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class OtherinformlistActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private TextView tv_title;
	private PullToRefreshListView listView;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private int SID;
	private String title;
	private HttpClient httpClient;
	private static final int SHOW_RESPONSE = 1;
	private Handler handler;
	private InformAdapter adapter;
	
	private boolean isRefreshing = false;
	private int n = 1;
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otherinformlist);
		iv_return = (ImageView) findViewById(R.id.iv_return);
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_return.setOnClickListener(this);
		Intent it = getIntent();
		SID = it.getIntExtra("SID", 0);
		title = it.getStringExtra("SName");
		tv_title.setText(title);
		httpClient = new DefaultHttpClient();
		sendRequest(SID,1);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					if(flag){
						list.clear();
						flag = false;
					}
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String str = Goodo.getString("R");
						char[] str2 = str.toCharArray();
						if(str2[0]=='['){
							JSONArray Rarray = Goodo.getJSONArray("R");
							int iSize = Rarray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = Rarray.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("title", jo.get("ContentTitle"));
								map.put("content", jo.get("BPUnitName"));
								map.put("date", jo.get("SubmitDate"));
								map.put("Contents_ID", jo.get("Contents_ID"));
								map.put("image", R.drawable.inform_u99);
								map.put("SubjectName", jo.get("SubjectName"));
								map.put("UserName", jo.get("UserName"));
								list.add(map);
							}
						}else{
							JSONObject Rjo = Goodo.getJSONObject("R");
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("title", Rjo.get("ContentTitle"));
							map.put("content", Rjo.get("BPUnitName"));
							map.put("date", Rjo.get("SubmitDate"));
							map.put("Contents_ID", Rjo.get("Contents_ID"));
							map.put("image", R.drawable.inform_u99);
							map.put("SubjectName", Rjo.get("SubjectName"));
							map.put("UserName", Rjo.get("UserName"));
							list.add(map);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter = new InformAdapter(list, OtherinformlistActivity.this);
					listView = (PullToRefreshListView) findViewById(R.id.listView_content);
					listView.setAdapter(adapter);
					listView.setMode(Mode.BOTH);
					
					listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

						@Override
						public void onRefresh(
								PullToRefreshBase<ListView> refreshView) {
							// TODO Auto-generated method stub
							if(!isRefreshing){
								isRefreshing = true;
								if(listView.isHeaderShown()){
									n = 1;
									flag = true;
									sendRequest(SID, 1);
									Toast.makeText(OtherinformlistActivity.this, "Ë¢ÐÂ", Toast.LENGTH_SHORT).show();
								}else if(listView.isFooterShown()){
									n++;
									sendRequest(SID, n);
									Toast.makeText(OtherinformlistActivity.this, "¼ÓÔØ", Toast.LENGTH_SHORT).show();
								}
							}else{
								listView.onRefreshComplete();
								isRefreshing = false;
							}
						}
					});
					
					
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent it = new Intent();
							it.putExtra("extra", (String)list.get(arg2-1).get("Contents_ID"));
							it.setClass(OtherinformlistActivity.this, MoreInformActivity.class);
							startActivity(it);
						}
					});
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;

		default:
			break;
		}
	}
	
	private void sendRequest(final int SID,final int page){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/Get_Json_SubjectContentSearchListByID");
				builder.appendQueryParameter("Subjects_ID", SID+"");
				builder.appendQueryParameter("Page", n+"");
				builder.appendQueryParameter("PageSize", "10");
				builder.appendQueryParameter("Keyword", "");
				String Url = builder.toString();
				
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpResponse= httpClient.execute(get);
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
}
