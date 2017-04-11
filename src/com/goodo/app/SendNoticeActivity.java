package com.goodo.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class SendNoticeActivity extends Activity implements OnClickListener{
	private String NoticeID;
	private String title;
	private String Content;
	private String SubmitDate;
	private String UserName;
	private String ICount;
	private String TotalCount;
	private String SendUserName;
	private String State;
	private String IsReply;
	
	private ImageView iv_return;
	private ImageView iv_del;
	private TextView tv_title;
	private TextView tv_inform;
	private TextView tv_content;
	
	private HttpClient httpClient;
	private Handler handler;
	public static final int DEL_RESPONSE = 2;
	public static final int REPLY_RESPONSE = 3;
	public static final int LLQK_RESPONSE = 4;
	private List<Map<String, Object>> list;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private LinearLayout ll_fujian;
	
	private TextView tv_llqk;
	private TextView tv_huifu;
	private View view_llqk;
	private View view_huifu;
	private LinearLayout ll_landh;
	private LinearLayout ll_llqk;
	private LinearLayout ll_huifu;
	private List<Map<String, String>> list_huifu = new ArrayList<Map<String,String>>();
	private List<Map<String, String>> list_llqk = new ArrayList<Map<String,String>>();
	private LinearLayout.LayoutParams lp;
	
	private boolean flag = false;
	
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_notice);
		init();
		sendRequest();
		receRequest();
		
	}
	
	
	private void init(){
		Intent it = getIntent();
		NoticeID = it.getStringExtra("NoticeID");
		httpClient = new DefaultHttpClient();
		
		iv_return = (ImageView) findViewById(R.id.iv_send_return);
		iv_del = (ImageView) findViewById(R.id.iv_send_del);
		tv_title = (TextView) findViewById(R.id.tv_send_title);
		tv_inform = (TextView) findViewById(R.id.tv_send_inform);
		tv_content = (TextView) findViewById(R.id.tv_send_content);
		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
		tv_llqk = (TextView) findViewById(R.id.tv_llqk);
		tv_huifu = (TextView) findViewById(R.id.tv_huifu);
		view_llqk = findViewById(R.id.view_llqk);
		view_huifu = findViewById(R.id.view_huifu);
		ll_landh = (LinearLayout) findViewById(R.id.ll_landh);
		webView = (WebView) findViewById(R.id.webview);

		lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = 10;
		
		iv_return.setOnClickListener(this);
		iv_del.setOnClickListener(this);
		tv_llqk.setOnClickListener(this);
		tv_huifu.setOnClickListener(this);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_send_return:
			finish();
			break;
		case R.id.iv_send_del:
			sendDelRequest();
			receDelRequest();
			break;
		case R.id.tv_llqk:
			tv_llqk.setTextColor(0xff3da1d5);
			view_llqk.setBackgroundColor(0xff3da1d5);
			tv_huifu.setTextColor(0xff000000);
			view_huifu.setBackgroundColor(0xffffffff);
			if(ll_huifu!=null){
				ll_huifu.setVisibility(View.GONE);
			}
			if(ll_llqk!=null){
				ll_llqk.bringToFront();
				ll_llqk.setVisibility(View.VISIBLE);
			}
			
			break;
		case R.id.tv_huifu:
			tv_llqk.setTextColor(0xff000000);
			view_llqk.setBackgroundColor(0xffffffff);
			tv_huifu.setTextColor(0xff3da1d5);
			view_huifu.setBackgroundColor(0xff3da1d5);
			if(ll_llqk!=null){
				ll_llqk.setVisibility(View.GONE);
			}
			if(ll_huifu!=null){
				if(!flag){
					ll_landh.addView(ll_huifu,lp);
					ll_huifu.setVisibility(View.VISIBLE);
					flag = true;
				}else{
					ll_huifu.bringToFront();
					if(ll_llqk!=null){
						ll_llqk.setVisibility(View.GONE);
					}
					ll_huifu.setVisibility(View.VISIBLE);
				}
			}
			break;
		default:
			break;
		}
	}
	
	
	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/Notice_Get");
				builder.appendQueryParameter("Notice_ID", NoticeID);
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
					System.out.println("7777777777"+response);
					list = new ArrayList<Map<String,Object>>();
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						title = Goodo.getString("title");
						Content = Goodo.getString("Content");
						SubmitDate = Goodo.getString("SubmitDate");
						UserName = Goodo.getString("UserName");
						ICount = Goodo.getString("ICount");
						TotalCount = Goodo.getString("TotalCount");
						SendUserName = Goodo.getString("SendUserName");
						State = Goodo.getString("State");
						IsReply = Goodo.getString("IsReply");
						try {
							String Rstr = Goodo.getString("R");
							char[] Rstrchar = Rstr.toCharArray();
							if(Rstrchar[0]=='['){
								JSONArray RArray = Goodo.getJSONArray("R");
								int iSize = RArray.length();
								for(int i = 0;i<iSize;i++){
									JSONObject jo = RArray.getJSONObject(i);
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("FileName", jo.get("FileName"));
									map.put("Url", jo.get("Url"));
									list.add(map);
								}
							}else{
								JSONObject jo = Goodo.getJSONObject("R");
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("FileName", jo.get("FileName"));
								map.put("Url", jo.get("Url"));
								list.add(map);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						for(int i = 0;i<list.size();i++){
							LinearLayout ll = getLinearLayout(list.size(), list);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							ll_fujian.addView(ll, lp);
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tv_title.setText(title);
					tv_inform.setText("发送人："+SendUserName+" 发送时间："+SubmitDate+" 浏览次数："+TotalCount);
					webView.loadDataWithBaseURL(Myconfig.IP, Content, "text/html", "utf-8", null);
					webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
					webView.getSettings().setSupportZoom(true);
					webView.getSettings().setBuiltInZoomControls(true);
					webView.getSettings().setUseWideViewPort(true);
					webView.getSettings().setLoadWithOverviewMode(true);
					webView.setInitialScale(100); 
					
					sendLlqkRequest();
					receLlqkRequest();
					
					break;

				default:
					break;
				}
			}
		};
	}
	
	
	private void sendDelRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet get = new HttpGet(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/Notice_Delete?Notice_ID="+NoticeID);
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
						message.what = DEL_RESPONSE;
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
				case DEL_RESPONSE:
					String response = (String) msg.obj;
					try {
						JSONArray jsonArray = new JSONArray(response);
						JSONObject jo = jsonArray.getJSONObject(0);
						String Result = jo.getString("Result");
						System.out.println("456789----"+response);
						if(Result.equals("1")){
							Toast.makeText(SendNoticeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							it.putExtra("extra", true);
							setResult(Activity.RESULT_OK, it);
							finish();
						}else{
							Toast.makeText(SendNoticeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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
			final String str = (String)list.get(j).get("FileName");
			System.out.println("4567890-------"+str);
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rlbottom.addView(tv, rllptitle);
			final String Url = (String) list.get(j).get("Url");
			String[] Doc = Url.split("/");
			int n = Doc.length;
			final String doc = Doc[n-1];
			System.out.println("333333333333333"+doc);
			rlbottom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					System.out.println("456789----"+Url);
					// TODO Auto-generated method stub
					preferences = getSharedPreferences("Attach", MODE_PRIVATE);
					editor = preferences.edit();
					Myconfig.set_AttachName.add(str+" /"+Url);
					System.out.println("cuiweicong"+str+Url);
					editor.putStringSet("AttachName", Myconfig.set_AttachName);
					editor.commit();
					String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
					File f = new File(file+"Download/"+doc);
					System.out.println("333333333cui"+f);
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
						Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(Myconfig.IP+Url));
						startActivity(it);
					}
				}
			});
			ll.addView(rlbottom, lllp);
		}
		return ll;
	}
	
	private void sendReplyRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet get = new HttpGet(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/NoticeReplyList_Get?Notice_ID="+NoticeID+"&Keyword=");
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
						message.what = REPLY_RESPONSE;
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
	
	private void receReplyRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case REPLY_RESPONSE:
					String response = (String)msg.obj;
					System.out.println("456789"+response);
					try {
						JSONArray array = new JSONArray(response);
						int iSize = array.length();
						for(int i = 0;i<iSize;i++){
							JSONObject jo = array.getJSONObject(i);
							Map<String, String> map = new HashMap<String, String>();
							map.put("Name", jo.getString("ReceiverName"));
							map.put("Content", jo.getString("BkContent"));
							map.put("State", jo.getString("State"));
							map.put("LookTime", jo.getString("LookTime"));
							System.out.println("456789"+jo.getString("BkContent"));
							list_huifu.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ll_huifu = new LinearLayout(SendNoticeActivity.this);
					ll_huifu.setOrientation(1);
					int n = list_huifu.size();
					for(int i = 0;i<list_huifu.size();i++){
						LinearLayout ll = new LinearLayout(SendNoticeActivity.this);
						ll.setOrientation(1);
						RelativeLayout rl = new RelativeLayout(SendNoticeActivity.this);
						TextView tv_name = new TextView(SendNoticeActivity.this);
						
						tv_name.setText(list_huifu.get(i).get("Name"));
						tv_name.setTextColor(0xff000000);
						tv_name.setTextSize(18);
						ll.addView(tv_name,lp);
						
						TextView tv_content = new TextView(SendNoticeActivity.this);
						tv_content.setTextColor(0xff000000);
						tv_content.setText(list_huifu.get(i).get("Content"));
						RelativeLayout.LayoutParams rllp1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						rllp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						rl.addView(tv_content, rllp1);
						
						TextView tv_date = new TextView(SendNoticeActivity.this);
						tv_date.setTextColor(0xff000000);
						tv_date.setText(list_huifu.get(i).get("LookTime"));
						RelativeLayout.LayoutParams rllp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						rllp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						rl.addView(tv_date, rllp2);
						
						ll.addView(rl,lp);
						
						LinearLayout.LayoutParams lp_view = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
						lp_view.topMargin = 10;
						View view = new View(SendNoticeActivity.this);
						view.setBackgroundColor(0xff666666);
						ll.addView(view, lp_view);
						ll_huifu.addView(ll, lp);
					}
					tv_huifu.setText("回复("+n+")");
					break;
				default:
					break;
				}
			}
		};
	}
	
	private void sendLlqkRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet get = new HttpGet(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/NoticeReplyPersonList_Get?Notice_ID="+NoticeID);
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
						message.what = LLQK_RESPONSE;
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
	
	private void receLlqkRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case LLQK_RESPONSE:
					String llqkresponse = (String) msg.obj;
					System.out.println("66666666666"+llqkresponse);
					try {
						JSONArray array = new JSONArray(llqkresponse);
						int iSize = array.length();
						for(int i = 0;i<iSize;i++){
							JSONObject jo = array.getJSONObject(i);
							Map<String, String> map = new HashMap<String, String>();
							map.put("Name", jo.getString("Name"));
							map.put("StateID", jo.getString("StateID"));
							map.put("State", jo.getString("State"));
							list_llqk.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ll_llqk = new LinearLayout(SendNoticeActivity.this);
					ll_llqk.setOrientation(1);
					int n = list_llqk.size();
					int m = 0;
					for(int i = 0;i<list_llqk.size();i++){
						System.out.println("666666666"+list_llqk.get(i).get("Name"));
						LinearLayout ll = new LinearLayout(SendNoticeActivity.this);
						ll.setOrientation(1);
						TextView tv = new TextView(SendNoticeActivity.this);
						tv.setText(list_llqk.get(i).get("Name")+"("+list_llqk.get(i).get("State")+")");
						if(list_llqk.get(i).get("StateID").equals("2")){
							tv.setTextColor(0xff000000);
							m++;
						}else{
							tv.setTextColor(Color.RED);
						}
						tv.setTextSize(18);
						ll.addView(tv, lp);
						
						LinearLayout.LayoutParams lp_view = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
						lp_view.topMargin = 10;
						View view = new View(SendNoticeActivity.this);
						view.setBackgroundColor(0xff666666);
						ll.addView(view, lp_view);
						
						ll_llqk.addView(ll, lp);
						
						
					}
					tv_llqk.setText("浏览情况("+m+"/"+n+")");
					ll_landh.addView(ll_llqk, lp);
					sendReplyRequest();
					receReplyRequest();
					break;
					
				default:
					break;
				}
			}
		};
	}
	
}
