package com.goodo.app;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DbswAttachActivity extends Activity implements OnClickListener{

	private ImageView iv_return;
	private List<Map<String, Object>> list;
	private LinearLayout ll_fujian;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dbsw_attach);
		init();
		getAttachll();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_return:
			finish();
			break;
			
		default:
			break;
		}
	}
	
	private void init(){
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(this);
		
		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
		
		Intent it = getIntent();
		list = (List<Map<String, Object>>) it.getSerializableExtra("list");
		
		
	}
	
	private void getAttachll(){
		LinearLayout ll = new LinearLayout(this);
		ll.setBackgroundColor(0xffdae1f1);
		LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lllp.topMargin = 5;
		TextView tv = new TextView(this);
		tv.setText("附件：共"+list.size()+"个");
		tv.setTextSize(20);
		tv.setTextColor(0xff000000);
		ll.addView(tv);
		ll_fujian.addView(ll, lllp);
		LinearLayout.LayoutParams lllp_attach = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lllp_attach.topMargin = 10;
		for(int i = 0;i<list.size();i++){
			RelativeLayout rl = new RelativeLayout(this);
			RelativeLayout.LayoutParams rllp_iv = new RelativeLayout.LayoutParams(40, 40);
			rllp_iv.addRule(RelativeLayout.ALIGN_LEFT);
			rllp_iv.addRule(RelativeLayout.CENTER_VERTICAL);
			
			RelativeLayout.LayoutParams rllp_tv = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rllp_tv.addRule(RelativeLayout.RIGHT_OF, 3);
			
			RelativeLayout.LayoutParams rllp_ivjiantou = new RelativeLayout.LayoutParams(40, 40);
			rllp_ivjiantou.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rllp_ivjiantou.addRule(RelativeLayout.CENTER_VERTICAL);
			
			ImageView iv = new ImageView(this);
			iv.setId(3);
			iv.setImageResource(R.drawable.fujian);
			iv.setScaleType(ScaleType.FIT_CENTER);
			rl.addView(iv, rllp_iv);
			
			ImageView iv_jiantou = new ImageView(this);
			iv_jiantou.setId(1);
			iv_jiantou.setImageResource(R.drawable.jiantou);
			iv.setScaleType(ScaleType.FIT_CENTER);
			rl.addView(iv_jiantou, rllp_ivjiantou);
			
			TextView tv_attach = new TextView(this);
			tv_attach.setText((String)list.get(i).get("AttachName"));
			tv_attach.setTextSize(20);
			tv_attach.setTextColor(0xff000000);
			rl.addView(tv_attach, rllp_tv);
			
			ll_fujian.addView(rl, lllp_attach);
			final int a = i;
			rl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					String Name = (String) list.get(a).get("AttachName");
					String Url = (String) list.get(a).get("AttachUrl");
					preferences = getSharedPreferences("Attach", MODE_PRIVATE);
					editor = preferences.edit();
					editor.clear();
					editor.commit();
					Myconfig.set_AttachName.add(Name+" /"+Url);
					editor.putStringSet("AttachName", Myconfig.set_AttachName);
					editor.commit();
					
					String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
					File f = new File(file + "Download/" +Name);
					if(f.exists()){
						String[] docstr = Url.split("\\.");
						String str2 = docstr[1];
						MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
						String type = mimeTypeMap.getMimeTypeFromExtension(str2);
						Intent it = new Intent(Intent.ACTION_VIEW);
						it.addCategory("android.intent.category.DEFAULT");
						it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Uri uri = Uri.fromFile(f);
						it.setDataAndType(uri, type);
						startActivity(it);
					}else{
						Intent it = new Intent(DbswAttachActivity.this,AttachActivity.class);
						it.putExtra("Kind", "待办事务");
						it.putExtra("Url", Myconfig.IP+"/EduPlate/MSGSchedule/InterfaceJson.asmx/File_Download");
						it.putExtra("ID", (String)list.get(a).get("Plan_ID"));
						it.putExtra("ReMark_ID", (String)list.get(a).get("ReMark_ID"));
						it.putExtra("Attach_ID", (String)list.get(a).get("Attach_ID"));
						it.putExtra("Name", (String)list.get(a).get("AttachName"));
						it.putExtra("AttachUrl", (String)list.get(a).get("AttachUrl"));
						startActivity(it);
					}
				}
			});
		}
	}
	
}
