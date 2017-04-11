package com.goodo.app;

import java.io.BufferedReader;
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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class ReceActivity extends Activity implements OnClickListener{
	
	private PullToRefreshListView listView = null;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private ImageView rece_iv_return;
	private String Url = null;
	private HttpClient httpClient;
	private Handler handler;
	private String title;
	private TextView tv_title;
	private ReceAdapter adapter;
	int FID = 0;
	int CID = 0;
	private ImageView iv_addmail;
	private String IsInBox;
	private int nPage = 1;
	private int nSize = 10;
	private boolean isRefreshing = false;
	private boolean flag = false;
	private boolean Outer = false;
	private String Kind;
	private List<Map<String, String>> list_group = new ArrayList<Map<String,String>>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rece);
		init();
		sendRequest();
		receRequest();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			flag = true;
			sendRequest();
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rece_iv_return:
			finish();
			break;
		case R.id.iv_addmail:
			if(Outer){
				Intent it = new Intent(ReceActivity.this,OuteremailWriteActivity.class);
				it.putExtra("Kind", Kind);
				it.putExtra("kind", "写邮件");
				startActivityForResult(it, 0);
			}else{
				Intent it = new Intent(ReceActivity.this,EmailwriteActivity.class);
				startActivityForResult(it, 0);
			}
			break;
		default:
			break;
		}
	}
	private void init(){
		httpClient = new DefaultHttpClient();
		Url = getUrl();
		rece_iv_return = (ImageView) findViewById(R.id.rece_iv_return);
		iv_addmail = (ImageView) findViewById(R.id.iv_addmail);
		tv_title = (TextView) findViewById(R.id.tv_title);
		listView = (PullToRefreshListView) findViewById(R.id.rece_list);
		adapter = new ReceAdapter(list, ReceActivity.this,list_group);
		
		listView.setAdapter(adapter);
		listView.setMode(Mode.BOTH);
		
		
		rece_iv_return.setOnClickListener(this);
		iv_addmail.setOnClickListener(this);
	}
	
	private String getUrl(){
		Intent it = getIntent();
		list_group = (List<Map<String, String>>) it.getSerializableExtra("list");
		FID = it.getIntExtra("FID", 0);
		String ReceiveClassify_ID = "";
		String Url = "";
		Uri.Builder builder = new Uri.Builder();
		if(FID == 0){
			CID = it.getIntExtra("CID", 0);
			if(CID == 0){
				title = "收件箱";
				IsInBox = "1";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/TotalReceive_GetListByCache");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
			}else if(CID == 1){
				title = "发件箱";
				IsInBox = "0";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/TotalMail_GetListByCache");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/TotalMail_GetListByCache?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&nPage="+nPage+"&nPageSize="+nSize;
			}else if(CID == 2){
				title = "草稿箱";
				IsInBox ="0";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_DraftListGet");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_DraftListGet?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&nPage="+nPage+"&nPageSize="+nSize;
			}else if(CID == 3){
				title = "回收箱";
				IsInBox ="1";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_RecycleListGet");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_RecycleListGet?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&nPage="+nPage+"&nPageSize="+nSize;
			}
		}else if(FID == 1){
			CID = it.getIntExtra("CID", 0);
			if(CID == 0){
				title = "收件箱";
				IsInBox ="1";
				Kind = it.getStringExtra("Kind");
				ReceiveClassify_ID = Kind;
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Receive_GetListByCache");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("ReceiveClassify_ID", ReceiveClassify_ID);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Receive_GetListByCache?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&ReceiveClassify_ID="+ReceiveClassify_ID+"&nPage="+nPage+"&nPageSize="+nSize;
				//内部电函收件箱
			}else{
				//内部电函发件箱
				title = "发件箱";
				IsInBox ="0";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_GetListByCache");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Mail_GetListByCache?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&nPage="+nPage+"&nPageSize="+nSize;
			}
		}else if(FID > 1){
			Outer = true;
			CID = it.getIntExtra("CID", 0);
			Kind = it.getStringExtra("Kind");
			if(CID == 0){
				title = "收件箱";
				IsInBox = "1";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterReceive_GetListByCache");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("OuterMailAddr_ID", Kind);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterReceive_GetListByCache?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&OuterMailAddr_ID="+Kind+"&nPage="+nPage+"&nPageSize="+nSize;
			}else{
				title = "发件箱";
				IsInBox = "0";
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_GetListByCache");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("OuterMailAddr_ID", Kind);
				builder.appendQueryParameter("nPage", nPage+"");
				builder.appendQueryParameter("nPageSize", nSize+"");
				
				Url = builder.toString();
//				Url = Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/OuterMail_GetListByCache?SessionID="+Myconfig.SessionID+"&User_ID="+Myconfig.User_ID+"&OuterMailAddr_ID="+Kind+"&nPage="+nPage+"&nPageSize="+nSize;
			}
			
		}
		System.out.println("45679----"+Kind);
		return Url;
	}
	
	public void sendRequest(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
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
						if(listView!=null){
							listView.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									listView.onRefreshComplete();
								}
							}, 1000);
							isRefreshing = false;
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
					if(flag){
						list.clear();
						flag = false;
					}
					System.out.println("4567890"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						int Count = Integer.parseInt(Goodo.getString("Count"));
						if(Count>1){
							JSONArray RArray = Goodo.getJSONArray("R");
							int iSize = RArray.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = RArray.getJSONObject(i);
								Map<String, Object> map = new HashMap<String, Object>();
								if(FID == 0&&CID == 0){
									map.put("ID", jo.get("Receive_ID"));
									map.put("Name", jo.get("From"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("Date"));
									map.put("OuterMailAddr_ID", jo.get("OuterMailAddr_ID"));
									map.put("Case_ID", jo.get("Case_ID"));
									map.put("Item_ID", jo.get("Item_ID"));
								}
								if(FID == 0&&CID == 1){
									map.put("ID", jo.get("Mail_ID"));
									map.put("Name", jo.get("To"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("Date"));
									map.put("OuterMailAddr_ID", jo.get("OuterMailAddr_ID"));
									map.put("Case_ID", jo.get("Case_ID"));
								}
								if(FID == 0&&CID == 2){
									map.put("ID", jo.get("Mail_ID"));
									map.put("Name", jo.get("To"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("SaveDate"));
									map.put("Case_ID", jo.get("Case_ID"));
								}
								if(FID == 0&&CID == 3){
									map.put("ID", jo.get("Mail_ID"));
									map.put("Name", jo.get("To"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("SaveDate"));
									map.put("Case_ID", jo.get("Case_ID"));
									map.put("IsInBox", jo.get("IsInBox"));
									IsInBox = jo.getString("IsInBox");
								}
								if(FID == 1&&CID == 0){
									map.put("ID", jo.get("Receive_ID"));
									map.put("Name", jo.get("From"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("Date"));
									map.put("Case_ID", jo.get("Case_ID"));
									map.put("Item_ID", jo.get("Item_ID"));
								}
								if(FID == 1&&CID == 1){
									map.put("ID", jo.get("Mail_ID"));
									map.put("Name", jo.get("To"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("Date"));
									map.put("SendUser_ID", jo.get("SendUser_ID"));
									map.put("Case_ID", jo.get("Case_ID"));
								}
								if(FID>1&&CID == 0){
									map.put("ID", jo.get("Receive_ID"));
									map.put("Name", jo.get("From"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("Date"));
									map.put("Case_ID", jo.get("Case_ID"));
									map.put("Item_ID", jo.get("Item_ID"));
								}
								if(FID>1&&CID == 1){
									map.put("ID", jo.get("Mail_ID"));
									map.put("Name", jo.get("To"));
									map.put("Subject", jo.get("Subject"));
									map.put("Date", jo.get("Date"));
									map.put("SendUser_ID", jo.get("SendUser_ID"));
									map.put("Case_ID", jo.get("Case_ID"));
								}
								map.put("IsInBox", IsInBox);
								map.put("image", R.drawable.inform_u99);
								map.put("image2", R.drawable.rece_image2);
								map.put("State", "false");
								list.add(map);
							}
						}else{
							JSONObject jo = Goodo.getJSONObject("R");
							Map<String, Object> map = new HashMap<String, Object>();
							if(FID == 0&&CID == 0){
								map.put("ID", jo.get("Receive_ID"));
								map.put("Name", jo.get("From"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("Date"));
								map.put("OuterMailAddr_ID", jo.get("OuterMailAddr_ID"));
								map.put("Item_ID", jo.get("Item_ID"));
							}
							if(FID == 0&&CID == 1){
								map.put("ID", jo.get("Mail_ID"));
								map.put("Name", jo.get("To"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("Date"));
								map.put("OuterMailAddr_ID", jo.get("OuterMailAddr_ID"));
							}
							if(FID == 0&&CID == 2){
								map.put("ID", jo.get("Mail_ID"));
								map.put("Name", jo.get("To"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("SaveDate"));
							}
							if(FID == 0&&CID == 3){
								map.put("ID", jo.get("Mail_ID"));
								map.put("Name", jo.get("To"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("SaveDate"));
								map.put("IsInBox", jo.get("IsInBox"));
								IsInBox = jo.getString("IsInBox");
							}
							if(FID == 1&&CID == 0){
								map.put("ID", jo.get("Receive_ID"));
								map.put("Name", jo.get("From"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("Date"));
								map.put("Item_ID", jo.get("Item_ID"));
							}
							if(FID == 1&&CID == 1){
								map.put("ID", jo.get("Mail_ID"));
								map.put("Name", jo.get("To"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("Date"));
								map.put("SendUser_ID", jo.get("SendUser_ID"));
							}
							if(FID>1&&CID == 0){
								map.put("ID", jo.get("Receive_ID"));
								map.put("Name", jo.get("From"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("Date"));
								map.put("Item_ID", jo.get("Item_ID"));
							}
							if(FID>1&&CID == 1){
								map.put("ID", jo.get("Mail_ID"));
								map.put("Name", jo.get("To"));
								map.put("Subject", jo.get("Subject"));
								map.put("Date", jo.get("Date"));
								map.put("SendUser_ID", jo.get("SendUser_ID"));
							}
							map.put("IsInBox", IsInBox);
							map.put("Case_ID", jo.get("Case_ID"));
							map.put("image", R.drawable.inform_u99);
							map.put("image2", R.drawable.rece_image2);
							map.put("State", "false");
							list.add(map);
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					tv_title.setText(title);
					adapter.notifyDataSetChanged();
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							
							for(int i = 0;i<list.size();i++){
								if(list.get(i).get("State").equals("true")){
									list.get(i).put("State", "false");
								}
							}
							adapter.notifyDataSetChanged();
							int n = Integer.parseInt((String)list.get(position-1).get("ID"));
							Intent it = new Intent();
							if(title.equals("草稿箱")){
								it.putExtra("kind", "草稿箱");
								if(n>0){
									it.setClass(ReceActivity.this, EmailwriteActivity.class);
								}else{
									it.setClass(ReceActivity.this, OuteremailWriteActivity.class);
									it.putExtra("OuterMailAddr_ID", (String)list.get(position-1).get("OuterMailAddr_ID"));
								}
								
							}else{
								it.setClass(ReceActivity.this, MoreReceActivity.class);
							}
							it.putExtra("ID", (String)list.get(position-1).get("ID"));
							if(FID == 0&& CID == 3){
								IsInBox = (String) list.get(position - 1).get("IsInBox");
							}
							it.putExtra("IsInBox", IsInBox);
							if(FID == 0){
								if(n<0){
									it.putExtra("Outer", true);
									it.putExtra("Kind", (String)list.get(position-1).get("OuterMailAddr_ID"));
								}
							}else{
								it.putExtra("Outer", Outer);
								it.putExtra("Kind", Kind);
							}
							startActivityForResult(it, 0);
						}
					});
					
					listView.setOnLongItemClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							for(int i = 0;i<list.size();i++){
								if(list.get(i).get("State").equals("true")){
									list.get(i).put("State", "false");
								}
							}
							list.get(position-1).put("State", "true");
							adapter.notifyDataSetChanged();
							return true;
						}
					});
					
					listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

						@Override
						public void onRefresh(
								PullToRefreshBase<ListView> refreshView) {
							// TODO Auto-generated method stub
							if(!isRefreshing){
								isRefreshing = true;
								if(listView.isHeaderShown()){
									flag = true;
									nPage = 1;
									Url = getUrl();
									sendRequest();
									Toast.makeText(ReceActivity.this, "刷新", Toast.LENGTH_SHORT).show();
								}else if(listView.isFooterShown()){
									nPage++;
									System.out.println("44444444cui"+nPage);
									Url = getUrl();
									sendRequest();
									Toast.makeText(ReceActivity.this, "加载", Toast.LENGTH_SHORT).show();
								}
								
							}else{
								listView.onRefreshComplete();
								isRefreshing = false;
							}
						}
					});
					
					
					break;

				default:
					break;
				}
			}
		};
	}
	
}
