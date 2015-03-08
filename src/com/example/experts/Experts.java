package com.example.experts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csdn_blog.R;
import com.example.myclass.Expert;
import com.example.network.Network;
import com.example.network.PictureAsyncTask;
import com.example.util.FunctionUtils;

public class Experts {
	private final static String tag = "Experts";
	private final static int GET_SUCCESS = 1;//获取信息成功
	private final static int GET_FAIL = 2;//获取信息失败
	
	private String cacheFilePath = "/CSDN/Cache/Experts/";//缓存文件夹路径
	private String cacheFileName = "";//缓存文件名
	
	private Context context = null;
	private String expertsUrl = "";
	private Network network = null;
	private Handler handler = null;
	
	private List<Expert> expertsList = null;
	private List<Expert> expertsListTemp = null;
	
	private GridLayout experts_table = null;
	private TextView experts_searching_icon = null;
	
	//缓存
	private HashMap<String,SoftReference<List<Expert> > > expertsCache = new HashMap<String,SoftReference<List<Expert> > >();
	
	public Experts(Context context,FrameLayout experts_grid)
	{
		this.context = context;
		this.experts_table = (GridLayout) experts_grid.findViewById(R.id.experts_table);
		this.experts_searching_icon = (TextView) experts_grid.findViewById(R.id.experts_searching_icon);
		
		network = new Network();
		
		//初始化Handler对象
		initHandler();
	}
	/**
	 * 
	 * 初始化Handler对象
	 * 
	 * */
	private void initHandler()
	{
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what)
				{
				//获取信息成功
				case GET_SUCCESS:
					updateUI();
					saveCacheToMemory();//将博客专家信息保存到内存缓存中
					saveCacheToFile();//将博客专家信息保存到缓存文件中
					break;
				//获取信息失败
				case GET_FAIL:
					experts_searching_icon.clearAnimation();
					Toast.makeText(context, "获取信息失败", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
		};
	}
	/**
	 * 
	 * 初始化
	 * 
	 * */
	public void init(String expertsUrl,String cacheFileName)
	{
		this.expertsUrl = expertsUrl;
		this.cacheFileName = cacheFileName;
		
		if(!Network.isAvailable(context)) Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
		
		//移除先前所有的子控件
		experts_table.removeAllViews();
		
		boolean success;
		//从内存中读取缓存
		success = readCacheFromMemory();
		if(success)
		{
			updateUI();
		}
		else
		{
			//从文件中读取上次保存的缓存
			success = readCacheFromFile();
			if(success)
			{
				updateUI();
			}
			else
			{
				experts_searching_icon.setVisibility(View.VISIBLE);
				Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading_icon_anim);
				experts_searching_icon.startAnimation(anim);
				//通过网络获取
				getData();
			}
		}
	}
	/**
	 * 
	 * 将博客专家信息保存到内存缓存中
	 * 
	 * */
	public void saveCacheToMemory()
	{
		if(expertsCache.containsKey(cacheFileName))
		{
			if(expertsCache.get(cacheFileName).get() == null)
			{
				if(expertsList != null) expertsCache.put(cacheFileName,new SoftReference<List<Expert> >(expertsList));
			}
		}
		else
		{
			if(expertsList != null) expertsCache.put(cacheFileName,new SoftReference<List<Expert> >(expertsList));
		}
	}
	/**
	 * 
	 * 从内存中读取缓存
	 * 
	 * @return 内存缓存存在而且读取成功返回true,否则返回false
	 * 
	 * */
	public boolean readCacheFromMemory()
	{
		try
		{
			if(expertsCache.containsKey(cacheFileName))
			{
				expertsListTemp = expertsCache.get(cacheFileName).get();
				//在内存中缓存
				if(expertsListTemp != null)
				{
					expertsList = expertsListTemp;
					updateUI();
					return true;
				}
				else return false;
			}
			else return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 
	 * 将博客专家信息保存到缓存文件中
	 * 
	 * */
	public void saveCacheToFile()
	{
		if(expertsList == null)
    	{
    		Log.i(tag,"expertsList is null,save cache to file fail");
    		return;
    	}
    	//sd卡没有挂载
    	if(!FunctionUtils.sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,save cache to file fail");
    		return;
    	}
    	Log.i(tag,"start save experts cache to file");
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
			}//创建缓存文件夹
    	}
    	try
    	{
    		file = new File(Environment.getExternalStorageDirectory(),cacheFilePath+"/"+cacheFileName+".xml");
			createSuccess = file.createNewFile();
    	}
    	catch(Exception e2)
    	{
    		e2.printStackTrace();
    		return;
    	}
    	FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			xmlSerializer.setOutput(fos, "utf-8");
			xmlSerializer.startDocument("utf-8", true);
			
			xmlSerializer.startTag(null, "expertsList");
			//遍历newsList
			for(int i=0;i<expertsList.size();i++)
			{
				Expert expert = expertsList.get(i);
				
				xmlSerializer.startTag(null, "expertsitem");
				
				xmlSerializer.startTag(null, "headPictureUrl");
				xmlSerializer.text(expert.headPictureUrl);
				xmlSerializer.endTag(null, "headPictureUrl");
				
				xmlSerializer.startTag(null, "name");
				xmlSerializer.text(expert.name);
				xmlSerializer.endTag(null, "name");
				
				xmlSerializer.startTag(null, "blogUrl");
				xmlSerializer.text(expert.blogUrl);
				xmlSerializer.endTag(null, "blogUrl");
				
				xmlSerializer.endTag(null, "expertsitem");
			}
			xmlSerializer.endDocument();
			
			fos.close();//关闭输出流
			
			Log.i(tag,"save experts cache to file finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * 从缓存文件中读取缓存信息
	 * 
	 * @return 文件缓存存在而且读取成功返回true,否则返回false
	 * 
	 * */
	public boolean readCacheFromFile()
	{
		//sd卡没有挂载
    	if(!FunctionUtils.sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,read cache from file fail");
    		return false;
    	}
    	File file = new File(Environment.getExternalStorageDirectory(),cacheFilePath+"/"+cacheFileName+".xml");
    	//不存在缓存文件
    	if(!file.exists())
    	{
    		Log.i(tag,"experts cache file not exist,read cache from file fail");
    		return false;
    	}
    	FileInputStream fis = null;
    	XmlPullParser xmlPullParser = Xml.newPullParser();
    	expertsList = new ArrayList<Expert>();
    	Expert expert = null;
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
    				if("expertsitem".equals(xmlPullParser.getName()))
    				{
    					expert = new Expert();
    				}
    				else if("headPictureUrl".equals(xmlPullParser.getName()))
    				{
    					expert.headPictureUrl = xmlPullParser.nextText();
    				}
    				else if("name".equals(xmlPullParser.getName()))
    				{
    					expert.name = xmlPullParser.nextText();
    				}
    				else if("blogUrl".equals(xmlPullParser.getName()))
    				{
    					expert.blogUrl = xmlPullParser.nextText();
    				}
    				break;
    			case XmlPullParser.END_TAG:
    				if("expertsitem".equals(xmlPullParser.getName()))
    				{
    					expertsList.add(expert);
    					expert = null;
    				}
    				break;
    			}
    			type = xmlPullParser.next();
    		}
    		if(expertsList==null) Log.i(tag,"expertsList is null");
    		else Log.i(tag,"expertsList is not null");
    		System.out.println("expertsList size is "+expertsList.size());
    		fis.close();//关闭输入流
    		if(expertsList.size()>0) return true;
    		else return false;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
	}
	/**
	 * 
	 * 通过网络获取博客专家信息
	 * 
	 * */
	public void getData()
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				Log.i(tag,"start get experts data");
				String html = network.getData(expertsUrl);
				expertsList = network.parseBlogExpertsHtml(html);
				Log.i(tag,"get experts data finish");
				//下拉刷新(获取数据)成功
				if(expertsList != null) 
				{
					handler.sendEmptyMessage(GET_SUCCESS);
				}
				//下拉刷新(获取数据)失败
				else handler.sendEmptyMessage(GET_FAIL);
			}
    	}).start();
	}
	/**
	 * 
	 * 更新UI
	 * 
	 * */
	public void updateUI()
	{
		experts_searching_icon.clearAnimation();
		experts_searching_icon.setVisibility(View.GONE);
		
		for(Expert expert:expertsList)
		{
			LinearLayout gridItemView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.expert_item_layout, null);
			ImageView expert_picture = (ImageView) gridItemView.findViewById(R.id.expert_picture);
			TextView expert_name = (TextView) gridItemView.findViewById(R.id.expert_name);
			new PictureAsyncTask(context,expert_picture).execute(expert.headPictureUrl);
			expert_name.setText(expert.name);
			final Expert temp = expert;
			gridItemView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Log.i(tag,"the home page of this author is " + temp.blogUrl);
					Intent intent = new Intent(context,ExpertDetailActivity.class);
					intent.putExtra("expert", temp);
					context.startActivity(intent);
				}
			});
			experts_table.addView(gridItemView);
		}
	}
}
