package com.example.blog;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.example.csdn_blog.R;
import com.example.myclass.News;
import com.example.network.BlogDetailAsyncTask;

public class BlogDetailActivity extends Activity {
	private final static String tag = "BlogDetailActivity";
	
	private News news = null;//保存当前的博客项信息
	private int position = -1;//保存当前的博客项位置信息
	public boolean hasRead = false;//标记该博客项是否已读(只有加载成功才算已读)
	private static String cacheFileName = "";//缓存文件名
	private ImageView detail_item_back = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置布局
		this.setContentView(R.layout.blog_detail_layout);
		
		detail_item_back = (ImageView) this.findViewById(R.id.detail_item_back);
		
		news = (News) getIntent().getSerializableExtra("news");
		position = getIntent().getIntExtra("position", -1);
		cacheFileName = getIntent().getStringExtra("cacheFileName");
		
		//if(!Network.isAvailable(this)) Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        //else new BlogDetailAsyncTask(this,news).execute(news.textUrl);
		new BlogDetailAsyncTask(this,news,cacheFileName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,news.textUrl);
		
		detail_item_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("position", position);
				data.putExtra("hasRead", hasRead);
				setResult(100, data);
				onBackPressed();
				BlogDetailActivity.this.finish();
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent data = new Intent();
			data.putExtra("position", position);
			data.putExtra("hasRead", hasRead);
			setResult(100, data);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
