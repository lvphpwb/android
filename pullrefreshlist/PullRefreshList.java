package com.lvphp.pullrefreshlist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class PullRefreshList extends ListView implements OnScrollListener{
	private Context mContext;
	
	private ListHeaderLayout mListHeaderLayout;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight;
	private boolean mPullRefreshing = false;
	private boolean mEnablePullRefresh = false;
	private ImageView mHeaderArrowView;
	
	private ListFooterLayout mListFooterLayout;
	private boolean mEnablePullLoad = false;
	private boolean mPullLoading = false;
	
	private Scroller mScroller;
	private float mLastY = -1;
	private final static float OFFSET_RADIO = 1.8f;
	private PullRefreshListListener mPullRefreshListListener;
	private int mTotalItemCount;
	
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;
	
	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 100; // when pull up >= 50px
	
	public boolean isAllowHeader = true;
	public boolean isAllowFooter = true;
	public int arrowImage;

	public PullRefreshList(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public PullRefreshList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub 
		mContext = context;
	}
	
	public void initUI(){
		mScroller = new Scroller(mContext, new DecelerateInterpolator());
		super.setOnScrollListener(this);
		if(isAllowHeader){
			mEnablePullRefresh = true;
			mListHeaderLayout = new PullRefreshList.ListHeaderLayout(mContext);
			mHeaderViewContent = (RelativeLayout) mListHeaderLayout.findViewById(R.id.list_header_content);
			mHeaderTimeView = (TextView) mListHeaderLayout.findViewById(R.id.list_header_time);
			mHeaderArrowView = (ImageView) mListHeaderLayout.findViewById(R.id.list_header_arrow);
			mHeaderArrowView.setImageResource(arrowImage);
			addHeaderView(mListHeaderLayout);
			Log.e("test", "addHeaderView");
			mListHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				}
			);
		}
		if(isAllowFooter){
			mEnablePullLoad = true;
			mListFooterLayout = new PullRefreshList.ListFooterLayout(mContext);
			mListFooterLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPullLoading = true;
					mListFooterLayout.setState(ListFooterLayout.STATE_LOADING);
					if (mPullRefreshListListener != null) {
						mPullRefreshListListener.onLoadMore();
					}
				}
			});
			addFooterView(mListFooterLayout);
		}
	}
	
	private void updateFooterHeight(float delta) {
		int height = mListFooterLayout.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
				mListFooterLayout.setState(ListFooterLayout.STATE_READY);
			} else {
				mListFooterLayout.setState(ListFooterLayout.STATE_NORMAL);
			}
		}
		mListFooterLayout.setBottomMargin(height);
	}
	
	private void updateHeaderHeight(float delta) {
		mListHeaderLayout.setVisiableHeight((int) delta + mListHeaderLayout.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mListHeaderLayout.getVisiableHeight() > mHeaderViewHeight) {
				mListHeaderLayout.setState(ListHeaderLayout.STATE_READY);
			} else {
				mListHeaderLayout.setState(ListHeaderLayout.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}
	
	private void resetHeaderHeight() {
		int height = mListHeaderLayout.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();
	}
	
	private void resetFooterHeight() {
		int bottomMargin = mListFooterLayout.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mListHeaderLayout.setVisiableHeight(mScroller.getCurrY());
			} else {
				mListFooterLayout.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
		}
		super.computeScroll();
	}
	
	public void setPullRefreshListListener(PullRefreshListListener l) {
		mPullRefreshListListener = l;
	}
	
	public void stopRefresh() {
		if (isAllowHeader && mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}
	
	public void stopLoadMore() {
		if (isAllowFooter && mPullLoading == true) {
			mPullLoading = false;
			mListFooterLayout.setState(ListFooterLayout.STATE_NORMAL);
		}
	}

	public void setRefreshTime(String time) {
		if(isAllowHeader){
			mHeaderTimeView.setText(time);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (mEnablePullRefresh && getFirstVisiblePosition() == 0 && (mListHeaderLayout.getVisiableHeight() > 0 || deltaY > 0)) {
				updateHeaderHeight(deltaY / OFFSET_RADIO);
			}else if (mEnablePullLoad && getLastVisiblePosition() == mTotalItemCount - 1 && (mListFooterLayout.getBottomMargin() > 0 || deltaY < 0)) {
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0 && mEnablePullRefresh) {
				if (mListHeaderLayout.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mListHeaderLayout.setState(ListHeaderLayout.STATE_REFRESHING);
					if (mPullRefreshListListener != null) {
						mPullRefreshListListener.onRefresh();
					}
				}
				resetHeaderHeight();
			}else if (mEnablePullLoad && getLastVisiblePosition() == mTotalItemCount - 1) {
				if (mListFooterLayout.getBottomMargin() > PULL_LOAD_MORE_DELTA && !mPullLoading) {
					mPullLoading = true;
					mListFooterLayout.setState(ListFooterLayout.STATE_LOADING);
					if (mPullRefreshListListener != null) {
						mPullRefreshListListener.onLoadMore();
					}
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		mTotalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public interface PullRefreshListListener {
		public void onRefresh();
		public void onLoadMore();
	}
	
	
	public class ListHeaderLayout extends LinearLayout {
		private Context mContext;
		private LinearLayout mContainer;
		private ImageView mArrowImageView;
		private TextView mHintTextView;
		private ProgressBar mProgressBar;
		private RotateAnimation mRotateUpAnim;
		private RotateAnimation mRotateDownAnim;
		private int mState = STATE_NORMAL;
		private String headerHintNormal = "下拉刷新";
		private String headerHintReady = "松开刷新数据";
		private String hintLoading = "正在加载...";
		private String headerLastTime = "上次更新时间：";
		
		public final static int STATE_NORMAL = 0;
		public final static int STATE_READY = 1;
		public final static int STATE_REFRESHING = 2;

		public ListHeaderLayout(Context context) {
			this(context, null);
		}
		
		public ListHeaderLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
			mContext = context;
			this.initUI();
		}
		
		private void initUI(){
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
			mContainer = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_header, null);
			addView(mContainer, lp);
			setGravity(Gravity.BOTTOM);
			mArrowImageView = (ImageView)findViewById(R.id.list_header_arrow);
			mHintTextView = (TextView)findViewById(R.id.list_header_hint_textview);
			mHintTextView.setText(headerHintNormal);
			TextView timeHead = (TextView)findViewById(R.id.list_header_time_head);
			timeHead.setText(headerLastTime);
			mProgressBar = (ProgressBar)findViewById(R.id.list_header_progressbar);
			
			mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
			mRotateUpAnim.setDuration(180);
			mRotateUpAnim.setFillAfter(true);
			mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
			mRotateDownAnim.setDuration(180);
			mRotateDownAnim.setFillAfter(true);
		}
		
		public void setState(int state) {
			if (state == mState) return ;
			if (state == STATE_REFRESHING) {	// 显示进度
				mArrowImageView.clearAnimation();
				mArrowImageView.setVisibility(View.INVISIBLE);
				mProgressBar.setVisibility(View.VISIBLE);
			} else {	// 显示箭头图片
				mArrowImageView.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.INVISIBLE);
			}
			switch(state){
				case STATE_NORMAL:
					if (mState == STATE_READY) {
						mArrowImageView.startAnimation(mRotateDownAnim);
					}
					if (mState == STATE_REFRESHING) {
						mArrowImageView.clearAnimation();
					}
					mHintTextView.setText(headerHintNormal);
					break;
				case STATE_READY:
					if (mState != STATE_READY) {
						mArrowImageView.clearAnimation();
						mArrowImageView.startAnimation(mRotateUpAnim);
						mHintTextView.setText(headerHintReady);
					}
					break;
				case STATE_REFRESHING:
					mHintTextView.setText(hintLoading);
					break;
					default:
			}
			mState = state;
		}
		
		public void setVisiableHeight(int height) {
			if (height < 0) height = 0;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
			lp.height = height;
			mContainer.setLayoutParams(lp);
		}
		
		public int getVisiableHeight() {
			return mContainer.getLayoutParams().height;
		}
	}
	
	
	public class ListFooterLayout extends LinearLayout {
		public final static int STATE_NORMAL = 0;
		public final static int STATE_READY = 1;
		public final static int STATE_LOADING = 2;
		
		private Context mContext;
		private View mContentView;
		private View mProgressBar;
		private TextView mHintView;
		private String footerHintText = "查看更多";
		private String footerHintTextReady = "松开载入更多";

		public ListFooterLayout(Context context) {
			this(context, null);
			// TODO Auto-generated constructor stub
		}
		
		public ListFooterLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
			mContext = context;
			this.initUI();
		}
		
		private void initUI(){
			LinearLayout moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.list_footer, null);
			addView(moreView);
			moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			mContentView = moreView.findViewById(R.id.list_footer_content);
			mProgressBar = moreView.findViewById(R.id.list_footer_progressbar);
			mHintView = (TextView)moreView.findViewById(R.id.list_footer_hint_textview);
			mHintView.setText(footerHintText);
		}
		
		public void setState(int state) {
			mHintView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			mHintView.setVisibility(View.INVISIBLE);
			if (state == STATE_READY) {
				mHintView.setVisibility(View.VISIBLE);
				mHintView.setText(footerHintTextReady);
			} else if (state == STATE_LOADING) {
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				mHintView.setVisibility(View.VISIBLE);
				mHintView.setText(footerHintText);
			}
		}
		
		public void hide() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
			lp.height = 0;
			mContentView.setLayoutParams(lp);
		}
		
		public void setBottomMargin(int height) {
			if (height < 0) return ;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
			lp.bottomMargin = height;
			mContentView.setLayoutParams(lp);
		}
		
		public int getBottomMargin() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
			return lp.bottomMargin;
		}
		
	}

}
