package com.example.blog;

import com.example.csdn_blog.MainActivity;

import android.content.Context;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

public class BlogOnPageChangeListener implements OnPageChangeListener {
	private final static String tag = "BlogOnPageChangeListener";
	
	private Context context;
	
	public BlogOnPageChangeListener(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int index) {
		((MainActivity)context).changeNavigationItem(index);
	}
}
