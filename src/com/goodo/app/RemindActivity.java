package com.goodo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.goodo.adapter.RemindAdapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;


public class RemindActivity extends Activity implements OnClickListener{
	private SharedPreferences preferences;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	private ListView listView;
	private RemindAdapter adapter;
	private ImageView iv_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remind);
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(this);
		
		//获取提醒数据
		preferences = getSharedPreferences("GrapRemind", MODE_PRIVATE);
		preferences.edit();
		Set<String> set = preferences.getStringSet("remind", new HashSet<String>());
		System.out.println("4567890----"+set.size());
		
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String str = it.next();
			System.out.println("4567890----"+str);
			String[] strchar = str.split(" ");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("information", str);
			map.put("id", strchar[0]);
			map.put("remind", strchar[1]);
			map.put("time", strchar[2]+" "+strchar[3]);
			if(strchar.length>4){
				map.put("Work", strchar[4]);
			}else{
				map.put("Work", "");
			}
			list.add(map);
		}
		
		//listView
		listView = (ListView) findViewById(R.id.listView);
		adapter = new RemindAdapter(list, this);
		listView.setAdapter(adapter);
		
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;

		default:
			break;
		}
	}
}
