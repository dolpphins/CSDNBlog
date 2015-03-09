package com.example.column;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.BlogDetailActivity;
import com.example.csdn_blog.R;
import com.example.myclass.ColumnItem;
import com.example.myclass.News;
import com.example.network.ColumnSearchAsyncTask;
import com.example.network.Network;
import com.example.util.ColumnListViewAdapter;

public class ColumnSearchActivity extends Activity{	
	private final static String tag = "ColumnSearchActivity";
	
	private ImageView detail_item_back = null;
	private EditText column_search_edit = null;
	private TextView column_search_button = null;
	private ListView column_search_result = null;
	private TextView column_searching_icon = null;
	
	private List<News> searchResultList = new ArrayList<News>();
	private ColumnListViewAdapter columnListViewAdapter = null;
	private String keywork = "";
	private int currentPageIndex = 0;//记录当前加载到第几页
	private String baseUrlString = "http://blog.csdn.net/column/list.html";
	
	public boolean isLoading = false;//标识是否正在获取数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置布局
		this.setContentView(R.layout.column_search_layout);
		
		detail_item_back = (ImageView) this.findViewById(R.id.detail_item_back);
		column_search_edit = (EditText) this.findViewById(R.id.column_search_edit);
		column_search_button = (TextView) this.findViewById(R.id.column_search_button);
		column_search_result = (ListView) this.findViewById(R.id.column_search_result);
		column_searching_icon = (TextView) this.findViewById(R.id.column_searching_icon);
		
		columnListViewAdapter = new ColumnListViewAdapter(ColumnSearchActivity.this);
		columnListViewAdapter.setDataSet(searchResultList);
		column_search_result.setAdapter(columnListViewAdapter);
		
		detail_item_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		column_search_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				keywork = column_search_edit.getText().toString().trim();
				if("".equals(keywork)) Toast.makeText(ColumnSearchActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
				else
				{
					if(!Network.isAvailable(ColumnSearchActivity.this))
						Toast.makeText(ColumnSearchActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
					else
					{
						searchResultList.clear();
						columnListViewAdapter.notifyDataSetChanged();
						
						columnListViewAdapter = new ColumnListViewAdapter(ColumnSearchActivity.this);
						columnListViewAdapter.setDataSet(searchResultList);
						column_search_result.setAdapter(columnListViewAdapter);
						
						currentPageIndex = 1;
						String urlString = baseUrlString + "?q=" + keywork;
						if(currentPageIndex>1) urlString += "&page=" + currentPageIndex;
						isLoading = true;
						//new ColumnSearchAsyncTask(ColumnSearchActivity.this,column_search_result,column_searching_icon,searchResultList,columnListViewAdapter).execute(keywork,urlString);
						new ColumnSearchAsyncTask(ColumnSearchActivity.this,column_search_result,column_searching_icon,searchResultList,columnListViewAdapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keywork,urlString);
					}
				}
			}
		});
		column_search_result.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState)
				{
				//不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					//最后一项可见时
					if(view.getLastVisiblePosition() == view.getCount() - 1)
					{
						currentPageIndex++;
						String urlString = baseUrlString + "?q=" + keywork;
						if(currentPageIndex>1) urlString += "&page=" + currentPageIndex;
						if(!isLoading)
						{
							isLoading = true;
							new ColumnSearchAsyncTask(ColumnSearchActivity.this,column_search_result,column_searching_icon,searchResultList,columnListViewAdapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keywork,urlString);
						}
						else
						{
							column_searching_icon.setVisibility(View.VISIBLE);
							Animation anim = AnimationUtils.loadAnimation(ColumnSearchActivity.this,R.anim.loading_icon_anim);
							if(column_searching_icon.getAnimation() == null) column_searching_icon.startAnimation(anim);
						}
					}
					break;
					//滚动时
					case OnScrollListener.SCROLL_STATE_FLING:
						column_searching_icon.clearAnimation();
						column_searching_icon.setVisibility(View.GONE);
						break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		column_search_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(tag,"click item "+position);
				Intent intent = new Intent(ColumnSearchActivity.this,ColumnItemActivity.class);
				ColumnItem columnItem = new ColumnItem();
				columnItem.ColumnUrl = searchResultList.get(position).textUrl;
				columnItem.ColumnName = searchResultList.get(position).title;
				intent.putExtra("columnItem", columnItem);
				startActivity(intent);
			}
		});
	}
}
