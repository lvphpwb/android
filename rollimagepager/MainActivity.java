package com.lvphp.rollimagepager;

import com.lvphp.rollimagepager.RollImagePager.RollImagePagerObserver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements RollImagePagerObserver{
	private RollImagePager mRollViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.e("test", "activity");
		mRollViewPager = (RollImagePager)findViewById(R.id.rollimagepager);
		mRollViewPager.imagedrawables = new int[] {R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3, R.drawable.welcome_4};
		mRollViewPager.dotNormal = R.drawable.welcome_dot_normal;
		mRollViewPager.dotSelected = R.drawable.welcome_dot_selected;
		mRollViewPager.mRollImagePagerObserver = this;
		mRollViewPager.initUI();
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
	public void onImageClick(int index) {
		// TODO Auto-generated method stub
		Log.e("test", "click "+index);
	}
}
