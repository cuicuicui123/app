package com.goodo.adapter;

import java.util.List;
import java.util.Map;

import com.goodo.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelAttachAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	

	public SelAttachAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public TextView AttachName;
		public TextView State;
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
			
			arg1 = layoutInflater.inflate(R.layout.list_selattach, null);
			zujian.AttachName = (TextView) arg1.findViewById(R.id.tv_AttachName);
			zujian.State = (TextView) arg1.findViewById(R.id.tv_State);
			arg1.setTag(zujian);
		}else{
			zujian = (Zujian) arg1.getTag();
		}
		zujian.AttachName.setText((String)data.get(arg0).get("AttachName"));
		zujian.State.setText((String)data.get(arg0).get("State"));
		return arg1;
	}

}
