package com.goodo.app.fragment;

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

import com.goodo.app.InformAdapter;
import com.goodo.app.MoreInformActivity;
import com.goodo.app.Myconfig;
import com.goodo.app.OtherInformActivity;
import com.goodo.app.R;
import com.goodo.app.Select2Activity;
import com.goodo.app.javabean.JsonString;
import com.google.gson.Gson;
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
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPage1 extends Fragment{
	private ImageView iv_user_1;
	private PullToRefreshListView listView = null;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private ImageView iv_page1_more;
	
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	InformAdapter adapter;
	private boolean isRefreshing = false;
	private int n = 1;
	private boolean flag = false;
	
	private TextView tv_name;
	private LinearLayout ll;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.fragment_1, container, false);
		ll = (LinearLayout) view.findViewById(R.id.ll);
		tv_name = (TextView) view.findViewById(R.id.tv_person_1);
		tv_name.setText(Myconfig.UserName);
		adapter = new InformAdapter(list, getActivity());
		
		listView = (PullToRefreshListView) view.findViewById(R.id.listview_page1);
		listView.setAdapter(adapter);
		listView.setMode(Mode.BOTH);
		
		iv_user_1 = (ImageView) view.findViewById(R.id.iv_user_1);
		final Select2Activity select2Activity = (Select2Activity) getActivity();
		iv_user_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				select2Activity.xcSlideMenu.switchMenu();
			}
		});
		
		iv_page1_more = (ImageView) view.findViewById(R.id.iv_more);
		iv_page1_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent();
				it.setClass(FragmentPage1.this.getActivity(), OtherInformActivity.class);
				startActivity(it);
			}
		});
		
		
		httpClient = new DefaultHttpClient();
		sendRequest("1");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String)msg.obj;
					ll.setVisibility(View.GONE);
					System.out.println("------------"+response);
					if(flag){
						list.clear();
						flag = false;
					}
					Gson gson = new Gson();
					try {
						JsonString jsonString = gson.fromJson(response, JsonString.class);
						jsonString.getGoodo().getR().get(0).getContentTitle();
						System.out.println("------------"+jsonString);
						for(int i = 0;i<jsonString.getGoodo().getR().size();i++){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("title", jsonString.getGoodo().getR().get(i).getContentTitle());
							map.put("content",jsonString.getGoodo().getR().get(i).getSubjectName());
							map.put("date", jsonString.getGoodo().getR().get(i).getSubmitDate());
							map.put("BPUnitName", jsonString.getGoodo().getR().get(i).getBPUnitName());
							map.put("SubjectName", jsonString.getGoodo().getR().get(i).getSubjectName());
							map.put("Contents_ID", jsonString.getGoodo().getR().get(i).getContents_ID());
							map.put("image", R.drawable.inform_u99);
							map.put("UserName", jsonString.getGoodo().getR().get(i).getUserName());
							list.add(map);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					adapter.notifyDataSetChanged();
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
									sendRequest("1");
									Toast.makeText(getActivity(), "Ë¢ÐÂ", Toast.LENGTH_SHORT).show();
								}else if(listView.isFooterShown()){
									n++;
									sendRequest(""+n);
									Toast.makeText(getActivity(), "¼ÓÔØ", Toast.LENGTH_SHORT).show();
								}
							}else{
								listView.onRefreshComplete();
								isRefreshing = false;
							}
						}
					});
					
					
					
					listView.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							
								Intent it = new Intent();
								it.putExtra("extra", (String)list.get(arg2-1).get("Contents_ID"));
								it.setClass(FragmentPage1.this.getActivity(), MoreInformActivity.class);
								startActivity(it);

						}
					});
					break;

				default:
					break;
				}
			}
		};
		
		
		
		return view;		
	}		
	
	private void sendRequest(final String page){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/Get_Json_SubjectContentSearchList");
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
						while((line=reader.readLine())!=null){
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
