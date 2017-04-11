package com.goodo.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.goodo.app.Myconfig;
import com.goodo.app.R;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DataAdapter extends BaseAdapter
{

	private List<Map<String, String>> mDatas;
	private LayoutInflater mInflater;
	private SwipeListView mSwipeListView ;
	private HttpClient httpClient;
	private Handler handler;
	private Boolean IsSenderOrReceiver;
	public static final int SHOW_RESPONSE = 2;
	private Context context;

	public DataAdapter(Context context, List<Map<String, String>> datas , SwipeListView swipeListView , Boolean IsSenderOrReceiver)
	{
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);
		mSwipeListView = swipeListView;
		httpClient = new DefaultHttpClient();
		this.IsSenderOrReceiver = IsSenderOrReceiver;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		convertView = mInflater.inflate(R.layout.swipe_item, null);

		TextView tv_title = (TextView) convertView.findViewById(R.id.id_text);
		TextView tv_from = (TextView) convertView.findViewById(R.id.id_from);
		TextView tv_time = (TextView) convertView.findViewById(R.id.id_time);
		Button del = (Button) convertView.findViewById(R.id.id_remove);
		Button finish = (Button) convertView.findViewById(R.id.id_finish);
		tv_title.setText(mDatas.get(position).get("title"));
		tv_from.setText(mDatas.get(position).get("from"));
		tv_time.setText(mDatas.get(position).get("time"));
		
		del.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sendDelRequest(position);
				receDelRequest(position);
				
			}
		});
		
		finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendFinishRequest(position);
				receFinishRequest(position);
			}
		});
		
		return convertView;
	}
	
	private void sendDelRequest(final int position){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String Plan_ID = mDatas.get(position).get("Plan_ID");
				String PlanType = mDatas.get(position).get("PlanType");
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_Delete");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("Plan_ID", Plan_ID);
				builder.appendQueryParameter("PlanType", PlanType);
				String Url = builder.toString();
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
	
	private void receDelRequest(final int position){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Myconfig.SHOW_RESPONSE:
					String response = (String) msg.obj;
					JSONObject jsonObject;
					String EID = "";
					try {
						jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						EID = Goodo.getString("EID");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(EID.equals("0")){
						mDatas.remove(position);
						notifyDataSetChanged();
						Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
					}else if(EID.equals("3")){
						Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
					}
					mSwipeListView.closeOpenedItems();
					break;

				default:
					break;
				}
			}
		};
	}
	
	private void sendFinishRequest(final int position){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String Plan_ID = mDatas.get(position).get("Plan_ID");
				Uri.Builder builder = new Uri.Builder();
				builder.encodedPath(Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/Plan_Finish");
				builder.appendQueryParameter("SessionID", Myconfig.SessionID);
				builder.appendQueryParameter("User_ID", Myconfig.User_ID);
				builder.appendQueryParameter("UserName", Myconfig.UserName);
				builder.appendQueryParameter("Plan_ID", Plan_ID);
				builder.appendQueryParameter("IsSenderOrReceiver", IsSenderOrReceiver+"");
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
	
	private void receFinishRequest(final int position){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					JSONObject jsonObject;
					String EID = "";
					try {
						jsonObject = new JSONObject(response);
						JSONObject Goodo = jsonObject.getJSONObject("Goodo");
						EID = Goodo.getString("EID");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(EID.equals("0")){
						mDatas.remove(position);
						notifyDataSetChanged();
						Toast.makeText(context, "完成成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "完成失败", Toast.LENGTH_SHORT).show();
					}
					mSwipeListView.closeOpenedItems();
					break;

				default:
					break;
				}
			}
		};
	}
	
}
