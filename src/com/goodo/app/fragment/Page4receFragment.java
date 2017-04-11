package com.goodo.app.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.goodo.adapter.DocAdapter;
import com.goodo.app.DocRece2Activity;
import com.goodo.app.InformAdapter;
import com.goodo.app.Myconfig;
import com.goodo.app.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Page4receFragment extends Fragment{
	private PullToRefreshListView listview_page4_rece;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private HttpClient httpClient;
	public static final int SHOW_RESPONSE = 1;
	private Handler handler;
	DocAdapter adapter;
	private boolean isRefreshing = false;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.listview_page4_rece, container, false);
		
		listview_page4_rece = (PullToRefreshListView) view.findViewById(R.id.listview_page4_rece);
		adapter = new DocAdapter(list, getActivity());					
		listview_page4_rece.setAdapter(adapter);					
		
		httpClient = new DefaultHttpClient();
		sendRequest();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
					System.out.println("00000000"+response);
					list.clear();
					StringReader read = new StringReader(response);
					InputSource source = new InputSource(read);
					SAXBuilder sb = new SAXBuilder();
					try {
						Document doc = sb.build(source);
						Element root = doc.getRootElement();
						
						List jiedian = root.getChildren();
						Element et = null;
						System.out.println("---------");
						for(int i = 0;i<jiedian.size();i++){
							et = (Element) jiedian.get(i);
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("Title", et.getAttributeValue("Title"));
							map.put("UserName", et.getAttributeValue("UserName"));
							map.put("ReceiveDate", et.getAttributeValue("ReceiveDate"));
							map.put("红点", R.drawable.inform_u99);
							map.put("箭头", R.drawable.jiantou);
							map.put("Receive_ID", et.getAttributeValue("Receive_ID"));
							list.add(map);
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
					listview_page4_rece.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
								//跳转到详情
								Intent it = new Intent();
								it.putExtra("extra", (String)(list.get(arg2-1).get("Receive_ID")));
								it.setClass(Page4receFragment.this.getActivity(), DocRece2Activity.class);
								startActivity(it);
						}
					});
					
					listview_page4_rece.setOnRefreshListener(new OnRefreshListener<ListView>() {

						@Override
						public void onRefresh(
								PullToRefreshBase<ListView> refreshView) {
							// TODO Auto-generated method stub
							if(!isRefreshing){
								isRefreshing = true;
								if(listview_page4_rece.isHeaderShown()){
									sendRequest();
									Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
								}
							}else{
								listview_page4_rece.onRefreshComplete();
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
		return view;
	}
	private void sendRequest(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
					Uri.Builder builder = new Uri.Builder();
					builder.encodedPath(Myconfig.IP+"/EduPlate/jfyPublicDocument/IPublicDocument.asmx/ReceiveDocument_GetXmlByUser");
					builder.appendQueryParameter("User_ID", Myconfig.User_ID);
					String Url = builder.toString();
					HttpGet get = new HttpGet(Url);
					try {
					HttpResponse httpResponse = httpClient.execute(get);
					HttpEntity entity = httpResponse.getEntity();
					
					if(entity!=null){
						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						StringBuilder response = new StringBuilder();
						String line;
						while((line=reader.readLine())!=null){
							response.append(line);
						}
						if(listview_page4_rece!=null){
							listview_page4_rece.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									listview_page4_rece.onRefreshComplete();
								}
							}, 1000);
							isRefreshing = false;
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
	

	
}
