package com.example.csdn_blog;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.example.blog.BlogNews;
import com.example.blog.BlogOnPageChangeListener;
import com.example.blog.BlogPagerAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends Activity {
	private final static String tag = "MainActivity";
	
	/**
	 * Tabѡ������йس���������
	 * 
	 * */
	private TabHost tabHost = null;
	private String[] tabLabels = new String[4];
	private LinearLayout[] tabViews = new LinearLayout[4];
	private int[] tabLayoutIds = new int[4];
	private int currentSelTab = 0;//��¼��ǰѡ�ѡ�е�����
	
	/**
	 * ���͵����������йس���������
	 * 
	 * */
	private HorizontalScrollView viewpager_head_scrollview = null;//������ScrollView
	private LinearLayout viewpager_head = null;//������ˮƽ���Բ���
	private TextView[] viewpager_head_textviews = new TextView[10];//������ʮ��TextView
	private int[] viewpager_head_textviews_width = new int[10];//������ʮ��TextView���
	private TextView viewpager_head_tip = null;//����������Ļ�����
	private int viewpager_head_tip_width;//����������Ļ��������
	private int currentNavigationIndex = 0;//��ǰѡ�еĵ�����
	private ViewPager viewpager = null;
	private BlogPagerAdapter blogPagerAdapter = null;
	private BlogOnPageChangeListener blogOnPageChangeListener = null;
	private List<LinearLayout> blogPagerViews = new ArrayList<LinearLayout>();
	/**
	 * 
	 * ����
	 * 
	 * */
	//�ƶ�����
	private PullToRefreshListView mobileNewsListView = null;
	private BlogNews mobileBlogNews;
	private String mobileBaseUrlString = "http://blog.csdn.net/mobile/index.html";
	private String mobileCacheFileName = "mobile";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //�����ޱ���
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //���ò���
        setContentView(R.layout.activity_main);
        
        //��ʼ��Tabѡ�
        initTab();
        //��ʼ��������
        initNavigation();
        //��ʼ������PagerView
        initBlogPagerView();
        //��ʼ�������ؼ�(��pulltorefresh��)
        initOther();
        //��ʼ����������
        mobileBlogNews = new BlogNews(this, mobileNewsListView,mobileBaseUrlString,mobileCacheFileName);
    }
    /**
     * 
     * ��ʼ��Tabѡ�
     * 
     * */
    private void initTab()
    {
		tabHost = (TabHost) this.findViewById(R.id.tabhost);
		tabHost.setup();//ʵ����tabWidget��tabContent
		tabLabels[0] = getResources().getString(R.string.blog);
		tabLabels[1] = getResources().getString(R.string.column);
		tabLabels[2] = getResources().getString(R.string.experts);
		tabLabels[3] = getResources().getString(R.string.personality);
		tabViews[0] = (LinearLayout) getLayoutInflater().inflate(R.layout.blog_layout, null);
		tabViews[1] = (LinearLayout) getLayoutInflater().inflate(R.layout.column_layout, null);
		tabViews[2] = (LinearLayout) getLayoutInflater().inflate(R.layout.experts_layout, null);
		tabViews[3] = (LinearLayout) getLayoutInflater().inflate(R.layout.personality_layout, null);
		tabLayoutIds[0] = R.id.tab1;
		tabLayoutIds[1] = R.id.tab2;
		tabLayoutIds[2] = R.id.tab3;
		tabLayoutIds[3] = R.id.tab4;
		//���ѡ��
		for(int i=0;i<tabLabels.length;i++)
		{
			tabHost.addTab(tabHost.newTabSpec(tabLabels[i]).setIndicator(tabViews[i]).setContent(tabLayoutIds[i]));
		}
		//Ĭ�ϵ�һ��ѡ��ѡ��
		ImageView  iv = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
		TextView tv = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
		iv.setBackground(getResources().getDrawable(R.drawable.icon_blog_sel));
		tv.setTextColor(Color.parseColor("#0000ff"));
		tabHost.setCurrentTab(currentSelTab);
		//ע�����¼�
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				ImageView  iv = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
				TextView tv = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
				
				if(currentSelTab == 0) iv.setBackground(getResources().getDrawable(R.drawable.icon_blog_nor));
				else if(currentSelTab == 1) iv.setBackground(getResources().getDrawable(R.drawable.icon_column_nor));
				else if(currentSelTab == 2) iv.setBackground(getResources().getDrawable(R.drawable.icon_experts_nor));
				else if(currentSelTab == 3) iv.setBackground(getResources().getDrawable(R.drawable.icon_personality_nor));
				tv.setTextColor(Color.parseColor("#000000"));
				
				if(MainActivity.this.getResources().getString(R.string.blog).equals(tabId))
				{
					currentSelTab = 0;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_blog_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
				}
				else if(MainActivity.this.getResources().getString(R.string.column).equals(tabId))
				{
					currentSelTab = 1;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_column_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
					
				}
				else if(MainActivity.this.getResources().getString(R.string.experts).equals(tabId))
				{
					currentSelTab = 2;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_experts_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
				}
				else if(MainActivity.this.getResources().getString(R.string.personality).equals(tabId))
				{
					currentSelTab = 3;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_personality_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
				}
			}
		});
    }
    /**
     * ��ʼ��������
     * 
     * */
    private void initNavigation()
    {
    	viewpager_head_scrollview = (HorizontalScrollView) this.findViewById(R.id.viewpager_head_scrollview);
        viewpager_head = (LinearLayout) this.findViewById(R.id.viewpager_head);
        viewpager_head_textviews[0] = (TextView) this.findViewById(R.id.viewpager_head_1);
        viewpager_head_textviews[1] = (TextView) this.findViewById(R.id.viewpager_head_2);
        viewpager_head_textviews[2] = (TextView) this.findViewById(R.id.viewpager_head_3);
        viewpager_head_textviews[3] = (TextView) this.findViewById(R.id.viewpager_head_4);
        viewpager_head_textviews[4] = (TextView) this.findViewById(R.id.viewpager_head_5);
        viewpager_head_textviews[5] = (TextView) this.findViewById(R.id.viewpager_head_6);
        viewpager_head_textviews[6] = (TextView) this.findViewById(R.id.viewpager_head_7);
        viewpager_head_textviews[7] = (TextView) this.findViewById(R.id.viewpager_head_8);
        viewpager_head_textviews[8] = (TextView) this.findViewById(R.id.viewpager_head_9);
        viewpager_head_textviews[9] = (TextView) this.findViewById(R.id.viewpager_head_10);
        viewpager_head_tip = (TextView) this.findViewById(R.id.viewpager_head_tip);
        
        viewpager_head.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	
			@Override
			public void onGlobalLayout() {
				if(viewpager_head_textviews_width[0]!=0) return;
				for(int i=0;i<10;i++)
				{
					viewpager_head_textviews_width[i] = viewpager_head_textviews[i].getWidth();
					System.out.println(viewpager_head_textviews_width[i]);
				}
				viewpager_head_tip_width = viewpager_head_textviews_width[9];
				//viewpager_head_tip.setWidth(viewpager_head_tip_width);
				//viewpager_head_tip.setLeft((viewpager_head_textviews_width[0]-viewpager_head_tip_width)/2);
			    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(viewpager_head_tip_width, viewpager_head.getHeight()/15);
			    lp.setMargins((viewpager_head_textviews_width[0]-viewpager_head_tip_width)/2,0,0,0);
				//viewpager_head_tip.setMa;
			    viewpager_head_tip.setLayoutParams(lp);
			}
		});
        //���õ���¼�
        for(int i = 0; i<10; i++)
        {
        	final int t = i;
        	viewpager_head_textviews[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					viewpager.setCurrentItem(t);
					changeNavigationItem(t);
				}
			});
        }
    }
    /**
     * ��ʼ������PagerView
     * 
     * */
    private void initBlogPagerView()
    {
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.mobile_development_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.web_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.framework_design_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.programming_languages_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.internet_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.database_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.system_operation_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.cloud_computing_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.research_development_management_layout, null));
    	blogPagerViews.add((LinearLayout) getLayoutInflater().inflate(R.layout.synthesize_layout, null));
    	
    	viewpager = (ViewPager) this.findViewById(R.id.viewpager);
    	
    	blogPagerAdapter = new BlogPagerAdapter(blogPagerViews);
    	viewpager.setAdapter(blogPagerAdapter);
    	blogOnPageChangeListener = new BlogOnPageChangeListener(this);
    	viewpager.setOnPageChangeListener(blogOnPageChangeListener);
    }
    /**
     * ����������
     * 
     * @param t ��ǰѡ�е�TextView������
     * 
     * */
    public void changeNavigationItem(int t)
    {
    	Log.i(tag,"currentNavigationIndex1:"+currentNavigationIndex);
    	viewpager_head_textviews[currentNavigationIndex].setTextColor(Color.parseColor("#afafaf"));
		viewpager_head_textviews[t].setTextColor(Color.parseColor("#148af7"));
		currentNavigationIndex = t;
		Log.i(tag,"currentNavigationIndex2O:"+currentNavigationIndex);
		int marginLeft = 0,startPosition = 0;
    	for(int i=0;i<t;i++)
    	{
    		marginLeft += viewpager_head_textviews_width[i];
    	}
    	startPosition = marginLeft - viewpager_head_scrollview.getScrollX();
    	marginLeft += (viewpager_head_textviews_width[t]-viewpager_head_tip_width)/2;
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(viewpager_head_tip_width, viewpager_head.getHeight()/15);
	    lp.setMargins(marginLeft,0,0,0);
	    viewpager_head_tip.setLayoutParams(lp);
	    
	    viewpager_head_scrollview.smoothScrollBy(startPosition, 0);
    }
    
    /**
     * 
     * ��ʼ�������ؼ�(��pulltorefresh��)
     * 
     * */
    private void initOther()
    {
    	mobileNewsListView = (PullToRefreshListView) blogPagerViews.get(0).findViewById(R.id.newsListView);
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i(tag,"requestCode:"+requestCode);
    	Log.i(tag,"resultCode:"+resultCode);
    	//�Ӳ������ķ���
		if(requestCode == 0 && resultCode == 100 && null != data)
		{
			mobileBlogNews.handleForReadBlog(data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mobileBlogNews.saveCacheToFile();//�����µĻ��汣�浽�ļ���
		}
		return super.onKeyDown(keyCode, event);
	}
}
