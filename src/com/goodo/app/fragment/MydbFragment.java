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

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.goodo.adapter.DataAdapter;
import com.goodo.app.ChakandbswActivity;
import com.goodo.app.MoreYfMainActivity;
import com.goodo.app.Myconfig;
import com.goodo.app.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MydbFragment extends Fragment{
	


	protected static final String TAG = "Activity";
	private SwipeListView mSwipeListView;
	private DataAdapter mAdapter;
	private List<Map<String, String>> mDatas = new ArrayList<Map<String,String>>();
	
	private HttpClient httpClient;
	private Handler handler;

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
				if(mDatas.size()>0){
					sendRequest();
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.dbsw_mydb_fragment, container, false);
		httpClient = new DefaultHttpClient();
		sendRequest();
		receRequest();
		mSwipeListView = (SwipeListView) view.findViewById(R.id.id_swipelistviewMydb);
		return view;
	}


	
	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet get = new HttpGet(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_GetToDoList?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID);
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
					System.out.println("4567890-----"+response);
					mDatas = new ArrayList<Map<String,String>>();
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String str = Goodo.getString("R");
						char[] str2 = str.toCharArray();
						if(str2[0]=='['){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RArray.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();
								map.put("title", jo.getString("Work"));
								map.put("time", jo.getString("CreateDate"));
								map.put("from", jo.getString("CreateUser_ID"));
								map.put("PlanType", jo.getString("PlanType"));
								map.put("Plan_ID", jo.getString("Plan_ID"));
								mDatas.add(map);
							}
						}else{
							JSONObject Rjo = Goodo.getJSONObject("R");
							Map<String, String> map = new HashMap<String, String>();
							map.put("title", Rjo.getString("Work"));
							map.put("time", Rjo.getString("CreateDate"));
							map.put("from", Rjo.getString("CreateUser_ID"));
							map.put("PlanType", Rjo.getString("PlanType"));
							map.put("Plan_ID", Rjo.getString("Plan_ID"));
							mDatas.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mAdapter = new DataAdapter(getActivity(), mDatas, mSwipeListView,false);
					mSwipeListView.setOffsetLeft(Myconfig.width-120);
					mSwipeListView.setOffsetRight(Myconfig.width-120);
					mSwipeListView.setAdapter(mAdapter);
					
					mSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener(){
						
						@Override
						public void onClickFrontView(int position)
						{
							Log.d(TAG, "onClickFrontView:" + position);
							mSwipeListView.closeOpenedItems();
							Intent it = new Intent(getActivity(),MoreYfMainActivity.class);
							String Plan_ID = mDatas.get(position).get("Plan_ID");
							it.putExtra("Plan_ID", Plan_ID);
							startActivityForResult(it, 0);
						}
						
						@Override
						public void onListChanged() {
							// TODO Auto-generated method stub
							super.onListChanged();
							
							mSwipeListView.closeOpenedItems();
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
