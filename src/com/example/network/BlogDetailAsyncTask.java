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
	private final static String pictureCacheFilePath = "/CSDN/Cache/HeadPicture";//ͷ��ͼƬ�����ļ���
	private final static String blogDetailCacheFilePath = "/CSDN/Cache/BlogDetail";//�������Ļ����ļ���
	private static String cacheFileName = "";//�����ļ���
	
	private News news = null;
	private Context context = null;
	
	private String urlString = "";
	
	private ImageView blog_detail_head_picture = null;//ͷ��
	private TextView blog_detail_news_title = null;//����
	private TextView blog_detail_news_author_publishtime = null;//�����ߺͷ���ʱ��
	private WebView blog_detail_text = null;//����
	private TextView blog_detail_load_icon = null;//����ͼ��
	
	private boolean readFromFile = false;//��ǵ�ǰ���������Ƿ��Ǵ��ļ������ж�ȡ��
	
	private Network network = null;//�����������������
	
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
		//�ж��Ƿ����ڻ�����
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
		
		//����ͼ�꿪ʼ����
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
		//����ͷ��
		blog_detail_head_picture.setBackground(context.getResources().getDrawable(R.drawable.csdn));
		if(bitmap == null) new PictureAsyncTask(blog_detail_head_picture).execute(news.headPictureUrl);
		else blog_detail_head_picture.setImageBitmap(bitmap);
		//���ñ���
		blog_detail_news_title.setText(news.title);
		//���÷����ߺͷ���ʱ��
		blog_detail_news_author_publishtime.setText(news.author+" ������ "+news.publishTime);
		//��������
		blog_detail_text.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
		//�ò�������Ϊ�Ѷ�
		((BlogDetailActivity)context).hasRead = true;
		//���ò�������д�������ļ���
		if(!readFromFile) FunctionUtils.saveBlogDetailCacheToFile(urlString, result, blogDetailCacheFilePath,cacheFileName);
		
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO �Զ����ɵķ������
		super.onProgressUpdate(values);
	}
}
