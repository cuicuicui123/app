package com.goodo.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.goodo.app.javabean.SendAttachObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class EmailwriteActivity extends Activity implements OnClickListener{

	private ImageView iv_return;
	private ImageView iv_send;
	private ImageView iv_save;
	private TextView tv_alltitle;
	
	private TextView tv_send;;
	private ImageView iv_selsend;
	private TextView tv_chaosong;
	private ImageView iv_selchaosong;
	private TextView tv_misong;
	private ImageView iv_selmisong;
	private EditText edt_title;
	private EditText edt_content;
	
	private Intent it;
	private List<Map<String, String>> list_send;
	private String send_name = "";
	private String send_id = "";
	private List<Map<String, String>> list_chaosong;
	private String chaosong_name = "";
	private String chaosong_id = "";
	private List<Map<String, String>> list_misong;
	private String misong_name = "";
	private String misong_id = "";
	
	private HttpClient httpClient;
	private Handler handler;
	private String Uri;
	private String saveUri;
	
	private String DraftUri;
	private String answerUri;
	
	private String EMail_ID = "-1";
	private String IsInBox = "";
	private String Subject = "";
	private String Body = "";
	private String SendUser_ID = "";
	private String SendUserName = "";
	private String To = "";
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
	
	public static final int SAVE_RESPONSE = 2;
	public static final int DRAFT_RESPONSE = 3;
	
	private String kind = "写邮件";
	
	private LinearLayout ll_addfujian;
	private LinearLayout ll_Attach;
	private String[] dia_sel;
	private HashMap<Integer, SendAttachObject> map_send;
	private HashMap<Integer, SendAttachObject> map_send2;
	private LinearLayout.LayoutParams lllp;
	private LinearLayout.LayoutParams lllp_iv;
	private Uri uri;
	private FrameLayout fl_all;
	private ProgressBar progressBar;
	private FrameLayout.LayoutParams fllp;
	private String OriginAttachs = "";
	private String FileNames = "";
	private String Base64Datas = "";
	private boolean flag;
	
	private List<String> list;
	private List<String> list_name;
	private List<NameValuePair> params;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emailwrite);
		init();
		receRequest();
		list_send = new ArrayList<Map<String,String>>();
		list_chaosong = new ArrayList<Map<String,String>>();
		list_misong = new ArrayList<Map<String,String>>();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case 0:
				send_id = "";
				send_name = "";
				list_send = (List<Map<String, String>>) data.getSerializableExtra("list");
				for(int i = 0;i<list_send.size();i++){
					if(send_name.equals("")){
						send_name = list_send.get(i).get("Name");
						send_id = list_send.get(i).get("ID")+"|0|"+list_send.get(i).get("Name")+"|默认分类";
					}else{
						send_name = send_name+","+list_send.get(i).get("Name");
						send_id = send_id+";"+list_send.get(i).get("ID")+"|0|"+list_send.get(i).get("Name")+"|默认分类";
					}
				}
				tv_send.setText(send_name);
				break;
			case 1:
				chaosong_id = "";
				chaosong_name = "";
				list_chaosong = (List<Map<String, String>>) data.getSerializableExtra("list");
				for(int i = 0;i<list_chaosong.size();i++){
					if(chaosong_name.equals("")){
						chaosong_name = list_chaosong.get(i).get("Name");
						chaosong_id = list_chaosong.get(i).get("ID")+"|0|"+list_chaosong.get(i).get("Name")+"|默认分类";
					}else{
						chaosong_name = chaosong_name +","+ list_chaosong.get(i).get("Name");
						chaosong_id = chaosong_id +";"+ list_chaosong.get(i).get("ID")+"|0|"+list_chaosong.get(i).get("Name")+"|默认分类";
					}
				}
				tv_chaosong.setText(chaosong_name);
				break;
			case 2:
				misong_id = "";
				misong_name = "";
				list_misong = (List<Map<String, String>>) data.getSerializableExtra("list");
				for(int i = 0;i<list_misong.size();i++){
					if(misong_name.equals("")){
						misong_name = list_misong.get(i).get("Name");
						misong_id = list_misong.get(i).get("ID")+"|0|"+list_misong.get(i).get("Name")+"|默认分类";
					}else{
						misong_name = misong_name +","+ list_misong.get(i).get("Name");
						misong_id = misong_id +";"+ list_misong.get(i).get("ID")+"|0|"+list_misong.get(i).get("Name")+"|默认分类";
					}
				}
				tv_misong.setText(misong_name);
				break;
			case 3:
				map_send2 = (HashMap<Integer, SendAttachObject>) data.getSerializableExtra("map");
				for(int i = 0;i<map_send2.size();i++){
					SendAttachObject object = map_send2.get(i);
					RelativeLayout rl = getrl(object, i);
					ll_Attach.addView(rl, lllp);
				}
				getFile();
				break;
			case 4:
				uri = data.getData();
				String[] uri_pah = uri.toString().split("/");
				int len = uri_pah.length;
				String name = uri_pah[len-1]+".jpg";
				
				System.out.println("0000000000name"+name);
				ContentResolver contentResolver = this.getContentResolver();
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
					byte[] bytes = baos.toByteArray();
					System.out.println("666666666"+bytes.length);
					baos.close();
					byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
					String str = new String(encode);
					list.add(str);
					list_name.add(name);
					getImageView(bitmap,list.size()-1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			if(!flag){
				finish();
			}
			break;
		case R.id.iv_send:
			Subject = edt_title.getText().toString();
			Body = edt_content.getText().toString();
			SendUser_ID = Myconfig.User_ID;
			SendUserName = Myconfig.UserName;
			To = send_name;
			Cc = chaosong_name;
			Bcc = misong_name;
			ToIDs = send_id;
			CcIDs = chaosong_id;
			BccIDs = misong_id;
			if(!flag){
				fl_all.addView(progressBar,fllp);
				flag = true;
				getUri();
				sendRequest(Uri,Myconfig.SHOW_RESPONSE,params);
			}
			break;
		case R.id.iv_selsend:
			if(!flag){
				it = new Intent(EmailwriteActivity.this,SelectPersonActivity.class);
				it.putExtra("list", (Serializable)list_send);
				startActivityForResult(it, 0);
			}
			break;
		case R.id.iv_selchaosong:
			if(!flag){
				it = new Intent(EmailwriteActivity.this,SelectPersonActivity.class);
				it.putExtra("list", (Serializable)list_chaosong);
				startActivityForResult(it, 1);
			}
			break;
		case R.id.iv_selmisong:
			if(!flag){
				it = new Intent(EmailwriteActivity.this,SelectPersonActivity.class);
				it.putExtra("list", (Serializable)list_misong);
				startActivityForResult(it, 2);
			}
			break;
		case R.id.iv_save:
			if(!flag){
				Subject = edt_title.getText().toString();
				Body = edt_content.getText().toString();
				SendUser_ID = Myconfig.User_ID;
				SendUserName = Myconfig.UserName;
				To = send_name;
				Cc = chaosong_name;
				Bcc = misong_name;
				ToIDs = send_id;
				CcIDs = chaosong_id;
				BccIDs = misong_id;
				getSaveUri();
				sendRequest(Uri, SAVE_RESPONSE,params);
			}
			break;
		case R.id.ll_addfujian:
			if(!flag){
				new AlertDialog.Builder(EmailwriteActivity.this)
				.setTitle("选择类型")
				.setItems(dia_sel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:
							Intent it = new Intent(EmailwriteActivity.this,SelAttachActivity.class);
							startActivityForResult(it, 3);
							break;
						case 1:
							Intent it_pic = new Intent(Intent.ACTION_PICK
									,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(it_pic, 4);
							break;
						default:
							break;
						}
					}
				})
				.setNegativeButton("取消", null)
				.show();
			}
			break;
		}
	}
	
	private void init(){
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_send = (ImageView) findViewById(R.id.iv_send);
		iv_save = (ImageView) findViewById(R.id.iv_save);
		tv_alltitle = (TextView) findViewById(R.id.tv_alltitle);
		
		tv_send = (TextView) findViewById(R.id.tv_rece);
		iv_selsend = (ImageView) findViewById(R.id.iv_selsend);
		tv_chaosong = (TextView) findViewById(R.id.tv_chaosong);
		iv_selchaosong = (ImageView) findViewById(R.id.iv_selchaosong);
		tv_misong = (TextView) findViewById(R.id.tv_misong);
		iv_selmisong = (ImageView) findViewById(R.id.iv_selmisong);
		edt_title = (EditText) findViewById(R.id.edt_title);
		edt_content = (EditText) findViewById(R.id.edt_content);
		ll_addfujian = (LinearLayout) findViewById(R.id.ll_addfujian);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_fujian);
		dia_sel = new String[]{"文件夹","相册"};
		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lllp_iv = new LinearLayout.LayoutParams(100, 100);
		fl_all = (FrameLayout) findViewById(R.id.fl_all);
		progressBar = new ProgressBar(this);
		fllp = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);
		list = new ArrayList<String>();
		list_name = new ArrayList<String>();
		map_send = new HashMap<Integer, SendAttachObject>();
		map_send2 = new HashMap<Integer, SendAttachObject>();
		
		
		iv_return.setOnClickListener(this);
		iv_save.setOnClickListener(this);
		iv_send.setOnClickListener(this);
		iv_selsend.setOnClickListener(this);
		iv_selchaosong.setOnClickListener(this);
		iv_selmisong.setOnClickListener(this);
		ll_addfujian.setOnClickListener(this);
		
		httpClient = new DefaultHttpClient();
		
		Intent it = getIntent();
		if(it.getStringExtra("kind")!=null){
			if(it.getStringExtra("kind").equals("草稿箱")){//从草稿箱跳转
				tv_alltitle.setText("草稿箱");
				kind = "草稿箱";
				EMail_ID = it.getStringExtra("ID");
				IsInBox = it.getStringExtra("IsInBox");
				getDraftUri();
				sendRequest(Uri, DRAFT_RESPONSE,params);
			}else if(it.getStringExtra("kind").equals("回复")){//回复
				kind = "回复";
				tv_alltitle.setText("回复");
				iv_save.setVisibility(View.GONE);
				EMail_ID = it.getStringExtra("ID");
				IsInBox = it.getStringExtra("IsInBox");
				Subject = "Re:"+it.getStringExtra("Subject");
				edt_title.setText(Subject);
				Body = it.getStringExtra("Body");
				edt_content.setText(Body);
				send_name = it.getStringExtra("To");
				tv_send.setText(send_name);
				send_id = it.getStringExtra("ToIDs");
			}else if(it.getStringExtra("kind").equals("转发")){
				kind = "转发";
				tv_alltitle.setText("转发");
				iv_save.setVisibility(View.GONE);
				EMail_ID = it.getStringExtra("ID");
				IsInBox = it.getStringExtra("IsInBox");
				Subject = "（转）"+it.getStringExtra("Subject");
				edt_title.setText(Subject);
				Body = it.getStringExtra("Body");
				edt_content.setText(Body);
				map_send = (HashMap<Integer, SendAttachObject>) it.getSerializableExtra("map");
				for(int i = 0;i<map_send.size();i++){
					SendAttachObject object = map_send.get(i);
					RelativeLayout rl = getrl(object, i);
					ll_Attach.addView(rl, lllp);
				}
			}
		}
	}
	
	private void getUri(){
		params = new ArrayList<NameValuePair>();
		if(kind.equals("写邮件")){
			Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_Send";
			params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
			params.add(new BasicNameValuePair("Mail_ID", EMail_ID));
		}else if(kind.equals("草稿箱")){
			Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_Send";
			params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
			params.add(new BasicNameValuePair("Mail_ID", EMail_ID));
		}else if(kind.equals("回复")){
			Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_Reply";
			params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
			params.add(new BasicNameValuePair("Receive_ID", EMail_ID));
		}else if(kind.equals("转发")){
			if(IsInBox.equals("1")){
				System.out.println("cuicuicuicuicui"+IsInBox);
				Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_RelayByReceiver";
				params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
				params.add(new BasicNameValuePair("Receive_ID", EMail_ID));
			}else{
				
				Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_RelayBySender";
				params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
				params.add(new BasicNameValuePair("Mail_ID", EMail_ID));
			}
		}
		
		params.add(new BasicNameValuePair("Subject", Subject));
		params.add(new BasicNameValuePair("Body", Body));
		params.add(new BasicNameValuePair("SendUser_ID", SendUser_ID));
		params.add(new BasicNameValuePair("SendUserName", SendUserName));
		params.add(new BasicNameValuePair("To", To));
		params.add(new BasicNameValuePair("Cc", Cc));
		params.add(new BasicNameValuePair("Bcc", Bcc));
		params.add(new BasicNameValuePair("ToIDs", ToIDs));
		params.add(new BasicNameValuePair("CcIDs", CcIDs));
		params.add(new BasicNameValuePair("BccIDs", BccIDs));
		params.add(new BasicNameValuePair("IsAttached", IsAttached));
		params.add(new BasicNameValuePair("Case_ID", Case_ID));
		params.add(new BasicNameValuePair("CaseName", CaseName));
		params.add(new BasicNameValuePair("IsEncrypt", IsEncrypt));
		params.add(new BasicNameValuePair("EncryptPWD", EncryptPWD));
		params.add(new BasicNameValuePair("IsSplitSend", IsSplitSend));
		
		for(int i = 0;i < map_send.size();i ++){
			if(i == 0){
				OriginAttachs = map_send.get(i).getAttachID()+"|"+map_send.get(i).getAttachName()+"|"+map_send.get(i).getAttachSize()+"|"+map_send.get(i).getAttachUrl();
			}else{
				OriginAttachs = OriginAttachs+","+map_send.get(i).getAttachID()+"|"+map_send.get(i).getAttachName()+"|"+map_send.get(i).getAttachSize()+"|"+map_send.get(i).getAttachUrl();
			}
		}
		params.add(new BasicNameValuePair("OriginAttachs", OriginAttachs));
		for(int i = 0;i<list_name.size();i++){
			if(i == 0){
				FileNames = list_name.get(i);
			}else{
				FileNames = FileNames + "," + list_name.get(i);
			}
		}
		params.add(new BasicNameValuePair("FileNames", FileNames));
			for(int i = 0 ; i<list.size() ; i ++ ){
				if(i == 0){
					Base64Datas = list.get(i);
				}else{
					Base64Datas = Base64Datas + "," + list.get(i);
				}
			}
		
		params.add(new BasicNameValuePair("Base64Datas", Base64Datas));
		System.out.println("22222222222222"+params);
	}
	private void getSaveUri(){
		params = new ArrayList<NameValuePair>();
		
		Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_Save";
		params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
		params.add(new BasicNameValuePair("Mail_ID", EMail_ID));
		params.add(new BasicNameValuePair("Subject", Subject));
		params.add(new BasicNameValuePair("Body", Body));
		params.add(new BasicNameValuePair("SendUser_ID", SendUser_ID));
		params.add(new BasicNameValuePair("SendUserName", SendUserName));
		params.add(new BasicNameValuePair("To", To));
		params.add(new BasicNameValuePair("Cc", Cc));
		params.add(new BasicNameValuePair("Bcc", Bcc));
		params.add(new BasicNameValuePair("ToIDs", ToIDs));
		params.add(new BasicNameValuePair("CcIDs", CcIDs));
		params.add(new BasicNameValuePair("BccIDs", BccIDs));
		params.add(new BasicNameValuePair("IsAttached", IsAttached));
		params.add(new BasicNameValuePair("Case_ID", Case_ID));
		params.add(new BasicNameValuePair("CaseName", CaseName));
		params.add(new BasicNameValuePair("IsEncrypt", IsEncrypt));
		params.add(new BasicNameValuePair("EncryptPWD", EncryptPWD));
		params.add(new BasicNameValuePair("IsSplitSend", IsSplitSend));
		for(int i = 0;i < map_send.size();i ++){
			if(i == 0){
				OriginAttachs = "0|"+map_send.get(i).getAttachName()+"|0|"+map_send.get(i).getAttachUrl();
			}else{
				OriginAttachs = OriginAttachs+",0|" + map_send.get(i).getAttachName()+"|0|"+map_send.get(i).getAttachUrl();
			}
		}
		params.add(new BasicNameValuePair("OriginAttachs", OriginAttachs));
		for(int i = 0;i<list_name.size();i++){
			if(i == 0){
				FileNames = list_name.get(i);
			}else{
				FileNames = FileNames + "," + list_name.get(i);
			}
		}
		params.add(new BasicNameValuePair("FileNames", FileNames));
			for(int i = 0 ; i<list.size() ; i ++ ){
				if(i == 0){
					Base64Datas = list.get(i);
				}else{
					Base64Datas = Base64Datas + "," + list.get(i);
				}
			}
		
		params.add(new BasicNameValuePair("Base64Datas", Base64Datas));
	}
	
	private void getDraftUri(){
		params = new ArrayList<NameValuePair>();
		Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_GetSingleInfo";
		params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
		params.add(new BasicNameValuePair("User_ID", Myconfig.User_ID));
		params.add(new BasicNameValuePair("EMail_ID", EMail_ID));
		params.add(new BasicNameValuePair("IsInBox", IsInBox));
	}
	
	private void sendRequest(final String Uri,final int flag,final List<NameValuePair> params){
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {

				HttpPost post = new HttpPost(Uri);
				try {
					post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
					HttpResponse httpResponse = httpClient.execute(post);
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						if(httpResponse.getEntity()!=null){
							HttpEntity httpEntity = httpResponse.getEntity();
							String result = EntityUtils.toString(httpEntity);
							Message message = new Message();
							message.what = flag;
							message.obj = result;
							handler.sendMessage(message);
						}
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	private void receRequest(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					try {
						String response = (String) msg.obj;
						System.out.println("2222222222"+response);
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(EmailwriteActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(EmailwriteActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
							OriginAttachs = "";
							FileNames = "";
							Base64Datas = "";
							fl_all.removeView(progressBar);
							flag = false;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					break;
				case SAVE_RESPONSE:
					String saveresponse = (String) msg.obj;
					System.out.println("2222222222"+saveresponse+ToIDs);
					
					try {
						JSONObject jsonObject = new JSONObject(saveresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(EmailwriteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(EmailwriteActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case DRAFT_RESPONSE:
					String draftresponse = (String) msg.obj;
					System.out.println("333333333"+draftresponse);
					try {
						JSONObject jsonObject = new JSONObject(draftresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONObject jo = Goodo.getJSONObject("R");
						EMail_ID = jo.getString("Mail_ID");
						Subject = jo.getString("Subject");
						edt_title.setText(Subject);
						Body = jo.getString("Body");
						edt_content.setText(Body);
						send_name = jo.getString("To");
						tv_send.setText(send_name);
						chaosong_name = jo.getString("Cc");
						tv_chaosong.setText(chaosong_name);
						misong_name = jo.getString("Bcc");
						tv_misong.setText(misong_name);
						send_id = jo.getString("ToIDs");
						chaosong_id = jo.getString("CcIDs");
						misong_id = jo.getString("BccIDs");
						IsSplitSend = jo.getString("IsSplitSend");
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
	
	private RelativeLayout getrl(final SendAttachObject object , final int i){
		final RelativeLayout rl = new RelativeLayout(this);
		RelativeLayout.LayoutParams iv_cancellp = new RelativeLayout.LayoutParams(40, 40);
		iv_cancellp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		RelativeLayout.LayoutParams tvlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvlp.addRule(RelativeLayout.ALIGN_PARENT_START);
			
			ImageView iv_cancel = new ImageView(this);
			iv_cancel.setImageResource(R.drawable.cancelfujian);
			iv_cancel.setScaleType(ScaleType.FIT_END);
			rl.addView(iv_cancel, iv_cancellp);
			
			TextView tv = new TextView(this);
			tv.setText(object.getAttachName());
			tv.setTextSize(20);
			tv.setTextColor(0xff000000);
			rl.addView(tv, tvlp);
			
			iv_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ll_Attach.removeView(rl);
					map_send.remove(i);
				}
			});
		return rl;
	}
	
	private void getImageView(Bitmap bitmap,final int i){
		final RelativeLayout rl = new RelativeLayout(this);
		RelativeLayout.LayoutParams lp_iv = new RelativeLayout.LayoutParams(100, 100);
		lp_iv.addRule(RelativeLayout.CENTER_VERTICAL);
		
		RelativeLayout.LayoutParams lp_cancel = new RelativeLayout.LayoutParams(40, 40);
		lp_cancel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp_cancel.addRule(RelativeLayout.CENTER_VERTICAL);
		
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(bitmap);
		iv.setScaleType(ScaleType.FIT_CENTER);
		rl.addView(iv, lp_iv);
		
		ImageView iv_cancel = new ImageView(this);
		iv_cancel.setImageResource(R.drawable.cancelfujian);
		iv_cancel.setScaleType(ScaleType.FIT_CENTER);
		rl.addView(iv_cancel, lp_cancel);
		
		ll_Attach.addView(rl,lllp);
		iv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ll_Attach.removeView(rl);
				list.remove(i);
				list_name.remove(i);
			}
		});
	}
	
	private void getFile(){
		for(int i = 0;i<map_send2.size();i++){
			String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
			File f = new File(file+"Download/"+map_send2.get(i).getAttachName());
			String str = null;
			if(f.exists()){
				FileInputStream in;
				try {
					in = new FileInputStream(f);
					byte[] buff = new byte[(int)f.length()];
					in.read(buff);
					in.close();
					System.out.println("000000000"+new String(buff));
					byte[] encode = Base64.encode(buff, Base64.DEFAULT);
					str = new String(encode);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			list.add(str);
			list_name.add(map_send2.get(i).getAttachName());
			
		}
	}
}
