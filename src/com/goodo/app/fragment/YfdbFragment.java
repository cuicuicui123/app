package com.goodo.app.fragment;

import com.goodo.app.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class YfdbFragment extends Fragment implements OnClickListener{
	private TextView anren;
	private TextView anshi;
	private TextView xian1;
	private TextView xian2;
	
	private YfanrenFragment yfanrenFragment;
	private YfanshiFragment yfanshiFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.dbsw_yfdb_fragment, container, false);
		
		anren = (TextView) view.findViewById(R.id.id_anren);
		anshi = (TextView) view.findViewById(R.id.id_anshi);
		xian1 = (TextView) view.findViewById(R.id.id_xian1);
		xian2 = (TextView) view.findViewById(R.id.id_xian2);
		
		anren.setOnClickListener(this);
		anshi.setOnClickListener(this);
		
		if(yfanrenFragment == null&&yfanshiFragment == null){
			setDefaultFragment();
		}
		return view;
	}

	private void setDefaultFragment() {
		// TODO Auto-generated method stub
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		yfanrenFragment = new YfanrenFragment();
		transaction.add(R.id.content2, yfanrenFragment);
		transaction.commit();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (arg0.getId()) {
		case R.id.id_anren:
			if(yfanshiFragment != null){
				transaction.hide(yfanshiFragment);
				transaction.show(yfanrenFragment);
			}
			xian1.setBackgroundColor(0xff3da1d5);
			xian2.setBackgroundColor(0xffffffff);
			transaction.commit();
			break;

		case R.id.id_anshi:
			if(yfanshiFragment == null){
				yfanshiFragment = new YfanshiFragment();
				transaction.add(R.id.content2, yfanshiFragment);
			}
			transaction.hide(yfanrenFragment);
			transaction.show(yfanshiFragment);
			
			xian1.setBackgroundColor(0xffffffff);
			xian2.setBackgroundColor(0xff3da1d5);
			transaction.commit();
			break;
		}
	}
	
}
