package com.goodo.app;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InformAdapter extends BaseAdapter{
	
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	

	public InformAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public ImageView image;
		public TextView title;
		public TextView content;
		public TextView date;
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
		Zujian zujian = null;
		
		if(convertView == null){
			zujian = new Zujian();
			
			convertView = layoutInflater.inflate(R.layout.inform_list, null);
			zujian.image = (ImageView) convertView.findViewById(R.id.inform_iv_point);
			zujian.title = (TextView) convertView.findViewById(R.id.inform_tv_title);
			zujian.content = (TextView) convertView.findViewById(R.id.inform_tv_content);
			zujian.date = (TextView) convertView.findViewById(R.id.inform_tv_data);
			convertView.setTag(zujian);
		}else{
			zujian = (Zujian) convertView.getTag();
		}
//		zujian.image.setBackgroundResource((Integer) data.get(position).get("image"));
		zujian.title.setText("°æ"+data.get(position).get("SubjectName")+"°ø"+(String)data.get(position).get("title"));
		zujian.content.setText("∑¢≤º»À£∫"+(String)data.get(position).get("UserName"));
		zujian.date.setText((String)data.get(position).get("date"));
		
		
		return convertView;
	}

}
