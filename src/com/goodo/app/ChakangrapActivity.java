package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
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



import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ChakangrapActivity extends Activity implements OnClickListener{
	private ImageView chakan_grap_return;
	private ImageView chakan_grap_iv_more;
	private TextView chakan_grap_remind;
	private TextView chakan_grap_noremind;
	private TextView tv_Work;
	private TextView tv_Date;
	private TextView tv_Content;
	
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	public static final int DELSHOW_RESPONSE = 2;
	private Handler handler;
	String ID;
	String Type;
	String Date;
	String IsAllDay;
	
	String BeginTime;
	String EndTime;
	String Work;
	String Content;
	String Address;
	
	private PopupWindow popupWindow;
	private Button btn_bianji;
	private Button btn_del;
	private Button btn_zwdb;
	
	private SimpleDateFormat format;
	private int time = 0;
	private Calendar c = Calendar.getInstance();
	private PendingIntent pd;
	private AlarmManager alarmManager;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			HashMap<Integer,String> map = (HashMap<Integer,String>) data.getSerializableExtra("extra");
			String str = map.size()+"";
			String[] myDatechar = Date.split(" ");
			String myDate = myDatechar[0];
			chakan_grap_noremind.setText(str+"个提醒");

			format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent intent = new Intent(this,NotificationService.class);
			intent.putExtra("BeginTime", BeginTime);
			intent.putExtra("Work", Work);
			
			for(Integer key:map.keySet()){
				
				switch (key) {
				case 0:
					time = 0;
					break;
				case 1:
					time = 5;
					break;
				case 2:
					time = 10;
				    break;
				case 3:
					time = 15;
				    break;
				case 4:
					time = 20;
				    break;
				case 5:
					time = 30;
				    break;
				case 6:
					time = 45;
				    break;
				case 7:
					time = 1*60;
				    break;
				case 8:
					time = 2*60;
				    break;
				case 9:
					time = 5*60;
				    break;
				case 10:
					time = 12*60;
				    break;
				default:
					break;
				}
				
				String flag = ID+key;
				int id = Integer.parseInt(flag);
				pd = PendingIntent.getService(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
				java.util.Date date = new java.util.Date();
				try {
					date = format.parse(myDate+" "+BeginTime);
					c.setTime(date);
					c.add(Calendar.MINUTE, -time);
					if(c.getTimeInMillis()<System.currentTimeMillis()){
						Toast.makeText(this, "请设置有效提醒时间", Toast.LENGTH_SHORT).show();
					}else{
						alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pd);
						Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
						preferences = getSharedPreferences("GrapRemind", MODE_PRIVATE);
						editor = preferences.edit();
						
						editor.clear();
						editor.commit();
						Myconfig.set_Grapremind.add(id+" "+map.get(key)+" "+myDate+" "+BeginTime+" "+Work);
						editor.putStringSet("remind", Myconfig.set_Grapremind);
						editor.commit();
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		if(requestCode == 1&&resultCode == Activity.RESULT_OK){
			sendRequest(ID, Type);
		}
		
	}


	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chakangrap);
		
		
		Intent it = getIntent();
		ID = it.getStringExtra("ID");
		Type = it.getStringExtra("Type");
		httpClient = new DefaultHttpClient();
		sendRequest(ID, Type);
		
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String)msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						Date = Goodo.getString("Date");
						Work = Goodo.getString("Work");
						BeginTime = Goodo.getString("BeginTime");
						EndTime = Goodo.getString("EndTime");
						Content = Goodo.getString("Content");
						IsAllDay = Goodo.getString("IsAllDay");
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tv_Work = (TextView) findViewById(R.id.chakan_grap_title);
					tv_Date = (TextView) findViewById(R.id.chakan_grap_time);
					tv_Content = (TextView) findViewById(R.id.chakan_grap_content);
					tv_Work.setText(Work);
					tv_Content.setText(Content);
					String[] myDate = Date.split(" ");
					tv_Date.setText(myDate[0]+" "+BeginTime);
					break;

				default:
					break;
				}
			}
		};
		
		chakan_grap_return = (ImageView) findViewById(R.id.chakan_grap_return);
		chakan_grap_iv_more = (ImageView) findViewById(R.id.chakan_grap_iv_more);
		chakan_grap_remind = (TextView) findViewById(R.id.chakan_grap_remind);
		
		chakan_grap_noremind = (TextView) findViewById(R.id.chakan_grap_noremind);
		chakan_grap_noremind.setText(list.size()+"个提醒");
		
		chakan_grap_return.setOnClickListener(this); 
		chakan_grap_remind.setOnClickListener(this); 
		chakan_grap_noremind.setOnClickListener(this); 
		chakan_grap_iv_more.setOnClickListener(this);
	}
	
	
	
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.chakan_grap_return:
			finish();
			break;
		case R.id.chakan_grap_iv_more:
			getPopupWindow(arg0);
			break;
		case R.id.chakan_grap_remind:
			
			break;
		case R.id.chakan_grap_noremind:
			Intent it = new Intent(ChakangrapActivity.this,AddremindActivity.class);
			startActivityForResult(it, 0);
			break;
		}
	}
	

	
	private PopupWindow getPopupWindow(View v){
		LayoutInflater layoutInflater = getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.chakan_grap_dialog, null,false);
		
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(v);
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(popupWindow!=null && popupWindow.isShowing()){
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
		btn_bianji = (Button) view.findViewById(R.id.chakan_grap_bianji);
		btn_del = (Button) view.findViewById(R.id.chakan_grap_del);
		
		btn_bianji.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(ChakangrapActivity.this,BianJigrapActivity.class);
				it.putExtra("ID", ID);
				it.putExtra("Date", Date);
				it.putExtra("IsAllDay", IsAllDay);
				it.putExtra("BeginTime", BeginTime);
				it.putExtra("EndTime", EndTime);
				it.putExtra("Work", Work);
				it.putExtra("Content", Content);
				it.putExtra("Address", Address);
				startActivityForResult(it, 1);
			}
		});
		
		btn_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ChakangrapActivity.this)
				  	.setTitle("删除本条安排")
				  	.setMessage("确定吗？")
				  	.setPositiveButton("是", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							senDelRequest(ID);
							receDelRequest();
							
						}
					})
				  	.setNegativeButton("否", null)
				  	.show();
			}
		});
		return popupWindow;
	}
	
	
	private void sendRequest(final String ID,final String Type){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Schedule_GetSingle");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Unit_ID", Myconfig.Unit_ID);
				builder.appendQueryParameter("ID", ID);
				builder.appendQueryParameter("Type", Type);
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
	
	private void senDelRequest(final String ID){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/MySchedule_Delete");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("MySchedule_ID", ID);
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
						message.what = DELSHOW_RESPONSE;
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
	
	
	private void receDelRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case DELSHOW_RESPONSE:
					String response = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(ChakangrapActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							it.putExtra("extra", true);
							setResult(Activity.RESULT_OK, it);
							finish();
						}else{
							Toast.makeText(ChakangrapActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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
