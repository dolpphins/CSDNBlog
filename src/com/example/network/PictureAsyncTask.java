package com.example.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.example.csdn_blog.MainActivity;
import com.example.myclass.Setting;
import com.example.util.FunctionUtils;

public class PictureAsyncTask extends AsyncTask<String,Void,Bitmap>{
	private final static String tag = "PictureAsyncTask";
	private final static String pictureCacheFilePath = "/CSDN/Cache/HeadPicture";//ͷ��ͼƬ�����ļ���
	private String pictureFileName;//�����ڻ����ļ��е�ͼƬ��
	private ImageView imageView = null;
	private String urlString = "";//��ǰ���ڻ�ȡ��ͼƬ�ĵ�ַ
	
	private Context context = null;
	private String settingSharePreferenceFileName = "setting.xml";
	//ͼƬ����
	private static HashMap<String,SoftReference<Bitmap> > bitmapCache = new HashMap<String,SoftReference<Bitmap> >();
	
	public PictureAsyncTask(Context context,ImageView imageView)
	{
		this.context = context;
		this.imageView = imageView;
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		urlString = params[0];
		if("#".equals(urlString)) return null;
		this.urlString = urlString;
		pictureFileName = FunctionUtils.getPictureNameByUrl(urlString);
		Bitmap result = null;
		//����ڴ滺���и�ͼƬ
		if((result = readBitmapFromMemory(pictureFileName)) != null) return result;
		//����ļ��������и�ͼƬ
		if((result = FunctionUtils.readPictureCacheFromFile(pictureFileName,pictureCacheFilePath)) != null) return result; 
		if(MainActivity.setting.OnlyShowPictureInWifi && (Network.getNetworkType(context) != ConnectivityManager.TYPE_WIFI)) return null;
		try {
			HttpGet httpGet = new HttpGet(urlString);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();
			result = getBitmap(inputStream);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(Bitmap result) {
		if(result == null) return;
		imageView.setImageBitmap(result);
		//����ȡ����ͼƬ���浽�ڴ滺����
		saveBitmapToMemory(FunctionUtils.getPictureNameByUrl(urlString),result);
		//����ȡ����ͼƬ���浽�ļ�������
		savePictureCacheToFile(result);
		
		super.onPostExecute(result);
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	/**
	 * 
	 * ��λͼ���浽�ڴ滺����
	 * 
	 * @param pictureFileName Ҫ�����λͼ��
	 * 
	 * @param bitmap Ҫ�����λͼ
	 * 
	 * */
	private void saveBitmapToMemory(String pictureFileName,Bitmap bitmap)
	{
		if(bitmapCache.containsKey(pictureFileName))
		{
			if(bitmapCache.get(pictureFileName).get() == null)
			{
				bitmapCache.put(pictureFileName, new SoftReference<Bitmap>(bitmap));
				Log.i(tag,"bitmapCache size is "+bitmapCache.size());
			}
		}
		else 
		{
			bitmapCache.put(pictureFileName, new SoftReference<Bitmap>(bitmap));
			Log.i(tag,"1bitmapCache size is "+bitmapCache.size());
		}
	}
	/**
	 * 
	 * ���ڴ滺���ж�ȡһ��λͼ
	 * 
	 * @param pictureFileName Ҫ��ȡ��λͼ��
	 * 
	 * */
	private Bitmap readBitmapFromMemory(String pictureFileName)
	{
		try
		{
			if(bitmapCache.containsKey(pictureFileName))
			{
				Bitmap bitmap = bitmapCache.get(pictureFileName).get();
				if(bitmap != null) 
				{
					Log.i(tag,"the bitmap "+pictureFileName +" in the memeory cache");
					return bitmap;
				}
			}
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * ���������õ�һ��λͼ
	 * 
	 * @param inputStream ������
	 * 
	 * @return ��ȡͼƬ�ɹ�ʱ����һ��λͼ,ʧ�ܷ���null
	 * 
	 * */
	private Bitmap getBitmap(InputStream inputStream)
	{
		try
		{
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			return bitmap;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ÿ�λ�ȡ��ͼƬ����ø÷�������ͼƬ���浽�����ļ���
	 * 
	 * @param bitmap Ҫ�����λͼ�������ʽΪJPG
	 * 
	 * */
	private void savePictureCacheToFile(Bitmap bitmap)
	{
		//sd��û�й���
    	if(!FunctionUtils.sdcardExist())
    	{
    		Log.i(tag,"sdcard is not exist,save picture cache to file fail");
    		return;
    	}
    	File file = new File(Environment.getExternalStorageDirectory(),pictureCacheFilePath);
    	boolean createSuccess = true;
    	if(!file.exists())
			try {
				createSuccess = file.mkdirs();
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}//���������ļ�
    	if(!createSuccess)
    	{
    		Log.i(tag,"create picture cache file fail");
    		return;
    	}
    	FileOutputStream fos = null;
    	try
    	{
    		file = new File(Environment.getExternalStorageDirectory(),pictureCacheFilePath+"/"+pictureFileName+".jpg");
    		createSuccess = true;
    		if(file.exists()) return;
    		createSuccess = file.createNewFile();
    		if(!createSuccess) return;
    		fos = new FileOutputStream(file);
    		bitmap.compress(CompressFormat.JPEG, 100, fos);//�����ļ���ָ����·��
    		fos.close();//�ر������
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
}
