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

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DocRece2Activity extends Activity {
	List<Map<String, Object>> list;
	
	private ImageView doc_rece_iv_return;
	private TextView doc_rece_title;
	private TextView doc_rece_author;
	private TextView doc_rece_date;
	
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
		setContentView(R.layout.activity_doc_rece2);
		doc_rece_title = (TextView) findViewById(R.id.doc_rece_title);
		doc_rece_author = (TextView) findViewById(R.id.doc_rece_author);
		doc_rece_date = (TextView) findViewById(R.id.doc_rece_date);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_Attach);
		
		
		doc_rece_iv_return = (ImageView) findViewById(R.id.doc_rece_iv_return);
		doc_rece_iv_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		


		
		httpClient = new DefaultHttpClient();
		Intent it = getIntent();
		String str = it.getStringExtra("extra");
		sendRequest(str);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					list = new ArrayList<Map<String,Object>>();
					System.out.println("00000000000"+response);
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
						for (int i = 0; i < jiedian.size() - 1; i++) {
							et = (Element) jiedian.get(i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("OriginalFile", et.getAttributeValue("OriginalFile"));
							map.put("Url", et.getAttributeValue("Url"));
							map.put("File_ID", et.getAttributeValue("File_ID"));
							list.add(map);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					doc_rece_title.setText(Title);
					doc_rece_author.setText(UserName);
					doc_rece_date.setText(ReceiveDate);
					
					if(list.size() > 0){
						LinearLayout ll = getLinearLayout(list.size(),list);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						ll_Attach.addView(ll, lp);
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
				builder.encodedPath(Myconfig.IP+"/EduPlate/jfyPublicDocument/IPublicDocument.asmx/ReceiveDocument_GetSingle");
				builder.appendQueryParameter("Receive_ID", str);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			final String str = (String) list.get(j).get("OriginalFile");
			tv.setText(str);
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			RelativeLayout.LayoutParams rllptitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllptitle.addRule(RelativeLayout.RIGHT_OF, 1);
			rllptitle.addRule(RelativeLayout.CENTER_VERTICAL);
			rlbottom.addView(tv, rllptitle);
			final String Url = (String) list.get(j).get("Url");
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
							Intent it = new Intent(DocRece2Activity.this,AttachActivity.class);
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
