package com.goodo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.goodo.adapter.SelAttachAdapter;
import com.goodo.app.javabean.SendAttachObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SelAttachActivity extends Activity implements OnClickListener{
	private ListView listView;
	private SelAttachAdapter adapter;
	private List<Map<String, Object>> list;
	private SharedPreferences preferences;
	private Set<String> set_AttachName;
	private HashMap<Integer, SendAttachObject> map = new HashMap<Integer, SendAttachObject>();
	
	private ImageView iv_return;
	private ImageView iv_sure;
	private int i = 0;
	
	private ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sel_attach);
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_sure = (ImageView) findViewById(R.id.iv_sure);
		preferences = getSharedPreferences("Attach", MODE_PRIVATE);
		preferences.edit();
		set_AttachName = new HashSet<String>();
		if(preferences.getStringSet("AttachName", null)!=null){
			list = new ArrayList<Map<String,Object>>();
			set_AttachName = preferences.getStringSet("AttachName", null);
			Iterator<String> itName = set_AttachName.iterator();
			
			while (itName.hasNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String[] str = itName.next().split(" /");
				if(str.length > 1){
					String Name = str[0];
					String Url = str[1];
					map.put("AttachName", Name);
					map.put("AttachUrl", Url);
					map.put("State", "");
					list.add(map);
				}
			}
			listView = (ListView) findViewById(R.id.listView);
			adapter = new SelAttachAdapter(list, this);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					SendAttachObject object = new SendAttachObject();
					if(list.get(arg2).get("State").equals("")){
						Integer integer = new Integer(i);
						list.get(arg2).put("State", "¡Ì");
						object.setAttachName((String)(list.get(arg2).get("AttachName")));
						object.setAttachUrl((String)(list.get(arg2).get("AttachUrl")));
						map.put(integer, object);
						i++;
					}else{
						i--;
						Integer integer = new Integer(i);
						list.get(arg2).put("State", "");
						if(map.containsKey(integer)){
							map.remove(integer);
						}
					}
					adapter.notifyDataSetChanged();
				}
			});
			
			
		}
		
		iv_return.setOnClickListener(this);
		iv_sure.setOnClickListener(this);
		
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_sure:
			progressBar.setVisibility(View.VISIBLE);
			Intent it = getIntent();
			it.putExtra("map", map);
			setResult(Activity.RESULT_OK, it);
			finish();
			break;

		default:
			break;
		}
	}

}
