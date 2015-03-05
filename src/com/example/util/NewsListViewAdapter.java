package com.example.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.csdn_blog.R;
import com.example.myclass.News;
import com.example.network.PictureAsyncTask;

public class NewsListViewAdapter extends BaseAdapter {
	protected Context context;
	protected List<News> newsList = null;
	//view对象缓存
	protected HashMap<Integer,SoftReference<LinearLayout> > viewCache = new HashMap<Integer,SoftReference<LinearLayout> >();
	
	public NewsListViewAdapter(Context context)
	{
		this.context = context;
	}
	public void setDataSet(List<News> newsList)
	{
		this.newsList = newsList;
	}
	
	@Override
	public int getCount() {
		if(newsList == null) return 0;
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(viewCache.containsKey(position) && viewCache.get(position).get() != null) 
		{
			TextView title = (TextView) viewCache.get(position).get().findViewById(R.id.blog_news_title);
			TextView summary = (TextView) viewCache.get(position).get().findViewById(R.id.blog_summary);
			TextView news_author_publishtime = (TextView) viewCache.get(position).get().findViewById(R.id.news_author_publishtime);
			if(newsList.get(position).hasRead) 
			{
				title.setTextColor(Color.parseColor("#7c7979"));
				summary.setTextColor(Color.parseColor("#7c7979"));
				news_author_publishtime.setTextColor(Color.parseColor("#7c7979"));
			}
			return viewCache.get(position).get();
		}
		LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.news_item_layout, null);
		//标题
		TextView title = (TextView) linearLayout.findViewById(R.id.blog_news_title);
		title.setText(newsList.get(position).title);
		if(newsList.get(position).hasRead) title.setTextColor(Color.parseColor("#7c7979"));
		//头像
		ImageView head_picture = (ImageView) linearLayout.findViewById(R.id.blog_head_picture);
		head_picture.setBackground(context.getResources().getDrawable(R.drawable.csdn));
		PictureAsyncTask pictureAsyncTask = new PictureAsyncTask(head_picture);
		pictureAsyncTask.execute(newsList.get(position).headPictureUrl);
		//摘要
		TextView summary = (TextView) linearLayout.findViewById(R.id.blog_summary);
		summary.setText("    "+newsList.get(position).summary);
		if(newsList.get(position).hasRead) summary.setTextColor(Color.parseColor("#7c7979"));
		System.out.println(newsList.get(position).title);
		System.out.println(newsList.get(position).hasRead);
		//发布者与发布时间
		TextView news_author_publishtime = (TextView) linearLayout.findViewById(R.id.news_author_publishtime);
		news_author_publishtime.setText(newsList.get(position).author+" 发布于 "+newsList.get(position).publishTime);
		if(newsList.get(position).hasRead) news_author_publishtime.setTextColor(Color.parseColor("#7c7979"));
		
		viewCache.put(position, new SoftReference<LinearLayout>(linearLayout));
		return linearLayout;
	}
}
