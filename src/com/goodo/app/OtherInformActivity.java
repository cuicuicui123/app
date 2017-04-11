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



import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;


public class OtherInformActivity extends Activity {
	
	private ListView listView;
	ImageView iv_other_inform_return;
	List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	private OtherInformAdapter adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_inform);
		adapter = new OtherInformAdapter(list, OtherInformActivity.this);
		listView = (ListView) findViewById(R.id.other_inform_list);
		listView.setAdapter(adapter);
		
		httpClient = new DefaultHttpClient();
		sendRequest();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("-------------"+response);
					try {
						JSONArray jsonArray = new JSONArray(response);
						int iSize = jsonArray.length();
						for(int i = 0;i<iSize;i++){
							JSONObject object = jsonArray.getJSONObject(i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("SID", object.get("SID"));
							map.put("SName", object.get("SName"));
							list.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent it = new Intent(OtherInformActivity.this,OtherinformlistActivity.class);
							it.putExtra("SID", (Integer)list.get(arg2).get("SID"));
							it.putExtra("SName", (String)list.get(arg2).get("SName"));
							startActivity(it);
						}
					});
					break;

				default:
					break;
				}
			}
		};
		
		
		
		//·µ»Ø¼ü
		iv_other_inform_return = (ImageView) findViewById(R.id.other_inform_iv_return);
		iv_other_inform_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}

	
	

	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/Get_Json_SubjectChlidList");
				String Url = builder.toString();
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					
					if (entity!=null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine())!=null) {
							response.append(line);
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
