package com.goodo.app;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalAdapter extends BaseAdapter{
	
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	

	public CalAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public TextView CreateDate;
		public TextView Work;
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Zujian zujian = null;
		if(arg1 == null){
			zujian = new Zujian();
			
			arg1 = layoutInflater.inflate(R.layout.cal_list, null);
			zujian.CreateDate = (TextView) arg1.findViewById(R.id.cal_list_time);
			zujian.Work = (TextView) arg1.findViewById(R.id.cal_list_content);
			arg1.setTag(zujian);
		}else{
			zujian = (Zujian) arg1.getTag();
		}
		zujian.CreateDate.setText((String) data.get(arg0).get("CreateDate"));
		zujian.Work.setText((String)data.get(arg0).get("Work"));
		return arg1;
	}

}
