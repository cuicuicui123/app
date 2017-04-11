package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.goodo.adapter.AddgrapAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
//添加个人安排
public class DocSendActivity extends Activity implements OnClickListener{

	ImageView docsend_iv_return;
	EditText data_et;
	EditText work_et;
	EditText location_et;
	EditText content_et;
	ImageView iv_sure;
	RadioGroup radioGroup;
	RadioButton checkRadioButton;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private String Date = "";
	private String Work = "";
	private String Address = "";
	private String Content = "";
	private Boolean IsAllDay;
	private String BeginTime = "";
	private String EndTime = "";
	private Dialog CustomDialog;
	private SimpleDateFormat format;
	
	private HttpClient httpClient;
	private Handler handler;
	public static final int SHOW_RESPONSE = 1;
	
	private TextView tv_begin;
	private TextView tv_end;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doc_send);
		httpClient = new DefaultHttpClient();
		format = new SimpleDateFormat("HH:mm");
		
		
		data_et = (EditText) findViewById(R.id.data_et);
		Intent it = getIntent();
		data_et.setText(it.getStringExtra("Date"));
		
		
		
		work_et = (EditText) findViewById(R.id.work_et);
		location_et = (EditText) findViewById(R.id.location_et);
		content_et = (EditText) findViewById(R.id.content_et);
		iv_sure = (ImageView) findViewById(R.id.iv_sure);
		docsend_iv_return = (ImageView) findViewById(R.id.docsend_iv_return);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.check(R.id.hole_day);
		tv_begin = (TextView) findViewById(R.id.tv_begin);
		tv_end = (TextView) findViewById(R.id.tv_end);
		
		//默认全天
		IsAllDay = true;
		BeginTime = "06:00";
		EndTime = "18:00";
		tv_begin.setText("开始时间："+BeginTime);
		tv_end.setText("结束时间："+EndTime);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
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
		
		docsend_iv_return.setOnClickListener(this);
		iv_sure.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.docsend_iv_return:
			finish();
			break;
		case R.id.iv_sure:
			Date = data_et.getText().toString();
			Work = work_et.getText().toString();
			Address = location_et.getText().toString();
			Content = content_et.getText().toString();
			
			sendRequest(Date, IsAllDay, BeginTime, EndTime, Work, Content, Address);
			receRequest();
			
			break;
		default:
			break;
		}
	}
	
	private Dialog getCustomDialog(){
		final Dialog dialog = new Dialog(DocSendActivity.this,R.style.add_grap_dialog);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.custom,null);
		TimePicker timeBegin = (TimePicker) view.findViewById(R.id.custom_timePicker);
		timeBegin.setIs24HourView(true);
		TimePicker timeEnd = (TimePicker) view.findViewById(R.id.custom_timePickerend);
		timeEnd.setIs24HourView(true);
		
		BeginTime = EndTime = format.format(System.currentTimeMillis());
		System.out.println("234567----"+BeginTime);
		timeBegin.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				if(arg1<10){
					BeginTime = "0"+arg1+":"+arg2;
				}else{
					BeginTime = arg1+":"+arg2;
				}
			}
		});
		
		
		timeEnd.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				if(arg1<10){
					EndTime = "0"+arg1+":"+arg2;
				}else{
					EndTime = arg1+":"+arg2;
				}
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
	
	
	
	private void sendRequest(final String Date,final Boolean IsAllDay,final String BeginTime,
							 final String EndTime,final String Work,final String Content,final String Address){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/MySchedule_AddNew");
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
				String Url = builder.toString();
		
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpResponse = httpClient.execute(get);
					System.out.println("456789-----"+Date);
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
					System.out.println("456789-------"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(DocSendActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							it.putExtra("extra", true);
							setResult(Activity.RESULT_OK, it);
							finish();
						}else{
							Toast.makeText(DocSendActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
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
