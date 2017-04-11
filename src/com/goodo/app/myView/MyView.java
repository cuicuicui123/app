package com.goodo.app.myView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
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
import android.graphics.Path;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View{
	private Surface surface;
	private List<ScheduleObject> list = new ArrayList<ScheduleObject>();
	private Calendar calendar = Calendar.getInstance();
	private HttpClient httpClient;
	private Handler handler;
	public static final int SHOW_RESPONSE = 1;
	private String beginDateAnzhou;
	private String endDateAnzhou;
	private SimpleDateFormat format;
	private Calendar calendar_anzhou = Calendar.getInstance();
	private Boolean flag = false;
	private Date curDate;
	private int downIndex;
	private Date downDate;
	private List<ScheduleObject> list_send;
	
	//给事件设置监听事件
	private MyAnzhouOnItemClickListener myAnzhouOnItemClickListener;
	private MyanzhouOnLongItemClickListener myanzhouOnLongItemClickListener;

	public MyView(Context context) {
		super(context);
		init();
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	
	private void init(){
		curDate = new Date();
		if(calendar_anzhou.get(Calendar.DAY_OF_WEEK) == 1){
			calendar_anzhou.add(Calendar.DAY_OF_MONTH, -7);
			curDate = calendar_anzhou.getTime();
		}
		calendar_anzhou.setTime(curDate);
		flag = false;
		surface = new Surface();
		surface.init();
		httpClient = new DefaultHttpClient();
		
		format = new SimpleDateFormat("yyyy-MM-dd");
		
		
		calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
		beginDateAnzhou = format.format(calendar_anzhou.getTime());
		calendar_anzhou.add(Calendar.DAY_OF_MONTH, 7);
		endDateAnzhou = format.format(calendar_anzhou.getTime());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
				View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
				View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		calendar_anzhou.setTime(curDate);
		calendar.setTime(curDate);
		
		
		//按下状态改变颜色
		if(downDate!=null){
			canvas.drawRect(surface.cellWidth*downIndex, 0, surface.cellWidth*(downIndex+1), surface.weekHeight, surface.bgPaint);
		}
		
		float weekTextY = surface.weekHeight*3/4f;
		for(int i = 1;i< surface.weekText.length+1;i++){
			float weekTextX = i*surface.cellWidth + (surface.cellWidth - surface.weekPiant.measureText(surface.weekText[i-1]))/2f;
			canvas.drawText(surface.weekText[i-1], weekTextX, weekTextY, surface.weekPiant);
		}
		float dayTextX = (surface.cellWidth-surface.dayPaint.measureText(surface.dayText[0]))/2f;
		canvas.drawText(surface.dayText[0], dayTextX, surface.weekHeight+surface.cellHeight-surface.dayPaint.measureText(surface.weekText[0])-5, surface.dayPaint);
		canvas.drawText(surface.dayText[1], dayTextX, surface.weekHeight+surface.cellHeight-(surface.dayPaint.measureText(surface.weekText[0]))/2+5,surface.dayPaint);
		canvas.drawText(surface.dayText[2], dayTextX, surface.weekHeight+surface.cellHeight*2-10, surface.dayPaint);
		canvas.drawText(surface.dayText[3], dayTextX, surface.weekHeight+surface.cellHeight*2+surface.dayPaint.measureText(surface.weekText[0])/2+10, surface.dayPaint);
		canvas.drawText(surface.dayText[4], dayTextX, surface.weekHeight+surface.cellHeight*4-10, surface.dayPaint);
		canvas.drawText(surface.dayText[5], dayTextX, surface.weekHeight+surface.cellHeight*4+surface.dayPaint.measureText(surface.weekText[0])/2+10, surface.dayPaint);
		
		int[][] times = new int[4][7];
			for(int i = 0;i<list.size();i++){
				int MaxMorrow = 1;
				int MaxAftRow = 3;
				//获取绘制位置column
				ScheduleObject object = list.get(i);
				String[] dateAndtime = object.getDate().split(" ");
				
				String date = dateAndtime[0];
				String[] dateMore = date.split("/");
				int Month = Integer.parseInt(dateMore[1]);
				int Day = Integer.parseInt(dateMore[2]);
				if(Month<10){
					dateMore[1] = "0"+dateMore[1];
				}
				if(Day<10){
					dateMore[2] = "0"+dateMore[2];
				}
				date = dateMore[0]+"-"+dateMore[1]+"-"+dateMore[2];
				try {
					calendar.setTime(format.parse(date));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int column = 0;
				if(calendar.get(Calendar.DAY_OF_WEEK)==1){
					column = 7;
				}else{
					column = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				}
				//判断是否全天并获取row
				int row = 0;
				String myWork = "";
				String myWork2 = "";
				String Work = object.getWork();
				char[] Works = Work.toCharArray();
				String IsAllDay = object.getIsAllDay();
				if(IsAllDay.equals("0")){
					String time = object.getBeginTime();
					String[] moreTime = time.split(":");
					String hour = moreTime[0];
					int numHour = Integer.parseInt(hour);
					
					if(numHour<12){
						 row = MaxMorrow;
					}else{
						 row = MaxAftRow;
					}
					
					if(times[row-1][column-1] == 1){
						row++;
					}else{
						times[row-1][column-1] = 1;
					}
					
					//获得安排内容
					myWork = time;
					if(Works.length>4){
						myWork2 = Works[0]+""+Works[1]+""+Works[2]+""+Works[3]+"";
					}else{
						myWork2 = Work;
					}
					
				}else{
					row = 0;
					if(Works.length>8){
						myWork = Works[0]+""+Works[1]+""+Works[2]+""+Works[3]+"";
						myWork2 = Works[4]+""+Works[5]+""+Works[6]+""+Works[7]+"";
					}else if(Works.length>4&&Works.length<8){
						myWork = Works[0]+""+Works[1]+""+Works[2]+""+Works[3]+"";
						int iSize = Works.length;
						for(int size = 4;size<iSize;size++){
							myWork2 = myWork2+Works[size];
						}
					}else{
						myWork = Work;
					}
				}
				
				
				//判断类型，确定背景颜色
				String kind = object.getType();
				int paintColor = 0;
				int numKind = Integer.parseInt(kind);
				switch (numKind) {
				case 1:
					paintColor = Color.RED;
					break;
				case 2:
					paintColor = Color.BLUE;
					break;
				case 3:
					paintColor = Color.GREEN;
					break;
				default:
					break;
				}
				
				if(MaxMorrow<3&&MaxAftRow<5){
					surface.rectPaint.setColor(paintColor);
					canvas.drawRect(column*surface.cellWidth, surface.weekHeight+row*surface.cellHeight, (column+1)*surface.cellWidth, surface.weekHeight+(row+1)*surface.cellHeight, surface.rectPaint);
					canvas.drawText(myWork, column*surface.cellWidth+(surface.cellWidth-surface.littlePaint.measureText(myWork))/2, surface.weekHeight+row*surface.cellHeight+surface.cellHeight*1/2, surface.littlePaint);
					canvas.drawText(myWork2, column*surface.cellWidth+(surface.cellWidth-surface.littlePaint.measureText(myWork2))/2, surface.weekHeight+row*surface.cellHeight+surface.cellHeight*1/2+surface.littlePaint.measureText("各"), surface.littlePaint);
				}
			}
			canvas.drawPath(surface.borderPath, surface.borderPaint);
		if(!flag){
			sendAnzhouRequest();
			receRequest();
		}
	}
	
	public class Surface{
		public Paint borderPaint;
		public Paint weekPiant;
		public Paint dayPaint;
		public Path borderPath;
		public Paint rectPaint;
		public Paint littlePaint;
		public Paint bgPaint;
		public int width;
		public int height;
		public float cellWidth;
		public float cellHeight;
		public float weekHeight;
		private int textColor = Color.BLACK;
		public String[] weekText = {"周一","周二","周三","周四","周五","周六","周日"};
		public String[] dayText = {"全","天","上","午","下","午"};
		
		public void init(){
			width = getResources().getDisplayMetrics().widthPixels;
			height = (int) (getResources().getDisplayMetrics().heightPixels*1/2);
			
			weekHeight = height/10f;
			cellWidth = width/8f;
			cellHeight = (height-weekHeight)/5f;
			
			borderPaint = new Paint();
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Paint.Style.STROKE);
			borderPaint.setColor(Color.BLACK);
			borderPaint.setStrokeWidth(1);
			
			float weekTextSize = weekHeight*0.5f;
			weekPiant = new Paint();
			weekPiant.setColor(textColor);
			weekPiant.setAntiAlias(true);
			weekPiant.setTextSize(weekTextSize);
			weekPiant.setTypeface(Typeface.DEFAULT_BOLD);
			
			float dayTextSize = weekHeight*0.5f;
			dayPaint = new Paint();
			dayPaint.setColor(textColor);
			dayPaint.setAntiAlias(true);
			dayPaint.setTextSize(dayTextSize);
			dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
			
			rectPaint = new Paint();
			rectPaint.setAntiAlias(true);
			rectPaint.setTypeface(Typeface.DEFAULT_BOLD);
			
			bgPaint = new Paint();
			bgPaint.setAntiAlias(true);
			bgPaint.setColor(0xffdae1f1);
			
			float littleSize = dayTextSize*0.7f;
			littlePaint = new Paint();
			littlePaint.setColor(textColor);
			littlePaint.setAntiAlias(true);
			littlePaint.setTextSize(littleSize);
			littlePaint.setTypeface(Typeface.DEFAULT);
			
			borderPath = new Path();
			borderPath.moveTo(0, weekHeight);
			borderPath.rLineTo(width, 0);
			borderPath.moveTo(cellWidth, 0);
			borderPath.rLineTo(0, height);
			for(int i = 1;i<5;i++){
				if(i%2==0){
					borderPath.moveTo(cellWidth, cellHeight*i+weekHeight);
				}else{
					borderPath.moveTo(0, cellHeight*i+weekHeight);
				}
				borderPath.rLineTo(width, 0);
				borderPath.moveTo(cellWidth*(i+1), 0);
				borderPath.rLineTo(0, height);
			}
			borderPath.moveTo(0, height);
			borderPath.rLineTo(width, 0);
			borderPath.moveTo(cellWidth*6, 0);
			borderPath.rLineTo(0, height);
			borderPath.moveTo(cellWidth*7, 0);
			borderPath.rLineTo(0, height);
			
		}
	}
	
	
	public void sendAnzhouRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Schedule_GetTotalList");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Unit_ID", Myconfig.Unit_ID);
				builder.appendQueryParameter("BeginDay", beginDateAnzhou);
				builder.appendQueryParameter("EndDay", endDateAnzhou);
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
					list = new ArrayList<ScheduleObject>();
					try {
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
					if(!flag){
						invalidate();
						flag = true;
					}
					break;
					
				default:
					break;
				}
			}
		};
	}
	
	public void clickLeftBtn(){
		calendar_anzhou.setTime(curDate);
		
		calendar_anzhou.add(Calendar.DAY_OF_MONTH,-7);
		curDate = calendar_anzhou.getTime();
		calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
		beginDateAnzhou = format.format(calendar_anzhou.getTime());
		calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
		endDateAnzhou = format.format(calendar_anzhou.getTime());
		list.clear();
		flag = false;
		downDate = null;
		invalidate();
	}
	public void clickRightBtn(){
		calendar_anzhou.setTime(curDate);
		
		calendar_anzhou.add(Calendar.DAY_OF_MONTH, 7);
		curDate = calendar_anzhou.getTime();
		calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
		beginDateAnzhou = format.format(calendar_anzhou.getTime());
		calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
		endDateAnzhou = format.format(calendar_anzhou.getTime());
		list.clear();
		flag = false;
		downDate = null;
		invalidate();
	}
	
	private void getDownDate(float x,float y){
		if(x>surface.cellWidth&&y>surface.weekHeight){
			int m = (int) Math.floor(x/surface.cellWidth);
			downIndex = m;
			calendar_anzhou.setTime(curDate);
			if(m==7){
				m = 1;
				calendar_anzhou.add(Calendar.WEEK_OF_MONTH, 1);
				calendar_anzhou.set(Calendar.DAY_OF_WEEK, m);
			}else{
				m = m+1;
				calendar_anzhou.set(Calendar.DAY_OF_WEEK, m);
			}
			downDate = calendar_anzhou.getTime();
		}
		System.out.println("4567------"+downDate);
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
					myanzhouOnLongItemClickListener.OnLongItemClickListener(date);
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
						System.out.println("456-------"+getDate[0]);
					}
				}
				System.out.println("3333333333"+date);
				myAnzhouOnItemClickListener.OnItemClick(downDate, list_send);
			}
			invalidate();
			break;

		default:
			break;
		}
		return true;
	}

	//给控件设置监听事件
	public void setMyAnzhouOnItemClickListener(MyAnzhouOnItemClickListener myAnzhouOnItemClickListener){
		this.myAnzhouOnItemClickListener = myAnzhouOnItemClickListener;
	}
	public void setMyAnzhouOnLongItemClickListener(MyanzhouOnLongItemClickListener myanzhouOnLongItemClickListener){
		this.myanzhouOnLongItemClickListener = myanzhouOnLongItemClickListener;
	}
	//监听接口
	public interface MyAnzhouOnItemClickListener{
		void OnItemClick(Date downdate,List<ScheduleObject> list);
	}
	public interface MyanzhouOnLongItemClickListener{
		void OnLongItemClickListener(String downdate);
	}
}
