package com.goodo.app;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 日历控件 功能：获得点选的时间区间
 * 
 */
public class CalendarView extends View implements View.OnTouchListener {
	private final static String TAG = "anCalendar";
	private Date selectedStartDate;
	private Date selectedEndDate;
	private Date curDate; // 当前日历显示的月
	private Date today; // 今天的日期文字显示红色
	private Date downDate; // 手指按下状态时临时日期
	private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
	private int downIndex; // 按下的格子索引
	private Calendar calendar;
	private Surface surface;
	private int[] date = new int[42]; //日历显示数字
	public int[] mypoint = new int[42];//日历显示是否有安排
	private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
	private boolean isSelectMore = false;
	//给控件设置监听事件 
	private MyOnItemClickListener MyonItemClickListener;
	private MyOnlongItemClickListener MyOnlongItemClickListener;
	
	private HttpClient httpClient;
	private Handler handler;
	public static final int SHOW_RESPONSE = 1;
	private String beginDate;
	private String endDate;
	private SimpleDateFormat format;
	public Boolean flag = false;
	private Calendar calendar_anyue;
	private List<ScheduleObject> list = new ArrayList<ScheduleObject>();
	private List<ScheduleObject> list_send;
	
	
	public CalendarView(Context context) {
		super(context);
		init();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		
		curDate = selectedStartDate = selectedEndDate = today = new Date();
		mypoint = new int[42];
		flag = false;
		httpClient = new DefaultHttpClient();
		format = new SimpleDateFormat("yyyy-MM-dd");
		calendar_anyue = Calendar.getInstance();
		calendar_anyue.setTime(curDate);
		calendar_anyue.set(Calendar.DAY_OF_MONTH, 1);
		beginDate = format.format(calendar_anyue.getTime());
		calendar_anyue.add(Calendar.MONTH, 1);
		calendar_anyue.set(Calendar.DAY_OF_MONTH, 0);
		endDate = format.format(calendar_anyue.getTime());
		
		
		
		
		calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		surface = new Surface();
		surface.density = getResources().getDisplayMetrics().density;
		setBackgroundColor(surface.bgColor);
		setOnTouchListener(this);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		surface.width = getResources().getDisplayMetrics().widthPixels;
		
		surface.height = (int) (getResources().getDisplayMetrics().heightPixels*1/2);
		
//		if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
//		}
//		if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
//		}
		widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
				View.MeasureSpec.EXACTLY);
		heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
				View.MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.d(TAG, "[onLayout] changed:"
				+ (changed ? "new size" : "not change") + " left:" + left
				+ " top:" + top + " right:" + right + " bottom:" + bottom);
		if (changed) {
			surface.init();
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw");
		
		mypoint = new int[42];
		//画框
		canvas.drawPath(surface.boxPath, surface.borderPaint);
		//年月
		//String monthText = getYearAndmonth();
		//float textWidth = surface.monthPaint.measureText(monthText);
		//canvas.drawText(monthText, (surface.width - textWidth) / 2f,
		//		surface.monthHeight * 3 / 4f, surface.monthPaint);
		//上一月/下一月
		//canvas.drawPath(surface.preMonthBtnPath, surface.monthChangeBtnPaint);
		//canvas.drawPath(surface.nextMonthBtnPath, surface.monthChangeBtnPaint);
		// 星期
		float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
		// 星期背景
//		surface.cellBgPaint.setColor(surface.textColor);
//		canvas.drawRect(surface.weekHeight, surface.width, surface.weekHeight, surface.width, surface.cellBgPaint);
		for (int i = 0; i < surface.weekText.length; i++) {
			float weekTextX = i
					* surface.cellWidth
					+ (surface.cellWidth - surface.weekPaint
							.measureText(surface.weekText[i])) / 2f;
			canvas.drawText(surface.weekText[i], weekTextX, weekTextY,
					surface.weekPaint);
		}
		
		//计算日期 
		calculateDate();
		//按下状态，选择状态背景色 
		drawDownOrSelectedBg(canvas);
		// write date number
		// today index
		int todayIndex = -1;
		calendar.setTime(curDate);
		String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		calendar.setTime(today);
		String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		if (curYearAndMonth.equals(todayYearAndMonth)) {
			int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
			todayIndex = curStartIndex + todayNumber - 1;
		}
		for (int i = 0; i < 42; i++) {
			int color = surface.textColor;
			if (isLastMonth(i)) {
				color = surface.borderColor;
			} else if (isNextMonth(i)) {
				color = surface.borderColor;
			}
			if (todayIndex != -1 && i == todayIndex) {
				color = surface.todayNumberColor;
				surface.datePaint.setTypeface(Typeface.DEFAULT_BOLD);
				drawCellText(canvas, i, date[i] + "", color,mypoint[i]);
			}else{
				surface.datePaint.setTypeface(Typeface.DEFAULT);
				drawCellText(canvas, i, date[i] + "", color,mypoint[i]);
			}
			
		}
		if(!flag){
			sendAnyueRequest();
			receRequest();
		}
		super.onDraw(canvas);
	}

	private void calculateDate() {
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "day in week:" + dayInWeek);
		int monthStart = dayInWeek;
		if (monthStart == 1) {
			monthStart = 8;
		}
		monthStart -= 2;  //以日为开头-1，以星期一为开头-2 
		curStartIndex = monthStart;
		date[monthStart] = 1;
		// last month
		if (monthStart > 0) {
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--) {
				date[i] = dayInmonth;
				mypoint[i] = 0;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]);
		}
		showFirstDate = calendar.getTime();
		// this month
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		// Log.d(TAG, "m:" + calendar.get(Calendar.MONTH) + " d:" +
		// calendar.get(Calendar.DAY_OF_MONTH));
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
		for (int i = 0; i < monthDay; i++) {
			date[monthStart + i] = i + 1;
			
		}
		curEndIndex = monthStart + monthDay;
		// next month
		for (int i = monthStart + monthDay; i < 42; i++) {
			date[i] = i - (monthStart + monthDay) + 1;
			mypoint[i] = 0;
		}
		if (curEndIndex < 42) {
			// 显示了下一月的
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]);
		showLastDate = calendar.getTime();
		
		if(list.size()>0){
			for(int i = 0;i<list.size();i++){
				
				//获得日期
				String dateAndtime = list.get(i).getDate();
				String[] dateAndtime2 = dateAndtime.split(" ");
				String date = dateAndtime2[0];
				String[] date2 = date.split("/");
				String day = date2[2];
				System.out.println("456---------"+day);
				int numDay = Integer.parseInt(day);	
				
				//获得是否全天
				String IsAllDay = list.get(i).getIsAllDay();
				if(IsAllDay.equals("1")){
					mypoint[monthStart+numDay-1] = 1;
				}else{
					mypoint[monthStart+numDay-1] = 2;
				}
				
			}
		}
		
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param text
	 */
	private void drawCellText(Canvas canvas, int index, String text, int color,int mypoint) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.datePaint.setColor(color);
		float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight+ surface.cellHeight * 1 / 2f;
		float cellX = (surface.cellWidth * (x - 1))+ (surface.cellWidth - surface.datePaint.measureText(text))
				/ 2f;
		canvas.drawText(text, cellX, cellY, surface.datePaint);
		

		float pointX = surface.cellWidth*(x-1)+surface.cellWidth*1/6;
		float pointY = surface.weekHeight+(y-1)*surface.cellHeight+surface.cellHeight*1/4;
		
		if(mypoint == 1){
			canvas.drawCircle(pointX, pointY, 10, surface.datePaint);
		}else if(mypoint == 2){
			canvas.drawCircle(pointX, pointY, 10, surface.datePaint);
			canvas.drawCircle(pointX, pointY, 8, surface.whitePaint);
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param color
	 */
	private void drawCellBg(Canvas canvas, int index, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.cellBgPaint.setColor(color);
		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1)
				* surface.cellHeight + surface.borderWidth;
		canvas.drawRect(left, top, left + surface.cellWidth
				- surface.borderWidth, top + surface.cellHeight
				- surface.borderWidth, surface.cellBgPaint);
	}

	private void drawDownOrSelectedBg(Canvas canvas) {
		// down and not up
		if (downDate != null) {
			drawCellBg(canvas, downIndex, surface.cellDownColor);
		}
		// selected bg color
		if (!selectedEndDate.before(showFirstDate)
				&& !selectedStartDate.after(showLastDate)) {
			int[] section = new int[] { -1, -1 };
			calendar.setTime(curDate);
			calendar.add(Calendar.MONTH, -1);
			findSelectedIndex(0, curStartIndex, calendar, section);
			if (section[1] == -1) {
				calendar.setTime(curDate);
				findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
			}
			if (section[1] == -1) {
				calendar.setTime(curDate);
				calendar.add(Calendar.MONTH, 1);
				findSelectedIndex(curEndIndex, 42, calendar, section);
			}
			if (section[0] == -1) {
				section[0] = 0;
			}
			if (section[1] == -1) {
				section[1] = 41;
			}
//			for (int i = section[0]; i <= section[1]; i++) {
//				drawCellBg(canvas, i, surface.cellSelectedColor);
//			}
		}
	}

	private void findSelectedIndex(int startIndex, int endIndex,
			Calendar calendar, int[] section) {
		for (int i = startIndex; i < endIndex; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, date[i]);
			Date temp = calendar.getTime();
			// Log.d(TAG, "temp:" + temp.toLocaleString());
			if (temp.compareTo(selectedStartDate) == 0) {
				section[0] = i;
			}
			if (temp.compareTo(selectedEndDate) == 0) {
				section[1] = i;
				return;
			}
		}
	}

	public Date getSelectedStartDate() {
		return selectedStartDate;
	}

	public Date getSelectedEndDate() {
		return selectedEndDate;
	}

	private boolean isLastMonth(int i) {
		if (i < curStartIndex) {
			return true;
		}
		return false;
	}

	private boolean isNextMonth(int i) {
		if (i >= curEndIndex) {
			return true;
		}
		return false;
	}

	private int getXByIndex(int i) {
		return i % 7 + 1; // 1 2 3 4 5 6 7
	}

	private int getYByIndex(int i) {
		return i / 7 + 1; // 1 2 3 4 5 6
	}

	// 获得当前应该显示的年月
	public String getYearAndmonth() {
		calendar.setTime(curDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year + "-" + month;
	}
	
	//上一月
	public String clickLeftMonth(){
		for(int i=0;i<42;i++){
			mypoint[i] = 0;
		}
		list.clear();
		flag = false;
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, -1);
		curDate = calendar.getTime();
		
		calendar_anyue.setTime(curDate);
		calendar_anyue.set(Calendar.DAY_OF_MONTH, 1);
		beginDate = format.format(calendar_anyue.getTime());
		calendar_anyue.add(Calendar.MONTH, 1);
		calendar_anyue.set(Calendar.DAY_OF_MONTH, 0);
		endDate = format.format(calendar_anyue.getTime());
		downDate = null;
		
		
		invalidate();
		return getYearAndmonth();
	}
	//下一月
	public String clickRightMonth(){
		for(int i=0;i<42;i++){
			mypoint[i] = 0;
		}
		list.clear();
		flag = false;
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		curDate = calendar.getTime();
		
		calendar_anyue.setTime(curDate);
		calendar_anyue.set(Calendar.DAY_OF_MONTH, 1);
		beginDate = format.format(calendar_anyue.getTime());
		calendar_anyue.add(Calendar.MONTH, 1);
		calendar_anyue.set(Calendar.DAY_OF_MONTH, 0);
		endDate = format.format(calendar_anyue.getTime());
		downDate = null;
		
		invalidate();
		return getYearAndmonth();
	}
	

	public void setCalendarData(Date date){
		calendar.setTime(date);
		invalidate();
	}
	

	public void getCalendatData(){
		calendar.getTime();	
	}
	

	public boolean isSelectMore() {
		return isSelectMore;
	}

	public void setSelectMore(boolean isSelectMore) {
		this.isSelectMore = isSelectMore;
	}

	private void setSelectedDateByCoor(float x, float y) {
		// change month
//		if (y < surface.monthHeight) {
//			// pre month
//			if (x < surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, -1);
//				curDate = calendar.getTime();
//			}
//			// next month
//			else if (x > surface.width - surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, 1);
//				curDate = calendar.getTime();
//			}
//		}
		// cell click down
		if (y > surface.monthHeight + surface.weekHeight) {
			int m = (int) (Math.floor(x / surface.cellWidth) + 1);
			int n = (int) (Math
					.floor((y - (surface.monthHeight + surface.weekHeight))
							/ Float.valueOf(surface.cellHeight)) + 1);
			downIndex = (n - 1) * 7 + m - 1;
			Log.d(TAG, "downIndex:" + downIndex);
			calendar.setTime(curDate);
			if (isLastMonth(downIndex)) {
				calendar.add(Calendar.MONTH, -1);
			} else if (isNextMonth(downIndex)) {
				calendar.add(Calendar.MONTH, 1);
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
			downDate = calendar.getTime();
		}
		invalidate();
	}

	long time;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setSelectedDateByCoor(event.getX(), event.getY());
			time = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_UP:
				if (downDate != null) {
					if(System.currentTimeMillis() - time >= 500){
						MyOnlongItemClickListener.OnItemClick(selectedStartDate, selectedEndDate, downDate);
					}else{
						String date = format.format(downDate);
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
						//响应监听事件
						MyonItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate,list_send);
						invalidate();
					}
					
				}
			break;
		}
		return true;
	}
	

	//给控件设置监听事件 
	public void setMyOnItemClickListener(MyOnItemClickListener MyonItemClickListener){
		this.MyonItemClickListener =  MyonItemClickListener;
	}
	
	public void setMyOnlongItemClickListener(MyOnlongItemClickListener MyOnlongItemClickListener){
		this.MyOnlongItemClickListener = MyOnlongItemClickListener;
	}
	//监听接口
	public interface MyOnItemClickListener {
		void OnItemClick(Date selectedStartDate,Date selectedEndDate, Date downDate,List<ScheduleObject> list);
	}
	
	public interface MyOnlongItemClickListener{
		void OnItemClick(Date selectedStartDate,Date selectedEndDate, Date downDate);
	}

	/**
	 * 
	 * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
	 */
	private class Surface {
		public float density;
		public int width; //  整个控件的宽度
		public int height; // 整个控件的高度
		public float monthHeight; // 显示月的高度
		//public float monthChangeWidth; // 上一月、下一月按钮宽度
		public float weekHeight; // 显示星期的高度
		public float cellWidth; // 日期方框宽度
		public float cellHeight; // 日期方框高度 	
		public float borderWidth;
		public int bgColor = Color.parseColor("#FFFFFF");
		private int textColor = Color.BLACK;
		//private int textColorUnimportant = Color.parseColor("#666666");
		private int btnColor = Color.parseColor("#666666");
		private int borderColor = Color.parseColor("#CCCCCC");
		public int todayNumberColor = 0xff3da1d5;
		public int cellDownColor = Color.parseColor("#CCFFFF");
		public int cellSelectedColor = Color.parseColor("#FFFFFF");
		public Paint borderPaint;
		public Paint monthPaint;
		public Paint weekPaint;
		public Paint datePaint;
		public Paint pointPaint;
		public Paint whitePaint;
		public Paint monthChangeBtnPaint;
		public Paint cellBgPaint;
		public Path boxPath; // 边框路径
		//public Path preMonthBtnPath; // 上一月按钮三角形
		//public Path nextMonthBtnPath; // 下一月按钮三角形
		public String[] weekText = { "周一", "周二", "周三", "周四", "周五", "周六","周日"};
		//public String[] monthText = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		   
		public void init() {
			float temp = height / 7f;
			monthHeight = 0;//(float) ((temp + temp * 0.3f) * 0.6);
			//monthChangeWidth = monthHeight * 1.5f;
			weekHeight = (float) ((temp + temp * 0.3f) * 0.7);
			cellHeight = (height - monthHeight - weekHeight) / 6f;
			cellWidth = width / 7f;
			borderPaint = new Paint();
			borderPaint.setColor(borderColor);
			borderPaint.setStyle(Paint.Style.STROKE);
			borderWidth = (float) (0.5 * density);
			// Log.d(TAG, "borderwidth:" + borderWidth);
			borderWidth = borderWidth < 1 ? 1 : borderWidth;
			borderPaint.setStrokeWidth(borderWidth);
			monthPaint = new Paint();
			monthPaint.setColor(textColor);
			monthPaint.setAntiAlias(true);
			float textSize = cellHeight * 0.4f;
			Log.d(TAG, "text size:" + textSize);
			monthPaint.setTextSize(textSize);
			monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
			weekPaint = new Paint();
			weekPaint.setColor(textColor);
			weekPaint.setAntiAlias(true);
			float weekTextSize = weekHeight * 0.5f;
			weekPaint.setTextSize(weekTextSize);
			weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
			
			datePaint = new Paint();
			datePaint.setColor(textColor);
			datePaint.setAntiAlias(true);
			whitePaint = new Paint();
			whitePaint.setColor(0xffffffff);
			whitePaint.setAntiAlias(true);
			
			
			float cellTextSize = cellHeight * 0.4f;
			datePaint.setTextSize(cellTextSize);
			datePaint.setTypeface(Typeface.DEFAULT);
			pointPaint = new Paint();
			pointPaint.setColor(0xffffffff);
			
			
			
			boxPath = new Path();
			//boxPath.addRect(0, 0, width, height, Direction.CW);
			//boxPath.moveTo(0, monthHeight);
//			boxPath.rLineTo(width, 0);
			boxPath.moveTo(0, monthHeight + weekHeight);
			boxPath.rLineTo(width, 0);
			for (int i = 1; i < 6; i++) {
				boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
				boxPath.rLineTo(width, 0);
				boxPath.moveTo(i * cellWidth, monthHeight);
				boxPath.rLineTo(0, height - monthHeight);
			}
			boxPath.moveTo(6 * cellWidth, monthHeight);
			boxPath.rLineTo(0, height - monthHeight);
			boxPath.moveTo(0, monthHeight+weekHeight+6*cellHeight-1);
			boxPath.rLineTo(width, 0);
			//preMonthBtnPath = new Path();
			//int btnHeight = (int) (monthHeight * 0.6f);
			//preMonthBtnPath.moveTo(monthChangeWidth / 2f, monthHeight / 2f);
			//preMonthBtnPath.rLineTo(btnHeight / 2f, -btnHeight / 2f);
			//preMonthBtnPath.rLineTo(0, btnHeight);
			//preMonthBtnPath.close();
			//nextMonthBtnPath = new Path();
			//nextMonthBtnPath.moveTo(width - monthChangeWidth / 2f,
			//		monthHeight / 2f);
			//nextMonthBtnPath.rLineTo(-btnHeight / 2f, -btnHeight / 2f);
			//nextMonthBtnPath.rLineTo(0, btnHeight);
			//nextMonthBtnPath.close();
			monthChangeBtnPaint = new Paint();
			monthChangeBtnPaint.setAntiAlias(true);
			monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			monthChangeBtnPaint.setColor(btnColor);
			cellBgPaint = new Paint();
			cellBgPaint.setAntiAlias(true);
			cellBgPaint.setStyle(Paint.Style.FILL);
			cellBgPaint.setColor(cellSelectedColor);
		}
	}
	
	public void sendAnyueRequest(){
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
	
	public void receRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("456------------"+response);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
