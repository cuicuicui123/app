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

import com.goodo.app.javabean.SendAttachObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class MoreReceActivity extends Activity implements OnClickListener{
	
	private ImageView more_rece_iv_return;
	private String EMail_ID;
	private String Url;
	private String del_Url;
	private String answer_Url;
	private String zhuanfa_Url;
	
	private String IsInBox;
	private String IsDel;
	private HttpClient httpClient;
	private Handler handler;
	public static final int SHOW_RESPONSE = 1;
	public static final int DEL_RESPONSE = 2;
	public static final int DOWNLOAD_RESPONSE = 3;
	
	private String SendUser_ID = "";
	private String SendUserName = "";
	private String To = "";
	private String From = "";
	private String Cc = "";
	private String Bcc = "";
	private String ToIDs = "";
	private String CcIDs = "";
	private String BccIDs = "";
	private String IsAttached = "1";
	private String Case_ID = "0";
	private String CaseName = "";
	private String IsEncrypt ="0";
	private String EncryptPWD = "";
	private String IsSplitSend ="0";
	
	private String title;
	private String inform;
	private String content;
	
	private TextView tv_title;
	private TextView tv_inform;
	private TextView tv_content;
	private ImageView iv_del;
	private ImageView iv_zhuanfa;
	private ImageView iv_answer;
	private ImageView iv_biaoji;
	
	private boolean Outer;
	private String Kind;
	
	private WebView webView;
	private List<Map<String, Object>> list;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private LinearLayout ll_fujian;
	private HashMap<Integer, SendAttachObject> map_send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_rece);
		init();
		sendRequest(Url,SHOW_RESPONSE);
		receRequest();
	}
	
	private void init(){
		map_send = new HashMap<Integer, SendAttachObject>();
		more_rece_iv_return = (ImageView) findViewById(R.id.more_rece_iv_return);
		tv_title = (TextView) findViewById(R.id.more_rece_title);
		tv_inform = (TextView) findViewById(R.id.tv_inform);
		tv_content = (TextView) findViewById(R.id.more_rece_content);
		iv_del = (ImageView) findViewById(R.id.more_rece_delete);
		iv_zhuanfa = (ImageView) findViewById(R.id.more_rece_transmit);
		iv_answer = (ImageView) findViewById(R.id.more_rece_answer);
		iv_biaoji = (ImageView) findViewById(R.id.more_rece_image);
		
		iv_biaoji.setVisibility(View.GONE);
		webView = (WebView) findViewById(R.id.webview);
		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
		
		iv_del.setOnClickListener(this);
		iv_zhuanfa.setOnClickListener(this);
		iv_answer.setOnClickListener(this);
		more_rece_iv_return.setOnClickListener(this);
		
		Intent it = getIntent();
		EMail_ID = it.getStringExtra("ID");
		IsInBox = it.getStringExtra("IsInBox");
		Outer = it.getBooleanExtra("Outer", false);
		if(IsInBox.equals("0")){
			iv_answer.setVisibility(View.GONE);
		}
		if(Outer){
			Kind = it.getStringExtra("Kind");
		}
		System.out.println("222222222"+EMail_ID);
		httpClient = new DefaultHttpClient();
		
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_GetSingleInfo");
		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
		builder.appendQueryParameter("EMail_ID", EMail_ID);
		builder.appendQueryParameter("IsInBox", IsInBox);
		
		Url = builder.toString();
		System.out.println("55555555555555555"+Url);
	}
	
	private void getDelUrl(){
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_Delete");
		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
		builder.appendQueryParameter("EMail_ID", EMail_ID);
		builder.appendQueryParameter("IsInBox", IsInBox);
		builder.appendQueryParameter("IsDel", IsDel);
		
		del_Url = builder.toString();
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.more_rece_iv_return:
			finish();
			break;
		case R.id.more_rece_answer:
			if(!Outer){
				Intent answer_it = new Intent(MoreReceActivity.this,EmailwriteActivity.class);
				answer_it.putExtra("ID", EMail_ID);
				answer_it.putExtra("kind", "回复");
				answer_it.putExtra("IsInBox", IsInBox);
				answer_it.putExtra("Subject", title);
				answer_it.putExtra("Body", content);
				answer_it.putExtra("To", From);
				answer_it.putExtra("ToIDs", ToIDs);
				startActivityForResult(answer_it, 0);
			}else{
				Intent answer_it = new Intent(MoreReceActivity.this,OuteremailWriteActivity.class);
				answer_it.putExtra("ID", EMail_ID);
				answer_it.putExtra("kind", "回复");
				answer_it.putExtra("IsInBox", IsInBox);
				answer_it.putExtra("Subject", title);
				answer_it.putExtra("Body", content);
				answer_it.putExtra("To", From);
				answer_it.putExtra("OuterMailAddr_ID", Kind);
				startActivityForResult(answer_it, 0);
			}
			
			break;
		case R.id.more_rece_transmit:
			if(!Outer){
				Intent transmit_it = new Intent(MoreReceActivity.this,EmailwriteActivity.class);
				transmit_it.putExtra("ID", EMail_ID);
				transmit_it.putExtra("kind", "转发");
				transmit_it.putExtra("IsInBox", IsInBox);
				transmit_it.putExtra("Subject", title);
				transmit_it.putExtra("Body", content);
				transmit_it.putExtra("map", map_send);
				startActivityForResult(transmit_it, 0);
			}else{
				Intent transmit_it = new Intent(MoreReceActivity.this,OuteremailWriteActivity.class);
				transmit_it.putExtra("ID", EMail_ID);
				transmit_it.putExtra("kind", "转发");
				transmit_it.putExtra("IsInBox", IsInBox);
				transmit_it.putExtra("Subject", title);
				transmit_it.putExtra("Body", content);
				transmit_it.putExtra("OuterMailAddr_ID", Kind);
				transmit_it.putExtra("map", map_send);
				startActivityForResult(transmit_it, 0);
			}
			break;
		case R.id.more_rece_delete:
			
			new AlertDialog.Builder(MoreReceActivity.this)
			.setTitle("是否删除？").setPositiveButton("放入回收箱", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					IsDel = "1";
					getDelUrl();
					sendRequest(del_Url,DEL_RESPONSE);
				}
			})
			.setNeutralButton("彻底删除", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					IsDel = "2";
					getDelUrl();
					sendRequest(del_Url,DEL_RESPONSE);
				}
			})
			.setNegativeButton("取消", null).show();
			
			break;
		default:
			break;
		}
	}
	
	private void sendRequest(final String Url,final int n){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
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
						message.what = n;
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
					String response = (String) msg.obj;
					System.out.println("5555555555cui"+response);
					list = new ArrayList<Map<String,Object>>();
						try {
							JSONObject jsonObject = new JSONObject(response);
							JSONObject Goodo = jsonObject.getJSONObject("Goodo");
							JSONObject jo = Goodo.getJSONObject("R");
							title = jo.getString("Subject");
							if(IsInBox.equals("1")){
								inform = "发件人："+jo.getString("From");
								try {
									inform = inform + " 发布日期："+jo.getString("Date")+" 浏览次数："+jo.getString("ReadCount");
								} catch (Exception e) {
									
								}
							}else 
								if(IsInBox.equals("0")){
								try {
									inform = "收件人："+jo.getString("To");
								} catch (Exception e) {
									// TODO: handle exception
								}
								try {
									inform = "发件人："+jo.getString("From");
								} catch (Exception e) {
									// TODO: handle exception
								}
								
								try {
									inform = inform + " 发布日期："+jo.getString("Date")+" 浏览次数："+jo.getString("ReadCount");
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							content = jo.getString("Body");
							To = jo.getString("To");
							From = jo.getString("From");
							Cc = jo.getString("Cc");
							Bcc = jo.getString("Bcc");
							IsSplitSend = jo.getString("IsSplitSend");
							if(!jo.getString("Case_ID").equals("0")){
								iv_biaoji.setVisibility(View.VISIBLE);
							}
							System.out.println("444444444444"+Bcc);
							try {
								ToIDs = jo.getString("ToIDs");
								CcIDs = jo.getString("CcIDs");
								BccIDs = jo.getString("BccIDs");
							} catch (Exception e) {
								// TODO: handle exception
							}
							try {
								String Attach = jo.getString("Attach");
								char[] Attachchar = Attach.toCharArray();
								if(Attachchar[0] == '['){
									JSONArray array = jo.getJSONArray("Attach");
									int iSize = array.length();
									for(int i = 0;i < iSize;i ++){
										JSONObject Ajo = array.getJSONObject(i);
										SendAttachObject object = new SendAttachObject();
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("Url", Ajo.get("Url"));
										map.put("Name", Ajo.get("Name"));
										map.put("Size", Ajo.get("Size"));
										map.put("ID", Ajo.get("ID"));
										list.add(map);
										object.setAttachName(Ajo.getString("Name"));
										object.setAttachUrl(Ajo.getString("Url"));
										object.setAttachID(Ajo.getString("ID"));
										object.setAttachSize(Ajo.getString("Size"));
										map_send.put(i, object);
									}
								}else{
									JSONObject Ajo = jo.getJSONObject("Attach");
									SendAttachObject object = new SendAttachObject();
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("Url", Ajo.get("Url"));
									map.put("Name", Ajo.get("Name"));
									map.put("Size", Ajo.get("Size"));
									map.put("ID", Ajo.get("ID"));
									list.add(map);
									object.setAttachName(Ajo.getString("Name"));
									object.setAttachUrl(Ajo.getString("Url"));
									object.setAttachID(Ajo.getString("ID"));
									object.setAttachSize(Ajo.getString("Size"));
									map_send.put(0, object);
								}
							} catch (Exception e) {
							}
							
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						tv_title.setText(title);
						tv_inform.setText(inform);
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
							ll_fujian.addView(ll, lp);
						}
					break;
				case DEL_RESPONSE:
					String delresponse = (String) msg.obj;
					System.out.println("444444444"+delresponse);
					try {
						JSONObject jsonObject = new JSONObject(delresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(MoreReceActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							it.putExtra("extra", true);
							setResult(Activity.RESULT_OK, it);
							finish();
						}else{
							Toast.makeText(MoreReceActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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
			final String Url = (String) list.get(j).get("Url");
			final String str = (String) list.get(j).get("Name");
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.CENTER_VERTICAL);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rlbottom.addView(tv, rllptitle);
			final String ID = (String) list.get(j).get("ID");
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
						System.out.println("0000000000000"+file);
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
							Intent it = new Intent(MoreReceActivity.this,AttachActivity.class);
							it.putExtra("Kind", "邮件");
							it.putExtra("Url", Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/File_Download");
							it.putExtra("ID", ID);
							it.putExtra("EMail_ID", EMail_ID);
							it.putExtra("IsInBox", IsInBox);
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
