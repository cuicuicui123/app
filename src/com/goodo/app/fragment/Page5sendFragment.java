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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goodo.adapter.NoticeAdapter;
import com.goodo.app.Myconfig;
import com.goodo.app.R;
import com.goodo.app.SendNoticeActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Page5sendFragment extends Fragment{
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	
	private HttpClient httpClient;
	private Handler handler;
	
	public NoticeAdapter adapter;
	private PullToRefreshListView sendlistview = null;
	private boolean isRefreshing = false;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			if(data.getBooleanExtra("extra", false)){
				adapter.notifyDataSetChanged();
			}
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.listview_page5, container, false);
		httpClient = new DefaultHttpClient();
		
		sendlistview = (PullToRefreshListView) view.findViewById(R.id.listview_page5);
		adapter = new NoticeAdapter(list, getActivity());
		sendlistview.setAdapter(adapter);
		
		sendRequest();
		receRequest();
		return view;
	}
	
	
	public void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/MyNoticeList_Get");
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
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
						if(sendlistview!=null){
							sendlistview.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									sendlistview.onRefreshComplete();
								}
							}, 1000);
							isRefreshing = false;
						}
						Message message = new Message();
						message.what = Myconfig.SHOW_RESPONSE;
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
	
	private void receRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("456789----"+response);
					list.clear();
					char[] responsechar = response.toCharArray();
					if(responsechar[0]=='['){
						try {
							JSONArray jsonArray = new JSONArray(response);
							int iSize = jsonArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = jsonArray.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("NoticeID", jo.get("NoticeID"));
								map.put("NoticeTitle", jo.get("NoticeTitle"));
								map.put("NotifyType", jo.get("NotifyType"));
								map.put("SubmitDate", jo.get("SubmitDate"));
								map.put("State", jo.get("State"));
								map.put("红点", R.drawable.inform_u99);
								map.put("箭头", R.drawable.jiantou);
								list.add(map);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						try {
							JSONObject jo = new JSONObject(response);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("NoticeID", jo.get("NoticeID"));
							map.put("NoticeTitle", jo.get("NoticeTitle"));
							map.put("NotifyType", jo.get("NotifyType"));
							map.put("SubmitDate", jo.get("SubmitDate"));
							map.put("State", jo.get("State"));
							map.put("红点", R.drawable.inform_u99);
							map.put("箭头", R.drawable.jiantou);
							list.add(map);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					System.out.println("3456789---"+list.size());
					adapter.notifyDataSetChanged();
					sendlistview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent it = new Intent(getActivity(),SendNoticeActivity.class);
							it.putExtra("NoticeID", list.get(arg2-1).get("NoticeID")+"");
							startActivityForResult(it, 0);
						}
					});
					sendlistview.setOnRefreshListener(new OnRefreshListener<ListView>() {

						@Override
						public void onRefresh(
								PullToRefreshBase<ListView> refreshView) {
							// TODO Auto-generated method stub
							if(!isRefreshing){
								isRefreshing = true;
								if(sendlistview.isHeaderShown()){
									sendRequest();
									Toast.makeText(getActivity(), "刷新",
										     Toast.LENGTH_SHORT).show();
								}
							}else{
								sendlistview.onRefreshComplete();
								isRefreshing = false;
							}
						}
					});
					break;

				default:
					break;
				}
			}
		};
	}

}
