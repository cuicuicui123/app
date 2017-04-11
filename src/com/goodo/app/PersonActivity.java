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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonActivity extends Activity implements OnClickListener{
	
	private ImageView iv_user3;
	private TextView tv_name;
	private TextView department;
	private TextView sex;
	private TextView qq;
	private TextView Email;
	private TextView tel;
	private TextView mobile;
	private ImageView iv_goout;
	
	private HttpClient httpClient;
	private Handler handler;
	private List<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		init();
		sendRequest();
		receRequest();
	}
	
	private void init(){
		httpClient = new DefaultHttpClient();
		iv_user3 = (ImageView) findViewById(R.id.iv_user3);
		iv_goout = (ImageView) findViewById(R.id.iv_goout);
		
		tv_name = (TextView) findViewById(R.id.tv_name);
		department = (TextView) findViewById(R.id.department2);
		sex = (TextView) findViewById(R.id.sex2);
		qq = (TextView) findViewById(R.id.qq2);
		Email = (TextView) findViewById(R.id.Email2);
		tel = (TextView) findViewById(R.id.tel2);
		mobile = (TextView) findViewById(R.id.mobile2);
		
		
		iv_user3.setOnClickListener(this);
		iv_goout.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_user3:
			finish();
			break;
		case R.id.iv_goout:
			Myconfig.login_flag = false;
			Intent it = new Intent(PersonActivity.this,StartActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			break;
		default:
			break;
		}
	}
	
	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet get = new HttpGet(Myconfig.IP+"/BasePlate/Interface/IInterfaceJson.asmx/UserInfo_GetSelf?User_ID="
										 +Myconfig.User_ID+"&SessionID="+Myconfig.SessionID);
				try {
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					if(entity!=null){
						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						StringBuffer response = new StringBuffer();
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
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void receRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("3456789-----"+response);
					list = new ArrayList<Map<String,String>>();
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						Map<String, String> map = new HashMap<String, String>();
						map.put("UserName", Goodo.getString("UserName"));
						map.put("OrganizationName", Goodo.getString("OrganizationName"));
						map.put("Sex", Goodo.getString("Sex"));
						map.put("QQ", Goodo.getString("QQ"));
						map.put("Email", Goodo.getString("Email"));
						map.put("Tel", Goodo.getString("Tel"));
						map.put("MPhone", Goodo.getString("MPhone"));
						list.add(map);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					tv_name.setText(list.get(0).get("UserName"));
					department.setText(list.get(0).get("OrganizationName"));
					if(list.get(0).get("Sex").equals("1")){
						sex.setText("ÄÐ");
					}else if(list.get(0).get("Sex").equals("0")){
						sex.setText("Å®");
					}
					qq.setText(list.get(0).get("QQ"));
					Email.setText(list.get(0).get("Email"));
					tel.setText(list.get(0).get("Tel"));
					mobile.setText(list.get(0).get("MPhone"));
					break;

				default:
					break;
				}
			}
		};
	}
	
	
}
