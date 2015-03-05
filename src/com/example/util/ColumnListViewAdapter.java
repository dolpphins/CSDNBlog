package com.example.util;

import java.lang.ref.SoftReference;

import com.example.csdn_blog.R;
import com.example.network.PictureAsyncTask;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColumnListViewAdapter extends NewsListViewAdapter {
	
	public ColumnListViewAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(viewCache.containsKey(position) && viewCache.get(position).get() != null) 
		{
			TextView title = (TextView) viewCache.get(position).get().findViewById(R.id.column_news_title);
			TextView summary = (TextView) viewCache.get(position).get().findViewById(R.id.column_summary);
			return viewCache.get(position).get();
		}
		LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.column_list_item_layout, null);
		//����
		TextView title = (TextView) linearLayout.findViewById(R.id.column_news_title);
		title.setText(newsList.get(position).title);
		//ͷ��
		ImageView head_picture = (ImageView) linearLayout.findViewById(R.id.column_head_picture);
		head_picture.setBackground(context.getResources().getDrawable(R.drawable.csdn));
		PictureAsyncTask pictureAsyncTask = new PictureAsyncTask(head_picture);
		pictureAsyncTask.execute(newsList.get(position).headPictureUrl);
		//ժҪ
		TextView summary = (TextView) linearLayout.findViewById(R.id.column_summary);
		summary.setText("    "+newsList.get(position).summary);
		
		viewCache.put(position, new SoftReference<LinearLayout>(linearLayout));
		return linearLayout;
	}
}
