package com.goodo.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.goodo.adapter.MydocAdapter;
import com.goodo.adapter.SelAttachAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MydocActivity extends Activity implements OnClickListener{
	private ImageView iv_return;
	private ListView listView;
	private SharedPreferences preferences;
	private Set<String> set_AttachName;
	private List<Map<String, Object>> list;
	private MydocAdapter adapter;
	private String file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mydoc);
		init();
	}

	private void init(){
		iv_return = (ImageView) findViewById(R.id.iv_return);
		listView = (ListView) findViewById(R.id.listView);
		
		preferences = getSharedPreferences("Attach", MODE_PRIVATE);
		preferences.edit();
		set_AttachName = new LinkedHashSet<String>();

		if(preferences.getStringSet("AttachName", null)!=null){
			list = new ArrayList<Map<String,Object>>();
			set_AttachName = preferences.getStringSet("AttachName", null);
			Iterator<String> itName = set_AttachName.iterator();
			
			while (itName.hasNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String str_all = itName.next();
				String[] str = str_all.split(" /");
				if(str.length > 1){
					String Name = str[0];
					String Url = str[1];
					map.put("All", str_all);
					map.put("AttachName", Name);
					map.put("AttachUrl", Url);
					map.put("State", "");
					list.add(map);
				}
			}
			adapter = new MydocAdapter(list, this);
			listView.setAdapter(adapter);
			file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String str = (String) list.get(arg2).get("AttachName");
					File f = new File(file+"Download/"+str);
					if(f.exists()){
						String docstr = (String) list.get(arg2).get("AttachUrl");
						String[] strs = docstr.split("\\.");
						String str2 = strs[1];
						MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
						String type = mimeTypeMap.getMimeTypeFromExtension(str2);
							Intent it = new Intent(Intent.ACTION_VIEW);
							it.addCategory("android.intent.category.DEFAULT");
							it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							Uri uri = Uri.fromFile(f);
							it.setDataAndType(uri, type);
							startActivity(it);
						
					}
				}
			});
		}
		
		iv_return.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;

		default:
			break;
		}
	}
}
