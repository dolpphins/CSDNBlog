package com.example.blog;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

public class BlogPagerAdapter extends PagerAdapter{
	private final static String tag = "MyPagerAdapter";
	
	private List<LinearLayout> blogPagerViews = null;//ÿһҳ
	
	public BlogPagerAdapter(List<LinearLayout> blogPagerViews)
	{
		this.blogPagerViews = blogPagerViews;
	}
	
	@Override
	public int getCount() {
		return blogPagerViews.size();
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (LinearLayout)arg0 == (LinearLayout)arg1;
	}
	public void destroyItem(View container,int position,Object object)
	{
		((ViewPager) container).removeView(blogPagerViews.get(position));
	}
	public CharSequence getPageTitle(int position)
	{
		return null;
	}
	public Object instantiateItem(View container,int position)
	{
		((ViewPager) container).addView(blogPagerViews.get(position));
		return blogPagerViews.get(position);
	}
}
