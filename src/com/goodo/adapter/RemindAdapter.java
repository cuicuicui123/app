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

public class RemindAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private Intent intent;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private Set<String> set = new HashSet<String>();
	
	

	public RemindAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(context.getApplicationContext(),NotificationService.class);
		preferences = context.getSharedPreferences("GrapRemind", context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	
	public final class Zujian{
		public TextView Time;
		public TextView Remind;
		public TextView Content;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Zujian zujian = null;
		if(arg1 == null){
			zujian = new Zujian();
			arg1 = layoutInflater.inflate(R.layout.list_remind2, null);
			zujian.Time = (TextView) arg1.findViewById(R.id.tv_Time);
			zujian.Remind = (TextView) arg1.findViewById(R.id.tv_Remind);
			zujian.Content = (TextView) arg1.findViewById(R.id.tv_Work);
			
			TextView tv_del = (TextView) arg1.findViewById(R.id.delete);
			tv_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String str = (String) data.get(arg0).get("id");
					int id = Integer.parseInt(str);
					pendingIntent = PendingIntent.getService(context.getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
					alarmManager.cancel(pendingIntent);
					
					set = preferences.getStringSet("remind", new HashSet<String>());
					set.remove((String)data.get(arg0).get("information"));
					editor.putStringSet("remind", set);
					editor.commit();
					
					data.remove(arg0);
					notifyDataSetChanged();
				}
			});
			arg1.setTag(zujian);
		}else{
			zujian = (Zujian) arg1.getTag();
		}
		zujian.Time.setText((String)data.get(arg0).get("time"));
		zujian.Content.setText((String)data.get(arg0).get("Work"));
		zujian.Remind.setText((String)data.get(arg0).get("remind"));
		return arg1;
	}

}

