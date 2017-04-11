package com.goodo.app;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{
	
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	

	public MyAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public ImageView image;
		public TextView title;
		public TextView content;
		public TextView time;
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
			
			convertView = layoutInflater.inflate(R.layout.list, null);
			zujian.image = (ImageView) convertView.findViewById(R.id.image);
			zujian.title = (TextView) convertView.findViewById(R.id.title);
			zujian.content = (TextView) convertView.findViewById(R.id.content);
			zujian.time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(zujian);
		}else{
			zujian = (Zujian) convertView.getTag();
		}
		if(data.get(position).get("Pic")!=null){
			zujian.image.setImageBitmap((Bitmap) data.get(position).get("Pic"));
			zujian.image.setVisibility(View.VISIBLE);
		}else{
			zujian.image.setVisibility(View.GONE);
		}
		zujian.title.setText("��"+data.get(position).get("SubjectName")+"��"+(String)data.get(position).get("ContentTitle"));
		zujian.content.setText("������:"+(String)data.get(position).get("UserName"));
		zujian.time.setText((String)data.get(position).get("SubmitDate"));
		return convertView;
	}

}
