package com.goodo.adapter;

import java.util.List;
import java.util.Map;

import com.goodo.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AddgrapAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	

	public AddgrapAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public TextView date;
		public TextView time;
		public ToggleButton toggleButton;
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
			
			arg1 = layoutInflater.inflate(R.layout.add_grap_list, null);
			zujian.date = (TextView) arg1.findViewById(R.id.add_grap_dialog_date);
			zujian.time = (TextView) arg1.findViewById(R.id.add_grap_dialog_time);
			zujian.toggleButton = (ToggleButton) arg1.findViewById(R.id.add_grap_dialog_TogBtn);
			if(data.get(arg0).get("State").equals("true")){
				zujian.toggleButton.setChecked(true);
			}else{
				zujian.toggleButton.setChecked(false);
			}
			zujian.toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton tog, boolean arg1) {
					// TODO Auto-generated method stub
					
					if(arg1){
						data.get(arg0).put("State", "true");
					}else{
						data.get(arg0).put("State", "false");
					}
				}
			});
			
			
			arg1.setTag(zujian);
		}else{
			zujian = (Zujian) arg1.getTag();
		}
		zujian.date.setText((String)data.get(arg0).get("日期"));
		zujian.time.setText((String)data.get(arg0).get("时间"));
		return arg1;
	}

}
