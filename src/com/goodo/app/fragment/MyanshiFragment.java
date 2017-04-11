package com.goodo.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.goodo.adapter.DataAdapter;
import com.goodo.app.Myconfig;
import com.goodo.app.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyanshiFragment extends Fragment{
	
	protected static final String TAG = "Activity";
	private SwipeListView mSwipeListView;
	private DataAdapter mAdapter;
	private List<Map<String, String>> mDatas;
	
	

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.my_anshi_fragment, container, false);
		
		initDatas();
		
		mSwipeListView = (SwipeListView) view.findViewById(R.id.id_swipelistviewanshi);
		mSwipeListView.setOffsetLeft(Myconfig.width-240);
		mSwipeListView.setOffsetRight(Myconfig.width-120);
		mAdapter = new DataAdapter(this.getActivity(), mDatas, mSwipeListView,true);
		mSwipeListView.setAdapter(mAdapter);
		
		mSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener()
		{

			@Override
			public void onClickFrontView(int position) {
				// TODO Auto-generated method stub
				super.onClickFrontView(position);
				mSwipeListView.closeOpenedItems();
			}

			@Override
			public void onClickBackView(int position) {
				// TODO Auto-generated method stub
				super.onClickBackView(position);
			}

			
			@Override
			public void onChoiceStarted() {
				// TODO Auto-generated method stub
				super.onChoiceStarted();
				mSwipeListView.closeOpenedItems();
			}

			@Override
			public void onChoiceEnded() {
				// TODO Auto-generated method stub
				super.onChoiceEnded();
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				// TODO Auto-generated method stub
				super.onClosed(position, fromRight);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
				// TODO Auto-generated method stub
				super.onDismiss(reverseSortedPositions);
			}

			@Override
			public void onFirstListItem() {
				// TODO Auto-generated method stub
				super.onFirstListItem();
			}

			@Override
			public void onLastListItem() {
				// TODO Auto-generated method stub
				super.onLastListItem();
			}

			@Override
			public void onOpened(int position, boolean toRight) {
				// TODO Auto-generated method stub
				super.onOpened(position, toRight);
			}

			@Override
			public void onListChanged() {
				// TODO Auto-generated method stub
				super.onListChanged();
				
				mSwipeListView.closeOpenedItems();
			}

			@Override
			public void onMove(int position, float x) {
				// TODO Auto-generated method stub
				super.onMove(position, x);
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				// TODO Auto-generated method stub
				super.onStartOpen(position, action, right);
			}

			@Override
			public void onStartClose(int position, boolean right) {
				// TODO Auto-generated method stub
				super.onStartClose(position, right);
			}
			
			
		});
		
		
		return view;
	}
	
	private void initDatas(){
		mDatas = new ArrayList<Map<String,String>>();
		for(int i = 0;i<10;i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", "123");
			map.put("time", "456");
			map.put("from", "789");
			mDatas.add(map);
 		}
	}
	

}
