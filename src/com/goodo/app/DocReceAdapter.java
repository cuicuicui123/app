package com.goodo.app;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DocReceAdapter extends BaseAdapter{
	
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	

	public DocReceAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public ImageView image1;
		public TextView title;
		public ImageView image2;
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
			
			convertView = layoutInflater.inflate(R.layout.doc_rece_list, null);
			zujian.image1 = (ImageView) convertView.findViewById(R.id.more_inform_rece_image1);
			zujian.title = (TextView) convertView.findViewById(R.id.more_inform_rece_title);
			zujian.image2 = (ImageView) convertView.findViewById(R.id.more_inform_rece_image2);
			convertView.setTag(zujian);
		}else{
			zujian = (Zujian) convertView.getTag();
		}
		zujian.image1.setBackgroundResource((Integer) data.get(position).get("image1"));
		zujian.title.setText((String)data.get(position).get("OriginalFile"));
		zujian.image2.setBackgroundResource((Integer) data.get(position).get("image2"));
		return convertView;
	}

}
