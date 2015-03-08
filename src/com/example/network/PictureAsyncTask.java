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
	private final static String pictureCacheFilePath = "/CSDN/Cache/HeadPicture";//头像图片缓存文件夹
	private String pictureFileName;//保存在缓存文件中的图片名
	private ImageView imageView = null;
	private String urlString = "";//当前正在获取的图片的地址
	
	private Context context = null;
	private String settingSharePreferenceFileName = "setting.xml";
	//图片缓存
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
		//如果内存缓存有该图片
		if((result = readBitmapFromMemory(pictureFileName)) != null) return result;
		//如果文件缓存中有该图片
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
		//将获取到的图片保存到内存缓存中
		saveBitmapToMemory(FunctionUtils.getPictureNameByUrl(urlString),result);
		//将获取到的图片保存到文件缓存中
		savePictureCacheToFile(result);
		
		super.onPostExecute(result);
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	/**
	 * 
	 * 将位图保存到内存缓存中
	 * 
	 * @param pictureFileName 要保存的位图名
	 * 
	 * @param bitmap 要保存的位图
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
	 * 从内存缓存中读取一张位图
	 * 
	 * @param pictureFileName 要读取的位图名
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
	 * 由输入流得到一张位图
	 * 
	 * @param inputStream 输入流
	 * 
	 * @return 读取图片成功时返回一张位图,失败返回null
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
	 * 每次获取到图片后调用该方法，将图片保存到缓存文件中
	 * 
	 * @param bitmap 要保存的位图，保存格式为JPG
	 * 
	 * */
	private void savePictureCacheToFile(Bitmap bitmap)
	{
		//sd卡没有挂载
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
			}//创建缓存文件
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
    		bitmap.compress(CompressFormat.JPEG, 100, fos);//保存文件到指定的路径
    		fos.close();//关闭输出流
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
}
