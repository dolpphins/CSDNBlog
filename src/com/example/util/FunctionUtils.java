package com.example.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * 工具函数类
 * 
 * */
public class FunctionUtils {
	private final static String tag = "FunctionUtils";
	
	/**
     * dip转px
     * 
     * @param context 上下文
     * 
     * @param dip 要转换的dip的值
     * 
     * @return 转换后的像素值
     * 
     * */
    public static int dip2px(Context context,float dip)
    {
    	float scale =context.getResources().getDisplayMetrics().density;
    	return (int)(dip*scale+0.5f);
    }
    /**
	 * 判断SD卡是否挂载
	 * 
	 * @return 如果sd卡挂载返回true，否则返回false
	 * 
	 * */
	public static boolean sdcardExist()
	{
		String status = Environment.getExternalStorageState();
		if(status.equals(android.os.Environment.MEDIA_MOUNTED)) return true;
		else return false;
	}
	/**
	 * 由图片路径得到图片名
	 * 
	 * @param urlString 图片在网络上的路径
	 * 
	 * @return 由url得到图片名
	 * 
	 * */
	public static String getPictureNameByUrl(String urlString)
	{
		int start = urlString.lastIndexOf("/");
		int end = urlString.lastIndexOf(".");
		String pictureFileName = urlString.substring(start+1, end);
		Log.i(tag,pictureFileName);
		return pictureFileName;
	}
	/**
	 * 
	 * 由专栏路径得到专栏唯一标识
	 * 
	 * @param urlString 某一专栏路径
	 * 
	 * @return 返回保存该专栏缓存的唯一文件名
	 * 
	 * */
	public static String getColumnNameByUrl(String urlString)
	{
		int start = urlString.lastIndexOf("/");
		int end = urlString.lastIndexOf(".");
		String pictureFileName = urlString.substring(start+1, end);
		Log.i(tag,pictureFileName);
		return pictureFileName;
	}
	/**
	 * 读取图片文件缓存
	 * 
	 * @param pictureFileName 图片文件名
	 * 
	 * @param pictureCacheFilePath 图片缓存文件夹在sd卡上的路径
	 * 
	 * @return 从图片缓存文件夹读取图片，如果不存在返回null
	 * 
	 * */
	public static Bitmap readPictureCacheFromFile(String pictureFileName,String pictureCacheFilePath)
	{
    	//sd卡没有挂载
    	if(!sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,read cache from file fail");
    		return null;
    	}
    	File file = new File(Environment.getExternalStorageDirectory(),pictureCacheFilePath+"/"+pictureFileName+".jpg");
    	//不存在缓存文件
    	if(!file.exists())
    	{
    		Log.i(tag,"cache file not exist,read cache from file fail");
    		return null;
    	}
    	FileInputStream fis = null;
    	try
    	{
    		fis = new FileInputStream(file);
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();        
            byte[] buffer = new byte[1024];        
            int len = 0;        
            while( (len=fis.read(buffer)) != -1){        
                outStream.write(buffer, 0, len);        
            }
            byte[] data = outStream.toByteArray();
            if(data==null) return null;
    		Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
    		outStream.close();
    		fis.close();//关闭输入流
    		if(bitmap==null) Log.i(tag,"bitmap is null");
    		else Log.i(tag,"bitmap is not null");
    		return bitmap;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
	}
	/**
	 * 获取博客正文的文件缓存
	 * 
	 * @param urlString 该博客在网络的URL，用于标识唯一
	 * 
	 * @param blogDetailCacheFilePath 博客正文文件夹在sd卡上的路径
	 * 
	 * @param cacheFileName 博客正文缓存文件
	 * 
	 * @return 返回从缓存文件中读取的博客正文，不存在返回null
	 * 
	 * */
	public static String readBlogDetailCacheFromFile(String urlString,String blogDetailCacheFilePath,String cacheFileName)
	{
		System.out.println("satrt read blog detail cache");
		FileInputStream fis = null;
		BufferedReader br = null;
		try
		{
			//sd卡没有挂载
	    	if(!sdcardExist())
	    	{
	    		Log.i(tag,"sdcard is not exist,read cache from file fail");
	    		return null;
	    	}
	    	File file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath+"/"+cacheFileName+".txt");
	    	if(!file.exists()) return null;
	    	fis = new FileInputStream(file);
	    	br = new BufferedReader(new InputStreamReader(fis));
	    	String result = "";
	    	String line = "";
	    	while((line = br.readLine()) != null)
	    	{
	    		result += line;
	    	}
	    	System.out.println("the blog detail cache is "+result);
	    	fis.close();
	    	br.close();
	    	String expString = "(?<="+urlString+"1"+")(.*?)(?="+urlString+"2"+")";
	    	Pattern pattern = Pattern.compile(expString);
	    	Matcher matcher = pattern.matcher(result);
	    	System.out.println("read blog detail cache finish");
	    	if(matcher.find()) return matcher.group();
	    	else return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try {
				br.close();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 将博客正文保存到缓存文件中
	 * 
	 * @param urlString 该博客在网络上的URL
	 * 
	 * @param content 博客正文内容
	 * 
	 * @param blogDetailCacheFilePath 博客正文文件夹在sd卡上的路径
	 * 
	 * @param cacheFileName 博客正文缓存文件名
	 * 
	 * */
	public static void saveBlogDetailCacheToFile(String urlString,String content,String blogDetailCacheFilePath,String cacheFileName)
	{
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try
		{
			//sd卡没有挂载
	    	if(!sdcardExist())
	    	{
	    		Log.i(tag,"sdcard is not exist,save cache to file fail");
	    		return;
	    	}
	    	File file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath);
	    	if(!file.exists()) file.mkdirs();
	    	file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath+"/"+cacheFileName+".txt");
	    	if(!file.exists()) file.createNewFile();
	    	fos = new FileOutputStream(file,true);//追加方式写入
	    	bw = new BufferedWriter(new OutputStreamWriter(fos));
	    	String str = urlString+"1"+content+urlString+"2\r\n";
	    	bw.write(str);
	    	bw.close();
	    	fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 清空博客正文缓存信息
	 * 
	 * @param blogDetailCacheFilePath 博客正文文件夹在sd卡上的路径
	 * 
	 * @param cacheFileName 博客正文缓存文件名
	 * 
	 * @return 当缓存文件存在而且清理不成功返回false,其它情况返回true
	 * 
	 * */
	public static boolean cleanBlogDetailCache(String blogDetailCacheFilePath,String cacheFileName)
	{
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try
		{
			//sd卡没有挂载
	    	if(!sdcardExist())
	    	{
	    		Log.i(tag,"sdcard is not exist,save cache to file fail");
	    		return true;
	    	}
	    	File file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath);
	    	if(!file.exists()) file.mkdirs();
	    	file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath+"/"+cacheFileName+".txt");
	    	if(!file.exists()) return true;
	    	fos = new FileOutputStream(file);
	    	bw = new BufferedWriter(new OutputStreamWriter(fos));
	    	String str = "";
	    	bw.write(str);
	    	bw.close();
	    	fos.close();
	    	return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
