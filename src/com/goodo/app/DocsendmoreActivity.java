package com.goodo.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.text.Html;

public class DocsendmoreActivity extends Activity {
	List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private List<String[]> locallist = new ArrayList<String[]>();
	private ImageView iv_doc_send_return;
	private TextView tv_doc_send_title;
	private TextView tv_doc_send_author;
	private TextView tv_doc_send_date;
	private TextView tv_doc_send_fujian;
	private LinearLayout ll_send;
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private LinearLayout ll_Attach;
	private String Title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_docsendmore);
		tv_doc_send_title = (TextView) findViewById(R.id.tv_doc_send_title);
		tv_doc_send_author = (TextView) findViewById(R.id.tv_doc_send_author);
		tv_doc_send_date = (TextView) findViewById(R.id.tv_doc_send_date);
		iv_doc_send_return = (ImageView) findViewById(R.id.iv_doc_send_return);
		ll_send = (LinearLayout) findViewById(R.id.ll_send);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_Attach);
		iv_doc_send_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		httpClient = new  DefaultHttpClient();
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
					System.out.println("000000000"+response);
					StringReader reader = new StringReader(response);
					InputSource source = new InputSource(reader);
					SAXBuilder sb = new SAXBuilder();

					String UserName = null;
					String ReceiveDate = null;
					try {
						Document doc = sb.build(source);
						Element root = doc.getRootElement();
						Title = root.getAttributeValue("Title");
						UserName = root.getAttributeValue("UserName");
						ReceiveDate = root.getAttributeValue("ReceiveDate");
						List jiedian = root.getChildren();
						Element et = null;
						
						et = (Element) jiedian.get(0);
						
						List list_Attach = et.getChildren();
						for (int i = 0; i < list_Attach.size(); i++) {
							et = (Element) list_Attach.get(i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("OriginalFile", et.getAttributeValue("OriginalFile"));
							map.put("Url", et.getAttributeValue("Url"));
							map.put("File_ID", et.getAttributeValue("File_ID"));
							list.add(map);
						}
						
						int y = 1;
						for(int i = 1;i<jiedian.size();i++){
							int x = 1;
							y = dfs((Element)jiedian.get(i),x,y);
						}
						
						if(list.size() > 0){
							LinearLayout ll = getLinearLayout(list.size(),list);
							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							ll_Attach.addView(ll, lp);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					tv_doc_send_title.setText(Title);
					tv_doc_send_author.setText(UserName);
					tv_doc_send_date.setText(ReceiveDate);

					TextView tv = new TextView(DocsendmoreActivity.this);
					tv.setText("我");
					tv.setTextSize(15);
					tv.setTextColor(Color.BLACK);
					ll_send.addView(tv);
					
					for(int i = 0;i<locallist.size();i++){
						TextView textView = new TextView(DocsendmoreActivity.this);
						String[] str = locallist.get(i)[0].split("");
						int n = str.length;
						String str2 = "";
						if(str[n-4].equals("已")){
							
							for(int j = n-5;j>=0;j--){
								str2 = str[j]+str2;
							}
							str2 =str2+"<font color='green'>"+str[n-4]+str[n-3]+str[n-2]+"</font>"+")";
							
						}else{
							for(int j = n-5;j>=0;j--){
								str2 = str[j]+str2;
							}
							str2 =str2+"<font color='red'>"+str[n-4]+str[n-3]+str[n-2]+"</font>"+")";
						}
						textView.setTextColor(Color.BLACK);
						textView.setText(Html.fromHtml(str2));
						int x = Integer.parseInt(locallist.get(i)[1]);
						System.out.println("------------"+x);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						lp.leftMargin = x*20;
						textView.setTextSize(15);
						ll_send.addView(textView,lp);
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
				builder.encodedPath(Myconfig.IP+"/EduPlate/jfyPublicDocument/IPublicDocument.asmx/SendDocument_GetSingle");
				builder.appendQueryParameter("Send_ID", str);
				builder.appendQueryParameter("UserID", Myconfig.User_ID);
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
	
	private int dfs(Element et,int x,int y){
		System.out.println("------------"+et.getAttributeValue("UserInfo"));
		System.out.println("-------------"+x+y);
		String[] local = {et.getAttributeValue("UserInfo"),x+"",y+""}; 
		locallist.add(local);
		List jiedian = et.getChildren();
			for(int i = 0;i<jiedian.size();i++){
				Element cet = (Element) jiedian.get(i);
				y++;
				x++;
				dfs(cet,x,y);
				x--;
				y++;
		}
		return y;
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
			final String str = (String) list.get(j).get("OriginalFile");
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rllptitle.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(tv, rllptitle);
			final String Url = (String)list.get(j).get("Url");
			final String File_ID = (String) list.get(j).get("File_ID");
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
							Intent it = new Intent(DocsendmoreActivity.this,AttachActivity.class);
							it.putExtra("Kind", "公文");
							it.putExtra("Url", Myconfig.IP+"/EduPlate/jfyPublicDocument/IPublicDocument.asmx/DocumentFile_Download");
							it.putExtra("Title", Title);
							it.putExtra("File_ID", File_ID);
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
