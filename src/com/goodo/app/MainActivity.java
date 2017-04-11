package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	EditText login_id;
	EditText login_pwd;
	Button btn_login;
	TextView login_error;
	private TextView tv_sel;
	
	String str1;
	String str2;
	String OnlineType = "64";
	String DeviceName = "2";
	String Place = "1";
	ImageView main_iv_return;
	
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	
	private LocationManager locationManager;
	private Location location;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private boolean flag = false;
	
	private String Url;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//获取设备信息
		Build build = new Build();
		DeviceName = build.MODEL;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location!=null){
			Place = "经度:"+location.getLongitude()+"纬度:"+location.getLatitude();
		}
		httpClient = new DefaultHttpClient();
		preferences = getSharedPreferences("login", MODE_PRIVATE);
		editor = preferences.edit();

		login_id = (EditText) findViewById(R.id.login_id);
		login_pwd = (EditText) findViewById(R.id.login_pwd);
		tv_sel = (TextView) findViewById(R.id.tv_sel);
		tv_sel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(flag){
					flag = false;
					tv_sel.setTextColor(0xffffffff);
					editor.putString("pwd", "");
					editor.commit();
				}else{
					flag = true;
					tv_sel.setTextColor(0xff000000);
				}
			}
		});
		
		
		String user = preferences.getString("user", "");
		String pwd = preferences.getString("pwd", "");
		if(user!=""){
			login_id.setText(user);
		}
		if(pwd!=""){
			login_pwd.setText(pwd);
			flag = true;
			tv_sel.setTextColor(0xff000000);
		}
		
		btn_login = (Button) findViewById(R.id.btn_login);
		login_error = (TextView) findViewById(R.id.login_error);
		main_iv_return = (ImageView) findViewById(R.id.main_iv_return);
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login_error.setText("");
				str1 = login_id.getText().toString();
				str2 = login_pwd.getText().toString();
				sendRequest(str1, str2,OnlineType,DeviceName,Place);
				
				handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						switch (msg.what) {
						case SHOW_RESPONSE:
							
							String response = (String) msg.obj;
							try {
								JSONObject jsonObject = new JSONObject(response);
								JSONObject Goodo = jsonObject.getJSONObject("Goodo");
								String login_flag = Goodo.getString("EID");
								if(login_flag.equals("0")){
									editor.putString("user", str1);
									if(flag){
										editor.putString("pwd", str2);
									}
									editor.commit();
									
									Myconfig.User_ID = (String)Goodo.get("User_ID");
									Myconfig.UserName = (String)Goodo.get("UserName");
									Myconfig.SessionID = (String)Goodo.get("SessionID");
									Myconfig.Unit_ID = (String)Goodo.get("Unit_ID");
									Myconfig.login_flag = true;
									Intent it = new Intent(MainActivity.this,Select2Activity.class);
									startActivity(it);
								}else{
									login_error.setText("用户名或密码错误，请重新输入！");
									login_id.setText("");
									login_pwd.setText("");
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
			}
		});
		
		main_iv_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void sendRequest(final String str1,final String str2 ,final String OnlineType,final String DeviceName,final String Place){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MyScheduleForJfy/Interface.asmx/LoginUser");
				builder.appendQueryParameter("LoginID", str1);
				builder.appendQueryParameter("EncyptPassword", str2);
				builder.appendQueryParameter("OnlineType", OnlineType);
				builder.appendQueryParameter("DeviceName", DeviceName);
				builder.appendQueryParameter("Place", Place);
				Url = builder.toString();
				
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
	
	private void getUrl(){
		
		
	}
}
