package com.goodo.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DocWriteActivity extends Activity {
	
	private TextView tv_isdoc;
	private TextView tv_doc_person;
	private ImageView iv_more;
	private Boolean flag = false;
	private ImageView doc_write_iv_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doc_write);
		
		tv_isdoc = (TextView) findViewById(R.id.isdoc);
		tv_doc_person = (TextView) findViewById(R.id.doc_person);
		iv_more = (ImageView) findViewById(R.id.doc_write_btn_more);
		doc_write_iv_return = (ImageView) findViewById(R.id.doc_write_iv_return);
		
		
		doc_write_iv_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		tv_isdoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!flag){
					tv_isdoc.setTextColor(0xff00bfff);
					flag = true;
				}else{
					tv_isdoc.setTextColor(0xff000000);
					flag = false;
				}
				
			}
		});
		
		iv_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(DocWriteActivity.this,SelectPersonActivity.class);
				startActivity(it);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.doc_write, menu);
		return true;
	}

}
