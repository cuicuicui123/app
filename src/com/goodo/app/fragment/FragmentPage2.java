package com.goodo.app.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.goodo.app.CalAdapter;
import com.goodo.app.CalendarView;
import com.goodo.app.ChakandbswActivity;
import com.goodo.app.ChakangrapActivity;
import com.goodo.app.DbswAddActivity;
import com.goodo.app.DbswMoreActivity;
import com.goodo.app.DocSendActivity;
import com.goodo.app.MoreYfMainActivity;
import com.goodo.app.Myconfig;
import com.goodo.app.R;
import com.goodo.app.Select2Activity;
import com.goodo.app.CalendarView.MyOnItemClickListener;
import com.goodo.app.CalendarView.MyOnlongItemClickListener;
import com.goodo.app.javabean.ScheduleObject;
import com.goodo.app.myView.AnriView;
import com.goodo.app.myView.AnriView.MyAnriOnItemClickListener;
import com.goodo.app.myView.AnriView.MyAnriOnLongItemClickListener;
import com.goodo.app.myView.MyView;
import com.goodo.app.myView.MyView.MyAnzhouOnItemClickListener;
import com.goodo.app.myView.MyView.MyanzhouOnLongItemClickListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentPage2 extends Fragment implements OnClickListener{
	private ImageView iv_user_2;
	private Select2Activity activity;
	private ImageView iv_page2_sel;
	private PopupWindow popupWindow;
	private ImageView iv_page2_pre;
	private ImageView iv_page2_next;
	private TextView tv_page2_title;
	private TextView tv_page2_dbsw;
	private TextView tv_page2_more;
	private ImageView iv_page2_tianjia;

	
	private Button btn_anzhou;
	private Button btn_anyue;
	private Button btn_anri;
	
	private RelativeLayout rl_page2_content;
	
	private LinearLayout anzhoull;
	private LinearLayout anzhoullf;
	private ScrollView scrollViewanzhou;
	private RelativeLayout anzhourl;
	private List<ScheduleObject> list_anzhou;
	private LinearLayout list_llanzhou;
	
	private LinearLayout anyuell;
	private LinearLayout anyuellf;
	private ScrollView scrollViewanyue;
	private List<ScheduleObject> list_anyue;
	private LinearLayout list_llanyue;
	
	
	private Calendar calendar_anzhou;
	private LinearLayout anrill;
	private LinearLayout anrillf;
	private ScrollView scrollViewanri;
	private AnriView anriView;
	private List<ScheduleObject> list_anri;
	private LinearLayout list_llanri;
	
	private SimpleDateFormat format;
	private CalendarView calendarView;
	private MyView myView;

	
	
	//待办事务列表
	private List<Map<String, Object>> list;
	private CalAdapter adapter;
	private HttpClient httpClient;
	private Handler handler;
	private int n = 4;//列表显示待办事务条数
	
	private TextView tv_name;
	
	private Date curDate;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			if(data.getBooleanExtra("extra", false)){
				if(list_llanri!=null){
					list_llanri.removeAllViews();
					anriView.sendRequest();
					anriView.invalidate();
				}
				if(list_llanyue!=null){
					list_llanyue.removeAllViews();
					calendarView.sendAnyueRequest();
					calendarView.invalidate();
				}
				if(list_llanzhou!=null){
					list_llanzhou.removeAllViews();
					myView.sendAnzhouRequest();
					myView.invalidate();
				}
				sendRequest(4);
			}
		}
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_2, container, false);
		format = new SimpleDateFormat("yyyy-MM-dd");
		httpClient = new DefaultHttpClient();
		sendRequest(n);
		receRequest();
		
		//主页面布局
		iv_user_2 = (ImageView) view.findViewById(R.id.iv_user_2);
		iv_page2_sel = (ImageView) view.findViewById(R.id.iv_sel);
		iv_page2_pre = (ImageView) view.findViewById(R.id.iv_page2_pre);
		iv_page2_next = (ImageView) view.findViewById(R.id.iv_page2_next);
		tv_page2_title = (TextView) view.findViewById(R.id.tv_page2_title);
		rl_page2_content = (RelativeLayout) view.findViewById(R.id.rl_page2_content);
		tv_page2_dbsw = (TextView) view.findViewById(R.id.tv_page2_dbsw);
		tv_page2_more = (TextView) view.findViewById(R.id.tv_page2_more);
		iv_page2_tianjia = (ImageView) view.findViewById(R.id.iv_page2_tianjia);
		tv_name = (TextView) view.findViewById(R.id.tv_person_2_anzhou);
		
		tv_name.setText(Myconfig.UserName);
		
		
		
		iv_user_2.setOnClickListener(this);
		iv_page2_sel.setOnClickListener(this);
		tv_page2_dbsw.setOnClickListener(this);
		tv_page2_more.setOnClickListener(this);
		iv_page2_tianjia.setOnClickListener(this);
		
		activity = (Select2Activity) getActivity();
		
		
		final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		final LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		anzhoullf = getll();
		scrollViewanzhou = getscrollView();
		rl_page2_content.addView(anzhoullf,lp);
		anzhoullf.addView(scrollViewanzhou,lllp);
		
		anzhoull = new LinearLayout(getActivity());
		anzhoull.setBackgroundColor(0xffffffff);
		anzhoull.setOrientation(1);

		
		myView = getAnzhou();
		anzhoull.addView(myView);
		anzhourl = getAnzhoubottom();
		anzhoull.addView(anzhourl,lllp);
		list_llanzhou = getll();
		anzhoull.addView(list_llanzhou,lllp);
		scrollViewanzhou.addView(anzhoull);
		anzhoullf.bringToFront();
		
		calendar_anzhou = Calendar.getInstance();
		curDate = calendar_anzhou.getTime();
		if(calendar_anzhou.get(Calendar.DAY_OF_WEEK)==1){
			calendar_anzhou.add(Calendar.DAY_OF_WEEK, -1);
		}
		calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
		int start = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
		int sMonth = calendar_anzhou.get(Calendar.MONTH)+1;
		calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
		int end = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
		int eMonth = calendar_anzhou.get(Calendar.MONTH)+1;
		tv_page2_title.setTextSize(15);
		tv_page2_title.setText(calendar_anzhou.get(Calendar.YEAR)+"年\n"+sMonth+"月"+start+"日~"+eMonth+"月"+end+"日");
		calendar_anzhou.setTime(curDate);
		iv_page2_pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				calendar_anzhou.add(Calendar.DAY_OF_MONTH, -7);
				curDate = calendar_anzhou.getTime();
				if(calendar_anzhou.get(Calendar.DAY_OF_WEEK)==1){
					calendar_anzhou.add(Calendar.DAY_OF_WEEK, -1);
				}
				calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
				int start = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
				int sMonth = calendar_anzhou.get(Calendar.MONTH)+1;
				calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
				int end = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
				int eMonth = calendar_anzhou.get(Calendar.MONTH)+1;
				tv_page2_title.setText(calendar_anzhou.get(Calendar.YEAR)+"年\n"+sMonth+"月"+start+"日~"+eMonth+"月"+end+"日");
				calendar_anzhou.setTime(curDate);
				myView.clickLeftBtn();
			}
		});
		iv_page2_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				calendar_anzhou.add(Calendar.DAY_OF_MONTH, 7);
				curDate = calendar_anzhou.getTime();
				if(calendar_anzhou.get(Calendar.DAY_OF_WEEK)==1){
					calendar_anzhou.add(Calendar.DAY_OF_WEEK, -1);
				}
				calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
				int start = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
				int sMonth = calendar_anzhou.get(Calendar.MONTH)+1;
				calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
				int end = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
				int eMonth = calendar_anzhou.get(Calendar.MONTH)+1;
				tv_page2_title.setText(calendar_anzhou.get(Calendar.YEAR)+"年\n"+sMonth+"月"+start+"日~"+eMonth+"月"+end+"日");
				calendar_anzhou.setTime(curDate);
				myView.clickRightBtn();
			}
		});
		
		myView.setMyAnzhouOnItemClickListener(new MyAnzhouOnItemClickListener() {
			
			@Override
			public void OnItemClick(Date downdate, List<ScheduleObject> list) {
				list_llanzhou.removeAllViews();
				list_anzhou = new ArrayList<ScheduleObject>();
				list_anzhou = list;
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				lp.setMargins(20, 10, 10, 10);
				LinearLayout.LayoutParams mylp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
				for(int i = 0;i<list_anzhou.size();i++){
					TextView textView = getTextView(list_anzhou.get(i));
					list_llanzhou.addView(textView,lp);
					View view = getmyView();
					list_llanzhou.addView(view,mylp);
				}
			}
		});
		
		myView.setMyAnzhouOnLongItemClickListener(new MyanzhouOnLongItemClickListener() {

			@Override
			public void OnLongItemClickListener(String downdate) {
				Intent it = new Intent(getActivity(),DocSendActivity.class);
				it.putExtra("Date", downdate);
				startActivityForResult(it, 0);
			}
		});
		
		
		return view;		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_user_2:
			activity.xcSlideMenu.switchMenu();
			break;
		case R.id.iv_sel:
			popupWindow = getPopupWindow(arg0);
			break;
		case R.id.tv_page2_dbsw:
			getPopupWindow2(arg0);
			sendRequest(n);
			break;
		case R.id.tv_page2_more:
			Intent itmore = new Intent(getActivity(),DbswMoreActivity.class);
			startActivity(itmore);
			break;
		case R.id.iv_page2_tianjia:
			Intent it = new Intent(getActivity(),DbswAddActivity.class);
			it.putExtra("Kind", "添加");
			startActivity(it);
			break;
		default:
			break;
		}
	}
	private PopupWindow getPopupWindow2(View v){
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.listview_page2_pop, null, false);
		int[] location = new int[2]; 
		v.getLocationOnScreen(location);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, 400, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight()+v.getHeight());
		view.setOnTouchListener(new OnTouchListener() {  
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				 if (popupWindow != null && popupWindow.isShowing()) {  
	                    popupWindow.dismiss();  
	                    popupWindow = null;  
	                }  
	                return false;  
			}  
        });

		ListView listView = (ListView) view.findViewById(R.id.listview_page2_dialog);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(list!=null){
					Intent it = new Intent(getActivity(),MoreYfMainActivity.class);
					it.putExtra("Plan_ID", (String)(list.get(arg2).get("Plan_ID")));
					startActivity(it);
					popupWindow.dismiss();
				}
			}
		});
		
		
		
		
		TextView tv_dbsw = (TextView) view.findViewById(R.id.tv_page2_dialog_dbsw);
		TextView tv_more = (TextView) view.findViewById(R.id.tv_page2_dialog_more);
		ImageView iv_tianjia = (ImageView) view.findViewById(R.id.iv_page2_dialog_tianjia);
		tv_dbsw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				popupWindow.dismiss();
			}
		});
		tv_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(getActivity(),DbswMoreActivity.class);
				startActivity(it);
			}
		});
		iv_tianjia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(getActivity(),DbswAddActivity.class);
				it.putExtra("Kind", "添加");
				startActivity(it);
			}
		});
		
		return popupWindow;
	}

	private PopupWindow getPopupWindow(View v){
		
		
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.sel_caldialog, null, false);

		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(v);
		view.setOnTouchListener(new OnTouchListener() {  
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				 if (popupWindow != null && popupWindow.isShowing()) {  
	                    popupWindow.dismiss();  
	                    popupWindow = null;  
	                }  
	                return false;  
			}  
        });
		btn_anzhou = (Button) view.findViewById(R.id.sel_cal_anzhou);
		btn_anyue = (Button) view.findViewById(R.id.sel_cal_anyue);
		btn_anri = (Button) view.findViewById(R.id.sel_cal_anri);
		final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		final LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		btn_anzhou.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				iv_page2_sel.setImageResource(R.drawable.iv_anzhou);
				anzhoullf.bringToFront();
				curDate = calendar_anzhou.getTime();
				if(calendar_anzhou.get(Calendar.DAY_OF_WEEK)==1){
					calendar_anzhou.add(Calendar.DAY_OF_WEEK, -1);
				}
				calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
				int start = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
				int sMonth = calendar_anzhou.get(Calendar.MONTH)+1;
				calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
				int end = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
				int eMonth = calendar_anzhou.get(Calendar.MONTH)+1;
				tv_page2_title.setTextSize(15);
				tv_page2_title.setTextAlignment(Gravity.CENTER_HORIZONTAL);
				tv_page2_title.setText(calendar_anzhou.get(Calendar.YEAR)+"年\n"+sMonth+"月"+start+"日~"+eMonth+"月"+end+"日");
				calendar_anzhou.setTime(curDate);
				
				iv_page2_pre.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						calendar_anzhou.add(Calendar.DAY_OF_MONTH, -7);
						curDate = calendar_anzhou.getTime();
						if(calendar_anzhou.get(Calendar.DAY_OF_WEEK)==1){
							calendar_anzhou.add(Calendar.DAY_OF_WEEK, -1);
						}
						calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
						int start = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
						int sMonth = calendar_anzhou.get(Calendar.MONTH)+1;
						calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
						int end = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
						int eMonth = calendar_anzhou.get(Calendar.MONTH)+1;
						tv_page2_title.setTextSize(15);
						tv_page2_title.setTextAlignment(Gravity.CENTER_HORIZONTAL);
						tv_page2_title.setText(calendar_anzhou.get(Calendar.YEAR)+"年\n"+sMonth+"月"+start+"日~"+eMonth+"月"+end+"日");
						calendar_anzhou.setTime(curDate);
						myView.clickLeftBtn();
						
					}
				});
				iv_page2_next.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						calendar_anzhou.add(Calendar.DAY_OF_MONTH, 7);
						curDate = calendar_anzhou.getTime();
						if(calendar_anzhou.get(Calendar.DAY_OF_WEEK)==1){
							calendar_anzhou.add(Calendar.DAY_OF_WEEK, -1);
						}
						calendar_anzhou.set(Calendar.DAY_OF_WEEK, 2);
						int start = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
						int sMonth = calendar_anzhou.get(Calendar.MONTH)+1;
						calendar_anzhou.add(Calendar.DAY_OF_MONTH, 6);
						int end = calendar_anzhou.get(Calendar.DAY_OF_MONTH);
						int eMonth = calendar_anzhou.get(Calendar.MONTH)+1;
						tv_page2_title.setTextSize(15);
						tv_page2_title.setTextAlignment(Gravity.CENTER_HORIZONTAL);
						tv_page2_title.setText(calendar_anzhou.get(Calendar.YEAR)+"年\n"+sMonth+"月"+start+"日~"+eMonth+"月"+end+"日");
						calendar_anzhou.setTime(curDate);
						myView.clickRightBtn();
						
					}
				});
				rl_page2_content.invalidate();
				popupWindow.dismiss();
			}
		});
		
		
		//按月
		btn_anyue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				iv_page2_sel.setImageResource(R.drawable.iv_anyue);
				if(anyuellf == null){
					anyuellf = getll();
					scrollViewanyue = getscrollView();
					rl_page2_content.addView(anyuellf,lp);
					anyuellf.addView(scrollViewanyue, lllp);
					list_llanyue = getll();
					
					anyuell = new LinearLayout(getActivity());
					anyuell.setBackgroundColor(0xffffffff);
					anyuell.setOrientation(1);
					calendarView = getCal();
					anyuell.addView(calendarView);
					anyuell.addView(list_llanyue, lllp);
					scrollViewanyue.addView(anyuell);
				}
				
				anyuellf.bringToFront();
				String[] ya = calendarView.getYearAndmonth().split("-");
		        tv_page2_title.setText(ya[0]+"年"+ya[1]+"月");
				
				iv_page2_pre.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String leftYearAndmonth = calendarView.clickLeftMonth();
						String[] ya = leftYearAndmonth.split("-");
						tv_page2_title.setText(ya[0]+"年"+ya[1]+"月");
					}
				});
				iv_page2_next.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String rightYearAndmonth = calendarView.clickRightMonth();
						String[] ya = rightYearAndmonth.split("-");
						tv_page2_title.setText(ya[0]+"年"+ya[1]+"月");
					}
				});
				
				calendarView.setMyOnlongItemClickListener(new MyOnlongItemClickListener() {
					
					@Override
					public void OnItemClick(Date selectedStartDate, Date selectedEndDate,
							Date downDate) {
						Intent it = new Intent(getActivity(),DocSendActivity.class);
						String Date = format.format(downDate);
						it.putExtra("Date", Date);
						startActivityForResult(it, 0);
					}
				});
				
				
				calendarView.setMyOnItemClickListener(new MyOnItemClickListener() {
					
					@Override
					public void OnItemClick(Date selectedStartDate, Date selectedEndDate,
							Date downDate, List<ScheduleObject> list) {
						list_llanyue.removeAllViews();
						list_anyue = new ArrayList<ScheduleObject>();
						list_anyue = list;
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						lp.setMargins(20, 10, 10, 10);
						LinearLayout.LayoutParams mylp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
						for(int i = 0;i<list_anyue.size();i++){
							TextView textView = getTextView(list_anyue.get(i));
							list_llanyue.addView(textView,lp);
							View view = getmyView();
							list_llanyue.addView(view, mylp);
						}
					}
				});
				rl_page2_content.invalidate();
				popupWindow.dismiss();
			}
		});
		
		btn_anri.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				iv_page2_sel.setImageResource(R.drawable.iv_anri);
				if(anrillf == null){
					anrillf = getll();
					scrollViewanri = getscrollView();
					rl_page2_content.addView(anrillf,lp);
					anrillf.addView(scrollViewanri, lllp);
					
					anrill = new LinearLayout(getActivity());
					anrill.setBackgroundColor(0xffffffff);
					anrill.setOrientation(1);
					anriView = getAnri();
					anrill.addView(anriView);
					list_llanri = getll();
					anrill.addView(list_llanri,lllp);
					scrollViewanri.addView(anrill);
				}
				anrillf.bringToFront();
				iv_page2_pre.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String YearAndmonth = anriView.clickLeftMonth();
						String ya[] = YearAndmonth.split("-");
						tv_page2_title.setText(ya[0]+"年"+ya[1]+"月");
					}
				});
				iv_page2_next.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String yearAndmonth = anriView.clickRightMonth();
						String ya[] = yearAndmonth.split("-");
						tv_page2_title.setText(ya[0]+"年"+ya[1]+"月");
					}
				});
				String ya[] = anriView.getYearAndmonth().split("-");
				tv_page2_title.setText(ya[0]+"年"+ya[1]+"月");
				
				
				anriView.setMyAnriOnItemClickListener(new MyAnriOnItemClickListener() {
					
					@Override
					public void OnItemClick(Date downDate, List<ScheduleObject> list) {
						list_llanri.removeAllViews();
						list_anri = new ArrayList<ScheduleObject>();
						list_anri = list;
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						lp.setMargins(20, 10, 10, 10);
						
						LinearLayout.LayoutParams mylp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
						for(int i = 0;i<list_anri.size();i++){
							TextView textView = getTextView(list_anri.get(i));
							list_llanri.addView(textView, lp);
							View view = getmyView();
							list_llanri.addView(view,mylp);
						}
					}
				});
				
				anriView.setMyAnriOnLongItemClickListener(new MyAnriOnLongItemClickListener() {
					
					@Override
					public void OnLongItemClick(String date) {
						// TODO Auto-generated method stub
						Intent it = new Intent(getActivity(),DocSendActivity.class);
						it.putExtra("Date", date);
						startActivityForResult(it, 0);
					}
				});
				rl_page2_content.invalidate();
				popupWindow.dismiss();
				
			}
		});
		
		return popupWindow;
		
	}
	public MyView getAnzhou(){
		MyView myView = new MyView(getActivity());
		return myView;
	}

	
	
	
	
	public RelativeLayout getAnzhoubottom(){
				RelativeLayout anzhourl2 = new RelativeLayout(getActivity());
				
				
				RelativeLayout.LayoutParams anzhoutvlp1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				anzhoutvlp1.leftMargin = 20;
				anzhoutvlp1.addRule(RelativeLayout.CENTER_VERTICAL);
				
				RelativeLayout.LayoutParams anzhoutvlp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				anzhoutvlp2.leftMargin = 20;
				anzhoutvlp2.addRule(RelativeLayout.CENTER_VERTICAL);
				anzhoutvlp2.addRule(RelativeLayout.RIGHT_OF, 4);
				
				RelativeLayout.LayoutParams anzhoutvlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				anzhoutvlp3.leftMargin = 20;
				anzhoutvlp3.addRule(RelativeLayout.CENTER_VERTICAL);
				anzhoutvlp3.addRule(RelativeLayout.RIGHT_OF, 5);
				
				RelativeLayout.LayoutParams anzhoutvlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				anzhoutvlp4.leftMargin = 10;
				anzhoutvlp4.addRule(RelativeLayout.CENTER_VERTICAL);
				anzhoutvlp4.addRule(RelativeLayout.RIGHT_OF, 1);
				
				RelativeLayout.LayoutParams anzhoutvlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				anzhoutvlp5.leftMargin = 10;
				anzhoutvlp5.addRule(RelativeLayout.CENTER_VERTICAL);
				anzhoutvlp5.addRule(RelativeLayout.RIGHT_OF, 2);
				
				RelativeLayout.LayoutParams anzhoutvlp6 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				anzhoutvlp6.leftMargin = 10;
				anzhoutvlp6.addRule(RelativeLayout.CENTER_VERTICAL);
				anzhoutvlp6.addRule(RelativeLayout.RIGHT_OF, 3);
				
				
				
				TextView tv_1 = new TextView(getActivity());
				tv_1.setId(1);
				tv_1.setWidth(40);
				tv_1.setHeight(40);
				tv_1.setBackgroundColor(Color.RED);
				
				TextView tv_2 = new TextView(getActivity());
				tv_2.setId(2);
				tv_2.setWidth(40);
				tv_2.setHeight(40);
				tv_2.setBackgroundColor(Color.BLUE);
				
				TextView tv_3 = new TextView(getActivity());
				tv_3.setId(3);
				tv_3.setWidth(40);
				tv_3.setHeight(40);
				tv_3.setBackgroundColor(Color.GREEN);
				
				TextView tv_4 = new TextView(getActivity());
				tv_4.setId(4);
				tv_4.setText("院级");
				tv_4.setTextSize(15);
				tv_4.setTextColor(Color.BLACK);
				
				TextView tv_5 = new TextView(getActivity());
				tv_5.setId(5);
				tv_5.setText("部门");
				tv_5.setTextSize(15);
				tv_5.setTextColor(Color.BLACK);
				
				TextView tv_6 = new TextView(getActivity());
				tv_6.setId(6);
				tv_6.setText("个人");
				tv_6.setTextSize(15);
				tv_6.setTextColor(Color.BLACK);
				
				anzhourl2.addView(tv_1,anzhoutvlp1);
				anzhourl2.addView(tv_4,anzhoutvlp4);
				anzhourl2.addView(tv_2,anzhoutvlp2);
				anzhourl2.addView(tv_5,anzhoutvlp5);
				anzhourl2.addView(tv_3,anzhoutvlp3);
				anzhourl2.addView(tv_6,anzhoutvlp6);
				
		return anzhourl2;
	}
	
	
	public CalendarView getCal(){
		final CalendarView calendarView = new CalendarView(getActivity());

        calendarView.setSelectMore(false);
        try {
			Date date = format.parse("2015-01-01");
			calendarView.setCalendarData(date);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
        calendarView.getYearAndmonth().split("-");
		return calendarView;
	}
	
	
	
	public AnriView getAnri(){
		final AnriView anriView = new AnriView(getActivity());
		return anriView;
	}
	
	
	public LinearLayout getll(){
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setBackgroundColor(0xffffffff);
		ll.setOrientation(1);
		ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			}
		});
		return ll;
	}
	
	
	public ScrollView getscrollView(){
		ScrollView scrollView = new ScrollView(getActivity());
		return scrollView;
	}
	
	



	private TextView getTextView(final ScheduleObject object){
		TextView textView = new TextView(getActivity());
		String str = object.getBeginTime()+"  "+object.getWork();
		textView.setText(str);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(20);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(),ChakangrapActivity.class);
				String id = object.getID();
				String Type = object.getType();
				it.putExtra("ID", id);
				it.putExtra("Type", Type);
				startActivityForResult(it, 0);
			}
		});
		return textView;
	}
	
	private View getmyView(){
		View view = new View(getActivity());
		view.setBackgroundColor(Color.BLACK);
		return view;
	}
	
	private void sendRequest(final int n){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_GetToDoTopList");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("nTop", n+"");
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
					list = new ArrayList<Map<String,Object>>();
					System.out.println("456789--------"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String Rstr = Goodo.getString("R");
						char[] Rchar = Rstr.toCharArray();
						
						if(Rchar[0]=='['){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RArray.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("Plan_ID", jo.getString("Plan_ID"));
								map.put("PlanType", jo.getString("PlanType"));
								map.put("Work", jo.getString("Work"));
								map.put("CreateDate", jo.getString("CreateDate"));
								list.add(map);
							}
							
						}else{
							JSONObject jo = Goodo.getJSONObject("R");
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Plan_ID", jo.getString("Plan_ID"));
							map.put("PlanType", jo.getString("PlanType"));
							map.put("Work", jo.getString("Work"));
							map.put("CreateDate", jo.getString("CreateDate"));
							list.add(map);
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					adapter = new CalAdapter(list, getActivity());
					adapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		};
	}
	
}