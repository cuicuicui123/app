package com.goodo.app.myView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.goodo.app.javabean.ScheduleObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AnriView extends View{
	private Surface surface;
	private Calendar calendar;
	private Calendar calendar2;
	private String[] data = new String[7];
	private Date curDate;
	private int downIndex;
	private Date downDate;
	private SimpleDateFormat format;
	
	private HttpClient httpClient;
	private Handler handler;
	public static final int SHOW_RESPONSE = 1;
	private List<ScheduleObject> list = new ArrayList<ScheduleObject>();
	private List<ScheduleObject> list_send;
	private String beginDate;
	private String endDate;
	private Boolean flag = false;
	private int year,month,day;
	
	//给事件设置监听事件
	private MyAnriOnItemClickListener myAnriOnItemClickListener;
	private MyAnriOnLongItemClickListener myAnriOnLongItemClickListener;

	public AnriView(Context context) {
		super(context);
		init();
	}

	public AnriView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		flag = false;
		format = new SimpleDateFormat("yyyy-MM-dd");
		curDate = new Date();
		calendar = Calendar.getInstance();
		calendar2 = Calendar.getInstance();
		if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
			calendar.add(Calendar.DAY_OF_MONTH, -7);
			curDate = calendar.getTime();
		}
		
		surface = new Surface();
		surface.init();
		
		year = calendar2.get(Calendar.YEAR);
		month = calendar2.get(Calendar.MONTH);
		day = calendar2.get(Calendar.DAY_OF_MONTH);
		
		
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		beginDate = format.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		endDate = format.format(calendar.getTime());
		
		httpClient = new DefaultHttpClient();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width, View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height, View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		getData();
		canvas.drawRect(0, 0, surface.width, surface.height, surface.paint);
		//按下状态改变背景颜色
		if(downDate!=null){
			canvas.drawRect((downIndex-1)*surface.cellWidth, surface.cellHeight, downIndex*surface.cellWidth, surface.cellHeight*7/4, surface.pointPaint);
		}
		for(int i = 0;i < 7;i++){
			if(i == 6){
				calendar.add(Calendar.DAY_OF_MONTH, 7);
				calendar.set(Calendar.DAY_OF_WEEK, 1);
			}else if(i == 0){
				calendar.add(Calendar.DAY_OF_MONTH, -7);
				calendar.set(Calendar.DAY_OF_WEEK, i+2);
			}else{
				calendar.set(Calendar.DAY_OF_WEEK, i+2);
			}
			
			if(calendar.get(Calendar.YEAR)==year&&calendar.get(Calendar.MONTH)==month&&calendar.get(Calendar.DAY_OF_MONTH)==day){
				surface.textPaint.setColor(0xff3da1d5);
				surface.textPaint.setTypeface(Typeface.DEFAULT_BOLD);
			}else{
				surface.textPaint.setColor(Color.BLACK);
				surface.textPaint.setTypeface(Typeface.DEFAULT);
				for(int j = 0;j<list.size();j++){
					
					String[] getDate = list.get(j).getDate().split(" ");
					String[] getDatemore = getDate[0].split("/");
					int myear = Integer.parseInt(getDatemore[0]);
					int mmonth = Integer.parseInt(getDatemore[1])-1;
					int mday = Integer.parseInt(getDatemore[2]);
					if(myear == calendar.get(Calendar.YEAR)&&mmonth == calendar.get(Calendar.MONTH)&&mday==calendar.get(Calendar.DAY_OF_MONTH)){
						surface.textPaint.setColor(Color.RED);
						break;
					}else{
						surface.textPaint.setColor(Color.BLACK);
					}
				}
			}
			canvas.drawText(surface.weekText[i], surface.cellWidth*i+(surface.cellWidth-surface.textPaint.measureText(surface.weekText[i]))/2, surface.cellHeight/2, surface.textPaint);
			canvas.drawText(data[i], surface.cellWidth*i+(surface.cellWidth-surface.textPaint.measureText(data[i]))/2, surface.cellHeight+surface.cellHeight*1/2, surface.textPaint);
			calendar.setTime(curDate);
		}
		
		if(!flag){
			sendRequest();
			receRequest();
		}
	}
	
	private void getData(){
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		for(int i = 0;i<7;i++){
			data[i] = calendar.get(Calendar.DAY_OF_MONTH)+"";
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	public String getYearAndmonth(){
		calendar.setTime(curDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year+"-"+month;
	}
	
	
	public String clickLeftMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.DAY_OF_MONTH, -7);
		curDate = calendar.getTime();
		downDate = null;
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		beginDate = format.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		endDate = format.format(calendar.getTime());
		flag = false;
		invalidate();
		return getYearAndmonth();
	}
	
	public String clickRightMonth(){
		calendar.setTime(curDate);
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		curDate = calendar.getTime();
		downDate = null;
		calendar.set(Calendar.DAY_OF_WEEK, 2);
		beginDate = format.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		endDate = format.format(calendar.getTime());
		flag = false;
		invalidate();
		return getYearAndmonth();
	}
	
	
	
	public class Surface{
		public int width;
		public int height;
		public float cellWidth;
		public float cellHeight;
		public Paint paint;
		public Paint textPaint;
		public Paint pointPaint;
		
		
		public String[] weekText = {"一","二","三","四","五","六","日"};
		
		public void init(){
			width = getResources().getDisplayMetrics().widthPixels;
			height = getResources().getDisplayMetrics().heightPixels/5;
			
			cellWidth = width/7f;
			cellHeight = height/2f;
			
			paint = new Paint();
			paint.setColor(0xffdae1f1);
			paint.setAntiAlias(true);
			paint.setTypeface(Typeface.DEFAULT);
			
			pointPaint = new Paint();
			pointPaint.setColor(0xffaae1f1);
			pointPaint.setAntiAlias(true);
			
			float textSize = cellHeight*0.3f;
			textPaint = new Paint();
			textPaint.setColor(0xff000000);
			textPaint.setTextSize(textSize);
			textPaint.setAntiAlias(true);
			textPaint.setTypeface(Typeface.DEFAULT);
		}
	}
	private void getDownDate(float x,float y){
		if(y > surface.cellHeight&&y < surface.cellHeight*2){
			int m = (int)(Math.floor(x / surface.cellWidth) + 1);
			
			downIndex = m;
			calendar.setTime(curDate);
			if(downIndex == 7){
				calendar.add(Calendar.DAY_OF_MONTH, 7);
				calendar.set(Calendar.DAY_OF_WEEK, 1);
			}else{
				calendar.set(Calendar.DAY_OF_WEEK, m+1);
			}
			downDate = calendar.getTime();
		}
		invalidate();
	}
	
	long time;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getDownDate(event.getX(), event.getY());
			time = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			if(downDate!=null){
				String date = format.format(downDate);
				if(System.currentTimeMillis() - time >= 500){
					myAnriOnLongItemClickListener.OnLongItemClick(date);
				}
				String[] Date = date.split("-");
				int month = Integer.parseInt(Date[1]);
				int day = Integer.parseInt(Date[2]);
				if(month<10){
					Date[1] = ""+month;
				}
				if(day<10){
					Date[2] = ""+day;
				}
				String mydate = Date[0]+"/"+Date[1]+"/"+Date[2];
				list_send = new ArrayList<ScheduleObject>();
				for(int i = 0;i<list.size();i++){
					String[] getDate = list.get(i).getDate().split(" ");
					if(mydate.equals(getDate[0])){
						list_send.add(list.get(i));
					}
				}
				
				myAnriOnItemClickListener.OnItemClick(downDate,list_send);
			}
			invalidate();
			break;
		default:
			break;
		}
		return true;
	}

	//给控件设置监听事件
	public void setMyAnriOnItemClickListener(MyAnriOnItemClickListener myAnriOnItemClickListener){
		this.myAnriOnItemClickListener = myAnriOnItemClickListener;
	}
	public void setMyAnriOnLongItemClickListener(MyAnriOnLongItemClickListener myAnriOnLongItemClickListener){
		this.myAnriOnLongItemClickListener = myAnriOnLongItemClickListener;
	}
	//监听接口
	public interface MyAnriOnItemClickListener{
		void OnItemClick(Date downDate,List<ScheduleObject> list);
	}
	public interface MyAnriOnLongItemClickListener{
		void OnLongItemClick(String date);
	}
	
	public void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Schedule_GetTotalList");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Unit_ID", Myconfig.Unit_ID);
				builder.appendQueryParameter("BeginDay", beginDate);
				builder.appendQueryParameter("EndDay", endDate);
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
	
	private void receRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String)msg.obj;
					try {
						list = new ArrayList<ScheduleObject>();
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String str = Goodo.getString("R");
						char[] str2 = str.toCharArray();
						if(str2[0]=='['){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RArray.getJSONObject(i);
								ScheduleObject object = new ScheduleObject();
								object.setID((String)jo.get("ID"));
								object.setDate((String)jo.get("Date"));
								object.setIsAllDay((String)jo.get("IsAllDay"));
								object.setBeginTime((String)jo.get("BeginTime"));
								object.setEndTime((String)jo.get("EndTime"));
								object.setWork((String)jo.get("Work"));
								object.setType((String)jo.get("Type"));
								list.add(object);
							}
						}else{
							JSONObject jo = Goodo.getJSONObject("R");
							ScheduleObject object = new ScheduleObject();
							object.setID((String)jo.get("ID"));
							object.setDate((String)jo.get("Date"));
							object.setIsAllDay((String)jo.get("IsAllDay"));
							object.setBeginTime((String)jo.get("BeginTime"));
							object.setEndTime((String)jo.get("EndTime"));
							object.setWork((String)jo.get("Work"));
							object.setType((String)jo.get("Type"));
							list.add(object);
						}						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					System.out.println("45678-----------"+list.size());
					invalidate();
					flag = true;
					break;
					
				default:
					break;
				}
			}
		};
	}
	
}
