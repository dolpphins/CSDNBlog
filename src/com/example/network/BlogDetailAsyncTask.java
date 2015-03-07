package com.example.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blog.BlogDetailActivity;
import com.example.csdn_blog.R;
import com.example.myclass.News;
import com.example.util.FunctionUtils;

public class BlogDetailAsyncTask extends AsyncTask<String,Void,String>{
	private final static String tag = "BlogDetailAsyncTask";
	private final static String pictureCacheFilePath = "/CSDN/Cache/HeadPicture";//头像图片缓存文件夹
	private final static String blogDetailCacheFilePath = "/CSDN/Cache/BlogDetail";//博客正文缓存文件夹
	private static String cacheFileName = "";//缓存文件名
	
	private News news = null;
	private Context context = null;
	
	private String urlString = "";
	
	private ImageView blog_detail_head_picture = null;//头像
	private TextView blog_detail_news_title = null;//标题
	private TextView blog_detail_news_author_publishtime = null;//发布者和发布时间
	private WebView blog_detail_text = null;//正文
	private TextView blog_detail_load_icon = null;//加载图标
	
	private boolean readFromFile = false;//标记当前博客正文是否是从文件缓存中读取的
	
	private Network network = null;//网络类用于网络操作
	
	//public BlogDetailAsyncTask(){}
	public BlogDetailAsyncTask(Context context,News news,String cacheFileName)
	{
		this.context = context;
		this.news = news;
		this.cacheFileName = cacheFileName; 
		
		network = new Network();
	}
	
	@Override
	protected String doInBackground(String... params) {
		urlString = params[0];
		//判断是否已在缓存中
		String result = FunctionUtils.readBlogDetailCacheFromFile(urlString,blogDetailCacheFilePath,cacheFileName);
		if(result == null) return network.getBlogDetail(urlString);
		readFromFile = true;
		return result;
	}

	@Override
	protected void onPreExecute() {
		blog_detail_head_picture = (ImageView) ((Activity)context).findViewById(R.id.blog_detail_head_picture);
		blog_detail_news_title = (TextView) ((Activity)context).findViewById(R.id.blog_detail_news_title);
		blog_detail_news_author_publishtime = (TextView) ((Activity)context).findViewById(R.id.blog_detail_news_author_publishtime);
		blog_detail_text = (WebView) ((Activity)context).findViewById(R.id.blog_detail_text);
		blog_detail_load_icon = (TextView) ((Activity)context).findViewById(R.id.blog_detail_load_icon);
		
		//加载图标开始动画
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading_icon_anim);
		blog_detail_load_icon.startAnimation(anim);
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		blog_detail_load_icon.clearAnimation();
		blog_detail_load_icon.setVisibility(View.GONE);
		
		if(result == null)
		{
			return;
		}
			
		Bitmap bitmap = FunctionUtils.readPictureCacheFromFile(news.headPictureUrl,pictureCacheFilePath);
		//设置头像
		blog_detail_head_picture.setBackground(context.getResources().getDrawable(R.drawable.csdn));
		if(bitmap == null) new PictureAsyncTask(blog_detail_head_picture).execute(news.headPictureUrl);
		else blog_detail_head_picture.setImageBitmap(bitmap);
		//设置标题
		blog_detail_news_title.setText(news.title);
		//设置发布者和发布时间
		blog_detail_news_author_publishtime.setText(news.author+" 发布于 "+news.publishTime);
		//设置正文
		blog_detail_text.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
		//该博客项标记为已读
		((BlogDetailActivity)context).hasRead = true;
		//将该博客正文写到缓存文件中
		if(!readFromFile) FunctionUtils.saveBlogDetailCacheToFile(urlString, result, blogDetailCacheFilePath,cacheFileName);
		
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO 自动生成的方法存根
		super.onProgressUpdate(values);
	}
}
