package com.example.blog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.example.csdn_blog.R;
import com.example.myclass.News;
import com.example.network.BlogDetailAsyncTask;

public class BlogDetailActivity extends Activity {
	private final static String tag = "BlogDetailActivity";
	
	private News news = null;//保存当前的博客项信息
	private int position = -1;//保存当前的博客项位置信息
	public boolean hasRead = false;//标记该博客项是否已读(只有加载成功才算已读)
	private static String cacheFileName = "";//缓存文件名
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置布局
		this.setContentView(R.layout.blog_detail_layout);
		
		news = (News) getIntent().getSerializableExtra("news");
		position = getIntent().getIntExtra("position", -1);
		cacheFileName = getIntent().getStringExtra("cacheFileName");
		
		//if(!Network.isAvailable(this)) Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        //else new BlogDetailAsyncTask(this,news).execute(news.textUrl);
		new BlogDetailAsyncTask(this,news,cacheFileName).execute(news.textUrl);
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
