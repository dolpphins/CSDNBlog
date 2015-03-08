package com.example.column;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.blog.BlogDetailActivity;
import com.example.blog.BlogNews;
import com.example.csdn_blog.MainActivity;
import com.example.myclass.News;
import com.example.network.Network;
import com.example.util.NewsListViewAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;


public class ColumnDetail extends BlogNews {
	private final String tag = "ColumnDetail";
	
	public ColumnDetail(Context context, PullToRefreshListView newsListView,
			String baseUrlString, String cacheFileName,
			 NewsListViewAdapter newsListViewAdapter) {
		super(context, newsListView, baseUrlString, cacheFileName,
				-1, newsListViewAdapter);
	}
	
	@Override
	protected void getData()
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Log.i(tag,"start getData");
				Log.i(tag,baseUrlString);
				String html = network.getData(baseUrlString);
				List<News> list = network.parseOneColumnHtml(html);
				Log.i(tag,"getData finish");
				//����ˢ��(��ȡ����)�ɹ�
				if(list != null) 
				{
					newsList = list;
					handler.sendEmptyMessage(REQUEST_FINISH_SUCCESS);
				}
				//����ˢ��(��ȡ����)ʧ��
				else handler.sendEmptyMessage(REQUEST_FINISH_FAIL);
			}
    	}).start();
	}
	
	@Override
	protected void setEvent()
	{
		newsListView.setOnRefreshListener(new OnRefreshListener2() {
        	//����ˢ��
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				Log.i(tag,"pull down to refresh");
				if(!network.isAvailable(context))
				{
					handler.sendEmptyMessage(FRESH_FINISH);
					Toast.makeText(context, "���粻����", Toast.LENGTH_SHORT).show();	
				}
				else getData();
			}
			//���ظ���
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				Log.i(tag,"pull up to refresh");
				if(!network.isAvailable(context))
				{
					handler.sendEmptyMessage(FRESH_FINISH);
					Toast.makeText(context, "���粻����", Toast.LENGTH_SHORT).show();
				}
				else loadMore();
			}
		});
        
        newsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context,BlogDetailActivity.class);
				intent.putExtra("news", newsList.get(position-1));
				intent.putExtra("position", position-1);
				intent.putExtra("cacheFileName", cacheFileName);
				((Activity) context).startActivity(intent);
			}
		});
	}
	@Override
	protected void loadMore()
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				currentPage++;
				String urlString = baseUrlString + "?&page=" + currentPage;
				System.out.println(urlString);
				Log.i(tag,"start loadMore");
				String html = network.getData(urlString);
				newsListTemp = network.parseOneColumnHtml(html);
				Log.i(tag,"loadMore finish");
				//���ظ���ɹ�
				if(newsListTemp != null) handler.sendEmptyMessage(LOADMORE_FINISH_SUCCESS);
				//���ظ���ʧ��
				else handler.sendEmptyMessage(LOADMORE_FINISH_FAIL);
			}
    	}).start();
	}
	/**
	 * 
	 * ���û����ļ���
	 * 
	 * */
	public void setCacheFolder(String cacheFilePath)
	{
		this.cacheFilePath = cacheFilePath;
	}
	
}
