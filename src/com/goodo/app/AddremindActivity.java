package com.goodo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.goodo.adapter.AddRemindAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddremindActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private ImageView iv_sure;
	private ListView listView;
	private String[] date = {"提前0分钟","提前5分钟","提前10分钟","提前15分钟","提前20分钟","提前30分钟","提前45分钟","提前1小时","提前2小时","提前5小时","提前12小时"};
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	
	private HashMap<Integer,String> map = new HashMap<Integer, String>();  
	
	private AddRemindAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addremind);
		for(int i = 0;i<date.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", date[i]);
			map.put("state", "");
			list.add(map);
		}
		
		
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_sure = (ImageView) findViewById(R.id.iv_sure);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new AddRemindAdapter(list, this);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Integer i = new Integer(arg2);
				if(list.get(arg2).get("state").equals("")){
					list.get(arg2).put("state", "√");
					map.put(i, date[arg2]);
					adapter.notifyDataSetChanged();
				}else{
					list.get(arg2).put("state", "");
					adapter.notifyDataSetChanged();
					map.remove(i);
				}
			}
		});
		
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
			Intent it = getIntent();
			it.putExtra("extra", map);
			setResult(Activity.RESULT_OK, it);
			finish();
			break;
		default:
			break;
		}
	}

}
