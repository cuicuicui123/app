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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
public class ReceNoticeActivity extends Activity implements OnClickListener{
	private String NoticeID;
	private String title = "";
	private String Content = "";
	private String SubmitDate;
	private String UserName;
	private String ICount;
	private String TotalCount;
	private String SendUserName;
	private String State;
	private String IsReply;
	
	
	private ImageView iv_return;
	private TextView tv_title;
	private TextView tv_inform;
	private TextView tv_content;
	private ImageView iv_sel;
	
	private PopupWindow popupWindow;
	private Button btn_reply;
	private Button btn_zhuanfa;
	
	
	
	private HttpClient httpClient;
	private Handler handler;
	
	private List<Map<String, Object>> list;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private LinearLayout ll_fujian;
	
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rece_notice);
		
		init();
		
		sendRequest();
		receRequest();
	}
	
	private void init(){
		Intent it = getIntent();
		NoticeID = it.getStringExtra("NoticeID");
		httpClient = new DefaultHttpClient();
		
		iv_return = (ImageView) findViewById(R.id.iv_return);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_inform = (TextView) findViewById(R.id.tv_inform);
		tv_content = (TextView) findViewById(R.id.tv_content);
		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
		iv_sel = (ImageView) findViewById(R.id.iv_sel);
		webView = (WebView) findViewById(R.id.webview);
		
		iv_return.setOnClickListener(this);
		iv_sel.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_sel:
			getPopupWindow(arg0);
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
					list = new ArrayList<Map<String,Object>>();
					System.out.println("111111111"+response);
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
			System.out.println("--------------"+doc);
			
			rlbottom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					System.out.println("456789----"+Url);
					preferences = getSharedPreferences("Attach", MODE_PRIVATE);
					editor = preferences.edit();
					Myconfig.set_AttachName.add(str+" /"+Url);
					editor.putStringSet("AttachName", Myconfig.set_AttachName);
					
					editor.commit();
					String file = Environment.getDownloadCacheDirectory().getAbsolutePath()+"/";
					File f = new File(file+"Download/"+doc);
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
	
	
	private PopupWindow getPopupWindow(View v){
		LayoutInflater layoutInflater = getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.pop_noticerece, null, false);
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(v);
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(popupWindow!=null&&popupWindow.isShowing()){
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
		btn_reply = (Button) view.findViewById(R.id.btn_reply);
		btn_zhuanfa = (Button) view.findViewById(R.id.btn_zhuanfa);
		btn_reply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(ReceNoticeActivity.this,ReplyActivity.class);
				it.putExtra("NoticeID", NoticeID);
				startActivity(it);
			}
		});
		
		btn_zhuanfa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent it = new Intent(ReceNoticeActivity.this,NoticewriteActivity.class);
				it.putExtra("title", title);
				it.putExtra("Content", Content);
				startActivity(it);
			}
		});
		
		
		
		return popupWindow;
	}
	
	
}
