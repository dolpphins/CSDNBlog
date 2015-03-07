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

import com.example.myclass.ColumnItem;
import com.example.myclass.News;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 网络类
 * 
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
	 * 
	 * @param urlString 要获取的数据的地址
	 * 
	 * @return 如果获取成功返回获取到的html源代码，失败返回null
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
	 * 解析获取到的博客html
	 * 
	 * @param html 要解析的html源代码
	 * 
	 * @return 解析成功返回一个集合，失败返回null
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
	 * 
	 * @param 某一博客正文的地址
	 * 
	 * @return 返回该博客正文的内容,获取失败返回null
	 * 
	 * */
	public String getBlogDetail(String urlString)
	{
		try
		{
			Log.i(tag,"the blog url is "+urlString);
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
	 * 
	 * @param html 获取博客正文得到的html源代码
	 * 
	 * @return 解析成功返回博客正文,失败返回null
	 * 
	 * */
	public String parseBlogDetailHtml(String html)
	{
		try
		{
			int start = html.indexOf("<div id=\"article_content\" class=\"article_content\">");
			int end = html.indexOf("<!-- Baidu Button BEGIN -->");
			String result = "";
			Log.i(tag,"start:"+start);
			Log.i(tag,"end:"+end);
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
	 * 解析获取到的专栏html
	 * 
	 * @param html 要解析的专栏html源代码
	 * 
	 * @return 解析成功返回专栏列表，解析失败返回null
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
			//头像链接
			Pattern patternSrc = Pattern.compile("(?<=src=\")(.*?)(?=\")");
			//标题
			Pattern patternTitle = Pattern.compile("(?<=class=\"title\">)(.*?)(?=</a>)");
			//摘要		
			Pattern patternSummmary = Pattern.compile("(?<=class=\"title\">)(.*?)(?=</dd>)");
			//正文链接
			Pattern patternTextUrl = Pattern.compile("(?<=href=\")(.*?)(?=\")");
			//发布者
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
					news.textUrl = "http://blog.csdn.net" + matcherTextUrl.group();
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
	/**
	 * 
	 * 解析某一专栏的html源代码
	 * 
	 * @param html 要解析的html源代码
	 * 
	 * @return 解析成功返回该专栏的所有文章列表,失败返回null
	 * 
	 * */
	public List<News> parseOneColumnHtml(String html)
	{
		try
		{
			List<News> newsList = new ArrayList<News>();
			int i,count;
			int start = html.indexOf("<h1 class=\"tit");
			int end = html.indexOf("<div class=\"page_nav\">");
			
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			String[] strArray = str1.split("class=\"blog_list\">");
			//标题
			Pattern patternTitle = Pattern.compile("(?<=target=\"_blank\">)(.*?)(?=</a>)");
			//摘要		
			Pattern patternSummmary = Pattern.compile("(?<=<p>)(.*?)(?=</p>)");
			//正文链接
			Pattern patternTextUrl = Pattern.compile("(?<=http://)(.*?)(?=\" target=\"_blank\")");
			//发布者
			Pattern patternAuthor = Pattern.compile("(?<=class=\"user_name\">)(.*?)(?=</a>)");
			//发布时间
			Pattern patternPublishTime = Pattern.compile("(?<=<span class=\"time\">)(.*?)(?=</span>)");
			
			count = strArray.length;
			System.out.println("count:"+count);
			for(i = 1 ; i < count ; i++)
			{
				System.out.println("strArray["+i+"] -->\n" + strArray[i]);
				Matcher matcherTitle = patternTitle.matcher(strArray[i]);
				Matcher matcherSummary = patternSummmary.matcher(strArray[i]);
				Matcher matcherTextUrl = patternTextUrl.matcher(strArray[i]);
				Matcher matcherAuthor = patternAuthor.matcher(strArray[i]);
				Matcher matcherPublishTime = patternPublishTime.matcher(strArray[i]);
				
				if(matcherTitle.find() && matcherSummary.find() && matcherTextUrl.find() && matcherAuthor.find() && matcherPublishTime.find())
				{
					News news = new News();
					news.title = matcherTitle.group();
					news.summary = matcherSummary.group();
					news.textUrl = "http://" + matcherTextUrl.group();
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
	 * 
	 * 解析得到某一专栏的具体信息,包括专栏创建者，专栏创建时间,专栏文章数和专栏浏览数
	 * 
	 * */
	public ColumnItem parseColumnInfo(String html)
	{
		try
		{
			ColumnItem columnItem = new ColumnItem();
			int i,count;
			int start = html.indexOf("<div class=\"page_nav\">");
			int end = html.indexOf("</ul>");
			
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			
			Pattern patternCreator = Pattern.compile("(?<=<li>)(.*?)(?=<a href=)");
			Matcher matcherCreator = patternCreator.matcher(str1);
			if(matcherCreator.find()) columnItem.ColumnCreateTime = matcherCreator.group();
			
		    Pattern pattern = Pattern.compile("(?<=<li>)(.*?)(?=</li>)");
			Matcher matcher = patternCreator.matcher(str1);
			if(matcher.find()) columnItem.ColumnCreateTime = matcher.group();
			if(matcher.find()) columnItem.ColumnNumberOfPassage = Integer.parseInt(matcher.group());
			if(matcher.find()) columnItem.ColumnPageView = Integer.parseInt(matcher.group());
			
			return columnItem;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
