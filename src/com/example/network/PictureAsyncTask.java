package com.example.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.util.FunctionUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class PictureAsyncTask extends AsyncTask<String,Void,Bitmap>{
	private final static String tag = "PictureAsyncTask";
	private final static String pictureCacheFilePath = "/CSDN/Cache/HeadPicture";//ͷ��ͼƬ�����ļ���
	private String pictureFileName;//�����ڻ����ļ��е�ͼƬ��
	private ImageView imageView = null;
	private String urlString = "";//��ǰ���ڻ�ȡ��ͼƬ�ĵ�ַ
	
	public PictureAsyncTask(ImageView imageView)
	{
		this.imageView = imageView;
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		urlString = params[0];
		if("#".equals(urlString)) return null;
		this.urlString = urlString;
		pictureFileName = FunctionUtils.getPictureNameByUrl(urlString);
		Bitmap result = null;
		//����ļ��������и�ͼƬ
		if((result = FunctionUtils.readPictureCacheFromFile(pictureFileName,pictureCacheFilePath)) != null) return result; 
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
		imageView.setImageBitmap(result);
		//����ȡ����ͼƬ���浽�ļ�������
		if(result!=null) savePictureCacheToFile(result);
		super.onPostExecute(result);
	}
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
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
