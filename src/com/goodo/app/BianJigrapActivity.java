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

import utils.DateTransform;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class BianJigrapActivity extends Activity implements OnClickListener{

	private ImageView iv_return;
	private Button btn_sure;
	private EditText et_date;
	private EditText et_work;
	private EditText et_address;
	private EditText et_content;
	private RadioGroup radioGroup;
	
	private String ID;
	private String Date;
	private String Work;
	private String Address;
	private String Content;
	private Boolean IsAllDay;
	private String BeginTime;
	private String EndTime;
	private Dialog CustomDialog;
	private SimpleDateFormat format;
	
	private HttpClient httpClient;
	private Handler handler;
	
	private TextView tv_begin;
	private TextView tv_end;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bian_jigrap);
		httpClient = new DefaultHttpClient();
		format = new SimpleDateFormat("HH:mm");
		//获取传入安排信息
		Intent it = getIntent();
		
		ID = it.getStringExtra("ID");
		Date = DateTransform.transform(it.getStringExtra("Date"));
		if(it.getStringExtra("IsAllDay")!=null){
			if(it.getStringExtra("IsAllDay").equals("1")){
				IsAllDay = true;
			}else{
				IsAllDay = false;
			}
		}
		
		BeginTime = it.getStringExtra("BeginTime");
		EndTime = it.getStringExtra("EndTime");
		Work = it.getStringExtra("Work");
		Content = it.getStringExtra("Content");
		Address = it.getStringExtra("Address");
		
		
		
		
		//布局
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(this);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		btn_sure.setOnClickListener(this);
		et_date = (EditText) findViewById(R.id.date_et);
		et_date.setText(Date);
		et_work = (EditText) findViewById(R.id.work_et);
		et_work.setText(Work);
		et_address = (EditText) findViewById(R.id.location_et);
		et_address.setText(Address);
		et_content = (EditText) findViewById(R.id.content_et);
		et_content.setText(Content);
		tv_begin = (TextView) findViewById(R.id.tv_begin);
		tv_end = (TextView) findViewById(R.id.tv_end);
		tv_begin.setText("开始时间："+BeginTime);
		tv_end.setText("结束时间："+EndTime);
		
		
		//选择时间
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				int radioButtonId = arg0.getCheckedRadioButtonId();
				switch (radioButtonId) {
				case R.id.hole_day:
					IsAllDay = true;
					BeginTime = "06:00";
					EndTime = "18:00";
					tv_begin.setText("开始时间："+BeginTime);
					tv_end.setText("结束时间："+EndTime);
					break;
				case R.id.morning:
					IsAllDay = false;
					BeginTime = "06:00";
					EndTime = "12:00";
					tv_begin.setText("开始时间："+BeginTime);
					tv_end.setText("结束时间："+EndTime);
					break;
				case R.id.afternoon:
					IsAllDay = false;
					BeginTime = "12:00";
					EndTime = "18:00";
					tv_begin.setText("开始时间："+BeginTime);
					tv_end.setText("结束时间："+EndTime);
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
		case R.id.btn_sure:
			Date = et_date.getText().toString();
			Work = et_work.getText().toString();
			Address = et_address.getText().toString();
			Content = et_content.getText().toString();
			sendRequest(ID, Date, IsAllDay, BeginTime, EndTime, Work, Content, Address);
			receRequest();
			
			break;
		default:
			
			break;
		}
	}
	
	private Dialog getCustomDialog(){
		final Dialog dialog = new Dialog(BianJigrapActivity.this,R.style.add_grap_dialog);
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
				tv_begin.setText("开始时间："+BeginTime);
				tv_end.setText("结束时间："+EndTime);
				dialog.dismiss();
			}
		});
		
		
		return dialog;
	}

	private void sendRequest(final String ID,final String Date,final Boolean IsAllDay,final String BeginTime,
			 final String EndTime,final String Work,final String Content,final String Address){
			new Thread(new Runnable() {

				@Override
				public void run() {
					Uri.Builder builder = new Uri.Builder();
					builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/MySchedule_Save");
					builder.appendQueryParameter("SessionID", Myconfig.SessionID);
					builder.appendQueryParameter("User_ID", Myconfig.User_ID);
					builder.appendQueryParameter("MySchedule_ID", ID);
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
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("456789-------"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(BianJigrapActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							it.putExtra("extra", true);
							setResult(Activity.RESULT_OK, it);
							finish();
						}else{
							Toast.makeText(BianJigrapActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
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
	
	
}
