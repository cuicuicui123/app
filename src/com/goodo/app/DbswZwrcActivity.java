package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class DbswZwrcActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private ImageView iv_sure;
	private EditText et_date;
	private EditText et_work;
	private EditText et_location;
	private EditText et_content;
	private RadioGroup radioGroup;
	private Dialog CustomDialog;
	private SimpleDateFormat format;
	
	private String Date = "";
	private Boolean IsAllDay = false;
	private String BeginTime;
	private String EndTime;
	private String Address;
	private String RelatedUser = "";
	private String Case_ID = "1";
	private String CaseName = "1";
	
	
	//传递过来的参数
	private String Plan_ID;
	private String Work;
	private String Content;
	
	private HttpClient httpClient;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dbsw_zwrc);
		format = new SimpleDateFormat("HH:mm");
		httpClient = new DefaultHttpClient();
		
		
		Intent it = getIntent();
		Plan_ID = it.getStringExtra("Plan_ID");
		Work = it.getStringExtra("Work");
		Content = it.getStringExtra("Content");
		
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_sure = (ImageView) findViewById(R.id.iv_sure);
		et_date = (EditText) findViewById(R.id.date_et);
		et_work = (EditText) findViewById(R.id.work_et);
		et_work.setText(Work);
		
		et_location = (EditText) findViewById(R.id.location_et);
		et_content = (EditText) findViewById(R.id.content_et);
		et_content.setText(Content);
		
		
		iv_return.setOnClickListener(this);
		iv_sure.setOnClickListener(this);
		IsAllDay = true;
		BeginTime = "06:00";
		EndTime = "18:00";
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.check(R.id.hole_day);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radioButtonId = arg0.getCheckedRadioButtonId();
				switch (radioButtonId) {
				case R.id.hole_day:
					IsAllDay = true;
					BeginTime = "06:00";
					EndTime = "18:00";
					break;
				case R.id.morning:
					IsAllDay = false;
					BeginTime = "06:00";
					EndTime = "12:00";
					break;
				case R.id.afternoon:
					IsAllDay = false;
					BeginTime = "12:00";
					EndTime = "18:00";
					break;
				case R.id.custom:
					IsAllDay = false;
					CustomDialog = getCustomDialog();
					CustomDialog.show();
					break;


				default:
					break;
				}
			}
		});
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_sure:
			Date = et_date.getText().toString();
			Work = et_work.getText().toString();
			Address = et_location.getText().toString();
			Content = et_content.getText().toString();
			sendRequest(Date, IsAllDay, BeginTime, EndTime, Work, Content, Address, RelatedUser, Case_ID, CaseName, Plan_ID);
			receRequest();
			finish();
			break;
		default:
			break;
		}
	}
	
	
	private Dialog getCustomDialog(){
		final Dialog dialog = new Dialog(DbswZwrcActivity.this,R.style.add_grap_dialog);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.custom,null);
		TimePicker timeBegin = (TimePicker) view.findViewById(R.id.custom_timePicker);
		timeBegin.setIs24HourView(true);
		TimePicker timeEnd = (TimePicker) view.findViewById(R.id.custom_timePickerend);
		timeEnd.setIs24HourView(true);
		
		BeginTime = EndTime = format.format(System.currentTimeMillis());
		timeBegin.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				BeginTime = arg1+":"+arg2;
			}
		});
		
		
		timeEnd.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				EndTime = arg1+":"+arg2;
			}
		});
		dialog.setContentView(view);
		
		ImageView iv = (ImageView) view.findViewById(R.id.iv_sure);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		
		return dialog;
	}
	
	
	
	
	
	private void sendRequest(final String Date,final Boolean IsAllDay,final String BeginTime,final String EndTime,final String Work,final String Content,final String Address,final String RelatedUser,final String Case_ID,final String CaseName,final String Plan_ID){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/MySchedule_AddFromPlan");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("UserName", Myconfig.UserName);
				builder.appendQueryParameter("Date", Date);
				builder.appendQueryParameter("IsAllDay", IsAllDay+"");
				builder.appendQueryParameter("BeginTime", BeginTime);
				builder.appendQueryParameter("EndTime", EndTime);
				builder.appendQueryParameter("Work", Work);
				builder.appendQueryParameter("Content", Content);
				builder.appendQueryParameter("Address", Address);
				builder.appendQueryParameter("RelatedUser", "1");
				builder.appendQueryParameter("Case_ID", "1");
				builder.appendQueryParameter("CaseName", "1");
				builder.appendQueryParameter("Plan_ID", Plan_ID);
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
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(DbswZwrcActivity.this, "转为日程成功", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(DbswZwrcActivity.this, "失败！请检查输入日期格式", Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
				}
			}
		};
	}
	
	
	
	
	
}
