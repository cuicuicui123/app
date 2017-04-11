package com.goodo.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodo.adapter.AddgrapAdapter.Zujian;
import com.goodo.app.R;

public class NoticeAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	

	public NoticeAdapter(List<Map<String, Object>> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public final class Zujian{
		public TextView title;
		public TextView time;
		public ImageView point;
		public ImageView jiantou;
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
			
			arg1 = layoutInflater.inflate(R.layout.list_notice_rece, null);
			zujian.title = (TextView) arg1.findViewById(R.id.tv_notice_rece_title);
			zujian.time = (TextView) arg1.findViewById(R.id.tv_notice_rece_time);
			zujian.point = (ImageView) arg1.findViewById(R.id.iv_notice_rece_point);
			zujian.jiantou = (ImageView) arg1.findViewById(R.id.iv_notice_rece_jiantou);
			arg1.setTag(zujian);
		}else{
			zujian = (Zujian) arg1.getTag();
		}
		zujian.title.setText((String)data.get(arg0).get("NoticeTitle"));
		zujian.time.setText((String)data.get(arg0).get("SubmitDate"));
//		zujian.point.setBackgroundResource((Integer) data.get(arg0).get("ºìµã"));
		zujian.jiantou.setBackgroundResource((Integer) data.get(arg0).get("¼ýÍ·"));
		return arg1;
	}
}
