package com.example.network;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.column.ColumnSearchActivity;
import com.example.csdn_blog.R;
import com.example.myclass.News;
import com.example.util.ColumnListViewAdapter;

public class ColumnSearchAsyncTask extends AsyncTask<String,Void,Void>{
	private final static String tag = "ColumnSearchAsyncTask";
	
	private Context context = null;
	private String keywork = "";
	private ListView column_search_result = null;
	private TextView column_searching_icon = null;
	private String urlString = "";
	
	private List<News> searchResultList = null;
	List<News> searchResultListTemp = null;
	private ColumnListViewAdapter columnListViewAdapter = null;
	
	private Network network = null;
	
	public ColumnSearchAsyncTask(Context context,ListView column_search_result,TextView column_searching_icon,List<News> searchResultList,ColumnListViewAdapter columnListViewAdapter)
	{
		this.context = context;
		this.column_search_result = column_search_result;
		this.column_searching_icon = column_searching_icon;
		this.searchResultList = searchResultList;
		this.columnListViewAdapter = columnListViewAdapter;
		
		network = new Network();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		column_searching_icon.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(context,R.anim.loading_icon_anim);
		if(column_searching_icon.getAnimation() == null) column_searching_icon.startAnimation(anim);
	}

	@Override
	protected Void doInBackground(String... params) {
		keywork = params[0];
		urlString = params[1];
		String html = network.getData(urlString);
		searchResultListTemp = network.parseColumnHtml(html);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		//如果软键盘显示就隐藏软键盘
		if(((Activity)context).getWindow().getDecorView() != null)
		{
			InputMethodManager m=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
			//m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			m.hideSoftInputFromWindow(column_searching_icon.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
		column_searching_icon.clearAnimation();
		column_searching_icon.setVisibility(View.GONE);
		if(searchResultListTemp == null)
			Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show();
		else if(searchResultListTemp.size() == 0)
			Toast.makeText(context, "没有更多的内容了", Toast.LENGTH_SHORT).show();
		else
		{
			searchResultList.addAll(searchResultListTemp);
			//if(((Activity)context).getWindow().getDecorView() == null)
				columnListViewAdapter.notifyDataSetChanged();
		}
		((ColumnSearchActivity)context).isLoading = false;
		
		super.onPostExecute(result);
	}
}
