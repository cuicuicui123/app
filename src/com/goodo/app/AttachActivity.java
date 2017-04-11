package com.goodo.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AttachActivity extends Activity implements OnClickListener{
	private HttpClient httpClient;
	private Handler handler;
	
	private String uri;
	private String Url;
	private String ID;
	private String EMail_ID;
	private String IsInBox;
	private String Name;
	private String ReMark_ID;
	private String Attach_ID;
	private String Attach_Url;
	
	
	private ImageView iv;
	private ProgressBar progressBar;
	private TextView tv;
	private ImageView iv_return;
	
	private String Kind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attach);
		
		init();
		sendGetRequest();
		receRequest();
	}
	private void init(){
		httpClient = new DefaultHttpClient();
		
		Intent it = getIntent();
		Kind = it.getStringExtra("Kind");
		if(it.getStringExtra("Kind").equals("邮件")){
			
			Url = it.getStringExtra("Url");
			ID = it.getStringExtra("ID");
			EMail_ID = it.getStringExtra("EMail_ID");
			IsInBox = it.getStringExtra("IsInBox");
			Name = it.getStringExtra("Name");
			Attach_Url = it.getStringExtra("AttachUrl");
			Uri.Builder builder = new Uri.Builder();
			builder.encodedPath(Url);
			builder.appendQueryParameter("SessionID", Myconfig.SessionID);
			builder.appendQueryParameter("User_ID", Myconfig.User_ID);
			builder.appendQueryParameter("Mail_ID", EMail_ID);
			builder.appendQueryParameter("IsInBox", IsInBox);
			builder.appendQueryParameter("Attach_ID", ID);
			uri = builder.toString();
		}else if(it.getStringExtra("Kind").equals("待办事务")){
			Url = it.getStringExtra("Url");
			ID = it.getStringExtra("ID");
			ReMark_ID = it.getStringExtra("ReMark_ID");
			Attach_ID = it.getStringExtra("Attach_ID");
			Name = it.getStringExtra("Name");
			Attach_Url = it.getStringExtra("AttachUrl");
			Uri.Builder builder = new Uri.Builder();
			builder.encodedPath(Url);
			builder.appendQueryParameter("SessionID", Myconfig.SessionID);
			builder.appendQueryParameter("User_ID", Myconfig.User_ID);
			builder.appendQueryParameter("Plan_ID", ID);
			builder.appendQueryParameter("ReMark_ID", ReMark_ID);
			builder.appendQueryParameter("Attach_ID", Attach_ID);
			uri = builder.toString();
			System.out.println("0000000000"+uri);
		}else if(it.getStringExtra("Kind").equals("其他")){
			uri = it.getStringExtra("Url");
			Name = it.getStringExtra("Name");
			Attach_Url = it.getStringExtra("AttachUrl");
		}else if(it.getStringExtra("Kind").equals("公文")){
			Kind = it.getStringExtra("Kind");
			Url = it.getStringExtra("Url");
			Name = it.getStringExtra("Name");
			Attach_Url = it.getStringExtra("AttachUrl");
			Uri.Builder builder = new Uri.Builder();
			builder.encodedPath(Url);
			System.out.println("00000000000"+it.getStringExtra("Title"));
			builder.appendQueryParameter("Title", it.getStringExtra("Title"));
			builder.appendQueryParameter("File_ID", it.getStringExtra("File_ID"));
			uri = builder.toString();
		}
		
		
		
		iv = (ImageView) findViewById(R.id.iv);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		tv = (TextView) findViewById(R.id.tv);
		iv_return = (ImageView) findViewById(R.id.iv_return);
		
		iv_return.setOnClickListener(this);
	}
	private void sendGetRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				HttpGet get = new HttpGet(uri);
				try {
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					if(entity != null){
						
						if(Kind.equals("其他")){
							byte[] bytes = EntityUtils.toByteArray(entity);
							Message message = new Message();
							message.what = Myconfig.SHOW_RESPONSE;
							message.obj = bytes;
							handler.sendMessage(message);
						}else{
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
						
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void receRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					
					progressBar.setVisibility(View.GONE);
					tv.setText("加载完成");
					String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
					File files = new File(file+"Download/");
					if(!files.exists()){
						files.mkdirs();
					}
					
					byte[] bytes = null;
					if(Kind.equals("其他")){
						bytes = (byte[]) msg.obj;
					}else if(Kind.equals("公文")){
						String response = (String) msg.obj;
						StringReader reader = new StringReader(response);
						InputSource source = new InputSource(reader);
						SAXBuilder sb = new SAXBuilder();
						
						try {
							Document doc = sb.build(source);
							Element root = doc.getRootElement();
							String Base64Data = root.getAttributeValue("File");
							bytes = Base64.decode(Base64Data, Base64.DEFAULT);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						String response = (String) msg.obj;
						System.out.println("00000000"+response);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(response);
							JSONObject Goodo = jsonObject.getJSONObject("Goodo");
							String Base64Data = Goodo.getString("Base64Data");
							bytes = Base64.decode(Base64Data, Base64.DEFAULT);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					try {
						File f = new File(file+"Download/"+Name);
						FileOutputStream fos = new FileOutputStream(f);
						
						fos.write(bytes);
						fos.close();
						
						String[] str = Attach_Url.split("\\.");
						MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
						String type = mimeTypeMap.getMimeTypeFromExtension(str[1]);
						Intent it = new Intent(Intent.ACTION_VIEW);
						it.addCategory("android.intent.category.DEFAULT");
						it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Uri uri = Uri.fromFile(f);
						it.setDataAndType(uri, type);
						startActivity(it);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
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

		default:
			break;
		}
	}
	
}
