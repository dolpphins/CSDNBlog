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
 * 网络工具类
 * */
public class Network {
	private static final String tag = "Network";
	/**
	 * 判断网络是否可用
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
	 * 获取网络数据并解析
	 * */
	public static List<News> getData(String urlString)
	{
		List<News> newsList = null;
		try
		{
			//HttpGet httpGet = new HttpGet(urlString);
			//HttpClient httpClient = new DefaultHttpClient();
			//HttpResponse httpResponse = httpClient.execute(httpGet);
			//HttpEntity httpEntity = httpResponse.getEntity();
			
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
			Log.i(tag,"html -->\n"+html);
			newsList = parseData(html);
			return newsList;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 解析网络数据
	 * */
	public static List<News> parseData(String html)
	{
		try
		{
			List<News> newsList = new ArrayList<News>();
			int i,count;

			//解析博客园html
			/*int start = html.indexOf("<div id=\"pager_top\" style=\"display:none\"></div>");
			int end = html.indexOf("<script>editorPickStat();aggSite.user.getUserInfo();</script>");
			//System.out.println("start:"+start);
			//System.out.println("end:"+end);
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			String[] strArray = str1.split("<div class=\"post_item_body\">");
			
			Pattern patternSrc = Pattern.compile("(?<=src=\")(.*?)(?=\")");
			Pattern patternTitle = Pattern.compile("(?<=target=\"_blank\">)(.*?)(?=</a></h3>)");
			Pattern patternSummmary = Pattern.compile("(?<=alt=\"\"/></a>)(.*?)(?=</p>)");
			Pattern patternTextUrl = Pattern.compile("(?<=href=\")(.*?)(?=\")");
			Pattern patternAuthor = Pattern.compile("(?<=class=\"lightblue\">)(.*?)(?=</a>)");
			Pattern patternPublishTime = Pattern.compile("(?<=发布于 )(.*?)(?=<span class=\"article_comment\">)");*/
			//解析csdn博客html
			int start = html.indexOf("<!--AdForward End--></div>");
			int end = html.indexOf("<div class=\"page_nav\">");
			//System.out.println("start:"+start);
			//System.out.println("end:"+end);
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			String[] strArray = str1.split("<div class=\"blog_list\">");
			//头像链接
			Pattern patternSrc = Pattern.compile("(?<=src=\")(.*?)(?=\")");
			//标题
			Pattern patternTitle = Pattern.compile("(?<=target=\"_blank\">)(.*?)(?=</a>)");
			//	摘要		
			Pattern patternSummmary = Pattern.compile("(?<=<dd>)(.*?)(?=</dd>)");
			//正文链接
			Pattern patternTextUrl = Pattern.compile("(?<=href=\")(.*?)(?=\")");
			//发布者
			Pattern patternAuthor = Pattern.compile("(?<=class=\"user_name\">)(.*?)(?=</a>)");
			//发布时间
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
//					System.out.println("--------------------");
//					System.out.println("src:"+news.headPictureUrl);
//					System.out.println("title:"+news.title);
//					System.out.println("summary:"+news.summary);
//					System.out.println("textUrl:"+news.textUrl);
//					System.out.println("author:"+news.author);
//					System.out.println("publishTime:"+news.publishTime);
//					System.out.println("--------------------");
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
	 * 获取博客项正文
	 * */
	public static String getBlogDetail(String urlString)
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
	 * 解析获取到的博客正文html
	 * */
	public static String parseBlogDetailHtml(String html)
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
}
