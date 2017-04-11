package com.goodo.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class OuteremailWriteActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private TextView tv_alltitle;
	private ImageView iv_send;
	private ImageView iv_save;
	private EditText edt_rece;
	private EditText edt_chaosong;
	private EditText edt_misong;
	private EditText edt_title;
	private EditText edt_content;
	
	private HttpClient httpClient;
	private Handler handler;
	private String Uri;

	private String Mail_ID = "0";
	private String OuterMailAddr_ID = "";
	private String Subject = "";
	private String Body = "";
	private String To = "";
	private String Cc = "";
	private String Bcc = "";
	private String IsAttached = "1";
	private String Case_ID = "0";
	private String CaseName = "";
	private String IsSplitSend = "0";
	private String kind = "写邮件";
	private String IsInBox = "";
	
	public static final int SAVE_RESPONSE = 2;
	public static final int DRAFT_RESPONSE = 3;
	
	private FrameLayout fl_all;
	private boolean flag = false;
	private ProgressBar progressBar;
	private LinearLayout ll_Attach;
	private LinearLayout ll_addfujian;
	private FrameLayout.LayoutParams fllp;
	private String[] dia_sel;
	private HashMap<Integer, SendAttachObject> map_send;
	private HashMap<Integer, SendAttachObject> map_send2;
	private List<String> list;
	private List<String> list_name;
	private LinearLayout.LayoutParams lllp;
	private Uri uri;
	private List<NameValuePair> params;
	private String OriginAttachs = "";
	private String FileNames = "";
	private String Base64Datas = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outeremail_write);
		init();
		receRequest();
	}
	
	private void init(){
		iv_return = (ImageView) findViewById(R.id.iv_return);
		tv_alltitle = (TextView) findViewById(R.id.tv_alltitle);
		iv_send = (ImageView) findViewById(R.id.iv_send);
		iv_save = (ImageView) findViewById(R.id.iv_save);
		iv_return.setOnClickListener(this);
		iv_send.setOnClickListener(this);
		iv_save.setOnClickListener(this);
		
		edt_rece = (EditText) findViewById(R.id.edt_rece);
		edt_chaosong = (EditText) findViewById(R.id.edt_chaosong);
		edt_misong = (EditText) findViewById(R.id.edt_misong);
		edt_title = (EditText) findViewById(R.id.edt_title);
		edt_content = (EditText) findViewById(R.id.edt_content);
		fl_all = (FrameLayout) findViewById(R.id.fl_all);
		progressBar = new ProgressBar(this);
		fllp = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);
		ll_addfujian = (LinearLayout) findViewById(R.id.ll_addfujian);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_fujian);
		ll_addfujian.setOnClickListener(this);
		dia_sel = new String[]{"文件夹","相册"};
		map_send = new HashMap<Integer, SendAttachObject>();
		map_send2 = new HashMap<Integer, SendAttachObject>();
		list = new ArrayList<String>();
		list_name = new ArrayList<String>();
		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		httpClient = new DefaultHttpClient();
		Intent it = getIntent();
		if(it.getStringExtra("kind").equals("草稿箱")){
			kind = it.getStringExtra("kind");
			tv_alltitle.setText(kind);
			Mail_ID = it.getStringExtra("ID");
			IsInBox = it.getStringExtra("IsInBox");
			getDraftUri();
			sendRequest(DRAFT_RESPONSE,params);
		}
		if(it.getStringExtra("kind").equals("回复")){//回复
			kind = it.getStringExtra("kind");
			tv_alltitle.setText(kind);
			Mail_ID = it.getStringExtra("ID");
			IsInBox = it.getStringExtra("IsInBox");
			Subject = it.getStringExtra("Subject");
			edt_title.setText("Re:"+Subject);
			Body = it.getStringExtra("Body");
			edt_content.setText(Body);
			To = it.getStringExtra("To");
			String[] Tochar = To.split("\\(");
			System.out.println("55555555"+Mail_ID);
			String[] Tochar2 = Tochar[1].split("\\)");
			To = Tochar2[0];
			edt_rece.setText(To);
			OuterMailAddr_ID = it.getStringExtra("OuterMailAddr_ID");
		}
		if(it.getStringExtra("kind").equals("转发")){
			kind = it.getStringExtra("kind");
			tv_alltitle.setText(kind);
			Mail_ID = it.getStringExtra("ID");
			IsInBox = it.getStringExtra("IsInBox");
			Subject = it.getStringExtra("Subject");
			edt_title.setText("（转）"+Subject);
			Body = it.getStringExtra("Body");
			edt_content.setText(Body);
			OuterMailAddr_ID = it.getStringExtra("OuterMailAddr_ID");
			map_send = (HashMap<Integer, SendAttachObject>) it.getSerializableExtra("map");
			for(int i = 0;i<map_send.size();i++){
				SendAttachObject object = map_send.get(i);
				RelativeLayout rl = getrl(object, i);
				ll_Attach.addView(rl, lllp);
			}
		}
		if(it.getStringExtra("kind").equals("写邮件")){
			OuterMailAddr_ID = it.getStringExtra("Kind");//写邮件
			System.out.println("6666666666"+OuterMailAddr_ID);
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
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
			To = edt_rece.getText().toString();
			if(!flag){
				fl_all.addView(progressBar, fllp);
				flag = true;
				getUri();
				sendRequest(Myconfig.SHOW_RESPONSE,params);
			}
			break;
		case R.id.iv_save:
			if(!flag){
				Subject = edt_title.getText().toString();
				Body = edt_content.getText().toString();
				To = edt_rece.getText().toString();
				getSaveUri();
				sendRequest(SAVE_RESPONSE,params);
				flag = true;
			}
			break;
		case R.id.ll_addfujian:
			if(!flag){
				selAttach();
			}
			break;
		default:
			break;
		}
	}
	
	private void getUri(){
		params = new ArrayList<NameValuePair>();
//		Uri.Builder builder = new Uri.Builder();
		if(kind.equals("写邮件")||kind.equals("草稿箱")){
//			builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_Send");
//			builder.appendQueryParameter("SessionID", Myconfig.SessionID);
//			builder.appendQueryParameter("Mail_ID", Mail_ID);
			Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_Send";
			params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
			params.add(new BasicNameValuePair("Mail_ID", Mail_ID));
		}
		if(kind.equals("回复")){
//			builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_Reply");
//			builder.appendQueryParameter("SessionID", Myconfig.SessionID);
//			builder.appendQueryParameter("Receive_ID", Mail_ID);
			
			Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_Reply";
			params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
			params.add(new BasicNameValuePair("Mail_ID", Mail_ID));
		}
		if(kind.equals("转发")){
			if(IsInBox.equals("0")){
//				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_RelayBySender");
//				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
//				builder.appendQueryParameter("Receive_ID", Mail_ID);
				
				Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_RelayBySender";
				params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
				params.add(new BasicNameValuePair("Mail_ID", Mail_ID));
			}else{
//				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_RelayByReceiver");
//				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
//				builder.appendQueryParameter("Receive_ID", Mail_ID);
				
				Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_RelayByReceiver";
				params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
				params.add(new BasicNameValuePair("Receive_ID", Mail_ID));
			}
		}
//		builder.appendQueryParameter("OuterMailAddr_ID", OuterMailAddr_ID);
//		builder.appendQueryParameter("Subject", Subject);
//		builder.appendQueryParameter("Body", Body);
//		builder.appendQueryParameter("SendUser_ID", Myconfig.User_ID);
//		builder.appendQueryParameter("SendUserName", Myconfig.UserName);
//		builder.appendQueryParameter("To", To);
//		builder.appendQueryParameter("Cc", Cc);
//		builder.appendQueryParameter("Bcc", Bcc);
//		builder.appendQueryParameter("IsAttached", IsAttached);
//		builder.appendQueryParameter("Case_ID", Case_ID);
//		builder.appendQueryParameter("CaseName", CaseName);
//		builder.appendQueryParameter("IsSplitSend", IsSplitSend);
		
		params.add(new BasicNameValuePair("OuterMailAddr_ID", OuterMailAddr_ID));
		params.add(new BasicNameValuePair("Subject", Subject));
		params.add(new BasicNameValuePair("Body", Body));
		params.add(new BasicNameValuePair("SendUser_ID", Myconfig.User_ID));
		params.add(new BasicNameValuePair("SendUserName", Myconfig.UserName));
		params.add(new BasicNameValuePair("To", To));
		params.add(new BasicNameValuePair("Cc", Cc));
		params.add(new BasicNameValuePair("Bcc", Bcc));
		params.add(new BasicNameValuePair("IsAttached", IsAttached));
		params.add(new BasicNameValuePair("Case_ID", Case_ID));
		params.add(new BasicNameValuePair("CaseName", CaseName));
		params.add(new BasicNameValuePair("IsSplitSend", IsSplitSend));
		
		for(int i = 0;i < map_send.size();i ++){
			if(i == 0){
				OriginAttachs = map_send.get(i).getAttachID()+"|"+map_send.get(i).getAttachName()+"|"+map_send.get(i).getAttachSize()+"|"+map_send.get(i).getAttachUrl();
			}else{
				OriginAttachs = OriginAttachs+","+map_send.get(i).getAttachID()+"|"+map_send.get(i).getAttachName()+"|"+map_send.get(i).getAttachSize()+"|"+map_send.get(i).getAttachUrl();
			}
		}
		System.out.println("6666666666"+OriginAttachs);
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
	
	private void getSaveUri(){
		params = new ArrayList<NameValuePair>();
//		Uri.Builder builder = new Uri.Builder();
//		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_Save");
//		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
//		builder.appendQueryParameter("Mail_ID", Mail_ID);
//		builder.appendQueryParameter("OuterMailAddr_ID", OuterMailAddr_ID);
//		builder.appendQueryParameter("Subject", Subject);
//		builder.appendQueryParameter("Body", Body);
//		builder.appendQueryParameter("SendUser_ID", Myconfig.User_ID);
//		builder.appendQueryParameter("SendUserName", Myconfig.UserName);
//		builder.appendQueryParameter("To", To);
//		builder.appendQueryParameter("Cc", Cc);
//		builder.appendQueryParameter("Bcc", Bcc);
//		builder.appendQueryParameter("IsAttached", IsAttached);
//		builder.appendQueryParameter("Case_ID", Case_ID);
//		builder.appendQueryParameter("CaseName", CaseName);
//		builder.appendQueryParameter("IsSplitSend", IsSplitSend);
//		
//		Uri = builder.toString();
		
		Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_Save";
		params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
		params.add(new BasicNameValuePair("Mail_ID", Mail_ID));
		params.add(new BasicNameValuePair("OuterMailAddr_ID", OuterMailAddr_ID));
		params.add(new BasicNameValuePair("Subject", Subject));
		params.add(new BasicNameValuePair("Body", Body));
		params.add(new BasicNameValuePair("SendUser_ID", Myconfig.User_ID));
		params.add(new BasicNameValuePair("SendUserName", Myconfig.UserName));
		params.add(new BasicNameValuePair("To", To));
		params.add(new BasicNameValuePair("Cc", Cc));
		params.add(new BasicNameValuePair("Bcc", Bcc));
		params.add(new BasicNameValuePair("IsAttached", IsAttached));
		params.add(new BasicNameValuePair("Case_ID", Case_ID));
		params.add(new BasicNameValuePair("CaseName", CaseName));
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
	
	private void getDraftUri(){//从草稿箱跳转过来获取草稿箱信息
		params = new ArrayList<NameValuePair>();
//		Uri.Builder builder = new Uri.Builder();
//		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_GetSingleInfo");
//		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
//		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
//		builder.appendQueryParameter("EMail_ID", Mail_ID);
//		builder.appendQueryParameter("IsInBox", IsInBox);
//		Uri = builder.toString();
		
		Uri = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_GetSingleInfo";
		params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
		params.add(new BasicNameValuePair("User_ID", Myconfig.User_ID));
		params.add(new BasicNameValuePair("EMail_ID", Mail_ID));
		params.add(new BasicNameValuePair("IsInBox", IsInBox));
	}
	
	
	private void sendRequest(final int flag,final List<NameValuePair> params){
		new Thread(new Runnable() {
			
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
					System.out.println("666666666"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(OuteremailWriteActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							setResult(Activity.RESULT_OK,it);
							finish();
						}else{
							Toast.makeText(OuteremailWriteActivity.this, "发送失败，请检查收件人地址格式", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case SAVE_RESPONSE:
					String saveresponse = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(saveresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(OuteremailWriteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							setResult(Activity.RESULT_OK);
							finish();
						}else{
							Toast.makeText(OuteremailWriteActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case DRAFT_RESPONSE:
					String draftresponse = (String) msg.obj;
					System.out.println("333333333333333"+draftresponse);
					try {
						JSONObject jsonObject = new JSONObject(draftresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						JSONObject jo = Goodo.getJSONObject("R");
						OuterMailAddr_ID = jo.getString("OuterMailAddr_ID");
						Subject = jo.getString("Subject");
						edt_title.setText(Subject);
						Body = jo.getString("Body");
						edt_content.setText(Body);
						To = jo.getString("To");
						edt_rece.setText(To);
						Cc = jo.getString("Cc");
						edt_chaosong.setText(Cc);
						Bcc = jo.getString("Bcc");
						edt_misong.setText(Bcc);
						IsAttached = jo.getString("IsAttached");
						Case_ID = jo.getString("Case_ID");
						CaseName = jo.getString("Case_Name");
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
	
	private void selAttach(){
		new AlertDialog.Builder(OuteremailWriteActivity.this)
		.setTitle("选择类型")
		.setItems(dia_sel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				switch (arg1) {
				case 0:
					Intent it = new Intent(OuteremailWriteActivity.this,SelAttachActivity.class);
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
