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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReceAdapter extends BaseAdapter{
	
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	private List<Map<String, String>> list;
	private HttpClient httpClient;
	private Handler handler;
	private String Url;
	public static final int CASE_RESPONSE = 2;
	public static final int SET_RESPONSE = 3;
	public static final int DEL_RESPONSE = 4;
	private List<Map<String, String>> list_case = new ArrayList<Map<String,String>>();
	private int n;
	private String ID;
	
	public ReceAdapter(List<Map<String, Object>> data, Context context,List<Map<String, String>> list) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.list = list;
		httpClient = new DefaultHttpClient();
		Url = getCaseUrl();
		sendRequest(Url, CASE_RESPONSE);
		receRequest();
	}
	
	public final class Zujian{
		public ImageView image;
		public TextView title;
		public TextView content;
		public TextView data;
		public LinearLayout ll;
		public ImageView iv_move;
		public ImageView iv_biaoji;
		public ImageView iv_del;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Zujian zujian = null;
		
		if(convertView == null){
			zujian = new Zujian();
			convertView = layoutInflater.inflate(R.layout.rece_list, null);
			zujian.image = (ImageView) convertView.findViewById(R.id.inform_iv_point);
			zujian.title = (TextView) convertView.findViewById(R.id.inform_tv_title);
			zujian.content = (TextView) convertView.findViewById(R.id.inform_tv_content);
			zujian.data = (TextView) convertView.findViewById(R.id.inform_tv_data);
			zujian.ll = (LinearLayout) convertView.findViewById(R.id.ll_rece_list);
			zujian.iv_move = (ImageView) convertView.findViewById(R.id.rece_list_move);//移动
			zujian.iv_biaoji = (ImageView) convertView.findViewById(R.id.rece_list_biaoji);
			zujian.iv_del = (ImageView) convertView.findViewById(R.id.rece_list_del);
			convertView.setTag(zujian);
		}else{
			zujian = (Zujian) convertView.getTag();
		}
		zujian.title.setText((String)data.get(position).get("Name"));
		zujian.content.setText((String)data.get(position).get("Subject"));
		zujian.data.setText((String)data.get(position).get("Date"));
		String str = (String) data.get(position).get("Case_ID");
		if(!str.equals("0")){
			zujian.iv_biaoji.setImageResource(R.drawable.box_02_yellow);
		}
		if((data.get(position).get("State")).equals("true")){
			zujian.ll.setVisibility(View.VISIBLE);
			zujian.iv_move.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String[] group = new String[list.size()-1];
					for(int i = 0;i<list.size()-1;i++){
						group[i] = list.get(i).get("Name");
					}
					new AlertDialog.Builder(context)
					.setTitle("移动到")
					.setItems(group, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String ID = list.get(arg1).get("ReceiveClassify_ID");
							Url = getUrl(ID,(String)data.get(position).get("ID"));
							sendRequest(Url,Myconfig.SHOW_RESPONSE);
						}
					}).show();
				}
			});
			zujian.iv_biaoji.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String[] case_group = new String[list_case.size()];
					for(int i = 0;i<list_case.size();i++){
						case_group[i] = list_case.get(i).get("CaseName");
					}
					new AlertDialog.Builder(context)
					.setTitle("选择项目")
					.setItems(case_group, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							ID = list_case.get(arg1).get("Case_ID"); 
							String Name = list_case.get(arg1).get("CaseName");
							String EMail_ID = (String) data.get(position).get("ID");
							String Item_ID = (String) data.get(position).get("Item_ID");
							Url = getsetCaseUrl(EMail_ID, Item_ID, ID, Name);
							sendRequest(Url, SET_RESPONSE);
							n = position;
						}
					}).show();
				}
			});
			zujian.iv_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new AlertDialog.Builder(context)
					.setTitle("删除后无法恢复。确定?")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String EMail_ID = (String) data.get(position).get("ID");
							String IsInBox = (String) data.get(position).get("IsInBox");
							Url = getDelUrl(EMail_ID, IsInBox);
							sendRequest(Url, DEL_RESPONSE);
							n = position;
						}
					})
					.setNegativeButton("取消", null).show();
				}
			});
		}else{
			zujian.ll.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private void sendRequest(final String Url,final int n){
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
						Message message = new Message();
						message.what = n;
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
					System.out.println("555555555"+response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(context, "移动成功", Toast.LENGTH_SHORT).show();
							data.remove(n);
							notifyDataSetChanged();
						}else{
							Toast.makeText(context, "移动失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case CASE_RESPONSE:
					String caseresponse = (String) msg.obj;
					System.out.println("5555555555"+caseresponse);
					try {
						JSONObject jsonObject = new JSONObject(caseresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String Rstr = Goodo.getString("R");
						char[] Rstrchar = Rstr.toCharArray();
						if(Rstrchar[0]=='['){
							JSONArray array = Goodo.getJSONArray("R");
							int iSize = array.length();
							for(int i = 0;i<iSize;i++){
								JSONObject jo = array.getJSONObject(i);
								Map<String, String> map = new HashMap<String, String>();
								map.put("Case_ID", jo.getString("Case_ID"));
								map.put("CaseName", jo.getString("CaseName"));
								list_case.add(map);
							}
						}else{
							JSONObject jo = Goodo.getJSONObject("R");
							Map<String, String> map = new HashMap<String, String>();
							map.put("Case_ID", jo.getString("Case_ID"));
							map.put("CaseName", jo.getString("CaseName"));
							list_case.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case SET_RESPONSE:
					String setresponse = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(setresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(context, "标记成功", Toast.LENGTH_SHORT).show();
							data.get(n).put("Case_ID", ID);
							notifyDataSetChanged();
						}else{
							Toast.makeText(context, "标记失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case DEL_RESPONSE:
					String delresponse = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(delresponse);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						String EID = Goodo.getString("EID");
						if(EID.equals("0")){
							Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
							data.remove(n);
							notifyDataSetChanged();
						}else{
							Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
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
	
	private String getUrl(String ID,String Receive_ID){
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/Receive_ClassifyEdit");
		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
		builder.appendQueryParameter("Receive_ID", Receive_ID);
		builder.appendQueryParameter("ReceiveClassify_ID", ID);
		String Url = builder.toString();
		return Url;
	}
	
	private String getCaseUrl(){
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGCase/InterfaceJson.asmx/Case_ListGet");
		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
		String Url = builder.toString();
		return Url;
	}
	
	private String getsetCaseUrl(final String EMail_ID,final String Item_ID,final String Case_ID,final String CaseName){
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_CaseSet");
		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
		builder.appendQueryParameter("EMail_ID", EMail_ID);
		builder.appendQueryParameter("IsInBox", "1");
		builder.appendQueryParameter("Case_ID", Case_ID);
		builder.appendQueryParameter("CaseName", CaseName);
		builder.appendQueryParameter("Item_ID", Item_ID);
		
		String Url = builder.toString();
		return Url;
	}
	
	private String getDelUrl(String EMail_ID,String IsInBox){
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(Myconfig.IP+"/EduPlate/MSGMail/InterfaceJson.asmx/EMail_Delete");
		builder.appendQueryParameter("SessionID", Myconfig.SessionID);
		builder.appendQueryParameter("User_ID", Myconfig.User_ID);
		builder.appendQueryParameter("EMail_ID", EMail_ID);
		builder.appendQueryParameter("IsInBox", IsInBox);
		builder.appendQueryParameter("IsDel", "2");
		String Url = builder.toString();
		return Url;
	}

}
