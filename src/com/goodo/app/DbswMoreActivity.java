package com.goodo.app;



import com.goodo.app.fragment.MydbFragment;
import com.goodo.app.fragment.YfdbFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DbswMoreActivity extends Activity implements OnClickListener{
	
	private TextView tv_1;
	private TextView tv_2;
	
	private YfdbFragment yfdbFragment;
	private MydbFragment mydbFragment;
	
	private ImageView iv_return;
	private Button btn_add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dbsw_more);
		
		iv_return = (ImageView) findViewById(R.id.iv_return);
		btn_add = (Button) findViewById(R.id.btn_sure);
		iv_return.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		
		tv_1 = (TextView) findViewById(R.id.id_tv1);
		tv_1.setTextColor(0xff3da1d5);
		tv_1.setBackgroundResource(R.drawable.mycorner);
		tv_2 = (TextView) findViewById(R.id.id_tv2);
		tv_1.setOnClickListener(this);
		tv_2.setOnClickListener(this);
		if(yfdbFragment == null&&mydbFragment == null){
			setDefaultFragment();
		}
	}

	private void setDefaultFragment() {
		// TODO Auto-generated method stub
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		mydbFragment = new MydbFragment();
		transaction.add(R.id.id_content, mydbFragment);
		transaction.commit();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		switch (arg0.getId()) {
		case R.id.id_tv1:
			if(yfdbFragment != null){
				transaction.hide(yfdbFragment);
				transaction.show(mydbFragment);
			}
			tv_1.setTextColor(0xff3da1d5);
			tv_1.setBackgroundResource(R.drawable.mycorner);
			tv_2.setTextColor(0xffffffff);
			tv_2.setBackgroundResource(R.drawable.mycorner_blue);
			transaction.commit();
			break;
		case R.id.id_tv2:
			if(yfdbFragment == null){
				yfdbFragment = new YfdbFragment();
				transaction.add(R.id.id_content, yfdbFragment);
			}
			transaction.hide(mydbFragment);
			transaction.show(yfdbFragment);
			tv_1.setTextColor(0xffffffff);
			tv_1.setBackgroundResource(R.drawable.mycorner_blue);
			tv_2.setTextColor(0xff3da1d5);
			tv_2.setBackgroundResource(R.drawable.mycorner);
			transaction.commit();
			break;
		case R.id.iv_return:
			finish();
			break;
		case R.id.btn_sure:
			Intent it = new Intent(DbswMoreActivity.this,DbswAddActivity.class);
			it.putExtra("Kind", "Ìí¼Ó");
			startActivity(it);
			break;
		}
		
	}
}
