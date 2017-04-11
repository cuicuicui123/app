package com.goodo.app;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OtherInformAdapter extends BaseAdapter{

	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	
	
	public OtherInformAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public TextView SName;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Zujian zujian = null;
		if(convertView == null){
			zujian = new Zujian();
			
			convertView = layoutInflater.inflate(R.layout.other_inform_list, null);
			zujian.SName = (TextView) convertView.findViewById(R.id.other_inform_list_kind);
			convertView.setTag(zujian);
		}else{
			zujian = (Zujian) convertView.getTag();
		}
		zujian.SName.setText((String)data.get(position).get("SName"));
		return convertView;
	}

}
