package com.goodo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoticewriteActivity extends Activity implements OnClickListener{
	private ImageView iv_noticeadd_return;
	private TextView tv_selperson;
	private TextView tv_showperson;
	private TextView tv_notice_msg;
	private TextView tv_no_msg;
	private Button btn_noticeadd_send;
	private ToggleButton tb_check;
	private ToggleButton tb_reply;
	private Dialog dialog;
	private TextView tv_nomsg;
	private TextView tv_sendtitle;
	private TextView tv_sendcontent;
	private EditText edt_title;
	private EditText edt_content;
	
	
	private String NoticeTitle = "";
	private String Content = "";
	private String Send_msg = "";
	private String UserName;
	private String isReply = "0";
	
	private String IsMessage = "1";
	
	private HttpClient httpClient;
	private Handler handler;
	
	private String Url;
	
	private HashMap<Integer, SendAttachObject> map_send;
	private RelativeLayout rl_addAttach;
	private LinearLayout ll_Attach;
	private LinearLayout.LayoutParams lllp;
	
	private List<Map<String, String>> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noticewrite);
		list = new ArrayList<Map<String,String>>();
		
		httpClient = new DefaultHttpClient();
		
		tv_selperson = (TextView) findViewById(R.id.sel_person);
		tv_showperson = (TextView) findViewById(R.id.show_person);
		iv_noticeadd_return = (ImageView) findViewById(R.id.iv_noticeadd_return);
		tv_notice_msg = (TextView) findViewById(R.id.tv_notice_msg);
		tv_no_msg = (TextView) findViewById(R.id.tv_no_msg);
		btn_noticeadd_send = (Button) findViewById(R.id.btn_send);
		edt_title = (EditText) findViewById(R.id.edt_noticeadd_title);
		edt_content = (EditText) findViewById(R.id.edt_noticeadd_content);
		tb_reply = (ToggleButton) findViewById(R.id.mTogBtn1);
		tb_check = (ToggleButton) findViewById(R.id.mTogBtn2);
		rl_addAttach = (RelativeLayout) findViewById(R.id.rl_addAttach);
		ll_Attach = (LinearLayout) findViewById(R.id.ll_Attach);
		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		Intent it = getIntent();
		if(it.getStringExtra("title")!=null){
			edt_title.setText("转"+it.getStringExtra("title"));
			edt_content.setText(it.getStringExtra("Content"));
		}
		
		iv_noticeadd_return.setOnClickListener(this);
		tv_notice_msg.setOnClickListener(this);
		tv_no_msg.setOnClickListener(this);
		btn_noticeadd_send.setOnClickListener(this);
		tv_selperson.setOnClickListener(this);
		rl_addAttach.setOnClickListener(this);
		tb_reply.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg0.isChecked()) {
					isReply = "1";
				}else{
					isReply = "0";
				}
			}
		});
		
		tb_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_noticeadd_return:
			finish();
			break;
		case R.id.tv_notice_msg:
			
			break;
		case R.id.tv_no_msg:
			dialog = getDialog();
			dialog.show();
			break;
		case R.id.btn_send:
			if(Send_msg.equals("")){
				Toast.makeText(NoticewriteActivity.this, "请选择接收人", Toast.LENGTH_SHORT).show();
			}else{
				NoticeTitle = edt_title.getText().toString();
				Content = edt_content.getText().toString();
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/NoticePlate/JsonNotice.asmx/Notice_Add");
				builder.appendQueryParameter("NoticeTitle", NoticeTitle);
				builder.appendQueryParameter("Content", Content);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("IsReply", isReply);
				builder.appendQueryParameter("CheckType", "1");
				builder.appendQueryParameter("CheckPersonID", "-1");
				builder.appendQueryParameter("IsMessage", IsMessage);
				builder.appendQueryParameter("xdReceive", Send_msg);
				Url = builder.toString();
				sendRequest();
				receRequest();
				Intent it = getIntent();
				it.putExtra("flag", "1");
				setResult(Activity.RESULT_OK, it);
				finish();
			}
			break;
		case R.id.rl_addAttach:
			Intent it = new Intent(NoticewriteActivity.this,SelAttachActivity.class);
			startActivityForResult(it, 1);
			break;
		case R.id.sel_person:
			Intent it_sel = new Intent(NoticewriteActivity.this,SelectPersonActivity.class);
			it_sel.putExtra("list", (Serializable)list);
			startActivityForResult(it_sel, 0);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			list = (List<Map<String, String>>) data.getSerializableExtra("list");
			System.out.println("2222222222444444"+list.size());
			for(int i = 0;i<list.size();i++){
				Send_msg = Send_msg + "<R User_ID=\""+list.get(i).get("ID")+"\" "+"UserName=\""+list.get(i).get("Name")+"\"/>";
				if(i==0){
					UserName = list.get(i).get("Name");
				}else{
					UserName = UserName+","+list.get(i).get("Name");
				}
			}
			tv_showperson.setText(UserName);
			
			Send_msg = "<Goodo>"+Send_msg+"</Goodo>";
			System.out.println("99999999"+Send_msg);
		}
		
		if(requestCode == 1&&resultCode == Activity.RESULT_OK){
			map_send = (HashMap<Integer, SendAttachObject>) data.getSerializableExtra("map");;
			for(int i = 0;i<map_send.size();i++){
				SendAttachObject object = (SendAttachObject) map_send.get(i);
				RelativeLayout rl = getrl(object,i);
				ll_Attach.addView(rl, lllp);
			}
		}
	}


	private Dialog getDialog(){
		final Dialog dialog = new Dialog(NoticewriteActivity.this,R.style.add_grap_dialog);
		WindowManager.LayoutParams wParams = dialog.getWindow().getAttributes();
		wParams.width = 300;
		wParams.height = 200;
		dialog.getWindow().setAttributes(wParams);
		
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.list_add_notice_dialog, null);
		dialog.setContentView(view);
		
		tv_nomsg = (TextView) view.findViewById(R.id.tv_msg_1);
		tv_sendtitle = (TextView) view.findViewById(R.id.tv_msg_2);
		tv_sendcontent = (TextView) view.findViewById(R.id.tv_msg_3);
		
		tv_nomsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IsMessage = "1";
				tv_no_msg.setText("不发送短信通知");
				dialog.dismiss();
			}
		});
		
		tv_sendtitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IsMessage = "2";
				tv_no_msg.setText("发送通知标题");
				dialog.dismiss();
			}
		});
		tv_sendcontent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IsMessage = "3";
				tv_no_msg.setText("发送通知内容");
				dialog.dismiss();
			}
		});
		
		return dialog;
	}
	
	private void sendRequest(){
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
					System.out.println("111111111111"+response);
					try {
						JSONArray jsonArray = new JSONArray(response);
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						String Result = jsonObject.getString("Result");
						if(Result.equals("1")){
							Toast.makeText(NoticewriteActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(NoticewriteActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
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
	
}
