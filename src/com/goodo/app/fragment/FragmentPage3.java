package com.goodo.app.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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

import com.goodo.app.Myconfig;
import com.goodo.app.R;
import com.goodo.app.ReceActivity;
import com.goodo.app.Select2Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentPage3 extends Fragment{
	private ImageView iv_user_3;
	private ExpandableListView expListview;
	private HttpClient httpClient;
	private Handler handler;
	private List<Map<String, String>> generalsTypes = new ArrayList<Map<String,String>>();
	private List<List<Map<String, String>>> generals = new ArrayList<List<Map<String,String>>>();
	
	private ExpandableListAdapter adapter;
	public static final int INNER_RESPONSE = 2;
	
	private TextView tv_name;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_3, container, false);
		tv_name = (TextView) view.findViewById(R.id.tv_person_3);
		tv_name.setText(Myconfig.UserName);
		
		
		
		httpClient = new DefaultHttpClient();
		sendInnerRequest();
		receInnerRequest();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("Name", "所有邮箱");
		generalsTypes.add(map);
		
		Map<String, String> map_inner = new HashMap<String, String>();
		map_inner.put("Name", "内部电函");
		generalsTypes.add(map_inner);
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map_all1 = new HashMap<String, String>();
		map_all1.put("Name", "收件箱");
		Map<String, String> map_all2 = new HashMap<String, String>();
		map_all2.put("Name", "发件箱");
		Map<String, String> map_all3 = new HashMap<String, String>();
		map_all3.put("Name", "草稿箱");
		Map<String, String> map_all4 = new HashMap<String, String>();
		map_all4.put("Name", "回收箱");
		list.add(map_all1);
		list.add(map_all2);
		list.add(map_all3);
		list.add(map_all4);
		
		generals.add(list);
		
		List<Map<String, String>> list2 = new ArrayList<Map<String,String>>();
		Map<String, String> map_send1 = new HashMap<String, String>();
		map_send1.put("1", "1");
		Map<String, String> map_send2 = new HashMap<String, String>();
		map_send2.put("1", "2");
		Map<String, String> map_send3 = new HashMap<String, String>();
		map_send3.put("1", "3");
		Map<String, String> map_send4 = new HashMap<String, String>();
		map_send4.put("1", "4");
		
		list2.add(map_send1);
		list2.add(map_send2);
		list2.add(map_send3);
		list2.add(map_send4);
		
		
		
		
		iv_user_3 = (ImageView) view.findViewById(R.id.iv_user_3);
		final Select2Activity select2Activity = (Select2Activity) getActivity();
		iv_user_3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				select2Activity.xcSlideMenu.switchMenu();
			}
		});
		
		expListview = (ExpandableListView) view.findViewById(R.id.explistview_page3);
		adapter = new BaseExpandableListAdapter() {
			

			
			TextView getTextView(){
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				
				TextView textView = new TextView(FragmentPage3.this.getActivity());
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
				LinearLayout ll = new LinearLayout(FragmentPage3.this.getActivity());
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
				return generalsTypes.size();
			}
			
			@Override
			public Object getGroup(int arg0) {
				// TODO Auto-generated method stub
				return generalsTypes.get(arg0).get("Name");
			}
			
			@Override
			public int getChildrenCount(int arg0) {
				// TODO Auto-generated method stub
				return generals.get(arg0).size();
			}
			
			@Override
			public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
					ViewGroup arg4) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(FragmentPage3.this.getActivity());
				ll.setOrientation(0);
				TextView textView = getTextView();
				textView.setTextSize(15);
				textView.setText(" "+getChild(arg0, arg1).toString());
				ll.addView(textView);
				return ll;
			}
			
			@Override
			public long getChildId(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arg1;
			}
			
			@Override
			public Object getChild(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return generals.get(arg0).get(arg1).get("Name");
			}
		};
		
		
				
		return view;		
	}
	
	
	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMailAddr_ListGet");
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
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String str = Goodo.getString("R");
						char[] strchar = str.toCharArray();
						if(strchar[0]=='['){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RArray.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();
								map.put("OuterMailAddr_ID", jo.getString("OuterMailAddr_ID"));
								map.put("OuterMailAddr", jo.getString("OuterMailAddr"));
								map.put("Name", jo.getString("OuterMailName"));
								map.put("Num", jo.getString("Num"));
								generalsTypes.add(map);
							}
						}else{
							JSONObject jo = Goodo.getJSONObject("R");
							Map<String, String> map = new HashMap<String, String>();
							map.put("OuterMailAddr_ID", jo.getString("OuterMailAddr_ID"));
							map.put("OuterMailAddr", jo.getString("OuterMailAddr"));
							map.put("Name", jo.getString("OuterMailName"));
							map.put("Num", jo.getString("Num"));
							generalsTypes.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					for(int i = 0;i<generalsTypes.size()-2;i++){
						List<Map<String, String>> list = new ArrayList<Map<String,String>>();
						Map<String, String> map_rece = new HashMap<String, String>();
						map_rece.put("Name", "收件箱");
						Map<String, String> map_send = new HashMap<String, String>();
						map_send.put("Name", "发件箱");
						list.add(map_rece);
						list.add(map_send);
						generals.add(list);
					}
					
					expListview.setAdapter(adapter);
					expListview.setOnChildClickListener(new OnChildClickListener() {
						
						@Override
						public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,
								int arg3, long arg4) {
							// TODO Auto-generated method stub
							Intent it = new Intent(getActivity(),ReceActivity.class);
							List<Map<String, String>> list = new ArrayList<Map<String,String>>();
							list = generals.get(1);
							it.putExtra("list", (Serializable)list);
							it.putExtra("FID", arg2);
							if(arg2 == 0){
								it.putExtra("CID", arg3);
							}else if(arg2 == 1){
								int i = generals.get(arg2).size();
								if(arg3<i-1){
									it.putExtra("CID", 0);
									it.putExtra("Kind", (String)generals.get(arg2).get(arg3).get("ReceiveClassify_ID"));
								}else{
									it.putExtra("CID", 1);
								}
							}else if(arg2>1){
								it.putExtra("CID", arg3);
								it.putExtra("Kind", generalsTypes.get(arg2).get("OuterMailAddr_ID"));
							}
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
	
	
	private void sendInnerRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Receive_ClassifyGetList");
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
						message.what = INNER_RESPONSE;
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
	
	private void receInnerRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case INNER_RESPONSE:
					String response = (String) msg.obj;
					List<Map<String, String>> list = new ArrayList<Map<String,String>>();
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String str = Goodo.getString("R");
						char[] strchar = str.toCharArray();
						if(strchar[0]=='['){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RArray.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();
								map.put("ReceiveClassify_ID", jo.getString("ReceiveClassify_ID"));
								map.put("Name", jo.getString("ReceiveClassifyName")+"（收件箱）");
								map.put("Num", jo.getString("Num"));
								list.add(map);
							}
						}else{
							JSONObject jo = Goodo.getJSONObject("R");
							Map<String, String> map = new HashMap<String, String>();
							map.put("ReceiveClassify_ID", jo.getString("ReceiveClassify_ID"));
							map.put("Name", jo.getString("ReceiveClassifyName"));
							map.put("Num", jo.getString("Num"));
							list.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Map<String, String> map = new HashMap<String, String>();
					map.put("Name", "发件箱");
					list.add(map);
					
					generals.add(list);
					sendRequest();
					receRequest();
					
					break;

				default:
					break;
				}
			}
		};
	}
	
}