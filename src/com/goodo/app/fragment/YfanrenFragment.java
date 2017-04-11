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

import com.goodo.app.ChakandbswActivity;
import com.goodo.app.MoreYfMainActivity;
import com.goodo.app.Myconfig;
import com.goodo.app.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class YfanrenFragment extends Fragment{
	
	protected static final String TAG = "Activity";
	private ExpandableListView expandableListView;
	private List<String> generalTypes = new ArrayList<String>();
	private List<List<Map<String, String>>> general = new ArrayList<List<Map<String,String>>>();
	private BaseExpandableListAdapter adapter;
	
	private HttpClient httpClient;
	private Handler handler;
	private boolean IsSenderOrReceiver = true;
	public static final int DEL_RESPONSE = 2;
	public static final int FINISH_RESPONSE= 3;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sendRequest();
	}



	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.yf_anren_fragment, container, false);

		
		httpClient = new DefaultHttpClient();
		sendRequest();
		receRequest();
		
		expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListViewyf);
		adapter = new BaseExpandableListAdapter() {
			
			
			TextView getTextView(){
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				
				TextView textView = new TextView(YfanrenFragment.this.getActivity());
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(30, 30, 30, 30);
				textView.setTextColor(Color.BLACK);
				return textView;
			}
			
			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(YfanrenFragment.this.getActivity());
				ll.setOrientation(0);
				TextView textView = getTextView();
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				textView.setText("  "+getGroup(arg0).toString());
				textView.setPadding(36, 0, 0, 0);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(36, 36, 36, 36);
				ll.addView(textView,lp);
				return ll;
			}
			
			@Override
			public long getGroupId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}
			
			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return generalTypes.size();
			}
			
			@Override
			public Object getGroup(int arg0) {
				// TODO Auto-generated method stub
				return generalTypes.get(arg0);
			}
			
			@Override
			public int getChildrenCount(int arg0) {
				// TODO Auto-generated method stub
				return general.get(arg0).size();
			}
			
			@Override
			public View getChildView(final int arg0, final int arg1, boolean arg2, View arg3,
					ViewGroup arg4) {
				// TODO Auto-generated method stub
				View view2 = inflater.inflate(R.layout.sample1, arg4,false);
				TextView tv_title = (TextView) view2.findViewById(R.id.id_title);
				TextView tv_time = (TextView) view2.findViewById(R.id.id_time);
				TextView tv_from = (TextView) view2.findViewById(R.id.id_from);
				
				//按人员列表操作
				TextView btn_del = (TextView) view2.findViewById(R.id.delete);
				TextView btn_finish = (TextView) view2.findViewById(R.id.finish);
				btn_del.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sendDelRequest(arg0, arg1);
						receDelRequest(arg0, arg1);
					}
				});
				btn_finish.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sendFinishRequest(arg0, arg1);
						receFinishRequest(arg0, arg1);
					}
				});
				
				
				tv_title.setText(general.get(arg0).get(arg1).get("title"));;
				tv_from.setText(general.get(arg0).get(arg1).get("from"));
				tv_time.setText(general.get(arg0).get(arg1).get("time"));
				
				
				return view2;
			}
			
			@Override
			public long getChildId(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arg1;
			}
			
			@Override
			public Object getChild(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return general.get(arg0).get(arg1);
			}
		};
		

		
		return view;
	}
	
	
	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_GetSendList2");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
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
					general = new ArrayList<List<Map<String,String>>>();
					generalTypes = new ArrayList<String>();
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String str = Goodo.getString("Receiver");
						char[] str2 = str.toCharArray();
						if(str2[0]=='['){
							JSONArray Receiver = Goodo.getJSONArray("Receiver") ;
							int iSize = Receiver.length();
							for(int i=0;i<iSize;i++){
								JSONObject jo = Receiver.getJSONObject(i);
								String UserName = jo.getString("UserName");
								generalTypes.add(UserName);
								String Rstr = jo.getString("R");
								char[] Rstr2 = Rstr.toCharArray();
								
								List<Map<String, String>> list = new ArrayList<Map<String,String>>();
								if(Rstr2[0]=='['){
									JSONArray RArray = jo.getJSONArray("R");
									int Rsize = RArray.length();
									for(int j = 0;j<Rsize;j++){
										JSONObject Rjo = RArray.getJSONObject(j);
										Map<String, String> map = new HashMap<String, String>();
										map.put("title", Rjo.getString("Work"));
										map.put("from", Rjo.getString("ReceiveUserNames"));
										map.put("time", Rjo.getString("CreateDate"));
										map.put("Plan_ID", Rjo.getString("Plan_ID"));
										map.put("PlanType", Rjo.getString("PlanType"));
										list.add(map);
									}
								}else{
									JSONObject Rjo = jo.getJSONObject("R");
									Map<String, String> map = new HashMap<String, String>();
									map.put("title", Rjo.getString("Work"));
									map.put("from", Rjo.getString("ReceiveUserNames"));
									map.put("time", Rjo.getString("CreateDate"));
									map.put("Plan_ID", Rjo.getString("Plan_ID"));
									map.put("PlanType", Rjo.getString("PlanType"));
									list.add(map);
								}
								
								general.add(list);
							}
						}else{
							JSONObject jo = Goodo.getJSONObject("Receiver");
							String UserName = jo.getString("UserName");
							generalTypes.add(UserName);
							String Rstr = jo.getString("R");
							char[] Rstr2 = Rstr.toCharArray();
							
							List<Map<String, String>> list = new ArrayList<Map<String,String>>();
							if(Rstr2[0]=='['){
								JSONArray RArray = jo.getJSONArray("R");
								int Rsize = RArray.length();
								for(int j = 0;j<Rsize;j++){
									JSONObject Rjo = RArray.getJSONObject(j);
									Map<String, String> map = new HashMap<String, String>();
									map.put("title", Rjo.getString("Work"));
									map.put("from", Rjo.getString("ReceiveUserNames"));
									map.put("time", Rjo.getString("CreateDate"));
									map.put("Plan_ID", Rjo.getString("Plan_ID"));
									map.put("PlanType", Rjo.getString("PlanType"));
									list.add(map);
								}
							}else{
								JSONObject Rjo = jo.getJSONObject("R");
								Map<String, String> map = new HashMap<String, String>();
								map.put("title", Rjo.getString("Work"));
								map.put("from", Rjo.getString("ReceiveUserNames"));
								map.put("time", Rjo.getString("CreateDate"));
								map.put("Plan_ID", Rjo.getString("Plan_ID"));
								map.put("PlanType", Rjo.getString("PlanType"));
								list.add(map);
							}
							general.add(list);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
					expandableListView.setAdapter(adapter);
					expandableListView.setOnChildClickListener(new OnChildClickListener() {
						
						@Override
						public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,
								int arg3, long arg4) {
							// TODO Auto-generated method stub
							Intent it = new Intent(getActivity(),ChakandbswActivity.class);
							String Plan_ID = general.get(arg2).get(arg3).get("Plan_ID");
							it.putExtra("Plan_ID", Plan_ID);
							startActivity(it);
							return false;
						}
					});
					break;

				default:
					break;
				}
			}
		};
	}
	
	private void sendDelRequest(final int arg0,final int arg1){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String Plan_ID = general.get(arg0).get(arg1).get("Plan_ID");
				String PlanType = general.get(arg0).get(arg1).get("PlanType");
				
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_Delete");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Plan_ID", Plan_ID);
				builder.appendQueryParameter("PlanType", PlanType);
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
						Message message = new Message();
						message.what = DEL_RESPONSE;
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
	
	private void receDelRequest(final int arg0,final int arg1){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case DEL_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("11111111111"+response);
					JSONObject jsonObject;
					String EID = "";
					try {
						jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						EID = Goodo.getString("EID");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(EID.equals("0")){
						general.get(arg0).get(arg1).remove(arg1);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
				}
			}
		};
	}
	
	private void sendFinishRequest(final int arg0,final int arg1){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String Plan_ID = general.get(arg0).get(arg1).get("Plan_ID");
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_Finish");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("UserName", Myconfig.UserName);
				builder.appendQueryParameter("Plan_ID", Plan_ID);
				builder.appendQueryParameter("IsSenderOrReceiver", IsSenderOrReceiver+"");
				String Url = builder.toString();
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpresponse = httpClient.execute(get);
					HttpEntity entity = httpresponse.getEntity();
					if(entity!=null){
						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						StringBuilder response = new StringBuilder();
						String line;
						while((line = reader.readLine())!=null){
							response.append(line);
						}
						Message message = new Message();
						message.what = FINISH_RESPONSE;
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
	
	private void receFinishRequest(final int arg0,final int arg1){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case FINISH_RESPONSE:
					String response = (String) msg.obj;
					JSONObject jsonObject;
					String EID = "";
					try {
						jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						EID = Goodo.getString("EID");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(EID.equals("0")){
						general.get(arg0).remove(arg1);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "完成成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getActivity(), "完成失败", Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
				}
			}
		};
	}
	
	
}
