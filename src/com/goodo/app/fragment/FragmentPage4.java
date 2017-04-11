package com.goodo.app.fragment;

import com.goodo.app.DocWriteActivity;
import com.goodo.app.Myconfig;
import com.goodo.app.R;
import com.goodo.app.Select2Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentPage4 extends Fragment implements OnClickListener{
	private ImageView iv_user_4;
//	private ImageView iv_doc_add;
	private Select2Activity select2Activity;
	private TextView tv_doc_rece;
	private TextView tv_doc_send;
	
	private Page4receFragment page4receFragment;
	private Page4sendFragment page4sendFragment;
	
	private TextView tv_name;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		
		View view = inflater.inflate(R.layout.fragment_4, container, false);
		select2Activity = (Select2Activity) getActivity();
		
		iv_user_4 = (ImageView) view.findViewById(R.id.iv_user_4);
//		iv_doc_add = (ImageView) view.findViewById(R.id.iv_doc_add);
		tv_doc_rece = (TextView) view.findViewById(R.id.tv_doc_rece);
		tv_doc_send = (TextView) view.findViewById(R.id.tv_doc_send);
		tv_name = (TextView) view.findViewById(R.id.tv_person_4);
		tv_name.setText(Myconfig.UserName);
		
		iv_user_4.setOnClickListener(this);
//		iv_doc_add.setOnClickListener(this);
		tv_doc_rece.setOnClickListener(this);
		tv_doc_send.setOnClickListener(this);
		
		setDefaultfragment();
		
		return view;		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (arg0.getId()) {
		case R.id.iv_user_4:
			select2Activity.xcSlideMenu.switchMenu();
			break;
//		case R.id.iv_doc_add:
//			Intent it = new Intent(FragmentPage4.this.getActivity(),DocWriteActivity.class);
//			startActivity(it);
//			break;
		case R.id.tv_doc_rece:
			tv_doc_rece.setTextColor(0xff3da1d5);
			tv_doc_send.setTextColor(0xffffffff);
			tv_doc_rece.setBackgroundResource(R.drawable.mycorner);
			tv_doc_send.setBackgroundResource(R.drawable.mycorner_blue);
			if(page4sendFragment != null){
				transaction.hide(page4sendFragment);
			}
			transaction.show(page4receFragment);
			break;
		case R.id.tv_doc_send:
			tv_doc_send.setTextColor(0xff3da1d5);
			tv_doc_rece.setTextColor(0xffffffff);
			tv_doc_send.setBackgroundResource(R.drawable.mycorner);
			tv_doc_rece.setBackgroundResource(R.drawable.mycorner_blue);
			if(page4sendFragment == null){
				page4sendFragment = new Page4sendFragment();
				transaction.add(R.id.fl_doc_content, page4sendFragment);
			}
			transaction.hide(page4receFragment);
			transaction.show(page4sendFragment);
			break;
		default:
			break;
		}
		transaction.commit();
	}
	
	private void setDefaultfragment(){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		page4receFragment = new Page4receFragment();
		transaction.add(R.id.fl_doc_content, page4receFragment);
		transaction.commit();
	}


}