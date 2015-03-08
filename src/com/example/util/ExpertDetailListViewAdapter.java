package com.example.util;

import java.lang.ref.SoftReference;

import com.example.csdn_blog.R;
import com.example.network.PictureAsyncTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpertDetailListViewAdapter extends NewsListViewAdapter{

	public ExpertDetailListViewAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(viewCache.containsKey(position) && viewCache.get(position).get() != null) 
		{
			TextView title = (TextView) viewCache.get(position).get().findViewById(R.id.expert_detail_title);
			TextView summary = (TextView) viewCache.get(position).get().findViewById(R.id.expert_detail_summary);
			TextView expert_pageview_publishtime = (TextView) viewCache.get(position).get().findViewById(R.id.expert_pageview_publishtime);
	
			return viewCache.get(position).get();
		}
		LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.expert_detail_item_layout, null);
		//标题
		TextView title = (TextView) linearLayout.findViewById(R.id.expert_detail_title);
		title.setText(newsList.get(position).title);
		//摘要
		TextView summary = (TextView) linearLayout.findViewById(R.id.expert_detail_summary);
		summary.setText("    "+newsList.get(position).summary);
		System.out.println(newsList.get(position).title);
		//发布时间和浏览数
		TextView expert_pageview_publishtime = (TextView) linearLayout.findViewById(R.id.expert_pageview_publishtime);
		expert_pageview_publishtime.setText(newsList.get(position).publishTime+"   浏览数:"+newsList.get(position).pageview);
		
		viewCache.put(position, new SoftReference<LinearLayout>(linearLayout));
		return linearLayout;
	}
}
