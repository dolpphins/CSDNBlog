package com.example.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.myclass.News;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * ������
 * 
 * */
public class Network {
	private static final String tag = "Network";
	/**
	 * �ж������Ƿ����
	 * */
	public static boolean isAvailable(Context context)
	{
		try
		{
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm != null)
			{
				NetworkInfo ni = cm.getActiveNetworkInfo();
				if(ni != null)
				{
					return ni.isAvailable();
				}
			}
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * ��ȡ�������ݲ�����
	 * 
	 * @param urlString Ҫ��ȡ�����ݵĵ�ַ
	 * 
	 * @return �����ȡ�ɹ����ػ�ȡ����htmlԴ���룬ʧ�ܷ���null
	 * 
	 * */
	public String getData(String urlString)
	{
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			InputStream inputStream = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String html = "";
			String line = "";
			while((line = br.readLine()) != null)
			{
				html += line;
			}
			Log.i(tag,"html -->\n"+html);
			return html;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ������ȡ���Ĳ���html
	 * 
	 * @param html Ҫ������htmlԴ����
	 * 
	 * @return �����ɹ�����һ�����ϣ�ʧ�ܷ���null
	 * 
	 * */
	public List<News> parseBlogData(String html)
	{
		try
		{
			List<News> newsList = new ArrayList<News>();
			int i,count;
			int start = html.indexOf("<!--AdForward End--></div>");
			int end = html.indexOf("<div class=\"page_nav\">");
			
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			String[] strArray = str1.split("<div class=\"blog_list\">");
			//ͷ������
			Pattern patternSrc = Pattern.compile("(?<=src=\")(.*?)(?=\")");
			//����
			Pattern patternTitle = Pattern.compile("(?<=target=\"_blank\">)(.*?)(?=</a>)");
			//	ժҪ		
			Pattern patternSummmary = Pattern.compile("(?<=<dd>)(.*?)(?=</dd>)");
			//��������
			Pattern patternTextUrl = Pattern.compile("(?<=href=\")(.*?)(?=\")");
			//������
			Pattern patternAuthor = Pattern.compile("(?<=class=\"user_name\">)(.*?)(?=</a>)");
			//����ʱ��
			Pattern patternPublishTime = Pattern.compile("(?<=<span class=\"time\">)(.*?)(?=</span>)");
			
			count = strArray.length;
			System.out.println("count:"+count);
			for(i = 1 ; i < count ; i++)
			{
				System.out.println("strArray["+i+"] -->\n" + strArray[i]);
				Matcher matcherSrc = patternSrc.matcher(strArray[i]);
				Matcher matcherTitle = patternTitle.matcher(strArray[i]);
				Matcher matcherSummary = patternSummmary.matcher(strArray[i]);
				Matcher matcherTextUrl = patternTextUrl.matcher(strArray[i]);
				Matcher matcherAuthor = patternAuthor.matcher(strArray[i]);
				Matcher matcherPublishTime = patternPublishTime.matcher(strArray[i]);
				
				if(matcherTitle.find() && matcherSrc.find() && matcherSummary.find() && matcherTextUrl.find() && matcherAuthor.find() && matcherPublishTime.find())
				{
					News news = new News();
					news.headPictureUrl = matcherSrc.group();
					news.title = matcherTitle.group();
					news.summary = matcherSummary.group();
					news.textUrl = matcherTextUrl.group();
					news.author = matcherAuthor.group();
					news.publishTime = matcherPublishTime.group();
					newsList.add(news);
				}
			}
			return newsList;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ��ȡ����������
	 * 
	 * @param ĳһ�������ĵĵ�ַ
	 * 
	 * @return ���ظò������ĵ�����,��ȡʧ�ܷ���null
	 * 
	 * */
	public String getBlogDetail(String urlString)
	{
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			InputStream inputStream = con.getInputStream();
			//InputStream inputStream = httpEntity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String html = "";
			String line = "";
			while((line = br.readLine()) != null)
			{
				html += line;
			}
			Log.i(tag,"blog detail html -->\n"+html);
			String result = parseBlogDetailHtml(html);
			Log.i(tag,"blog detail result -->\n"+result);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ������ȡ���Ĳ�������html
	 * 
	 * @param html ��ȡ�������ĵõ���htmlԴ����
	 * 
	 * @return �����ɹ����ز�������,ʧ�ܷ���null
	 * 
	 * */
	public String parseBlogDetailHtml(String html)
	{
		try
		{
			int start = html.indexOf("<div id=\"article_content\" class=\"article_content\">");
			int end = html.indexOf("<!-- Baidu Button BEGIN -->");
			String result = "";
			result = html.substring(start, end);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * ������ȡ����ר��html
	 * 
	 * @param html Ҫ������ר��htmlԴ����
	 * 
	 * @return �����ɹ�����ר���б�����ʧ�ܷ���null
	 * 
	 * */
	public List<News> parseColumnHtml(String html)
	{
		try
		{
			List<News> newsList = new ArrayList<News>();
			int i,count;
			int start = html.indexOf("<div class=\"columns_recom\">");
			int end = html.indexOf("<div class=\"page_nav\">");
			
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			String[] strArray = str1.split("<dl>");
			//ͷ������
			Pattern patternSrc = Pattern.compile("(?<=src=\")(.*?)(?=\")");
			//����
			Pattern patternTitle = Pattern.compile("(?<=class=\"title\">)(.*?)(?=</a>)");
			//ժҪ		
			Pattern patternSummmary = Pattern.compile("(?<=class=\"title\">)(.*?)(?=</dd>)");
			//��������
			Pattern patternTextUrl = Pattern.compile("(?<=href=\")(.*?)(?=\")");
			//������
			Pattern patternAuthor = Pattern.compile("(?<=class=\"builder user_list\">)(.*?)(?=</a></dt>)");
			
			count = strArray.length;
			System.out.println("count:"+count);
			for(i = 1 ; i < count ; i++)
			{
				System.out.println("strArray["+i+"] -->\n" + strArray[i]);
				Matcher matcherSrc = patternSrc.matcher(strArray[i]);
				Matcher matcherTitle = patternTitle.matcher(strArray[i]);
				Matcher matcherSummary = patternSummmary.matcher(strArray[i]);
				Matcher matcherTextUrl = patternTextUrl.matcher(strArray[i]);
				Matcher matcherAuthor = patternAuthor.matcher(strArray[i]);
				
				if(matcherTitle.find() && matcherSrc.find() && matcherSummary.find() && matcherTextUrl.find() && matcherAuthor.find())
				{
					News news = new News();
					news.headPictureUrl = matcherSrc.group();
					news.title = matcherTitle.group();
					news.summary = matcherSummary.group().split("</a>")[1];
					news.textUrl = "http://www.csdn.net" + matcherTextUrl.group();
					news.author = matcherAuthor.group();
					
					newsList.add(news);
				}
			}
			return newsList;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
