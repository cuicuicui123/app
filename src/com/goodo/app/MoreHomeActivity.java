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

import utils.DensityUtil;


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
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreHomeActivity extends Activity implements OnClickListener{
	ImageView iv_morehome_return;
	ImageView iv_morehome_goin;
	TextView tv_kind;
	private TextView tv_title;
	private TextView tv_inform;
	private String Content_Id = null;
	private String kind = null;
	private HttpClient httpClient;
	private Handler handler;
	public static final int SHOW_RESPONSE = 1;
	
	private LinearLayout ll_more_home;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	public static final int SHOW_DOC_RESPONSE = 2;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	private WebView webView;
	private LinearLayout ll;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_home);
		
		webView = (WebView) findViewById(R.id.webview);
		ll = (LinearLayout) findViewById(R.id.ll);
		
		
		httpClient = new DefaultHttpClient();
		iv_morehome_return = (ImageView) findViewById(R.id.iv_return);
		iv_morehome_goin = (ImageView) findViewById(R.id.iv_goin);
		iv_morehome_return.setOnClickListener(this);
		iv_morehome_goin.setOnClickListener(this);
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_inform = (TextView) findViewById(R.id.tv_inform);
		Intent it = getIntent();
		Content_Id = it.getStringExtra("extra");
		System.out.println("456789-----"+Content_Id);
		kind = it.getStringExtra("kind");
		tv_kind = (TextView) findViewById(R.id.tv_kind);
		tv_kind.setText(kind);
		kind = it.getStringExtra("kind");
		ll_more_home = (LinearLayout) findViewById(R.id.ll_more_home);
		
		
		httpClient = new DefaultHttpClient();
		sendRequest(Content_Id);
		handler = new Handler(){
			@SuppressWarnings("deprecation")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String)msg.obj;
					ll.setVisibility(View.GONE);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONObject Record = Goodo.getJSONObject("Record");
						tv_title.setText(Record.getString("ContentTitle"));
						tv_inform.setText("发送人："+Record.getString("UserName")+"  发布日期："+Record.getString("SubmitDate")+"  浏览次数："+Record.getString("ICount"));
//						tv_content.setText(Html.fromHtml(Record.getString("Content")));
						System.out.println("33333333333"+Record.getString("Content"));
						String[] strs = Record.getString("Content").split("<img");
						String content = "";
						DensityUtil densityUtil = new DensityUtil(MoreHomeActivity.this);
						int width = densityUtil.px2dip(ll_more_home.getWidth());
						int height = width * 4 / 5;
						System.out.println("00000000000" + width);
						System.out.println("00000000000"+ Record.getString("Content"));
						for(int i = 0;i < strs.length;i ++){
							content = content + strs[i] + "<img" + " height=\""+height+"\" width=\""+width+"\"";
						}
						webView.loadDataWithBaseURL(Myconfig.IP, content, "text/html", "utf-8", null);
						webView.getSettings().setDefaultFontSize(16);
						//支持缩放
//						webView.getSettings().setSupportZoom(true);
//						//显示缩放工具
//						webView.getSettings().setBuiltInZoomControls(true);
						//自适应屏幕居中
//						webView.getSettings().setUseWideViewPort(true);
//						webView.getSettings().setLoadWithOverviewMode(true);//完全适应屏幕
						

						String str = Record.getString("Attach");
						char[] str2 = str.toCharArray();
						
						if(str2[0]=='['){
							JSONArray jsonArray = Record.getJSONArray("Attach");
							int i = jsonArray.length();
							for(int j = 0;j<i;j++){
								JSONObject ob = jsonArray.getJSONObject(j);
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("Url", ob.get("Url"));
								map.put("Name", ob.get("Name"));
								map.put("Type", ob.get("Type"));
								list.add(map);
							}
						}else{
							
							JSONObject attachObject = Record.getJSONObject("Attach");
							
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Url", attachObject.get("Url"));
							map.put("Name", attachObject.get("Name"));
							map.put("Type", attachObject.get("Type"));
							list.add(map);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(list.size()>0){
						LinearLayout ll = getLinearLayout(list.size(),list);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						ll_more_home.addView(ll, lp);
					}
					break;

				default:
					break;
				}
			}
		};
	}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_goin:
			Intent it = new Intent(MoreHomeActivity.this,Select2Activity.class);
			startActivity(it);
			break;
		default:
			break;
		}
	}


	private void sendRequest(final String Content_Id){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/web/wsjson.asmx/Get_Json_Content");
				builder.appendQueryParameter("Content_ID", Content_Id);
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
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
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
			final String str = (String) list.get(j).get("Name");
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.CENTER_VERTICAL);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rlbottom.addView(tv, rllptitle);
			final String Url = (String) list.get(j).get("Url");
			String[] Doc = Url.split("/");
			int n = Doc.length;
			final String doc = Doc[n-1];
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
							Intent it = new Intent(MoreHomeActivity.this,AttachActivity.class);
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
