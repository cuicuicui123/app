package com.goodo.app.fragment;

import com.goodo.app.Myconfig;
import com.goodo.app.NoticewriteActivity;
import com.goodo.app.R;
import com.goodo.app.Select2Activity;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentPage5 extends Fragment implements OnClickListener{
	private ImageView iv_user_5;
	private ImageView iv_notice_add;
	private Select2Activity select2Activity;
	private TextView tv_notice_rece;
	private TextView tv_notice_send;
	
	private Page5receFragment page5receFragment;
	private Page5sendFragment page5sendFragment;
	
	private View view;
	private TextView tv_name;
	
	
	

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		
		if(view == null){
			view = inflater.inflate(R.layout.fragment_5, container, false);
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if(parent!=null){
			parent.removeView(view);
		}
		select2Activity = (Select2Activity) getActivity();
		
		iv_user_5 = (ImageView) view.findViewById(R.id.iv_user_5);
		iv_notice_add = (ImageView) view.findViewById(R.id.iv_notice_add);
		tv_notice_rece = (TextView) view.findViewById(R.id.tv_notice_rece);
		tv_notice_send = (TextView) view.findViewById(R.id.tv_notice_send);
		tv_name = (TextView) view.findViewById(R.id.tv_person_5);
		tv_name.setText(Myconfig.UserName);
		
		iv_user_5.setOnClickListener(this);
		iv_notice_add.setOnClickListener(this);
		tv_notice_rece.setOnClickListener(this);
		tv_notice_send.setOnClickListener(this);
		
		setDefaultfragment();
		
		return view;		
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0&&resultCode == Activity.RESULT_OK){
			if(data.getStringExtra("flag").equals("1")){
				if(page5sendFragment!=null){
					page5sendFragment.sendRequest();
				}
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (arg0.getId()) {
		case R.id.iv_user_5:
			select2Activity.xcSlideMenu.switchMenu();
			break;
		case R.id.iv_notice_add:
			Intent it = new Intent(getActivity(),NoticewriteActivity.class);
			startActivityForResult(it, 0);
			break;
		case R.id.tv_notice_rece:
			tv_notice_rece.setTextColor(0xff3da1d5);
			tv_notice_send.setTextColor(0xffffffff);
			tv_notice_rece.setBackgroundResource(R.drawable.mycorner);
			tv_notice_send.setBackgroundResource(R.drawable.mycorner_blue);
			if(page5sendFragment != null){
				transaction.hide(page5sendFragment);
			}
			
			transaction.show(page5receFragment);
			break;
		case R.id.tv_notice_send:
			tv_notice_send.setTextColor(0xff3da1d5);
			tv_notice_rece.setTextColor(0xffffffff);
			tv_notice_send.setBackgroundResource(R.drawable.mycorner);
			tv_notice_rece.setBackgroundResource(R.drawable.mycorner_blue);
			if(page5sendFragment == null){
				page5sendFragment = new Page5sendFragment();
				transaction.add(R.id.fl_notice_content, page5sendFragment);
			}
			transaction.hide(page5receFragment);
			transaction.show(page5sendFragment);
			break;
		default:
			break;
		}
		transaction.commit();
	}
	
	public void setDefaultfragment(){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		page5receFragment = new Page5receFragment();
		transaction.add(R.id.fl_notice_content, page5receFragment);
		transaction.commit();
	}
	
	
	
	
}