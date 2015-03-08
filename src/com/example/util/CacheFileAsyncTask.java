package com.example.util;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;


public class CacheFileAsyncTask extends AsyncTask<File,Void,String> {
	private final static String tag = "CacheFileAsyncTask";
	
	public final static int TYPE_DELETE = 0;//删除缓存文件
	public final static int TYPE_SIZE = 1;//读取缓存文件大小
	
	private Context context = null;
	private TextView setting_cache_size = null;
	
	private int type = -1;
	
	public CacheFileAsyncTask(Context context,TextView setting_cache_size,int type)
	{
		this.context = context;
		this.setting_cache_size = setting_cache_size;
		this.type = type;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(File... params) {
		File file = params[0];
		//删除缓存文件
		if(type == TYPE_DELETE)
		{
			FunctionUtils.deleteDir(file);
			return null;
		}
		//读取缓存文件大小
		else if(type == TYPE_SIZE)
		{
			long size = FunctionUtils.getCacheSize(file)/(1024*1024);
	    	String result = "0.0M";
	    	if(size>0)
	    	{
	    		DecimalFormat df = new DecimalFormat("0.0");
	    		result = df.format(size) + "M";
	    	}
	    	return result;
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(type == TYPE_SIZE && result != null) setting_cache_size.setText(result);
		else if(type == TYPE_DELETE) 
		{
			setting_cache_size.setText("0.0M");
			Toast.makeText(context, "删除缓存成功", Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}

}
