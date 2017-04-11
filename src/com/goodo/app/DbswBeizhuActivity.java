package com.goodo.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.apache.http.client.methods.HttpGet;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class DbswBeizhuActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private ImageView iv_sure;
	private EditText edt_content;
	private HttpClient httpClient;
	private Handler handler;
	private String Plan_ID;
	private LinearLayout ll_fujian;
	private LinearLayout ll_addAttach;
	private String[] dia_sel;
	private HashMap<Integer, SendAttachObject> map_send;
	private Uri uri;
	private LinearLayout.LayoutParams lllp;
	private List<String> list_pic;
	private List<String> list_name;
	private String FileNames = "";
	private String Base64Datas = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dbsw_beizhu);
		httpClient = new DefaultHttpClient();
		Intent it = getIntent();
		Plan_ID = it.getStringExtra("Plan_ID");
		System.out.println("456789-------"+Plan_ID);
		
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_sure = (ImageView) findViewById(R.id.iv_sure);
		edt_content = (EditText) findViewById(R.id.edt_content);
		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
		ll_addAttach = (LinearLayout) findViewById(R.id.ll_addattach);
		dia_sel = new String[]{"文件夹","本地图片"};
		list_pic = new ArrayList<String>();
		list_name = new ArrayList<String>();
		map_send = new HashMap<Integer, SendAttachObject>();
		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		iv_return.setOnClickListener(this);
		iv_sure.setOnClickListener(this);
		ll_addAttach.setOnClickListener(this);
		
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("00000000000"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(getApplicationContext(), "添加备注成功",
								     Toast.LENGTH_SHORT).show();
							Intent it = getIntent();
							it.putExtra("extra", true);
							finish();
						}else{
							Toast.makeText(getApplicationContext(), "添加备注失败",
								     Toast.LENGTH_SHORT).show();
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 3&& resultCode == Activity.RESULT_OK){
			map_send = (HashMap<Integer, SendAttachObject>) data.getSerializableExtra("map");
			for(int i = 0;i<map_send.size();i++){
				SendAttachObject object = (SendAttachObject) map_send.get(i);
				RelativeLayout rl = getrl(object,i,map_send);
				ll_fujian.addView(rl, lllp);
			}
			getFile();
		}
		if(requestCode == 4&& resultCode == Activity.RESULT_OK){
			uri = data.getData();
			String[] uri_pah = uri.toString().split("/");
			int len = uri_pah.length;
			String name = uri_pah[len-1]+".jpg";
			
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


	private void sendBeizhuRequest(final String Plan_ID,final String content){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String Uri = Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/PlanRemark_Add";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("SessionID", Myconfig.SessionID));
				params.add(new BasicNameValuePair("User_ID", Myconfig.User_ID));
				params.add(new BasicNameValuePair("UserName", Myconfig.UserName));
				params.add(new BasicNameValuePair("Plan_ID", Plan_ID));
				params.add(new BasicNameValuePair("Content", content));
				params.add(new BasicNameValuePair("FileNames", FileNames));
				params.add(new BasicNameValuePair("Base64Datas", Base64Datas));
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


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_sure:
			String content = edt_content.getText().toString();
			getFileNames();
			sendBeizhuRequest(Plan_ID, content);
			break;
		case R.id.ll_addattach:
			new AlertDialog.Builder(DbswBeizhuActivity.this)
			.setTitle("选择类型")
			.setItems(dia_sel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch (arg1) {
					case 0:
						Intent it = new Intent(DbswBeizhuActivity.this,SelAttachActivity.class);
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
		default:
			break;
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
					ll_fujian.removeView(rl);
					map.remove(i);
				}
			});
		return rl;
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
		
		ll_fujian.addView(rl,lllp);
		iv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ll_fujian.removeView(rl);
				list_pic.remove(i);
				list_name.remove(i);
			}
		});
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
}
