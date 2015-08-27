package com.lvphp.tabfragmentpager;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class MyViewPager extends ViewPager{
	private boolean mViewPagerDuringDragging = false;
	private MyViewPagerObserver mMyViewPagerObserver = null;
	public ArrayList<Fragment> mFragmentList;

	public MyViewPager(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MyViewPager(Context context,AttributeSet attr){
		super(context,attr);
	}
	
	public void setObserver(MyViewPagerObserver observer){
		mMyViewPagerObserver = observer;
	}
	
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		 @Override
	    public int getCount() {  
	       return mFragmentList.size();
	    }
	    @Override
	    public Fragment getItem(int index) {  
	    	return mFragmentList.get(index);
	    }
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (arg0 == ViewPager.SCROLL_STATE_DRAGGING) {
				mViewPagerDuringDragging = true;
			} else if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {	// stop slide
				// if change item, set mViewPagerDuringDragging by onPageSelected
				// else still in Dragging period
			} else if (arg0 == ViewPager.SCROLL_STATE_IDLE) {	// reach anchor
				mViewPagerDuringDragging = false;
			}
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// if during Dragging period, draw indicator by scroll offset
			// else draw by animation triggered by onPageSelected
			if (mViewPagerDuringDragging && mMyViewPagerObserver!=null)
				mMyViewPagerObserver.onPageScrolled(arg0, arg1, arg2);
		}
		@Override
		public void onPageSelected(int arg0) {
			mViewPagerDuringDragging = false;
			if(mMyViewPagerObserver!=null){
				mMyViewPagerObserver.onPageScrolled(arg0);
			}
		}
	}
	
	public interface MyViewPagerObserver{
		void onPageScrolled(int arg0, float arg1, int arg2);
		void onPageScrolled(int arg0);
	}
}
