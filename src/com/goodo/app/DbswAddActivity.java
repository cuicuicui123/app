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
import android.app.Dialog;
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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DbswAddActivity extends Activity implements OnClickListener{

	private ImageView iv_dbswadd_return;
	private TextView sel_person;
	private TextView show_person;
	
	private Dialog dialog;
	private ListView listView;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private Button btn_dialog_sure;
	private Button btn_dialog_quxiao;
	private ImageView add_dbsw_dialog_addremind;
	private String date;
	private String time;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button btn_gettime;
	private Map<String, Object> map;
	
	//实例化edittext
	private Button btn_sure;
	private EditText et_title;
	private EditText et_content;
	private ToggleButton mTogBtn;
	private RelativeLayout rl_addAttach;
	private LinearLayout ll_Attach;
	private LinearLayout.LayoutParams lllp;
	
	private String Kind = "";
	private String Kind2 = "";
	private String Work;
	private String Content;
	private String IsPublic = "false";
	private String EndDate = "2015-11-08";
	private String IsAutoFinish = "false";
	private String Case_ID = "1";
	private String CaseName = "1";
	private String Source = "1";
	private String ReceiveUserIDs;
	private String ReceiveUserNames;
	private String AttachNames = "";
	private String AttachUrls = "";
	private String FileNames = "";
	private String Base64Datas = "";
	
	private HashMap<Integer, SendAttachObject> map_send;
	private HashMap<Integer, SendAttachObject> map_send2;
	
	
	private HttpClient httpClient;
	private Handler handler;
	
	private List<Map<String, String>> list_send;
	private TextView tv_alltitle;
	private String Plan_ID;
	
	private String[] dia_sel;
	private Uri uri;
	private List<String> list_pic;
	private List<String> list_name;
	private LinearLayout ll_pro;
	private boolean flag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dbsw_add);
		httpClient = new DefaultHttpClient();
		map_send = new HashMap<Integer, SendAttachObject>();
		map_send2 = new HashMap<Integer, SendAttachObject>();
		list_send = new ArrayList<Map<String,String>>();
		dia_sel = new String[]{"文件夹","相册"};
		list_pic = new ArrayList<String>();
		list_name = new ArrayList<String>();
		
		sel_person = (TextView) findViewById(R.id.sel_person);
		show_person = (TextView) findViewById(R.id.show_person);
		
		iv_dbswadd_return = (ImageView) findViewById(R.id.iv_dbswadd_return);
		et_title = (EditText) findViewById(R.id.edt_dbswadd_title);
		et_content = (EditText) findViewById(R.id.edt_dbswadd_content);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn);
		rl_addAttach = (RelativeLayout) findViewById(R.id.rl_addAttach);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_Attach);
		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		iv_dbswadd_return.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		mTogBtn.setOnClickListener(this);
		rl_addAttach.setOnClickListener(this);
		sel_person.setOnClickListener(this);
		
		tv_alltitle = (TextView) findViewById(R.id.tv_alltitle);
		Intent it = getIntent();
		if(it.getStringExtra("Kind").equals("转发")){
			tv_alltitle.setText("转发");
			Kind = it.getStringExtra("Kind");
			Kind2 = it.getStringExtra("Kind2");
			Plan_ID = it.getStringExtra("Plan_ID");
			Work = it.getStringExtra("Work");
			et_title.setText(Work);
			Content = it.getStringExtra("Content");
			et_content.setText(Content);
			IsPublic = it.getStringExtra("IsPublic");
			EndDate = it.getStringExtra("EndDate");
			IsAutoFinish = it.getStringExtra("IsAutoFinish");
			Case_ID = it.getStringExtra("Case_ID");
			CaseName = it.getStringExtra("CaseName");
			AttachUrls = it.getStringExtra("AttachUrls");
			AttachNames = it.getStringExtra("AttachNames");
			map_send2 = (HashMap<Integer, SendAttachObject>) it.getSerializableExtra("map");
			for(int i = 0;i<map_send2.size();i++){
				SendAttachObject object = (SendAttachObject) map_send2.get(i);
				RelativeLayout rl = getrl(object,i,map_send2);
				ll_Attach.addView(rl, lllp);
			}
		}
		
		ll_pro = (LinearLayout) findViewById(R.id.ll_pro);
		ll_pro.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			list_send = (List<Map<String, String>>) data.getSerializableExtra("list");
			for(int i = 0;i<list_send.size();i++){
				if(i==0){
					ReceiveUserIDs = list_send.get(i).get("ID");
					ReceiveUserNames = list_send.get(i).get("Name");
				}else{
					ReceiveUserIDs = ReceiveUserIDs+","+list_send.get(i).get("ID");
					ReceiveUserNames = ReceiveUserNames+","+list_send.get(i).get("Name");
				}
			}
			show_person.setText(ReceiveUserNames);
		}
		if(requestCode == 3&&resultCode == Activity.RESULT_OK){
			map_send = (HashMap<Integer, SendAttachObject>) data.getSerializableExtra("map");
			for(int i = 0;i<map_send.size();i++){
				SendAttachObject object = (SendAttachObject) map_send.get(i);
				RelativeLayout rl = getrl(object,i,map_send);
				ll_Attach.addView(rl, lllp);
			}
			getFile();
		}
		if(requestCode == 4&&resultCode == Activity.RESULT_OK){
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
				baos.close();
				byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
				String str = new String(encode);
				list_pic.add(str);
				list_name.add(name);
				getImageView(bitmap,list_pic.size()-1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void onClick(View arg0) {
		if(!flag){
			switch (arg0.getId()) {
			case R.id.iv_dbswadd_return:
				finish();
				break;
			case R.id.mTogBtn:
				if(mTogBtn.isChecked()){
					IsPublic = "true";
				}else{
					IsPublic = "false";
				}
				break;
			case R.id.rl_addAttach:
				new AlertDialog.Builder(DbswAddActivity.this)
				.setTitle("选择类型")
				.setItems(dia_sel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:
							Intent it = new Intent(DbswAddActivity.this,SelAttachActivity.class);
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
				break;
			case R.id.btn_sure:
				getFileNames();
				if(ReceiveUserIDs == null){
					Toast.makeText(DbswAddActivity.this, "请选择接收人", Toast.LENGTH_SHORT).show();
				}else{
					Work = et_title.getText().toString();
					Content = et_content.getText().toString();
					ll_pro.setVisibility(View.VISIBLE);
					flag = true;
					sendRequest();
					receRequest();
				}
				break;
			case R.id.sel_person:
				Intent it_sel = new Intent(DbswAddActivity.this,SelectPersonActivity.class);
				it_sel.putExtra("list", (Serializable)list_send);
				startActivityForResult(it_sel, 0);
				break;
			}
		}
		
	}
	
	
	private RelativeLayout getrl(final SendAttachObject object , final int i,final HashMap<Integer, SendAttachObject> map){
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
					map.remove(i);
				}
			});
		return rl;
	}
	
	private void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String Uri = null;
				List<NameValuePair> params = new ArrayList<NameValuePair>();; 
				if(Kind.equals("转发")){
					if(Kind2.equals("sender")){
						Uri = Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_RelayBySender";
						params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
						params.add(new BasicNameValuePair("User_ID", Myconfig.User_ID));
						params.add(new BasicNameValuePair("UserName", Myconfig.UserName));
						params.add(new BasicNameValuePair("Plan_ID", Plan_ID));
						params.add(new BasicNameValuePair("Work", Work));
						params.add(new BasicNameValuePair("Content", Content));
						params.add(new BasicNameValuePair("IsPublic", IsPublic));
						params.add(new BasicNameValuePair("EndDate", EndDate));
						params.add(new BasicNameValuePair("IsAutoFinish", IsAutoFinish));
						params.add(new BasicNameValuePair("Case_ID", Case_ID));
						params.add(new BasicNameValuePair("CaseName", CaseName));
						params.add(new BasicNameValuePair("ReceiveUserIDs", ReceiveUserIDs));
						params.add(new BasicNameValuePair("ReceiveUserNames", ReceiveUserNames));
						params.add(new BasicNameValuePair("AttachUrls", AttachUrls));
						params.add(new BasicNameValuePair("AttachNames", AttachNames));
						params.add(new BasicNameValuePair("FileNames", FileNames));
						params.add(new BasicNameValuePair("Base64Datas", Base64Datas));
						System.out.println("0000000000"+params);
					}else{
						Uri = Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_RelayByReceiver";
						params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
						params.add(new BasicNameValuePair("User_ID", Myconfig.User_ID));
						params.add(new BasicNameValuePair("UserName", Myconfig.UserName));
						params.add(new BasicNameValuePair("Plan_ID", Plan_ID));
						params.add(new BasicNameValuePair("Work", Work));
						params.add(new BasicNameValuePair("Content", Content));
						params.add(new BasicNameValuePair("IsPublic", IsPublic));
						params.add(new BasicNameValuePair("EndDate", EndDate));
						params.add(new BasicNameValuePair("IsAutoFinish", IsAutoFinish));
						params.add(new BasicNameValuePair("Case_ID", Case_ID));
						params.add(new BasicNameValuePair("CaseName", CaseName));
						params.add(new BasicNameValuePair("ReceiveUserIDs", ReceiveUserIDs));
						params.add(new BasicNameValuePair("ReceiveUserNames", ReceiveUserNames));
						params.add(new BasicNameValuePair("AttachUrls", AttachUrls));
						params.add(new BasicNameValuePair("AttachNames", AttachNames));
						params.add(new BasicNameValuePair("FileNames", FileNames));
						params.add(new BasicNameValuePair("Base64Datas", Base64Datas));
					}
					
				}else{
					Uri = Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_Add";
					params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
					params.add(new BasicNameValuePair("User_ID", Myconfig.User_ID));
					params.add(new BasicNameValuePair("UserName", Myconfig.UserName));
					params.add(new BasicNameValuePair("Work", Work));
					params.add(new BasicNameValuePair("Content", Content));
					params.add(new BasicNameValuePair("IsPublic", IsPublic));
					params.add(new BasicNameValuePair("EndDate", EndDate));
					params.add(new BasicNameValuePair("IsAutoFinish", IsAutoFinish));
					params.add(new BasicNameValuePair("Case_ID", Case_ID));
					params.add(new BasicNameValuePair("CaseName", CaseName));
					params.add(new BasicNameValuePair("Source", Source));
					params.add(new BasicNameValuePair("ReceiveUserIDs", ReceiveUserIDs));
					params.add(new BasicNameValuePair("ReceiveUserNames", ReceiveUserNames));
					params.add(new BasicNameValuePair("FileNames", FileNames));
					params.add(new BasicNameValuePair("Base64Datas", Base64Datas));
				}
				
				HttpPost post = new HttpPost(Uri);
				
				try {
					post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
					HttpResponse httpResponse = httpClient.execute(post);
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						if(httpResponse.getEntity()!=null){
							HttpEntity httpEntity = httpResponse.getEntity();
							String result = EntityUtils.toString(httpEntity);
							Message message = new Message();
							message.what = Myconfig.SHOW_RESPONSE;
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
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("4567890---------"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(DbswAddActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(DbswAddActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
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
	
	private void getFile(){
		for(int i = 0;i<map_send.size();i++){
			String file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
			File f = new File(file+"Download/"+map_send.get(i).getAttachName());
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
			list_pic.add(str);
			list_name.add(map_send.get(i).getAttachName());
		}
	}
	
	private void getFileNames(){
		for(int i = 0;i<list_name.size();i++){
			if(i == 0){
				FileNames = list_name.get(i);
			}else{
				FileNames = FileNames + "," + list_name.get(i);
			}
		}
		for(int i = 0 ; i<list_pic.size() ; i ++ ){
			if(i == 0){
				Base64Datas = list_pic.get(i);
			}else{
				Base64Datas = Base64Datas + "," + list_pic.get(i);
			}
		}
		System.out.println("4567890"+Base64Datas);
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
				list_pic.remove(i);
				list_name.remove(i);
			}
		});
	}
	
}
