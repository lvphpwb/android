package com.lvphp.rollimagepager;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class RollImagePager extends RelativeLayout{
	private Context mContext;
	
	private ArrayList<View> imageViewsList;
	private ArrayList<View> dotViewsList;
	private ViewPager mViewPager;
	private LinearLayout dotLayout;
	private int currentItem = 0;
	private int maxItem = 0;
	private int itemNum = 0;
	//定时任务  
    private ScheduledExecutorService scheduledExecutorService;
	
	//自定义轮播图的资源  
    public String[] imageUrls = new String[] {}; //note 网络资源
    public int[] imagedrawables = new int[] {};; //note 本地资源
    public int dotSelected;
    public int dotNormal;
    public int dotMargin = 4;
    public int dotLayoutBottomMargin = 20;
    public int dotLayoutGravity = Gravity.CENTER_HORIZONTAL;
    public int repeatNum = 10;
    public int interval = 3;
    public RollImagePagerObserver mRollImagePagerObserver = null;
    
    //Handler  
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            // TODO Auto-generated method stub  
            super.handleMessage(msg);
            mViewPager.setCurrentItem(currentItem);
        }
    }; 

	public RollImagePager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public RollImagePager(Context context, AttributeSet attrs) {  
		super(context, attrs);
        // TODO Auto-generated constructor stub
		this.mContext = context;
    }
	
	public int dip2px(float dpValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
	
	private void startPlay(){
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), interval, interval, TimeUnit.SECONDS);
    }
	
	private void stopPlay(){  
        scheduledExecutorService.shutdown();
    }
	
	public void initUI(){
		RelativeLayout.LayoutParams matchparent =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		imageViewsList = new ArrayList<View>();
		OnClickListener clickListener = null;
		if(mRollImagePagerObserver != null){
			clickListener = new OnClickListener(){
				@Override
				public void onClick(View v) {
						mRollImagePagerObserver.onImageClick(currentItem%itemNum);
				}
			};
		}
		if(imageUrls.length>0){
			for (int i = 0; i < imageUrls.length; i++) {  
	            ImageView view =  new ImageView(mContext);  
	            view.setTag(imageUrls[i]);
	            view.setScaleType(ScaleType.FIT_XY);
	            view.setLayoutParams(matchparent);
	            if(mRollImagePagerObserver != null){
	            	view.setOnClickListener(clickListener);
	            }
	            imageViewsList.add(view);
	        }
		}
		if(imagedrawables.length>0){
			for (int i = 0; i < imagedrawables.length; i++) {  
	            ImageView view =  new ImageView(mContext);
	            view.setImageResource(imagedrawables[i]);
	            view.setScaleType(ScaleType.FIT_XY);
	            view.setLayoutParams(matchparent);
	            if(mRollImagePagerObserver != null){
	            	view.setOnClickListener(clickListener);
	            }
	            imageViewsList.add(view);
	        }
		}
		itemNum = imageUrls.length + imagedrawables.length;
		maxItem = itemNum*repeatNum;
		if(itemNum>0){
			dotLayout = new LinearLayout(mContext);
			dotLayout.setOrientation(LinearLayout.HORIZONTAL);
			RelativeLayout.LayoutParams dotlayout =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    	dotlayout.bottomMargin = dotLayoutBottomMargin;
	    	dotlayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    	dotLayout.setLayoutParams(dotlayout);
	    	dotLayout.setGravity(dotLayoutGravity);
			dotViewsList = new ArrayList<View>();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			for (int i = 0; i < itemNum; i++) {
	            ImageView dotView =  new ImageView(mContext);
	            params.leftMargin = dotMargin;  
	            params.rightMargin = dotMargin;
	            dotView.setBackgroundResource(dotNormal);
	            dotViewsList.add(dotView);
	            dotLayout.addView(dotView, params);
	        }
		}
		mViewPager = new ViewPager(mContext);
		mViewPager.setLayoutParams(matchparent);
		mViewPager.setFocusable(true);
		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.addOnPageChangeListener(new MyPageChangeListener());
		this.addView(mViewPager);
		this.addView(dotLayout);
		currentItem = maxItem/2;
		mViewPager.setCurrentItem(currentItem);
		((View)dotViewsList.get(currentItem%itemNum)).setBackgroundResource(dotSelected);
		this.startPlay();
	}
	
	private class MyPagerAdapter  extends PagerAdapter{
        @Override  
        public void destroyItem(View container, int position, Object object) {  
            // TODO Auto-generated method stub
            ((ViewPager)container).removeView(imageViewsList.get(position%itemNum));
        }
        @Override  
        public Object instantiateItem(View container, int position) {
        	position %= itemNum;
            View view = imageViewsList.get(position);
            ViewParent vp =view.getParent();
            if (vp!=null){  
                ViewGroup parent = (ViewGroup)vp;  
                parent.removeView(view);  
            }
            ((ViewPager)container).addView(view);
            return view;
        }
        @Override  
        public int getCount() {
            // TODO Auto-generated method stub  
            return maxItem;
        }  
        @Override
	    public boolean isViewFromObject(View arg0, Object arg1) {
	      return arg0 == arg1;
	    }
    } 
	private class MyPageChangeListener implements OnPageChangeListener{  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
            switch (arg0) {  
	            case 1:// 手势滑动，空闲中  
	                stopPlay();break;
	            case 2:// 界面切换中  
	            	stopPlay();break;
	            case 0:// 滑动结束，即切换完毕或者加载完毕  
	            	startPlay();
	                break; 
            }
        }
        @Override  
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub
        	currentItem = pos;
            for(int i=0;i < itemNum;i++){
            	((View)dotViewsList.get(i)).setBackgroundResource(i == pos%itemNum ? dotSelected : dotNormal);
            }
        }
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
    }
	
	private class SlideShowTask implements Runnable{  
        @Override  
        public void run() {
        	currentItem = (currentItem+1)%maxItem;
            handler.obtainMessage().sendToTarget();
        }
    }
	
	public interface RollImagePagerObserver{
		void onImageClick(int index);
	}
}
