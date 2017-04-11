package com.goodo.app;



import com.goodo.app.fragment.FragmentPage1;
import com.goodo.app.fragment.FragmentPage2;
import com.goodo.app.fragment.FragmentPage3;
import com.goodo.app.fragment.FragmentPage4;
import com.goodo.app.fragment.FragmentPage5;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Select2Activity extends Activity implements OnClickListener{	
	
	public XCSlideMenu xcSlideMenu;
	private RelativeLayout rl_inform;
	private RelativeLayout rl_richeng;
	private RelativeLayout rl_email;
	private RelativeLayout rl_doc;
	private RelativeLayout rl_notice;
	
	private FragmentPage1 page1;
	private FragmentPage2 page2;
	private FragmentPage3 page3;
	private FragmentPage4 page4;
	private FragmentPage5 page5;
	
	
	private ImageView iv_inform;
	private ImageView iv_richeng;
	private ImageView iv_email;
	private ImageView iv_doc;
	private ImageView iv_notice;
	private ImageView iv_user2;
	private TextView tv_user;
	private TextView tv_gohome;
	private TextView tv_remind;
	private TextView tv_doc;

		
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select2);
        xcSlideMenu = (XCSlideMenu) findViewById(R.id.slideMenu);
        
        rl_inform = (RelativeLayout) findViewById(R.id.rl_inform);
        rl_richeng = (RelativeLayout) findViewById(R.id.rl_richeng);
        rl_email = (RelativeLayout) findViewById(R.id.rl_email);
        rl_doc = (RelativeLayout) findViewById(R.id.rl_doc);
        rl_notice = (RelativeLayout) findViewById(R.id.rl_notice);
        iv_inform = (ImageView) findViewById(R.id.iv_inform);
        iv_richeng = (ImageView) findViewById(R.id.iv_richeng);
        iv_email = (ImageView) findViewById(R.id.iv_email);
        iv_doc = (ImageView) findViewById(R.id.iv_doc);
        iv_notice = (ImageView) findViewById(R.id.iv_notice);
        iv_inform.setImageResource(R.drawable.yjgg_on);
        iv_user2 = (ImageView) findViewById(R.id.iv_user2);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_gohome = (TextView) findViewById(R.id.menu1);
        tv_remind = (TextView) findViewById(R.id.menu2);
        tv_doc = (TextView) findViewById(R.id.menu3);
        
        tv_user.setText(Myconfig.UserName);
        
        
        rl_inform.setOnClickListener(this);
        rl_richeng.setOnClickListener(this);
        rl_email.setOnClickListener(this);
        rl_doc.setOnClickListener(this);
        rl_notice.setOnClickListener(this);
        iv_user2.setOnClickListener(this);
        tv_gohome.setOnClickListener(this);
        tv_remind.setOnClickListener(this);
        tv_doc.setOnClickListener(this);
        
        setDefaultfragment();

    }



	@Override
	public void onClick(View arg0) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (arg0.getId()) {
		case R.id.rl_inform:
			transaction.show(page1);
			if(page2!=null){transaction.hide(page2);}
			if(page3!=null){transaction.hide(page3);}
			if(page4!=null){transaction.hide(page4);}
			if(page5!=null){transaction.hide(page5);}
			iv_inform.setImageResource(R.drawable.yjgg_on);
			iv_richeng.setImageResource(R.drawable.rc);
			iv_email.setImageResource(R.drawable.sfwj);
			iv_doc.setImageResource(R.drawable.gwlz);
			iv_notice.setImageResource(R.drawable.notice);
			break;
		case R.id.rl_richeng:
			if(page2 == null){
				page2 = new FragmentPage2();
				transaction.add(R.id.realtabcontent, page2);
			}
			transaction.show(page2);
			if(page1!=null){transaction.hide(page1);}
			if(page3!=null){transaction.hide(page3);}
			if(page4!=null){transaction.hide(page4);}
			if(page5!=null){transaction.hide(page5);}
			iv_inform.setImageResource(R.drawable.yjgg);
			iv_richeng.setImageResource(R.drawable.rc_on);
			iv_email.setImageResource(R.drawable.sfwj);
			iv_doc.setImageResource(R.drawable.gwlz);
			iv_notice.setImageResource(R.drawable.notice);
			break;
		case R.id.rl_email:
			if(page3 == null){
				page3 = new FragmentPage3();
				transaction.add(R.id.realtabcontent, page3);
			}
			transaction.show(page3);
			if(page1!=null){transaction.hide(page1);}
			if(page2!=null){transaction.hide(page2);}
			if(page4!=null){transaction.hide(page4);}
			if(page5!=null){transaction.hide(page5);}
			iv_inform.setImageResource(R.drawable.yjgg);
			iv_richeng.setImageResource(R.drawable.rc);
			iv_email.setImageResource(R.drawable.sfwj_on);
			iv_doc.setImageResource(R.drawable.gwlz);
			iv_notice.setImageResource(R.drawable.notice);
			break;
		case R.id.rl_doc:
			if(page4 == null){
				page4 = new FragmentPage4();
				transaction.add(R.id.realtabcontent, page4);
			}
			transaction.show(page4);
			if(page1!=null){transaction.hide(page1);}
			if(page3!=null){transaction.hide(page3);}
			if(page2!=null){transaction.hide(page2);}
			if(page5!=null){transaction.hide(page5);}
			iv_inform.setImageResource(R.drawable.yjgg);
			iv_richeng.setImageResource(R.drawable.rc);
			iv_email.setImageResource(R.drawable.sfwj);
			iv_doc.setImageResource(R.drawable.gwlz_on);
			iv_notice.setImageResource(R.drawable.notice);
			break;
		case R.id.rl_notice:
			if(page5 == null){
				page5 = new FragmentPage5();
				transaction.add(R.id.realtabcontent, page5);
			}
			transaction.show(page5);
			if(page1!=null){transaction.hide(page1);}
			if(page3!=null){transaction.hide(page3);}
			if(page2!=null){transaction.hide(page2);}
			if(page4!=null){transaction.hide(page4);}
			iv_inform.setImageResource(R.drawable.yjgg);
			iv_richeng.setImageResource(R.drawable.rc);
			iv_email.setImageResource(R.drawable.sfwj);
			iv_doc.setImageResource(R.drawable.gwlz);
			iv_notice.setImageResource(R.drawable.notice_on);
			break;
		case R.id.iv_user2:
			Intent it_person = new Intent(Select2Activity.this,PersonActivity.class);
			startActivity(it_person);
			break;
		case R.id.menu1:
			Intent it_home = new Intent(Select2Activity.this,StartActivity.class);
			startActivity(it_home);
			break;
		case R.id.menu2:
			Intent it_remind = new Intent(Select2Activity.this,RemindActivity.class);
			startActivity(it_remind);
			break;
		case R.id.menu3:
			Intent it = new Intent(Select2Activity.this,MydocActivity.class);
			startActivity(it);
			break;
		default:
			break;
		}
		transaction.commit();
	}
	
	private void setDefaultfragment(){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		page1 = new FragmentPage1();
		transaction.add(R.id.realtabcontent, page1);
		transaction.commit();
	}
}
