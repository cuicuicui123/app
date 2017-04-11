package com.goodo.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.goodo.app.NotificationService;
import com.goodo.app.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MydocAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private Set<String> set = new HashSet<String>();
	
	

	public MydocAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		preferences = context.getSharedPreferences("Attach", context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	
	public final class Zujian{
		public TextView Name;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		Zujian zujian = null;
		if(arg1 == null){
			zujian = new Zujian();
			arg1 = layoutInflater.inflate(R.layout.list_mydoc, null);
			zujian.Name = (TextView) arg1.findViewById(R.id.tv_name);
			
			TextView tv_del = (TextView) arg1.findViewById(R.id.delete);
			tv_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					set = preferences.getStringSet("AttachName", new HashSet<String>());
					set.remove(data.get(arg0).get("All"));
					editor.putStringSet("AttachName", set);
					editor.commit();
					
					data.remove(arg0);
					notifyDataSetChanged();
				}
			});
			arg1.setTag(zujian);
		}else{
			zujian = (Zujian) arg1.getTag();
		}
		zujian.Name.setText((String)data.get(arg0).get("AttachName"));
		return arg1;
	}

}

