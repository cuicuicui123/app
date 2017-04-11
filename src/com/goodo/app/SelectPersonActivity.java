package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectPersonActivity extends Activity {
	private ImageView iv_return;
	private ImageView iv_sure;
	private HttpClient httpClient;
	private Handler handler;
	public static final int MORE_RESPONSE = 2;
	private List<Map<String, Object>> generalsTypes;
	private List<List<Map<String, Object>>> generals = new ArrayList<List<Map<String,Object>>>();
	private ExpandableListView expandableListView;
	private ExpandableListAdapter adapter;
	private int a = 0;
	
	private downloadTask task;
	private boolean _isExe = false;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_person);
		Intent it = getIntent();
		list = (List<HashMap<String, String>>) it.getSerializableExtra("list");

		httpClient = new DefaultHttpClient();
		sendOrgRequest();
		receOrgRequest();
		
		iv_sure = (ImageView) findViewById(R.id.select_person_iv_more);
		iv_sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = getIntent();
				it.putExtra("list", (Serializable)list);
				setResult(Activity.RESULT_OK, it);
				finish();
			}
		});
		iv_return = (ImageView) findViewById(R.id.select_person_iv_return);
		iv_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		expandableListView = (ExpandableListView) findViewById(R.id.select_person_expandableListView);
		adapter = new BaseExpandableListAdapter() {
			
			
			TextView getTextView(){
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				TextView textView = new TextView(SelectPersonActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(30, 30, 30, 30);
				textView.setTextColor(Color.BLACK);
				return textView;
		    }
			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				RelativeLayout rl = new RelativeLayout(SelectPersonActivity.this);
				TextView textView = getTextView();
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				textView.setText("  "+getGroup(groupPosition).toString());
				textView.setPadding(36, 0, 0, 0);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
				lp.setMargins(36, 36, 36, 36);
				rl.addView(textView,lp);
				return rl;
			}
			
			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
			}
			
			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return generalsTypes.size();
			}
			
			@Override
			public Object getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return generalsTypes.get(groupPosition).get("NAME");
			}
			
			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return generals.get(groupPosition).size();
			}
			
			@Override
			public View getChildView(final int groupPosition, final int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				RelativeLayout rl = new RelativeLayout(SelectPersonActivity.this);
				TextView textView = getTextView();
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				textView.setText("  "+getChild(groupPosition, childPosition).toString());
				textView.setPadding(36, 0, 0, 0);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
				lp.setMargins(36, 36, 36, 36);
				rl.addView(textView,lp);
				
				final TextView textView2 = new TextView(SelectPersonActivity.this);
				textView2.setTextSize(30);
				if(generals.get(groupPosition).get(childPosition).get("flag").equals("1")){
					textView2.setTextColor(Color.BLACK);
				}else{
					textView2.setTextColor(Color.WHITE);
				}
				textView2.setText("¡Ì");
				RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
						60,
						RelativeLayout.LayoutParams.MATCH_PARENT);
				lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp2.addRule(RelativeLayout.CENTER_VERTICAL);
				rl.addView(textView2,lp2);
				rl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(generals.get(groupPosition).get(childPosition).get("flag").equals("0")){
							textView2.setTextColor(Color.BLACK);
							generals.get(groupPosition).get(childPosition).put("flag", "1");
							HashMap<String,String> map_send = new HashMap<String, String>(); 
							map_send.put("ID", (String)generals.get(groupPosition).get(childPosition).get("User_ID"));
							map_send.put("Name", (String)generals.get(groupPosition).get(childPosition).get("UserName"));
							map_send.put("F", groupPosition+"");
							map_send.put("C", childPosition+"");
							list.add(map_send);
						}else{
							textView2.setTextColor(Color.WHITE);
							generals.get(groupPosition).get(childPosition).put("flag", "0");
							for(int i = 0;i<list.size();i++){
								if(list.get(i).get("ID").equals(generals.get(groupPosition).get(childPosition).get("User_ID"))){
									list.remove(i);
								}
							}
						}
					}
				});
				
				return rl;
			}
			
			@Override
			public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childPosition;
			}
			
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return generals.get(groupPosition).get(childPosition).get("UserName");
			}
		};
	}
	

	private void sendOrgRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet get = new HttpGet(Myconfig.IP+"/BasePlate/Interface/IInterfaceJson.asmx/OrgTree_Get?User_ID="+Myconfig.User_ID+"&Unit_ID="+Myconfig.Unit_ID+"&SessionID="+Myconfig.SessionID);
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
	private void receOrgRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					generalsTypes = new ArrayList<Map<String,Object>>();
					System.out.println("11111111"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONObject Record = Goodo.getJSONObject("Record");
						String Recordstr = Record.getString("Record");
						char[] Recordchar = Recordstr.toCharArray();
						if(Recordchar[0]=='['){
							JSONArray RecordArray = Record.getJSONArray("Record");
							int iSize = RecordArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RecordArray.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("ID", jo.get("ID"));
								map.put("NAME", jo.get("NAME"));
								map.put("T", jo.get("T"));
								generalsTypes.add(map);
							}
						}else{
							JSONObject jo = Record.getJSONObject("Record");
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("ID", jo.get("ID"));
							map.put("NAME", jo.get("NAME"));
							map.put("T", jo.get("T"));
							generalsTypes.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for(int i = 0;i<generalsTypes.size();i++){
						if(!_isExe){
							task = new downloadTask();
							String ID = (String)generalsTypes.get(i).get("ID");
							task.execute(Myconfig.IP+"/BasePlate/Interface/IInterfaceJson.asmx/User_GetList?User_ID="+Myconfig.User_ID+"&Unit_ID="+Myconfig.Unit_ID+"&SessionID="+Myconfig.SessionID+"&Org_ID="+ID+"&IsListChild=true");
						}
					}
					_isExe = true;
					
					break;

				default:
					break;
				}
			}
		};
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(_isExe){
			task.cancel(true);
		}
	}

	class downloadTask extends AsyncTask<String, Integer, Boolean>{
		String str = null;

		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				HttpGet get = new HttpGet(arg0[0]);
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
						str = response.toString();
					
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			List<Map<String, Object>> mylist = new ArrayList<Map<String,Object>>();
			try {
				JSONObject jsonObject = new JSONObject(str);
				JSONObject Goodo = jsonObject.getJSONObject("Goodo");
				String Rstr = Goodo.getString("R");
				char[] Rchar = Rstr.toCharArray();
				if(Rchar[0]=='['){
					JSONArray RArray = Goodo.getJSONArray("R");
					int iSize = RArray.length();
					for(int i = 0;i<iSize;i++){
						JSONObject jo = RArray.getJSONObject(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("User_ID", jo.get("User_ID"));
						map.put("LoginID", jo.get("LoginID"));
						map.put("UserName", jo.get("UserName"));
						map.put("flag", "0");
						mylist.add(map);
					}
				}else{
					JSONObject jo = Goodo.getJSONObject("R");
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("User_ID", jo.get("User_ID"));
					map.put("LoginID", jo.get("LoginID"));
					map.put("UserName", jo.get("UserName"));
					map.put("flag", "0");
					mylist.add(map);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			generals.add(mylist);
			a++;
			if(a == generalsTypes.size()){
				expandableListView.setAdapter(adapter);
				for(int i = 0;i<list.size();i++){
					int f = Integer.parseInt(list.get(i).get("F"));
					int c = Integer.parseInt(list.get(i).get("C"));
					generals.get(f).get(c).put("flag", "1");
				}
			}
			System.out.println("2222222222"+a);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
	}
	
}
