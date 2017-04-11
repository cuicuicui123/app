package com.goodo.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goodo.adapter.AddgrapAdapter;
import com.goodo.app.fragment.MyanrenFragment;
import com.goodo.app.javabean.SendAttachObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView.ScaleType;
import android.widget.TimePicker.OnTimeChangedListener;

import static com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken.Uri;
import static javafx.scene.input.KeyCode.R;

public class ChakandbswActivity extends Activity implements OnClickListener{

	private ImageView chakan_dbsw_return;
	private TextView chakan_dbsw_remind;
	private TextView chakan_dbsw_noremind;
	private List<List<String>> general = new ArrayList<List<String>>();
	private List<Map<String, Object>> list_attach;
	private List<Map<String, Object>> list_liuzhuan;
	private List<Map<String, Object>> list_beizhu;
	private List<List<Map<String, Object>>> list_Bzattach;
	
	private Dialog dialog;
	private ListView listView;
	private List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
	private Button btn_dialog_sure;
	private Button btn_dialog_quxiao;
	private ImageView add_dbsw_dialog_addremind;
	private String date;
	private String time;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button btn_gettime;
	private Map<String, Object> map;
	
	//界面布局
	private TextView tv_title;
	private TextView tv_bianhao;
	private TextView tv_time;
	private TextView tv_content;
	private ImageView iv_beizhu;
	private ImageView iv_zhuanfa;
	
	//根据ID获取数据
	private String Plan_ID ="";
	private HttpClient httpClient;
	private Handler handler;
	public static final int ZHUANFA_RESPONSE = 2;
	
	private String Work = "";
	private String PlanNo = "";
	private String CreateDate = "";
	private String Content = "";
	private boolean IsPublic = false;
	private String EndDate = "";
	private boolean IsAutoFinish = false;
	private String Case_ID = "-1";
	private String CaseName = "12";
	private String ReceiveUserIDs = "";
	private String ReceiveUserNames ="";
	private String AttachUrls = "";
	private String AttachNames = "";
	private String PlanType = "";
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private LinearLayout ll_fujian;
	
	private Calendar c = Calendar.getInstance();
	private PendingIntent pd;
	private AlarmManager alarmManager;
	
	List<Map<String, String>> list;
	private HashMap<Integer, SendAttachObject> map_send;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chakandbsw);
		list = new ArrayList<Map<String,String>>();
		httpClient = new DefaultHttpClient();
		map_send = new HashMap<Integer, SendAttachObject>();
		//获取传入ID
		Intent it = getIntent();
		Plan_ID = it.getStringExtra("Plan_ID");
		System.out.println("456789--------"+Plan_ID);
		
		
		//界面布局
		tv_title = (TextView) findViewById(R.id.chakan_dbsw_title);
		tv_bianhao = (TextView) findViewById(R.id.chakan_dbsw_bianhao);
		tv_time = (TextView) findViewById(R.id.chakan_dbsw_time);
		tv_content = (TextView) findViewById(R.id.chakan_dbsw_content);
		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
		
		chakan_dbsw_return = (ImageView) findViewById(R.id.chakan_dbsw_return);
		chakan_dbsw_remind = (TextView) findViewById(R.id.chakan_dbsw_remind);
		chakan_dbsw_noremind = (TextView) findViewById(R.id.chakan_dbsw_noremind);
		
		iv_beizhu = (ImageView) findViewById(R.id.chakan_dbsw_iv_beizhu);
		iv_zhuanfa = (ImageView) findViewById(R.id.chakan_dbsw_iv_zhuanfa);
		
		chakan_dbsw_return.setOnClickListener(this);
		chakan_dbsw_remind.setOnClickListener(this);
		chakan_dbsw_noremind.setOnClickListener(this);
		iv_beizhu.setOnClickListener(this);
		iv_zhuanfa.setOnClickListener(this);
		
		

		
		
		//发送接收数据
		sendRequest(Plan_ID);
		
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("456789---------"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONObject Rjo = Goodo.getJSONObject("R");
						Work = Rjo.getString("Work");
						PlanNo = Rjo.getString("PlanNo");
						CreateDate = Rjo.getString("CreateDate");
						Content = Rjo.getString("Content");
						PlanType = Rjo.getString("PlanType");
						if(Rjo.getString("IsPublic").equals("0")){
							IsPublic = true;
						}
						ReceiveUserIDs = Myconfig.User_ID;
						ReceiveUserNames = Myconfig.UserName;
						
						list_attach = new ArrayList<Map<String,Object>>();
						try {
							String Attachstr = Rjo.getString("Attach");
							char[] Attachchar = Attachstr.toCharArray();
							if(Attachchar[0]=='['){
								JSONArray AttachArray = Rjo.getJSONArray("Attach");
								int iSize = AttachArray.length();
								for(int i = 0;i<iSize;i++){
									JSONObject jo = AttachArray.getJSONObject(i);
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("Attach_ID", jo.getString("Attach_ID"));
									map.put("AttachUrl", jo.getString("AttachUrl"));
									map.put("AttachName", jo.getString("AttachName"));
									list_attach.add(map);
									SendAttachObject object = new SendAttachObject();
									object.setAttachID(jo.getString("Attach_ID"));
									object.setAttachUrl(jo.getString("AttachUrl"));
									object.setAttachName(jo.getString("AttachName"));
									map_send.put(i, object);
								}
							}else{
								JSONObject jo = Rjo.getJSONObject("Attach");
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("Attach_ID", jo.getString("Attach_ID"));
								map.put("AttachUrl", jo.getString("AttachUrl"));
								map.put("AttachName", jo.getString("AttachName"));
								list_attach.add(map);
								SendAttachObject object = new SendAttachObject();
								object.setAttachID(jo.getString("Attach_ID"));
								object.setAttachUrl(jo.getString("AttachUrl"));
								object.setAttachName(jo.getString("AttachName"));
								map_send.put(0, object);
							}
						} catch (Exception e) {
						}
						
						
						
						list_beizhu = new ArrayList<Map<String,Object>>();
						list_Bzattach = new ArrayList<List<Map<String,Object>>>();
						String ReMark_ID = null;
						try {
							if(Rjo.getString("Remark")!=null){
								ReMark_ID = Rjo.getString("Remark_ID");
								String Remarkstr = Rjo.getString("Remark");
								char[] Remarkchar = Remarkstr.toCharArray();
								if(Remarkchar[0]=='['){
									JSONArray RemarkArray = Rjo.getJSONArray("Remark");
									int iSize = RemarkArray.length();
									for(int i = 0;i<iSize;i++){
										JSONObject jo = RemarkArray.getJSONObject(i);
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("UserName", jo.getString("UserName"));
										map.put("Content", jo.getString("Content"));
										list_beizhu.add(map);
										List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
										try {
											String Astr = jo.getString("Attach");
											char[] Astrchar = Astr.toCharArray();
											if(Astrchar[0] == '['){
												JSONArray Rarray = jo.getJSONArray("Attach");
												int RiSize = Rarray.length();
												for(int Ri = 0;Ri < RiSize;Ri ++){
													JSONObject RAjo = Rarray.getJSONObject(Ri);
													Map<String, Object> Rmap = new HashMap<String, Object>();
													Rmap.put("Attach_ID", RAjo.get("Attach_ID"));
													Rmap.put("AttachUrl", RAjo.get("AttachUrl"));
													Rmap.put("AttachName", RAjo.get("AttachName"));
													Rmap.put("Plan_ID", Plan_ID);
													Rmap.put("ReMark_ID", ReMark_ID);
													list.add(Rmap);
												}
											}else{
												JSONObject RAjo = jo.getJSONObject("Attach");
												Map<String, Object> Rmap = new HashMap<String, Object>();
												Rmap.put("Attach_ID", RAjo.get("Attach_ID"));
												Rmap.put("AttachUrl", RAjo.get("AttachUrl"));
												Rmap.put("AttachName", RAjo.get("AttachName"));
												Rmap.put("Plan_ID", Plan_ID);
												Rmap.put("ReMark_ID", ReMark_ID);
												list.add(Rmap);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										list_Bzattach.add(list);
									}
								}else{
									JSONObject RemarkJo = Rjo.getJSONObject("Remark");
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("UserName", RemarkJo.getString("UserName"));
									map.put("Content", RemarkJo.getString("Content"));
									list_beizhu.add(map);
									List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
									try {
										String Astr = RemarkJo.getString("Attach");
										char[] Astrchar = Astr.toCharArray();
										if(Astrchar[0] == '['){
											JSONArray Rarray = RemarkJo.getJSONArray("Attach");
											int RiSize = Rarray.length();
											for(int Ri = 0;Ri < RiSize;Ri ++){
												JSONObject RAjo = Rarray.getJSONObject(Ri);
												Map<String, Object> Rmap = new HashMap<String, Object>();
												Rmap.put("Attach_ID", RAjo.get("Attach_ID"));
												Rmap.put("AttachUrl", RAjo.get("AttachUrl"));
												Rmap.put("AttachName", RAjo.get("AttachName"));
												Rmap.put("Plan_ID", Plan_ID);
												Rmap.put("ReMark_ID", ReMark_ID);
												list.add(Rmap);
											}
										}else{
											JSONObject RAjo = RemarkJo.getJSONObject("Attach");
											Map<String, Object> Rmap = new HashMap<String, Object>();
											Rmap.put("Attach_ID", RAjo.get("Attach_ID"));
											Rmap.put("AttachUrl", RAjo.get("AttachUrl"));
											Rmap.put("AttachName", RAjo.get("AttachName"));
											Rmap.put("Plan_ID", Plan_ID);
											Rmap.put("ReMark_ID", ReMark_ID);
											list.add(Rmap);
										}
										
									} catch (Exception e) {
										e.printStackTrace();
									}
									list_Bzattach.add(list);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
						
						
						list_liuzhuan = new ArrayList<Map<String,Object>>();
						try {
							String Receiverstr = Rjo.getString("Receiver");
							char[] Receiverchar = Receiverstr.toCharArray();
							if(Receiverchar[0]=='['){
								JSONArray ReceiverArray = Rjo.getJSONArray("Receiver");
								int iSize = ReceiverArray.length();
								for(int i = 0;i<iSize;i++){
									JSONObject jo = ReceiverArray.getJSONObject(i);
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("User_ID", jo.getString("User_ID"));
									map.put("UserName", jo.getString("UserName"));
									map.put("IsFinish", jo.getString("IsFinish"));
									map.put("FinishDate", jo.getString("FinishDate"));
									list_liuzhuan.add(map);
								}
							}else{
								JSONObject jo = Rjo.getJSONObject("Receiver");
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("User_ID", jo.getString("User_ID"));
								map.put("UserName", jo.getString("UserName"));
								map.put("IsFinish", jo.getString("IsFinish"));
								map.put("FinishDate", jo.getString("FinishDate"));
								list_liuzhuan.add(map);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
						List<String> list = new ArrayList<String>();
						for(int i = 0;i<list_beizhu.size();i++){
							list.add((String)(list_beizhu.get(i).get("UserName"))+(String)(list_beizhu.get(i).get("Content")));
						}
						
						List<String> list_lz = new ArrayList<String>();
						for(int i = 0;i<list_liuzhuan.size();i++){
							list_lz.add((String)(list_liuzhuan.get(i).get("UserName")));
						}
						general.add(list);
						general.add(list_lz);
						
						
						if(list_attach.size()>0){
							LinearLayout ll = getLinearLayout(list_attach.size(), list_attach);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							ll_fujian.addView(ll, lp);
						}
						
						if(list.size()>0){
							LinearLayout ll =getBeizhull(list.size(), list);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							ll_fujian.addView(ll, lp);
						}
						
						if(list_liuzhuan.size()>0){
							LinearLayout ll = getLiuzhuanll(list_liuzhuan.size(), list_liuzhuan);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							ll_fujian.addView(ll, lp);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					tv_title.setText(Work);
					tv_bianhao.setText("编号："+PlanNo);
					tv_time.setText("创建时间："+CreateDate);
					tv_content.setText(Content);
					
					break;
				}
				
			}
		};
		
		
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.chakan_dbsw_return:
			finish();
			break;
		case R.id.chakan_dbsw_remind:
			break;
		case R.id.chakan_dbsw_noremind:
			dialog = getDialog();
			dialog.show();
			break;
		case R.id.chakan_dbsw_iv_beizhu:
			Intent it = new Intent(ChakandbswActivity.this,DbswBeizhuActivity.class);
			it.putExtra("Plan_ID", Plan_ID);
			startActivityForResult(it, 1);
			break;
		case R.id.chakan_dbsw_iv_zhuanfa:
			Intent it_zhuanfa = new Intent(ChakandbswActivity.this,DbswAddActivity.class);
			it_zhuanfa.putExtra("Kind", "转发");
			it_zhuanfa.putExtra("Kind2", "sender");
			it_zhuanfa.putExtra("Plan_ID", Plan_ID);
			it_zhuanfa.putExtra("Work", Work);
			it_zhuanfa.putExtra("Content", Content);
			it_zhuanfa.putExtra("IsPublic", IsPublic+"");
			it_zhuanfa.putExtra("EndDate", EndDate);
			it_zhuanfa.putExtra("IsAutoFinish", IsAutoFinish+"");
			it_zhuanfa.putExtra("Case_ID", Case_ID);
			it_zhuanfa.putExtra("CaseName", CaseName);
			it_zhuanfa.putExtra("AttachUrls", AttachUrls);
			it_zhuanfa.putExtra("AttachNames", AttachNames);
			it_zhuanfa.putExtra("map", map_send);
			startActivity(it_zhuanfa);
			break;
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			list = (List<Map<String, String>>) data.getSerializableExtra("list");
			System.out.println("2222222222444444"+list.size());
			for(int i = 0;i<list.size();i++){
				if(i==0){
					ReceiveUserIDs = list.get(i).get("ID");
					ReceiveUserNames = list.get(i).get("Name");
				}else{
					ReceiveUserIDs = ReceiveUserIDs+","+list.get(i).get("ID");
					ReceiveUserNames = ReceiveUserNames+","+list.get(i).get("Name");
				}
			}
			sendZhuanfaRequest(Plan_ID, Work, Content, IsPublic, EndDate, IsAutoFinish, Case_ID, CaseName, ReceiveUserIDs, ReceiveUserNames, AttachUrls, AttachNames);
			receZhuanfaRequest();
		}
	}

	private Dialog getDialog(){
		final Dialog dialog = new Dialog(ChakandbswActivity.this, R.style.add_grap_dialog);
		WindowManager.LayoutParams wParams = dialog.getWindow().getAttributes();
		wParams.width = 300;
		wParams.height = 200;
		dialog.getWindow().setAttributes(wParams);
		
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.add_grap_dialog, null);
		listView = (ListView) view.findViewById(R.id.add_grap_dialog_listView);
		final AddgrapAdapter adapter = new AddgrapAdapter(list2, ChakandbswActivity.this);
		listView.setAdapter(adapter);
		
		//确定,取消按钮
		btn_dialog_sure = (Button) view.findViewById(R.id.add_grap_dialog_sure);
		btn_dialog_quxiao = (Button) view.findViewById(R.id.add_grap_dialog_quxiao);
		btn_dialog_sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				chakan_dbsw_noremind.setText(list2.size()+"个提醒");
				for(int i = 0;i<list2.size();i++){
					if(list2.get(i).get("State").equals("true")){
						String date = (String) list2.get(i).get("日期");
						String time = (String) list2.get(i).get("时间");
						
						String[] dateYear = date.split("年");
						int Year = Integer.parseInt(dateYear[0]);
						String[] dateMonth = dateYear[1].split("月");
						int Month = Integer.parseInt(dateMonth[0]);
						String[] dateDay = dateMonth[1].split("日");
						
						int Day = Integer.parseInt(dateDay[0]);
						
						String[] timeMore = time.split("时");
						int Hour = Integer.parseInt(timeMore[0]);
						String[] minuteMore = timeMore[1].split("分");
						int Minute = Integer.parseInt(minuteMore[0]);
						System.out.println("4567890----"+Hour);
						
						c.set(Calendar.YEAR, Year);
						c.set(Calendar.MONTH, Month-1);
						c.set(Calendar.DAY_OF_MONTH, Day);
						c.set(Calendar.HOUR_OF_DAY, Hour);
						c.set(Calendar.MINUTE, Minute);
						
						
						
						alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
						Intent intent = new Intent(ChakandbswActivity.this,NotificationService.class);
						intent.putExtra("BeginTime", (String)list2.get(i).get("日期")+(String)list2.get(i).get("时间"));
						intent.putExtra("Work", Work);
						int n = Day+Hour;
						String flag = Plan_ID+n+Minute;
						
						int id = Integer.parseInt(flag);
						pd = PendingIntent.getService(ChakandbswActivity.this, id, intent, PendingIntent.FLAG_ONE_SHOT);
						
						System.out.println("11111111---"+System.currentTimeMillis());
						System.out.println("11111111----"+c.getTimeInMillis());
						System.out.println("11111111"+c.getTime());
						alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pd);
						Toast.makeText(ChakandbswActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
						
						preferences = getSharedPreferences("GrapRemind", MODE_PRIVATE);
						editor = preferences.edit();
						
						editor.clear();
						editor.commit();
						
						Myconfig.set_Grapremind.add(id+" "+"待办事务 "+" "+(String)list2.get(i).get("日期")+" "+(String)list2.get(i).get("时间")+" "+Work);
						editor.putStringSet("remind", Myconfig.set_Grapremind);
						editor.commit();
					}
					
				}
				
				
				dialog.dismiss();
			}
		});
		btn_dialog_quxiao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		
		//添加提醒
		add_dbsw_dialog_addremind = (ImageView) view.findViewById(R.id.add_grap_dialog_addremind);
		add_dbsw_dialog_addremind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Dialog dialog2 = new Dialog(ChakandbswActivity.this, R.style.add_grap_dialog);
				DisplayMetrics displayMetrics = new DisplayMetrics();
				Display display = ChakandbswActivity.this.getWindowManager().getDefaultDisplay();
				display.getMetrics(displayMetrics);
				
				WindowManager.LayoutParams wParams = dialog2.getWindow().getAttributes();
				wParams.width = display.getWidth();
				wParams.height = display.getHeight()/2;
				dialog2.getWindow().setAttributes(wParams);
				LayoutInflater inflater = getLayoutInflater();
				View view2 = inflater.inflate(R.layout.datapicker_dialog, null);
				dialog2.setContentView(view2);
				dialog2.show();
				
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				
				date = year+"年"+month+"月"+day+"日";
				time = hour+"时"+minute+"分";
				
				datePicker = (DatePicker) view2.findViewById(R.id.add_grap_datapicker);
				datePicker.init(year, month, day,new OnDateChangedListener(){

					@Override
					public void onDateChanged(DatePicker arg0, int arg1,
							int arg2, int arg3) {
						date = arg1+"年"+arg2+"月"+arg3+"日";
					}
					
				});
				
				timePicker = (TimePicker) view2.findViewById(R.id.add_grap_timepicker);
				timePicker.setIs24HourView(true);
				timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
					
					@Override
					public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
						// TODO Auto-generated method stub
						time = arg1+"时"+arg2+"分";
					}
				});
				
				btn_gettime = (Button) view2.findViewById(R.id.add_grap_gettime);
				btn_gettime.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						map = new HashMap<String, Object>();
						map.put("日期", date);
						map.put("时间", time);
						map.put("State","false");
						list2.add(map);
						adapter.notifyDataSetChanged();
					}
				});
				
			}
		});
		dialog.setContentView(view);
		return dialog;
	}
	
	private void sendRequest(final String ID){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_GetSingle");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Plan_ID", ID);
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
	
	
	private void sendZhuanfaRequest(final String Plan_ID,final String Work,final String Content,final boolean IsPublic,final String EndDate,final boolean IsAutoFinish,final String Case_ID,final String CaseName,final String ReceiveUserIDs,final String ReceiveUserNames,final String AttachUrls,final String AttachNames){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_RelayBySender");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("UserName", Myconfig.UserName);
				builder.appendQueryParameter("Plan_ID", Plan_ID);
				builder.appendQueryParameter("Work", Work);
				builder.appendQueryParameter("Content", Content);
				builder.appendQueryParameter("IsPublic", IsPublic+"");
				builder.appendQueryParameter("EndDate", EndDate);
				builder.appendQueryParameter("IsAutoFinish", IsAutoFinish+"");
				builder.appendQueryParameter("Case_ID", Case_ID);
				builder.appendQueryParameter("CaseName", CaseName);
				builder.appendQueryParameter("ReceiveUserIDs", ReceiveUserIDs);
				builder.appendQueryParameter("ReceiveUserNames", ReceiveUserNames);
				builder.appendQueryParameter("AttachUrls", AttachUrls);
				builder.appendQueryParameter("AttachNames", AttachNames);
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
						message.what = ZHUANFA_RESPONSE;
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
	
	
	private void receZhuanfaRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case ZHUANFA_RESPONSE:
					String response = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(ChakandbswActivity.this, "转发成功", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(ChakandbswActivity.this, "转发失败", Toast.LENGTH_SHORT).show();
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
	
	private LinearLayout getLinearLayout(int i,final List<Map<String, Object>> list){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(1);
		LinearLayout lltop = new LinearLayout(this);
		lltop.setBackgroundColor(0xffdae1f1);
		TextView tv_top = new TextView(this);
		tv_top.setText("附件：共"+i+"个");
		tv_top.setTextSize(20);
		tv_top.setTextColor(0xff000000);
		LinearLayout.LayoutParams lltoplp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lltop.addView(tv_top, lltoplp);
		LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.addView(lltop, lllp);
		
		for(int j = 0;j<i;j++){
			RelativeLayout rlbottom = new RelativeLayout(this);
			ImageView iv = new ImageView(this);
			iv.setId(1);
			iv.setImageResource(R.drawable.fujian);
			iv.setScaleType(ScaleType.FIT_START);
			RelativeLayout.LayoutParams rllpfujian = new RelativeLayout.LayoutParams(40, 40);
			rllpfujian.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rllpfujian.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(iv, rllpfujian);
			
			ImageView iv_jiantou = new ImageView(this);
			iv_jiantou.setId(3);
			iv_jiantou.setImageResource(R.drawable.jiantou);
			iv_jiantou.setScaleType(ScaleType.FIT_END);
			RelativeLayout.LayoutParams rllpjiantou = new RelativeLayout.LayoutParams(40, 40);
			rllpjiantou.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rllpjiantou.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(iv_jiantou, rllpjiantou);
			
			TextView tv = new TextView(this);
			final String str = (String)list.get(j).get("AttachName");
			System.out.println("4567890-------"+str);
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rlbottom.addView(tv, rllptitle);
			final String Url = (String) list.get(j).get("AttachUrl");
			final String ID = (String) list.get(j).get("Attach_ID");
			rlbottom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					preferences = getSharedPreferences("Attach", MODE_PRIVATE);
					editor = preferences.edit();
					editor.clear();
					editor.commit();
					
					Myconfig.set_AttachName.add(str+" /"+Url);
					editor.putStringSet("AttachName", Myconfig.set_AttachName);
					
					editor.commit();
					String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
					File f = new File(file+"Download/"+str);
					if(f.exists()){
						String[] docstr = Url.split("\\.");
						String str2 = docstr[1];
						MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
						String type = mimeTypeMap.getMimeTypeFromExtension(str2);
						Intent it = new Intent(Intent.ACTION_VIEW);
						it.addCategory("android.intent.category.DEFAULT");
						it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Uri uri = Uri.fromFile(f);
						it.setDataAndType(uri, type);
						startActivity(it);
					}else{
						Intent it = new Intent(ChakandbswActivity.this,AttachActivity.class);
						it.putExtra("Kind", "待办事务");
						it.putExtra("Url", Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/File_Download");
						it.putExtra("ID", Plan_ID);
						it.putExtra("ReMark_ID", "0");
						it.putExtra("Attach_ID", ID);
						it.putExtra("Name", str);
						it.putExtra("AttachUrl", Url);
						startActivity(it);
					}
				}
			});
			ll.addView(rlbottom, lllp);
		}
		
		
		return ll;
	}
	
	private LinearLayout getBeizhull(int i,List<String> list){
		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.setOrientation(1);
		TextView tv_title = new TextView(this);
		tv_title.setText("备注:");
		tv_title.setTextSize(20);
		tv_title.setTextColor(0xff000000);
		ll.addView(tv_title,lllp);
		
		for(int j = 0;j<i;j++){
			TextView tv = new TextView(this);
			tv.setText(list.get(j));
			tv.setTextSize(15);
			tv.setTextColor(0xff000000);
			ll.addView(tv,lllp);
			
			if(list_Bzattach.get(j).size() > 0){
				RelativeLayout rl = new RelativeLayout(this);
				RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				TextView tv_size = new TextView(this);
				tv_size.setId(1);
				tv_size.setText(list_Bzattach.get(j).size() + "");
				tv_size.setTextColor(0xff000000);
				tv_size.setTextSize(15);
				rl.addView(tv_size, rllp);
				
				RelativeLayout.LayoutParams rllp_iv = new RelativeLayout.LayoutParams(40, 40);
				ImageView iv = new ImageView(this);
				iv.setImageResource(R.drawable.fujian_blue);
				iv.setScaleType(ScaleType.FIT_CENTER);
				rllp_iv.addRule(RelativeLayout.LEFT_OF, 1);
				rl.addView(iv, rllp_iv);
				ll.addView(rl, lllp);
				final int a = j;
				rl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent it = new Intent(ChakandbswActivity.this,DbswAttachActivity.class);
						it.putExtra("list", (Serializable)list_Bzattach.get(a));
						startActivity(it);
					}
				});
			}
		}
		View view = new View(this);
		view.setBackgroundColor(0xffdae1f1);
		LinearLayout.LayoutParams viewlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1); 
		ll.addView(view, viewlp);
		return ll;
	}
	
	private LinearLayout getLiuzhuanll(int i,List<Map<String, Object>> list){
		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lllp.topMargin = 5;
		ll.setOrientation(1);
		TextView tv_title = new TextView(this);
		tv_title.setText("流转情况:");
		tv_title.setTextSize(20);
		tv_title.setTextColor(0xff000000);
		ll.addView(tv_title,lllp);
		
		for(int j = 0;j<i;j++){
			TextView tv = new TextView(this);
			if(list.get(j).get("IsFinish").equals("0")){
				String str = (String)(list.get(j).get("UserName"))+"<font color='green'>(已完成)</font>";
				tv.setText(Html.fromHtml(str));
			}else{
				String str = (String)(list.get(j).get("UserName"))+"<font color='red'>(未完成)</font>";
				tv.setText(Html.fromHtml(str));
			}
			tv.setTextSize(15);
			tv.setTextColor(0xff000000);
			ll.addView(tv,lllp);
		}
		View view = new View(this);
		view.setBackgroundColor(0xffdae1f1);
		LinearLayout.LayoutParams viewlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1); 
		ll.addView(view, viewlp);
		return ll;
	}
}
