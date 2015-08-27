package com.lvphp.tabfragmentpager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabContainer extends FrameLayout{
	private Context mContext;
	private int tabNum = 0;
	private LinearLayout mTabTitleLayout;
	private View mLine;
	private View mIndicator;
	private int mIndicatorItemWidth;
	private int mCurrentIndex = 0;
	private boolean mIsAnimating = false;
	
	public int tabTitleLayoutBackground;
	public String[] tabTitles = new String[]{};
	public int titleColor;
	public int titleTextBackground;
	public int lineBackground;
	public int indicatorHeight = 3;
	public int indicatorWidth;
	public int indicatorBackground;
	public OnClickListener titleOnClickListener = null;
	
	public int dip2px(float dpValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
	
	public TabContainer(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public TabContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mTabTitleLayout = new LinearLayout(mContext);
		FrameLayout.LayoutParams tmp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		mTabTitleLayout.setLayoutParams(tmp);
		mTabTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(mTabTitleLayout);
		mLine = new View(mContext);
		this.addView(mLine);
		mIndicator = new View(mContext);
		this.addView(mIndicator);
	}
	
	public void initUI(){
		mTabTitleLayout.setBackgroundResource(tabTitleLayoutBackground);
		FrameLayout.LayoutParams tmpLineParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(1));
		tmpLineParams.gravity = Gravity.BOTTOM;
		mLine.setLayoutParams(tmpLineParams);
		mLine.setBackgroundResource(lineBackground);
		tabNum = tabTitles.length;
		if(tabNum>0){
			LinearLayout.LayoutParams tmpTitleParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
			ColorStateList titlecolor = (ColorStateList)mContext.getResources().getColorStateList(titleColor);
			for(int i=0; i<tabNum; i++){
				TabTextView tmpTextView = new TabTextView(mContext);
				tmpTextView.index = i;
				tmpTextView.setText(tabTitles[i]);
				tmpTextView.setLayoutParams(tmpTitleParam);
				tmpTextView.setGravity(Gravity.CENTER);
				tmpTextView.setTextSize(15);
				tmpTextView.setTextColor(titlecolor);
				tmpTextView.setBackgroundResource(titleTextBackground);
				if(titleOnClickListener != null){
					tmpTextView.setOnClickListener(titleOnClickListener);
				}
				mTabTitleLayout.addView(tmpTextView);
			}
			mIndicatorItemWidth = getIndicatorWidth();
			FrameLayout.LayoutParams tmpIndicatorParams = new FrameLayout.LayoutParams(mIndicatorItemWidth, dip2px(indicatorHeight));
			tmpIndicatorParams.gravity = Gravity.BOTTOM;
			tmpIndicatorParams.leftMargin = getIndicatorMarginLeft();
			mIndicator.setLayoutParams(tmpIndicatorParams);
			mIndicator.setBackgroundResource(indicatorBackground);
		}
	}
	
	public void setCurrentIndex(int index){
		if(mCurrentIndex != index && index < tabNum){
			mCurrentIndex = index;
			layoutIndicator();
		}
	}
	
	public void setScrollOffset(int position, float positionOffset) {
		if (mIsAnimating != true) {
			int left = getIndicatorMarginLeft();
			int top = getHeight() - dip2px(indicatorHeight);
			if(position == mCurrentIndex){
				float moveOffset = (float)indicatorWidth / tabNum * positionOffset;
				left = (int)(left + moveOffset);
			}else{
				float moveOffset = (float)indicatorWidth / tabNum * (1 - positionOffset);
				left = (int)(left - moveOffset);
			}
			mIndicator.layout(left, top, left + mIndicatorItemWidth, top + dip2px(indicatorHeight));
		}
	}
	
	private int getIndicatorMarginLeft(){
		return (int)((indicatorWidth/tabNum)*mCurrentIndex + ((indicatorWidth/tabNum)-mIndicatorItemWidth)/2);
	}
	
	private int getIndicatorWidth(){
		int itemwidth = (int)((indicatorWidth/tabNum)*0.8);
		return itemwidth;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int leftIndicator = getIndicatorMarginLeft();
		int topIndicator = getHeight() - dip2px(indicatorHeight);
		mIndicator.layout(leftIndicator, topIndicator, leftIndicator + mIndicatorItemWidth, topIndicator + dip2px(indicatorHeight));
	}
	
	private void layoutIndicator(){
		int left = getIndicatorMarginLeft();
		int top = getHeight() - dip2px(indicatorHeight);
		int location[] = new int[2];
		mIndicator.getLocationOnScreen(location);
		mIndicator.setVisibility(View.GONE);
		mIndicator.layout(left, top, left + mIndicatorItemWidth, top + dip2px(indicatorHeight));
		TranslateAnimation animation = new TranslateAnimation(location[0] - left, 0, 0, 0);
		int duration = (int)((float)(Math.abs(left - location[0])) / getWidth() * 600);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				mIndicator.setVisibility(View.VISIBLE);
				mIsAnimating = false;
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		mIsAnimating = true;
		mIndicator.startAnimation(animation);
	}
	
	public class TabTextView extends TextView{
		public int index;
		public TabTextView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
	}

}
