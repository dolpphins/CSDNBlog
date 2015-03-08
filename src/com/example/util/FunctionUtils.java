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

import com.example.csdn_blog.MainActivity;
import com.example.myclass.Setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * ���ߺ�����
 * 
 * */
public class FunctionUtils {
	private final static String tag = "FunctionUtils";
	
	/**
     * dipתpx
     * 
     * @param context ������
     * 
     * @param dip Ҫת����dip��ֵ
     * 
     * @return ת���������ֵ
     * 
     * */
    public static int dip2px(Context context,float dip)
    {
    	float scale =context.getResources().getDisplayMetrics().density;
    	return (int)(dip*scale+0.5f);
    }
    /**
	 * �ж�SD���Ƿ����
	 * 
	 * @return ���sd�����ط���true�����򷵻�false
	 * 
	 * */
	public static boolean sdcardExist()
	{
		String status = Environment.getExternalStorageState();
		if(status.equals(android.os.Environment.MEDIA_MOUNTED)) return true;
		else return false;
	}
	/**
	 * ��ͼƬ·���õ�ͼƬ��
	 * 
	 * @param urlString ͼƬ�������ϵ�·��
	 * 
	 * @return ��url�õ�ͼƬ��
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
	 * ��ר��·���õ�ר��Ψһ��ʶ
	 * 
	 * @param urlString ĳһר��·��
	 * 
	 * @return ���ر����ר�������Ψһ�ļ���
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
	 * 
	 * �ɵ�ַ�õ�Ψһ��ʶ
	 * 
	 * @param urlString ĳһ��ַ
	 * 
	 * @return Ψһ��ʶ
	 * 
	 * */
	public static String getIdByUrl(String urlString)
	{
		int start = urlString.lastIndexOf("/");
		String id = urlString.substring(start+1);
		Log.i(tag,id);
		return id;
	}
	/**
	 * ��ȡͼƬ�ļ�����
	 * 
	 * @param pictureFileName ͼƬ�ļ���
	 * 
	 * @param pictureCacheFilePath ͼƬ�����ļ�����sd���ϵ�·��
	 * 
	 * @return ��ͼƬ�����ļ��ж�ȡͼƬ����������ڷ���null
	 * 
	 * */
	public static Bitmap readPictureCacheFromFile(String pictureFileName,String pictureCacheFilePath)
	{
    	//sd��û�й���
    	if(!sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,read cache from file fail");
    		return null;
    	}
    	File file = new File(Environment.getExternalStorageDirectory(),pictureCacheFilePath+"/"+pictureFileName+".jpg");
    	//�����ڻ����ļ�
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
    		fis.close();//�ر�������
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
	 * ��ȡ�������ĵ��ļ�����
	 * 
	 * @param urlString �ò����������URL�����ڱ�ʶΨһ
	 * 
	 * @param blogDetailCacheFilePath ���������ļ�����sd���ϵ�·��
	 * 
	 * @param cacheFileName �������Ļ����ļ�
	 * 
	 * @return ���شӻ����ļ��ж�ȡ�Ĳ������ģ������ڷ���null
	 * 
	 * */
	public static String readBlogDetailCacheFromFile(String urlString,String blogDetailCacheFilePath,String cacheFileName)
	{
		System.out.println("satrt read blog detail cache");
		FileInputStream fis = null;
		BufferedReader br = null;
		try
		{
			//sd��û�й���
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
				// TODO �Զ����ɵ� catch ��
				e1.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * ���������ı��浽�����ļ���
	 * 
	 * @param urlString �ò����������ϵ�URL
	 * 
	 * @param content ������������
	 * 
	 * @param blogDetailCacheFilePath ���������ļ�����sd���ϵ�·��
	 * 
	 * @param cacheFileName �������Ļ����ļ���
	 * 
	 * */
	public static void saveBlogDetailCacheToFile(String urlString,String content,String blogDetailCacheFilePath,String cacheFileName)
	{
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try
		{
			//sd��û�й���
	    	if(!sdcardExist())
	    	{
	    		Log.i(tag,"sdcard is not exist,save cache to file fail");
	    		return;
	    	}
	    	File file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath);
	    	if(!file.exists()) file.mkdirs();
	    	file = new File(Environment.getExternalStorageDirectory(),blogDetailCacheFilePath+"/"+cacheFileName+".txt");
	    	if(!file.exists()) file.createNewFile();
	    	fos = new FileOutputStream(file,true);//׷�ӷ�ʽд��
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
	 * ��ղ������Ļ�����Ϣ
	 * 
	 * @param blogDetailCacheFilePath ���������ļ�����sd���ϵ�·��
	 * 
	 * @param cacheFileName �������Ļ����ļ���
	 * 
	 * @return �������ļ����ڶ��������ɹ�����false,�����������true
	 * 
	 * */
	public static boolean cleanBlogDetailCache(String blogDetailCacheFilePath,String cacheFileName)
	{
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try
		{
			//sd��û�й���
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
	/**
	 * 
	 * ��ȡ��ǰ�汾��
	 * 
	 * @param context ������
	 * 
	 * @return ��ȡ�ɹ�����Ӧ�õ�ǰ�İ汾��,��ȡʧ�ܷ���null
	 * 
	 * */
	public static String getCurrentVersion(Context context)
	{
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * ��ȡ������Ϣ
	 * 
	 * @param context ������
	 * 
	 * @param settingSharedPreferenceFileName SharePreference�ļ���
	 * 
	 * @return ����������Ϣ
	 * 
	 * */
	public static Setting getSettingInfo(Context context,String settingSharePreferenceFileName)
	{
		Log.i(tag,"satrt read setting info");
		Setting setting = new Setting();
		SharedPreferences sp = context.getSharedPreferences(settingSharePreferenceFileName, Context.MODE_PRIVATE);
		boolean OnlyShowPictureInWifi = sp.getBoolean("OnlyShowPictureInWifi", false);
		setting.OnlyShowPictureInWifi = OnlyShowPictureInWifi;
		Log.i(tag,"read setting info finish");
		return setting;
	}
	/**
	 * 
	 * ����������Ϣ
	 * 
	 * @param context ������
	 * 
	 * @param settingSharedPreferenceFileName SharePreference�ļ���
	 * 
	 * @param setting Ҫ�����������Ϣ
	 * 
	 * */
	public static void setSettingInfo(Context context,String settingSharePreferenceFileName,Setting setting)
	{
		SharedPreferences sp = context.getSharedPreferences(settingSharePreferenceFileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("OnlyShowPictureInWifi", setting.OnlyShowPictureInWifi);
		editor.commit();
	}
	/**
	 * 
	 * ��ȡ�����С
	 * 
	 * @param cacheFilePath �����ļ���·��
	 * 
	 * @return ���ػ����С,��λΪbyte
	 * 
	 * */
	public static long getCacheSize(File file)
	{
		try
		{
			if(!file.exists()) return 0;
			if(!file.isDirectory()) 
			{
				return file.length();	
			}
			else
			{
				File[] childFiles = file.listFiles();
				long size = 0;
				for(File f:childFiles)
				{
					size += getCacheSize(f);
				}
				return size;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 
	 * ɾ��һ���ļ����µ������ļ�
	 * 
	 * */
	public static void deleteDir(File file)
	{
		try
		{
			if(file == null) return;
			if(!file.isDirectory())
			{
				file.delete();
			}
			else
			{
				File[] childFiles = file.listFiles();
				for(File f:childFiles)
				{
					deleteDir(f);
				}
				return;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
}
