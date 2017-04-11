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

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class MoreInformActivity extends Activity {
	ImageView iv_moreinform_return;
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	private TextView tv_title;
	private TextView tv_inform;
	private WebView webView;
	private LinearLayout ll;
	private List<Map<String, String>> list;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private LinearLayout ll_Attach;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_inform);
		tv_inform = (TextView) findViewById(R.id.tv_inform);
		tv_title = (TextView) findViewById(R.id.tv_moreinform_title);
		webView = (WebView) findViewById(R.id.webview);
		ll = (LinearLayout) findViewById(R.id.ll);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_Attach);
		
		iv_moreinform_return = (ImageView) findViewById(R.id.iv_moreinform_return);
		iv_moreinform_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		httpClient =new DefaultHttpClient();
		Intent it = getIntent();
		String str = it.getStringExtra("extra");
		sendRequest(str);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("00000000000"+response);
					ll.setVisibility(View.GONE);
					String title = "";
					String person = "";
					String date = "";
					String times = "";
					String content = "";
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONObject Record = Goodo.getJSONObject("Record");
						title = Record.getString("ContentTitle");
						person = Record.getString("UserName");
						date = Record.getString("SubmitDate");
						times = Record.getString("ICount");
						content = Record.getString("Content");
						
						list = new ArrayList<Map<String,String>>();
						try {
							String Attachstr = Record.getString("Attach");
							char[] Attachchar = Attachstr.toCharArray();
							if(Attachchar[0] == '['){
								JSONArray Rarray = Record.getJSONArray("Attach");
								int iSize = Rarray.length();
								for(int i = 0;i < iSize;i ++){
									JSONObject jo = Rarray.getJSONObject(i);
									Map<String,String> map = new HashMap<String, String>();
									map.put("Name", jo.getString("Name"));
									map.put("Url", jo.getString("Url"));
									map.put("Type", jo.getString("Type"));
									list.add(map);
								}
							}else{
								JSONObject jo = Record.getJSONObject("Attach");
								Map<String,String> map = new HashMap<String, String>();
								map.put("Name", jo.getString("Name"));
								map.put("Url", jo.getString("Url"));
								map.put("Type", jo.getString("Type"));
								list.add(map);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						tv_title.setText(title);
						tv_inform.setText("发布人:"+person+"  发布日期:"+date+"  浏览次数:"+times);
						webView.loadDataWithBaseURL(Myconfig.IP, content, "text/html", "utf-8", null);
						webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
						webView.getSettings().setSupportZoom(true);
						webView.getSettings().setBuiltInZoomControls(true);
						webView.getSettings().setUseWideViewPort(true);
						webView.getSettings().setLoadWithOverviewMode(true);
						webView.setInitialScale(100); 
						if(list.size() > 0){
							LinearLayout ll = getLinearLayout(list.size(),list);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							ll_Attach.addView(ll, lp);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					
					
					break;

				default:
					break;
				}
			}
		};
	}
	
	private void sendRequest(final String str){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/Get_Json_Content");
				builder.appendQueryParameter("Content_ID", str);
				String Url = builder.toString();
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					if(entity != null){
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
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	private LinearLayout getLinearLayout(int i,final List<Map<String, String>> list){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(1);
		LinearLayout lltop = new LinearLayout(this);
		lltop.setBackgroundColor(0xffdae1f1);
		TextView tv_top = new TextView(this);
		tv_top.setText("附件：共"+i+"个");
		tv_top.setTextSize(20);
		tv_top.setTextColor(0xff000000);
		LinearLayout.LayoutParams lltoplp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lltop.addView(tv_top, lltoplp);
		LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.addView(lltop, lllp);
		
		for(int j = 0;j<i;j++){
			RelativeLayout rlbottom = new RelativeLayout(this);
			ImageView iv = new ImageView(this);
			iv.setId(1);
			iv.setImageResource(R.drawable.fujian);
			iv.setScaleType(ScaleType.FIT_START);
			RelativeLayout.LayoutParams rllpfujian = new RelativeLayout.LayoutParams(40,40);
			rllpfujian.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rllpfujian.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(iv,rllpfujian);
			
			ImageView iv_jiantou = new ImageView(this);
			iv_jiantou.setId(3);
			iv_jiantou.setImageResource(R.drawable.jiantou);
			iv_jiantou.setScaleType(ScaleType.FIT_END);
			RelativeLayout.LayoutParams rllpjiantou = new RelativeLayout.LayoutParams(40, 40);
			rllpjiantou.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rllpjiantou.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(iv_jiantou, rllpjiantou);
			
			TextView tv = new TextView(this);
			final String str = list.get(j).get("Name");
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rllptitle.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(tv, rllptitle);
			final String Url = list.get(j).get("Url");
			rlbottom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try {
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
							Intent it = new Intent(MoreInformActivity.this,AttachActivity.class);
							it.putExtra("Kind", "其他");
							it.putExtra("Url", Myconfig.IP+Url);
							it.putExtra("Name", str);
							it.putExtra("AttachUrl", Url);
							startActivity(it);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			ll.addView(rlbottom,lllp);
		}
		return ll;
	}
	
}
