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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.myclass.ColumnItem;
import com.example.myclass.Expert;
import com.example.myclass.News;

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
	 * 
	 * �жϵ�ǰ��������
	 * 
	 * */
	public static int getNetworkType(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm != null)
		{
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if(ni != null)
			{
				if(ni.isAvailable())
				{
					return ni.getType();
				}
			}
		}
		return -1;
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
			Log.i(tag,"get data,the url is " + urlString);
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
	 * ����ĳһר����htmlԴ����
	 * 
	 * @param html Ҫ������htmlԴ����
	 * 
	 * @return �����ɹ����ظ�ר�������������б�,ʧ�ܷ���null
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
			//����
			Pattern patternTitle = Pattern.compile("(?<=target=\"_blank\">)(.*?)(?=</a>)");
			//ժҪ		
			Pattern patternSummmary = Pattern.compile("(?<=<p>)(.*?)(?=</p>)");
			//��������
			Pattern patternTextUrl = Pattern.compile("(?<=http://)(.*?)(?=\" target=\"_blank\")");
			//������
			Pattern patternAuthor = Pattern.compile("(?<=class=\"user_name\">)(.*?)(?=</a>)");
			//����ʱ��
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
	 * �����õ�ĳһר���ľ�����Ϣ,����ר�������ߣ�ר������ʱ��,ר����������ר�������
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
	/**
	 * 
	 * ��������ר��htmlԴ����
	 * 
	 * @param html Ҫ������htmlԴ����
	 * 
	 * @return �����ɹ����ز���ר���б�,����ʧ�ܷ���null
	 * 
	 * */
	public List<Expert> parseBlogExpertsHtml(String html)
	{
		try
		{
			List<Expert> expertsList = new ArrayList<Expert>();
			int start = html.indexOf("id=\"experts\">");
			int end = html.indexOf("id=\"btnShowMoreExperts\"");
			
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			
			//ͷ������
			Pattern patternHeadUrl = Pattern.compile("(?<=src=')(.*?)(?=')");
			//������ҳ����
			Pattern patternBlogUrl = Pattern.compile("(?<=href=')(.*?)(?=')");
			//������
			Pattern patternName = Pattern.compile("(?<=alt=')(.*?)(?=')");
			
			String[] strArray = str1.split("<li>");
			int i,count = strArray.length;
			for(i=1;i<count;i++)
			{
				Matcher matcherHeadUrl = patternHeadUrl.matcher(strArray[i]);
				Matcher matcherBlogUrl = patternBlogUrl.matcher(strArray[i]);
				Matcher matcherName = patternName.matcher(strArray[i]);
				if(matcherHeadUrl.find() && matcherBlogUrl.find() && matcherName.find())
				{
					Expert expert = new Expert();
					expert.blogUrl = matcherBlogUrl.group();
					expert.headPictureUrl = matcherHeadUrl.group();
					expert.name = matcherName.group();
					expertsList.add(expert);
					
					Log.i(tag,expert.name);
					Log.i(tag,expert.headPictureUrl);
					Log.i(tag,expert.blogUrl);
				}
			}
			return expertsList;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * ����ĳһ����ר�ҵĲ���
	 * 
	 * @param html Ҫ������html
	 * 
	 * @return ���ز����б�
	 * 
	 * */
	public List<News> parseExpertDetailHtml(String html)
	{
		try
		{
			List<News> newsList = new ArrayList<News>();
			int i,count;
			int start = html.indexOf("<div class=\"list_item_new\">");
			int end = html.indexOf("<div id=\"papelist\" class=\"pagelist\">");
			
			String str1 = html.substring(start, end);
			Log.i(tag,"str1 -->\n"+str1);
			String[] strArray = str1.split("<span class=\"link_title\">");
			//����
			Pattern patternTitle = Pattern.compile("(?<=>)(.*?)(?=</a></span>)");
			//ժҪ		
			Pattern patternSummmary = Pattern.compile("(?<=<div class=\"article_description\">)(.*?)(?=</div>)");
			//��������
			Pattern patternTextUrl = Pattern.compile("(?<=href=\")(.*?)(?=\")");
			//����ʱ��
			Pattern patternPublishTime = Pattern.compile("(?<=<span class=\"link_postdate\">)(.*?)(?=</span>)");
			//�����
			Pattern patternPageview = Pattern.compile("(?<=�Ķ�</a>)(.*?)(?=</span>)");
			
			count = strArray.length;
			System.out.println("count:"+count);
			for(i = 1 ; i < count ; i++)
			{
				System.out.println("strArray["+i+"] -->\n" + strArray[i]);
				Matcher matcherTitle = patternTitle.matcher(strArray[i]);
				Matcher matcherSummary = patternSummmary.matcher(strArray[i]);
				Matcher matcherTextUrl = patternTextUrl.matcher(strArray[i]);
				Matcher matcherPublishTime = patternPublishTime.matcher(strArray[i]);
				Matcher matcherPageview = patternPageview.matcher(strArray[i]);
				
				if(matcherTitle.find() && matcherSummary.find() && matcherTextUrl.find() && matcherPageview.find() && matcherPublishTime.find())
				{
					News news = new News();
					news.title = matcherTitle.group();
					if(news.title.contains("<font color=\"red\">[�ö�]</font>")) news.title = news.title.replace("<font color=\"red\">[�ö�]</font>", "").trim();
					news.summary = matcherSummary.group();
					news.textUrl = "http://blog.csdn.net" + matcherTextUrl.group();
					news.publishTime = matcherPublishTime.group();
					String pageview = matcherPageview.group();
					news.pageview = Integer.parseInt(pageview.substring(1, pageview.length()-1));
					
					newsList.add(news);
					
					Log.i(tag,news.title);
					Log.i(tag,news.summary);
					Log.i(tag,news.textUrl);
					Log.i(tag,news.publishTime);
					Log.i(tag,news.pageview+"");
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
	 * �����õ�ĳһ����ר�ҵĻ�����Ϣ
	 * 
	 * @param html Ҫ������htmlԴ����
	 * 
	 * @param expert ������
	 * 
	 * 
	 * */
	public void parseExpertInfo(String html,Expert expert)
	{
		try
		{
			Log.i(tag,"123");
			int i,count;
			int start = html.indexOf("<div id=\"blog_userface\">");
			int end = html.indexOf("<div id=\"custom_column");
			
			String str1 = html.substring(start, end);
			System.out.println("str1-->\n" + str1);
			//ͷ��
			Pattern patternHeadPicture = Pattern.compile("(?<=src=\")(.*?)(?=\")");
			//���ʴ���		
			Pattern patternPageview = Pattern.compile("(?<=<li>���ʣ�<span>)(.*?)(?=��)");
			//����
			Pattern patternScore = Pattern.compile("(?<=���֣�<span>)(.*?)(?=</span> </li>)");
			//����
			Pattern patternRank = Pattern.compile("(?<=<li>������<span>��)(.*?)(?=��</span>)");
			//ԭ��������
			Pattern patternOriginal = Pattern.compile("(?<=ԭ����<span>)(.*?)(?=ƪ</span></li>)");
			//ת��
			Pattern patternTransshipment = Pattern.compile("(?<=ת�أ�<span>)(.*?)(?=ƪ</span></li>)");
			//����
			Pattern patternTranslation = Pattern.compile("(?<=���ģ�<span>)(.*?)(?=ƪ</span></li>)");

			Matcher matcherHeadPicture = patternHeadPicture.matcher(str1);
			Matcher matcherPageview = patternPageview.matcher(str1);
			Matcher matcherScore = patternScore.matcher(str1);
			Matcher matcherRank = patternRank.matcher(str1);
			Matcher matcherOriginal = patternOriginal.matcher(str1);
			Matcher matcherTransshipment = patternTransshipment.matcher(str1);
			Matcher matcherTranslation = patternTranslation.matcher(str1);
				
			if(matcherHeadPicture.find() && matcherPageview.find() && matcherScore.find() && matcherRank.find() && matcherOriginal.find()  && matcherTransshipment.find() && matcherTranslation.find())
			{
				
				expert.headPictureUrl = matcherHeadPicture.group();
				expert.pageview = Integer.parseInt(matcherPageview.group());
				expert.score = Integer.parseInt(matcherScore.group());
				expert.rank = Integer.parseInt(matcherRank.group());
				expert.originalPassage = Integer.parseInt(matcherOriginal.group());
				expert.transshipmentPassage = Integer.parseInt(matcherTransshipment.group());
				expert.translationPassage = Integer.parseInt(matcherTranslation.group());
				
				System.out.println(expert.headPictureUrl);
				System.out.println(expert.pageview);
				System.out.println(expert.score);
				System.out.println(expert.rank);
				System.out.println(expert.originalPassage);
				System.out.println(expert.transshipmentPassage);
				System.out.println(expert.translationPassage);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
