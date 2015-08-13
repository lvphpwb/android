package com.lvphp.pullrefreshlist;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.lvphp.pullrefreshlist.PullRefreshList.PullRefreshListListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MainActivity extends Activity implements PullRefreshListListener{
	private PullRefreshList mListView;
	private ListViewAdapter mAdapter;
	private ArrayList<Book> items = new ArrayList<Book>();
	private int start = 0;
	private Handler mHandler;
	private static int refreshCnt = 0;
	private SimpleDateFormat mDateFormat;
	
	private void geneItems() {
		for (int i = 0; i != 20; ++i) {
			Book tmp = new Book();
			tmp.name = "refresh cnt " + (++start);
			tmp.price = "$12.00";
			tmp.auther = "hello";
			tmp.image = R.drawable.pushrefresh_arrow;
			tmp.id = ++start;
			items.add(tmp);
		}
	}
	
	private void pushItems() {
		for (int i = 0; i != 5; ++i) {
			Book tmp = new Book();
			tmp.name = "refresh cnt " + (++start);
			tmp.price = "$12.00";
			tmp.auther = "hello";
			tmp.image = R.drawable.pushrefresh_arrow;
			tmp.id = ++start;
			items.add(0, tmp);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListView = (PullRefreshList) findViewById(R.id.refresh_list);
		mListView.setPullRefreshListListener(this);
		mListView.arrowImage = R.drawable.pushrefresh_arrow;
		mListView.initUI();
		geneItems();
		mAdapter = new ListViewAdapter(this, items);
		mListView.setAdapter(mAdapter);
		mDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
		mListView.setRefreshTime(mDateFormat.format(new java.util.Date()));
		
		mHandler = new Handler();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start = ++refreshCnt;
				pushItems();
				mAdapter.notifyDataSetChanged();
				mListView.stopRefresh();
				mListView.setRefreshTime(mDateFormat.format(new java.util.Date()));
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems();
				mAdapter.notifyDataSetChanged();
				mListView.stopLoadMore();
			}
		}, 2000);
	}
	
	public class Book{
		public String name;
		public String price;
		public String auther;
		public int image;
		public int id;
	}
	
	public class ListViewAdapter extends BaseAdapter {
		private ArrayList<Book> mBookList;
		private LayoutInflater mLayoutInflater; 
		private Context mContext;
		
		public ListViewAdapter(Context context, ArrayList<Book> list) {
			super();
	        this.mBookList = list;
	        mContext = context;
	        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    } 

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBookList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBookList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View itemView = mLayoutInflater.inflate(R.layout.book_item, null);
			ImageView img = (ImageView)itemView.findViewById(R.id.list_header_arrow);
			Book item = (Book)this.getItem(position);
			img.setImageResource(item.image);
			return itemView;
		}
	}
}
