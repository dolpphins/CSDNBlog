package com.example.blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csdn_blog.MainActivity;
import com.example.csdn_blog.R;
import com.example.myclass.News;
import com.example.network.Network;
import com.example.util.FunctionUtils;
import com.example.util.NewsListViewAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class BlogNews {
	private final static String tag = "BlogNews";
	public final static int REQUEST_FINISH_SUCCESS = 1;//����ˢ��(��ȡ����)�ɹ�
	public final static int REQUEST_FINISH_FAIL = 2;//����ˢ��(��ȡ����)ʧ��
	public final static int LOADMORE_FINISH_SUCCESS = 3;//���ظ���ɹ�
	public final static int LOADMORE_FINISH_FAIL = 4;//���ظ���ʧ��
	public final static int FRESH_FINISH = 5;//ˢ�����
	
	protected String baseUrlString = "";
	private final String cacheFilePath = "/CSDN/Cache/";//�����ļ�·��
	private final String blogDetailCacheFilePath = "/CSDN/Cache/BlogDetail";//�������Ļ����ļ���
	private String cacheFileName = "";//�����ļ���
	public int currentViewPageeIndex = 0;//��ǰViewPager����
	
	protected int currentPage = 1;//��ǰҳ��
	protected Context context = null;
	protected PullToRefreshListView newsListView = null;
	private NewsListViewAdapter newsListViewAdapter = null;
	protected List<News> newsList = null;
	protected List<News> newsListTemp = null;//��ʱ������ظ����ȡ������
	protected Handler handler = null;
	private LinearLayout view;//����ListView��ǰ�����View����
	private int itemCount = 0;//�����һҳ������
	
	protected Network network = null;//�����������������
	
	public BlogNews(Context context,PullToRefreshListView newsListView,String baseUrlString,String cacheFileName,int currentViewPageeIndex,NewsListViewAdapter newsListViewAdapter)
	{
		this.context = context;
		this.newsListView = newsListView;
		this.baseUrlString = baseUrlString;
		this.cacheFileName = cacheFileName;
		this.currentViewPageeIndex = currentViewPageeIndex;
		this.newsListViewAdapter = newsListViewAdapter;
		
		network = new Network();
		
		newsListView.setMode(Mode.BOTH);
        newsListView.getLoadingLayoutProxy(true,false).setPullLabel("����ˢ��");
        newsListView.getLoadingLayoutProxy(true,false).setReleaseLabel("�ͷ�����ˢ��");
        newsListView.getLoadingLayoutProxy(true,false).setRefreshingLabel("����ˢ��...");
        newsListView.getLoadingLayoutProxy(false,true).setPullLabel("�������ظ���");
        newsListView.getLoadingLayoutProxy(false,true).setReleaseLabel("�ͷ���������");
        newsListView.getLoadingLayoutProxy(false,true).setRefreshingLabel("���ڼ���...");
        
        //��ʼ��Handler����
        initHandler();
        //�����¼�
        setEvent();    
	}
	
	/**
	 * 
	 * ��ʼ��
	 * 
	 * */
	public void init()
	{
		if(!Network.isAvailable(context)) Toast.makeText(context, "���粻����", Toast.LENGTH_SHORT).show();
		
		//���ļ��ж�ȡ�ϴα���Ļ���
    	boolean success = readCacheFromFile();
    	if(success) 
    	{
    		newsListViewAdapter.setDataSet(newsList);
    		newsListView.setAdapter(newsListViewAdapter);//Ϊlistview����������
    	}
    	else
    	{
    		getData();
    	}
	}
	/**
	 * 
	 * ��ʼ��Handler����
	 * 
	 * */
	private void initHandler()
	{
		handler = new Handler(){
        	@Override
        	public void handleMessage(Message msg)
        	{
        		int type = msg.what;
        		switch(type)
        		{
        		//����ˢ��(��ȡ����)�ɹ�
        		case REQUEST_FINISH_SUCCESS:
        			Log.i(tag,"request success");
        			currentPage = 1;
        			newsListView.onRefreshComplete();//ˢ�����
        			if(newsList != null) itemCount = newsList.size();
        			newsListViewAdapter.setDataSet(newsList);
        	        newsListView.setAdapter(newsListViewAdapter);//Ϊlistview����������
        	        saveCacheToFile();//���滺�浽�ļ���
        	        FunctionUtils.cleanBlogDetailCache(blogDetailCacheFilePath,cacheFileName);//����������Ļ���
        			break;
        		//����ˢ��(��ȡ����)ʧ��
        		case REQUEST_FINISH_FAIL:
        			Log.i(tag,"request fail");
        			newsListView.onRefreshComplete();//ˢ��ʧ��
        			Toast.makeText(context, "��������ʧ��", Toast.LENGTH_SHORT);
        			break;
        		//���ظ���ɹ�
        		case LOADMORE_FINISH_SUCCESS:
        			Log.i(tag,"loadmore success");
        			newsListView.onRefreshComplete();//���ظ������
        			if(newsList == null) newsList = new ArrayList<News>();
        			newsList.addAll(newsListTemp);
        			break;
        		//���ظ���ʧ��
        		case LOADMORE_FINISH_FAIL:
        			Log.i(tag,"loadmore fail");
        			newsListView.onRefreshComplete();//���ظ���ʧ��
        			Toast.makeText(context, "��������ʧ��", Toast.LENGTH_SHORT);
        			break;
        		//ˢ�����
        		case FRESH_FINISH:
        			newsListView.onRefreshComplete();
        			break;
        		}
        	}
        };
	}
	/**
	 * 
	 * �����¼�
	 * 
	 * */
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
				BlogNews.this.view = (LinearLayout)view;
				Intent intent = new Intent(context,BlogDetailActivity.class);
				intent.putExtra("news", newsList.get(position-1));
				intent.putExtra("position", position-1);
				intent.putExtra("cacheFileName", cacheFileName);
				((Activity) context).startActivityForResult(intent, ((MainActivity) context).currentViewPageeIndex);
			}
		});
	}
	/**
     * ÿ������ˢ�³ɹ��󶼽����ݱ��浽�ļ���
     * 
     * */
    public void saveCacheToFile()
    {
    	if(newsList == null)
    	{
    		Log.i(tag,"newsList is null,save cache to file fail");
    		return;
    	}
    	//sd��û�й���
    	if(!FunctionUtils.sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,save cache to file fail");
    		return;
    	}
    	Log.i(tag,"start save cache to file");
    	XmlSerializer xmlSerializer = Xml.newSerializer();
    	File file = new File(Environment.getExternalStorageDirectory(),cacheFilePath);
    	boolean createSuccess = false;
    	boolean deleteSuccess = false;
    	if(!file.exists()) 
    	{
			try {
				file.mkdirs();
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}//���������ļ���
    	}
    	try
    	{
    		file = new File(Environment.getExternalStorageDirectory(),cacheFilePath+"/"+cacheFileName+".xml");
			//if(file.exists()) 
			//{
			//	File tempFile = new File(file.getAbsolutePath()+System.currentTimeMillis());
			//	file.renameTo(tempFile);
			//	deleteSuccess = file.delete();
			//}
			createSuccess = file.createNewFile();
    	}
    	catch(Exception e2)
    	{
    		e2.printStackTrace();
    		return;
    	}
//    	if(!createSuccess)
//    	{
//    		Log.i(tag,"create cache file fail");
//    		return;
//    	}
    	FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			xmlSerializer.setOutput(fos, "utf-8");
			xmlSerializer.startDocument("utf-8", true);
			
			xmlSerializer.startTag(null, "newsList");
			Log.i(tag,"itemCount:"+itemCount);
			//����newsList
			for(int i=0;i<newsList.size()&&i<itemCount;i++)
			{
				News news = newsList.get(i);
				
				xmlSerializer.startTag(null, "newsitem");
				
				xmlSerializer.startTag(null, "headPictureUrl");
				xmlSerializer.text(news.headPictureUrl);
				xmlSerializer.endTag(null, "headPictureUrl");
				
				xmlSerializer.startTag(null, "title");
				xmlSerializer.text(news.title);
				xmlSerializer.endTag(null, "title");
				
				xmlSerializer.startTag(null, "summary");
				xmlSerializer.text(news.summary);
				xmlSerializer.endTag(null, "summary");
				
				xmlSerializer.startTag(null, "textUrl");
				xmlSerializer.text(news.textUrl);
				xmlSerializer.endTag(null, "textUrl");
				
				xmlSerializer.startTag(null, "author");
				xmlSerializer.text(news.author);
				xmlSerializer.endTag(null, "author");	
				
				xmlSerializer.endTag(null, "newsitem");
			}
			xmlSerializer.endTag(null, "newsList");
			
			xmlSerializer.endDocument();
			
			fos.close();//�ر������
			
			Log.i(tag,"save cache to file finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    /**
     * ÿ�γ�������ʱ��ȡ�ϴα�����ļ�����
     * 
     * */
    private boolean readCacheFromFile()
    {
    	//sd��û�й���
    	if(!FunctionUtils.sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,read cache from file fail");
    		return false;
    	}
    	File file = new File(Environment.getExternalStorageDirectory(),cacheFilePath+"/"+cacheFileName+".xml");
    	//�����ڻ����ļ�
    	if(!file.exists())
    	{
    		Log.i(tag,"cache file not exist,read cache from file fail");
    		return false;
    	}
    	FileInputStream fis = null;
    	XmlPullParser xmlPullParser = Xml.newPullParser();
    	newsList = new ArrayList<News>();
    	News news = null;
    	try
    	{
    		fis = new FileInputStream(file);
    		xmlPullParser.setInput(fis, "utf-8");
    		int type;
    		while((type = xmlPullParser.getEventType()) != XmlPullParser.END_DOCUMENT)
    		{
    			switch(type)
    			{
    			case XmlPullParser.START_TAG:
    				if("newsitem".equals(xmlPullParser.getName()))
    				{
    					news = new News();
    				}
    				else if("headPictureUrl".equals(xmlPullParser.getName()))
    				{
    					news.headPictureUrl = xmlPullParser.nextText();
    				}
    				else if("title".equals(xmlPullParser.getName()))
    				{
    					news.title = xmlPullParser.nextText();
    				}
    				else if("summary".equals(xmlPullParser.getName()))
    				{
    					news.summary = xmlPullParser.nextText();
    				}
    				else if("textUrl".equals(xmlPullParser.getName()))
    				{
    					news.textUrl = xmlPullParser.nextText();
    				}
    				else if("author".equals(xmlPullParser.getName()))
    				{
    					news.author = xmlPullParser.nextText();
    				}
    				else if("publishTime".equals(xmlPullParser.getName()))
    				{
    					news.publishTime = xmlPullParser.nextText();
    				}
    				else if("hasRead".equals(xmlPullParser.getName()))
    				{
    					String text = xmlPullParser.nextText();
    					if(text.equals("true")) news.hasRead = true;
    				}
    				break;
    			case XmlPullParser.END_TAG:
    				if("newsitem".equals(xmlPullParser.getName()))
    				{
    					newsList.add(news);
    					news = null;
    				}
    				break;
    			}
    			type = xmlPullParser.next();
    		}
    		fis.close();//�ر�������
    		itemCount = newsList.size(); 
    		if(newsList.size()>0) return true;
    		else return false;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    /**
     * �������̻߳�ȡ��������
     * 
     * */
    protected void getData()
    {
    	new Thread(new Runnable(){

			@Override
			public void run() {
				Log.i(tag,"start getData");
				String html = network.getData(baseUrlString);
				List<News> list = network.parseBlogData(html);
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
    /**
     * �������ص���
     * 
     * */
    protected void loadMore()
    {
    	new Thread(new Runnable(){

			@Override
			public void run() {
				currentPage++;
				String urlString = baseUrlString + "?&page=" + currentPage;
				System.out.println(urlString);
				Log.i(tag,"start loadMore");
				String html = network.getData(baseUrlString);
				newsListTemp = network.parseBlogData(html);
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
     * ���Ӳ������ķ���ʱ����
     * 
     * @param data ���ش��ݹ���������
     * 
     * */
    public void handleForReadBlog(Intent data)
    {
    	int position = data.getIntExtra("position", -1);
		boolean hasRead = data.getBooleanExtra("hasRead", false);
		//�ò����Ѷ�
		if(position != -1 && hasRead) 
		{
			newsList.get(position).hasRead = true;
			Log.i(tag,position+" hasRead");
			TextView title = (TextView) view.findViewById(R.id.blog_news_title);
			TextView summary = (TextView) view.findViewById(R.id.blog_summary);
			TextView news_author_publishtime = (TextView) view.findViewById(R.id.news_author_publishtime);
			title.setTextColor(Color.parseColor("#7c7979"));
			summary.setTextColor(Color.parseColor("#7c7979"));
			news_author_publishtime.setTextColor(Color.parseColor("#7c7979"));
		}
    }
}
