package com.lvphp.tabfragmentpager;

import java.util.ArrayList;

import com.lvphp.tabfragmentpager.MyViewPager.MyViewPagerObserver;
import com.lvphp.tabfragmentpager.TabContainer.TabTextView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class MainActivity extends FragmentActivity implements OnClickListener,MyViewPagerObserver{
	private TabContainer mTabContainer;
	private MyViewPager mViewPager;
	private ArrayList<Fragment> mFragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTabContainer = (TabContainer)findViewById(R.id.tab_container);
		mTabContainer.tabTitles = new String[]{"历史访问", "最近收藏", "精彩回顾"};
		mTabContainer.titleOnClickListener = this;
		mTabContainer.tabTitleLayoutBackground = R.color.title_layoutbackground;
		mTabContainer.titleColor = R.color.title_color;
		mTabContainer.titleTextBackground = R.drawable.title_textbackground;
		mTabContainer.lineBackground = R.color.tabline_background;
		mTabContainer.indicatorBackground = R.color.indicator_background;
		WindowManager wm = this.getWindowManager();
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		mTabContainer.indicatorWidth = width;
		mTabContainer.initUI();
		
		mViewPager = (MyViewPager)findViewById(R.id.tab_pager);
		mFragmentList = new ArrayList<Fragment>();
		mFragmentList.add(new MyFragment());
		mFragmentList.add(new MyFragment());
		mFragmentList.add(new MyFragment());
		mViewPager.mFragmentList = mFragmentList;
		mViewPager.setObserver(this);
		mViewPager.setAdapter(mViewPager.new MyFragmentPagerAdapter(getSupportFragmentManager()));
		mViewPager.setCurrentItem(0);
		
		mViewPager.addOnPageChangeListener(mViewPager.new MyOnPageChangeListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		TabTextView tmp = (TabTextView)v;
		Log.e("test", ""+tmp.index);
		mTabContainer.setCurrentIndex(tmp.index);
		mViewPager.setCurrentItem(tmp.index);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		mTabContainer.setScrollOffset(arg0, arg1);
	}

	@Override
	public void onPageScrolled(int arg0) {
		// TODO Auto-generated method stub
		mTabContainer.setCurrentIndex(arg0);
	}
}
