package com.lvphp.tabfragmentpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragment extends Fragment{
	private View mContainer = null;
	public MyFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mContainer == null) {
			mContainer = inflater.inflate(R.layout.myfragment, container, false);
		}
		return mContainer;
	}
}
