package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ReplyActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private ImageView iv_sure;
	private EditText edt_content;
	private HttpClient httpClient;
	private Handler handler;

	private String NoticeID;
	private String Content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		init();
	}

	private void init(){
		Intent it = getIntent();
		NoticeID = it.getStringExtra("NoticeID");
		httpClient = new DefaultHttpClient();
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_sure = (ImageView) findViewById(R.id.iv_sure);
		edt_content = (EditText) findViewById(R.id.edt_content);
		
		
		iv_return.setOnClickListener(this);
		iv_sure.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_sure:
			Content = edt_content.getText().toString();
			sendRequest();
			receRequest();
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
				builder.encodedPath(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/Notice_Reply");
				builder.appendQueryParameter("Notice_ID", NoticeID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Content", Content);
				String Url = builder.toString();
				
				HttpGet get = new HttpGet(Url);
				try {
					HttpResponse httpresponse = httpClient.execute(get);
					HttpEntity entity = httpresponse.getEntity();
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
					System.out.println("4567890-----"+response);
					try {
						JSONArray jsonArray = new JSONArray(response);
						JSONObject jo = jsonArray.getJSONObject(0);
						String Result = jo.getString("Result");
						if(Result.equals("1")){
							Toast.makeText(ReplyActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(ReplyActivity.this, "回复失败", Toast.LENGTH_SHORT).show();
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
	
}
