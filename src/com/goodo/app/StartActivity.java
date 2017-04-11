package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.HttpUtils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class StartActivity extends Activity {

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	private ImageView iv_start;
	private ImageView iv_startUser;
	private TextView tv_remind;
	private ImageView iv_mystart1;
	private ImageView iv_mystart2;
	private ImageView iv_mystart3;
	private ImageView iv_mystart4;
	
	//viewPager相关
	private ViewPager viewPager;
	private List<ImageView> imageViews = new ArrayList<ImageView>();
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private List<View> dots;
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};
	
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler_internet;
	private downloadImageTask task;
	private boolean _isExe = false;
	
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	
	private int iSize = 0;
	
	private MyAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		init();
		
		httpClient = new DefaultHttpClient();
		sendRequest();
		

		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		dots.add(findViewById(R.id.v_dot3));
		dots.add(findViewById(R.id.v_dot4));
		
		
		
		for(int i = 0;i < 5;i ++){
			ImageView iv = new ImageView(this);
			iv.setImageResource(R.drawable.myimage);
			
			iv.setScaleType(ScaleType.CENTER_CROP);
			
			imageViews.add(iv);
		}
		adapter = new MyAdapter();
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		//设置填充ViewPager页面的适配器
		
		viewPager.setAdapter(adapter);
		//设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		
		handler_internet = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					
					String response = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONArray R = Goodo.getJSONArray("R");
						int iSize = R.length();
						
						for(int i = 0;i<iSize;i++){
							JSONObject object = R.getJSONObject(i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Contents_ID",object.get("Contents_ID"));
							map.put("PicUrl", object.get("PicUrl"));
							list.add(map);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					for(int i = 0;i<list.size();i++){
						if(!_isExe){
							task = new downloadImageTask();
							task.execute(Myconfig.IP+(String)list.get(i).get("PicUrl"));
						}
					}
					
					break;

				default:
					break;
				}
			}
		};
		
		
		
		iv_mystart1 = (ImageView) findViewById(R.id.mystart1);
		iv_mystart2 = (ImageView) findViewById(R.id.mystart2);
		iv_mystart3 = (ImageView) findViewById(R.id.mystart3);
		iv_mystart4 = (ImageView) findViewById(R.id.mystart4);
		iv_mystart1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.putExtra("extra", "教学通知");
				it.setClass(StartActivity.this, HomeActivity.class);
				startActivity(it);
			}
		});
		iv_mystart2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(StartActivity.this,HomeActivity.class);
				it.putExtra("extra", "新闻动态");
				startActivity(it);
			}
		});
		iv_mystart3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(StartActivity.this,HomeActivity.class);
				it.putExtra("extra", "成果展示");
				startActivity(it);
			}
		});
		iv_mystart4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(StartActivity.this,HomeActivity.class);
				it.putExtra("extra", "党建工作");
				startActivity(it);
			}
		});
		
		iv_start = (ImageView) findViewById(R.id.iv_start);
		tv_remind = (TextView) findViewById(R.id.tv_remind);
		
		
		iv_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				if(Myconfig.login_flag){
					it.setClass(StartActivity.this, Select2Activity.class);
				}else{
					it.setClass(StartActivity.this, MainActivity.class);
				}
				startActivity(it);
			}
		});
		iv_startUser = (ImageView) findViewById(R.id.iv_start_user);
	}
	


	@Override
	protected void onStart() {
		if(iSize >= 4){
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
		}
		super.onStart();
	}



	@Override
	protected void onStop() {
		if(scheduledExecutorService != null){
			scheduledExecutorService.shutdown();
		}
		if(_isExe){
			task.cancel(true);
		}
		super.onStop();
	}



	@Override
	protected void onResume() {
		super.onResume();
		if(Myconfig.login_flag){
			tv_remind.setText(Myconfig.UserName+"，你好！");
		}
	}
	
	private class ScrollTask implements Runnable{

		@Override
		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget();
			}
		}
		
	}
	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author 崔卫聪
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener{
		private int oldPosition = 0;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			currentItem = arg0;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(arg0).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = arg0;
		}
		
	}
	
	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author 崔卫聪
	 */
	
	private class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {
			
		}
		
	}
	
	public void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/Get_Json_SubjectContentPicTopList");
				builder.appendQueryParameter("TopList", "5");
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
						System.out.println("4567890-----"+response);
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = response.toString();
						handler_internet.sendMessage(message);
					}
					
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	
	class downloadImageTask extends AsyncTask<String, Integer, Boolean>{
		Bitmap mDownloadImage = null;

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mDownloadImage = HttpUtils.getHttpGetBitmap(params[0]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			imageViews.get(iSize).setImageBitmap(mDownloadImage);
			final String Contents_ID = (String) list.get(iSize).get("Contents_ID");
			imageViews.get(iSize).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent it = new Intent(StartActivity.this,MoreHomeActivity.class);
					it.putExtra("extra", Contents_ID);
					it.putExtra("kind", "首页新闻");
					startActivity(it);
				}
			});
			
			iSize++;
			adapter.notifyDataSetChanged();
			if(iSize >= 5){
				scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
				scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
				_isExe = true;
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}		
	}
	private void init(){
		Myconfig.local_pic = BitmapFactory.decodeResource(getResources(), R.drawable.logo120);
		Myconfig.height = getResources().getDisplayMetrics().heightPixels;
		Myconfig.width = getResources().getDisplayMetrics().widthPixels;
		Myconfig.IP = "http://jfy.pudong-edu.sh.cn";
		Myconfig.set_AttachName = new LinkedHashSet<String>();
		
		preferences = getSharedPreferences("Attach", MODE_PRIVATE);
		editor = preferences.edit();
		Myconfig.set_AttachName = preferences.getStringSet("AttachName", new LinkedHashSet<String>());
		
		Myconfig.set_Grapremind = new LinkedHashSet<String>();
		preferences = getSharedPreferences("GrapRemind", MODE_PRIVATE);
		editor = preferences.edit();
		Myconfig.set_Grapremind = preferences.getStringSet("remind", new LinkedHashSet<String>());
		
		Myconfig.set_Dbswremind = new LinkedHashSet<String>();
		preferences = getSharedPreferences("DbswRemind", MODE_PRIVATE);
		editor = preferences.edit();
		Myconfig.set_Dbswremind = preferences.getStringSet("remind", new LinkedHashSet<String>());
	}
}
