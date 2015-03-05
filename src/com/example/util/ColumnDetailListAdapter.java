package com.example.util;

import java.lang.ref.SoftReference;

import com.example.csdn_blog.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColumnDetailListAdapter extends NewsListViewAdapter {
	private final static String tag = "ColumnDetailListAdapter";
	
	public ColumnDetailListAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(viewCache.containsKey(position) && viewCache.get(position).get() != null) 
		{
			TextView title = (TextView) viewCache.get(position).get().findViewById(R.id.column_detail_title);
			TextView summary = (TextView) viewCache.get(position).get().findViewById(R.id.column_detail_summary);
			TextView news_author_publishtime = (TextView) viewCache.get(position).get().findViewById(R.id.column_author_publishtime);
	
			return viewCache.get(position).get();
		}
		LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.column_detail_item_layout, null);
		//标题
		TextView title = (TextView) linearLayout.findViewById(R.id.column_detail_title);
		title.setText(newsList.get(position).title);
		//摘要
		TextView summary = (TextView) linearLayout.findViewById(R.id.column_detail_summary);
		summary.setText("    "+newsList.get(position).summary);
		System.out.println(newsList.get(position).title);
		System.out.println(newsList.get(position).hasRead);
		//发布者与发布时间
		TextView column_author_publishtime = (TextView) linearLayout.findViewById(R.id.column_author_publishtime);
		column_author_publishtime.setText(newsList.get(position).author+" 发布于 "+newsList.get(position).publishTime);
		
		viewCache.put(position, new SoftReference<LinearLayout>(linearLayout));
		return linearLayout;
	}
}
