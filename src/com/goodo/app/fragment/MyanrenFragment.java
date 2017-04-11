package com.goodo.app.fragment;

import java.util.ArrayList;
import java.util.List;

import com.goodo.app.R;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyanrenFragment extends Fragment{

	protected static final String TAG = "Activity";
	private List<String> mDatas;
	private ExpandableListView expandableListView;
	private String[] generalTypes = new String[]{"张三","李四","王五"};
	private List<List<String>> general = new ArrayList<List<String>>();
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initDatas();
		
		View view = inflater.inflate(R.layout.my_anren_fragment, container, false);
		expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
		ExpandableListAdapter adapter = new BaseExpandableListAdapter() {			
			TextView getTextView(){
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				
				TextView textView = new TextView(MyanrenFragment.this.getActivity());
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(30, 30, 30, 30);
				textView.setTextColor(Color.BLACK);
				return textView;
			}
			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(MyanrenFragment.this.getActivity());
				ll.setOrientation(0);
				TextView textView = getTextView();
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				textView.setText("  "+getGroup(arg0).toString());
				textView.setPadding(36, 0, 0, 0);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(36, 36, 36, 36);
				ll.addView(textView,lp);
				return ll;
			}
			
			@Override
			public long getGroupId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}
			
			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return generalTypes.length;
			}
			
			@Override
			public Object getGroup(int arg0) {
				// TODO Auto-generated method stub
				return generalTypes[arg0];
			}
			
			@Override
			public int getChildrenCount(int arg0) {
				// TODO Auto-generated method stub
				return general.get(arg0).size();
			}
			
			@Override
			public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
					ViewGroup arg4) {
				// TODO Auto-generated method stub
				View view2 = inflater.inflate(R.layout.sample1,arg4, false); 
				return view2;
			}
			
			@Override
			public long getChildId(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arg1;
			}
			
			@Override
			public Object getChild(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return general.get(arg0).get(arg1);
			}
		};
		
		expandableListView.setAdapter(adapter);
			
		return view;
	}
	
	private void initDatas()
	{
		mDatas = new ArrayList<String>();
		for (int i = 'A'; i <= 'Z'; i++)
			mDatas.add((char) i + "");
		for(int i = 0;i < generalTypes.length;i++){
			general.add(mDatas);
		}
	}
	
}
