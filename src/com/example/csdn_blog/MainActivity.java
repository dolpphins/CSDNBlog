package com.example.csdn_blog;


import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.BlogNews;
import com.example.blog.BlogOnPageChangeListener;
import com.example.blog.BlogPagerAdapter;
import com.example.column.Column;
import com.example.column.ColumnSearchActivity;
import com.example.experts.Experts;
import com.example.myclass.Setting;
import com.example.util.CacheFileAsyncTask;
import com.example.util.ColumnListViewAdapter;
import com.example.util.FunctionUtils;
import com.example.util.NewsListViewAdapter;
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
	public static int currentViewPageeIndex = 0;//��ǰViewPager����
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
	private NewsListViewAdapter mobileNewsListViewAdapter = null;
	private BlogNews mobileBlogNews;
	private String mobileBaseUrlString = "http://blog.csdn.net/mobile/index.html";
	private String mobileCacheFileName = "mobile";
	//Webǰ��
	private PullToRefreshListView webNewsListView = null;
	private NewsListViewAdapter webNewsListViewAdapter = null;
	private BlogNews webBlogNews;
	private String webBaseUrlString = "http://blog.csdn.net/web/index.html";
	private String webCacheFileName = "web";
	//�ܹ����
	private PullToRefreshListView frameworkNewsListView = null;
	private NewsListViewAdapter frameworkNewsListViewAdapter = null;
	private BlogNews frameworkBlogNews;
	private String frameworkBaseUrlString = "http://blog.csdn.net/enterprise/index.html";
	private String frameworkCacheFileName = "framework";
	//�������
	private PullToRefreshListView programmingNewsListView = null;
	private NewsListViewAdapter programmingNewsListViewAdapter = null;
	private BlogNews programmingBlogNews;
	private String programmingBaseUrlString = "http://blog.csdn.net/code/index.html";
	private String programmingCacheFileName = "programming";
	//������
	private PullToRefreshListView internetNewsListView = null;
	private NewsListViewAdapter internetNewsListViewAdapter = null;
	private BlogNews internetBlogNews;
	private String internetBaseUrlString = "http://blog.csdn.net/www/index.html";
	private String internetCacheFileName = "internet";
	//���ݿ�
	private PullToRefreshListView databaseNewsListView = null;
	private NewsListViewAdapter databaseNewsListViewAdapter = null;
	private BlogNews databaseBlogNews;
	private String databaseBaseUrlString = "http://blog.csdn.net/database/index.html";
	private String databaseCacheFileName = "database";
	//ϵͳ��ά
	private PullToRefreshListView systemNewsListView = null;
	private NewsListViewAdapter systemNewsListViewAdapter = null;
	private BlogNews systemBlogNews;
	private String systemBaseUrlString = "http://blog.csdn.net/system/index.html";
	private String systemCacheFileName = "database";
	//�Ƽ���
	private PullToRefreshListView cloudNewsListView = null;
	private NewsListViewAdapter cloudNewsListViewAdapter = null;
	private BlogNews cloudBlogNews;
	private String cloudBaseUrlString = "http://blog.csdn.net/cloud/index.html";
	private String cloudCacheFileName = "cloud";
	//�з�����
	private PullToRefreshListView researchNewsListView = null;
	private NewsListViewAdapter researchNewsListViewAdapter = null;
	private BlogNews researchBlogNews;
	private String researchBaseUrlString = "http://blog.csdn.net/software/index.html";
	private String researchCacheFileName = "research";
	//�ۺ�
	private PullToRefreshListView synthesizeNewsListView = null;
	private NewsListViewAdapter synthesizeNewsListViewAdapter = null;
	private BlogNews synthesizeBlogNews;
	private String synthesizeBaseUrlString = "http://blog.csdn.net/other/index.html";
	private String synthesizeCacheFileName = "synthesize";
	
	/**
	 * 
	 * ר��
	 * 
	 * */
	private PullToRefreshListView columnsNewsListView = null;
	private ColumnListViewAdapter columnsNewsListViewAdapter = null;
	private Column column = null;
	private String columnsBaseUrlString = "http://blog.csdn.net/all/column/list.html";
	private String columnsCacheFileName = "columns";
	private ImageView column_search_icon = null;//ר��������ť
	
	/**
	 * 
	 * ����ר��
	 * 
	 * */
	private TextView experts_mobile = null;//�ƶ�����
	private String expertsMobileUrl = "http://blog.csdn.net/mobile/experts.html";
	private String expertsMobileCacheFileName = "experts_mobile";
	private Experts mobileExperts = null;
	private FrameLayout mobileFrameLayout = null;
	
	private TextView experts_web = null;//Webǰ��
	private String expertsWebUrl = "http://blog.csdn.net/web/experts.html";
	private String expertsWebCacheFileName = "experts_web";
	private Experts webExperts = null;
	private FrameLayout webFrameLayout = null;
	
	private TextView experts_framework = null;//�ܹ����
	private String expertsFrameworkUrl = "http://blog.csdn.net/enterprise/experts.html";
	private String expertsFrameworkCacheFileName = "experts_frame";
	private Experts frameworkExperts = null;
	private FrameLayout frameworkFrameLayout = null;
	
	private TextView experts_programming = null;//�������
	private String expertsProgrammingUrl = "http://blog.csdn.net/code/experts.html";
	private String expertsProgrammingCacheFileName = "experts_programming";
	private Experts programmingExperts = null;
	private FrameLayout programmingFrameLayout = null;
	
	private TextView experts_internet = null;//������
	private String expertsInternetUrl = "http://blog.csdn.net/www/experts.html";
	private String expertsInternetCacheFileName = "experts_internet";
	private Experts internetExperts = null;
	private FrameLayout internetFrameLayout = null;
	
	private TextView experts_database = null;//���ݿ�
	private String expertsDatabaseUrl = "http://blog.csdn.net/database/experts.html";
	private String expertsDatabaseCacheFileName = "experts_database";
	private Experts databaseExperts = null;
	private FrameLayout databaseFrameLayout = null;
	
	private TextView experts_system = null;//ϵͳ��ά
	private String expertsSystemUrl = "http://blog.csdn.net/system/experts.html";
	private String expertsSystemCacheFileName = "experts_system";
	private Experts systemExperts = null;
	private FrameLayout systemFrameLayout = null;
	
	private TextView experts_cloud = null;//�Ƽ���
	private String expertsCloudUrl = "http://blog.csdn.net/cloud/experts.html";
	private String expertsCloudCacheFileName = "experts_cloud";
	private Experts cloudExperts = null;
	private FrameLayout cloudFrameLayout = null;
	
	private TextView experts_research = null;//�з�����
	private String expertsResearchUrl = "http://blog.csdn.net/software/experts.html";
	private String expertsResearchCacheFileName = "experts_research";
	private Experts researchExperts = null;
	private FrameLayout researchFrameLayout = null;
	
	private ScrollView experts_grid_scrollview = null;

	private TextView expertCurrentTextView = experts_mobile;//��ǰѡ�еķ���
	
	/**
	 * 
	 * ����
	 * 
	 * */
	private RelativeLayout setting_check_update = null;
	private TextView setting_current_version = null;
	private ImageView setting_select_button = null;
	private RelativeLayout setting_clear_cache = null;
	private TextView setting_cache_size = null;
	private TextView setting_about = null;
	
	public static Setting setting = null;
	private final String settingSharePreferenceFileName = "setting.xml";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //�����ޱ���
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //���ò���
        setContentView(R.layout.activity_main);
        //��ȡ������Ϣ
		setting = FunctionUtils.getSettingInfo(this, settingSharePreferenceFileName);
        //��ʼ��Tabѡ�
        initTab();
        //��ʼ��������
        initNavigation();
        //��ʼ������PagerView
        initBlogPagerView();
        //��ʼ�������ؼ�(��pulltorefresh��)
        initOther();
        //��ʼ�����͸�������
        initAllObject();
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
		tabLabels[3] = getResources().getString(R.string.setting);
		tabViews[0] = (LinearLayout) getLayoutInflater().inflate(R.layout.blog_layout, null);
		tabViews[1] = (LinearLayout) getLayoutInflater().inflate(R.layout.column_layout, null);
		tabViews[2] = (LinearLayout) getLayoutInflater().inflate(R.layout.experts_layout, null);
		tabViews[3] = (LinearLayout) getLayoutInflater().inflate(R.layout.setting_layout, null);
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
				else if(currentSelTab == 3) iv.setBackground(getResources().getDrawable(R.drawable.icon_setting_nor));
				tv.setTextColor(Color.parseColor("#000000"));
				//ѡ�в���
				if(MainActivity.this.getResources().getString(R.string.blog).equals(tabId))
				{
					currentSelTab = 0;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_blog_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
				}
				//ѡ��ר��
				else if(MainActivity.this.getResources().getString(R.string.column).equals(tabId))
				{
					currentSelTab = 1;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_column_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
					
					//��ʼ��ר������
			        if(columnsNewsListViewAdapter == null) columnsNewsListViewAdapter = new ColumnListViewAdapter(MainActivity.this);
			        if(column == null) column = new Column(MainActivity.this,columnsNewsListView,columnsBaseUrlString,columnsCacheFileName,10,columnsNewsListViewAdapter);
			        column.init();
					
				}
				//ѡ�в���ר��
				else if(MainActivity.this.getResources().getString(R.string.experts).equals(tabId))
				{
					currentSelTab = 2;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_experts_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
					
					//��ʼ������ר�Ҷ���
					if(experts_grid_scrollview.getChildCount()==0) 
					{
						experts_grid_scrollview.removeAllViews();
						experts_grid_scrollview.addView(mobileFrameLayout);
						expertCurrentTextView = experts_mobile;
						if(mobileExperts == null) mobileExperts = new Experts(MainActivity.this,mobileFrameLayout);
						mobileExperts.init(expertsMobileUrl,expertsMobileCacheFileName);
					}
				}
				//ѡ������
				else if(MainActivity.this.getResources().getString(R.string.setting).equals(tabId))
				{
					currentSelTab = 3;
					ImageView  iv1 = (ImageView) tabViews[currentSelTab].findViewById(R.id.tab_icon);
					TextView tv1 = (TextView) tabViews[currentSelTab].findViewById(R.id.tab_text);
					iv1.setBackground(getResources().getDrawable(R.drawable.icon_setting_sel));
					tv1.setTextColor(Color.parseColor("#0000ff"));
					//��ʼ��
					initSettingTab();
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
    	webNewsListView = (PullToRefreshListView) blogPagerViews.get(1).findViewById(R.id.newsListView);
    	frameworkNewsListView = (PullToRefreshListView) blogPagerViews.get(2).findViewById(R.id.newsListView);
    	programmingNewsListView = (PullToRefreshListView) blogPagerViews.get(3).findViewById(R.id.newsListView);
    	internetNewsListView = (PullToRefreshListView) blogPagerViews.get(4).findViewById(R.id.newsListView);
    	databaseNewsListView = (PullToRefreshListView) blogPagerViews.get(5).findViewById(R.id.newsListView);
    	systemNewsListView = (PullToRefreshListView) blogPagerViews.get(6).findViewById(R.id.newsListView);
    	cloudNewsListView = (PullToRefreshListView) blogPagerViews.get(7).findViewById(R.id.newsListView);
    	researchNewsListView = (PullToRefreshListView) blogPagerViews.get(8).findViewById(R.id.newsListView);
    	synthesizeNewsListView = (PullToRefreshListView) blogPagerViews.get(9).findViewById(R.id.newsListView);
    	
    	columnsNewsListView = (PullToRefreshListView) this.findViewById(R.id.column_newsListView);
    	column_search_icon = (ImageView) this.findViewById(R.id.column_search_icon);
    	//ר��������ť����¼�
    	column_search_icon.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					//��ת��ר����������
					Intent intent = new Intent(MainActivity.this,ColumnSearchActivity.class);
					startActivity(intent);
					break;
				}
				return true;
			}
		});
    	//����ר��
    	experts_mobile = (TextView) this.findViewById(R.id.experts_mobile);
    	experts_web = (TextView) this.findViewById(R.id.experts_web);
    	experts_framework = (TextView) this.findViewById(R.id.experts_framework);
    	experts_programming = (TextView) this.findViewById(R.id.experts_programming);
    	experts_internet = (TextView) this.findViewById(R.id.experts_internet);
    	experts_database = (TextView) this.findViewById(R.id.experts_database);
    	experts_system = (TextView) this.findViewById(R.id.experts_system);
    	experts_cloud = (TextView) this.findViewById(R.id.experts_cloud);
    	experts_research = (TextView) this.findViewById(R.id.experts_research);
    	
    	experts_grid_scrollview = (ScrollView) this.findViewById(R.id.experts_grid_scrollview);
    	
    	mobileFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	webFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	frameworkFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	programmingFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	internetFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	databaseFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	cloudFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	systemFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	researchFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.experts_grid_layout, null);
    	
    	//��ʼ������ר�Ҹ�������ĵ���¼�
    	initExpertsClickEvent();
    }
    /**
     * 
     * ��ʼ������ѡ���������
     * 
     * */
    private void initAllObject()
    {
    	mobileNewsListViewAdapter = new NewsListViewAdapter(this);
        mobileBlogNews = new BlogNews(this, mobileNewsListView,mobileBaseUrlString,mobileCacheFileName,0,mobileNewsListViewAdapter);
        mobileBlogNews.init();
        webNewsListViewAdapter = new NewsListViewAdapter(this);
        webBlogNews = new BlogNews(this, webNewsListView,webBaseUrlString,webCacheFileName,1,webNewsListViewAdapter);
        webBlogNews.init();
        frameworkNewsListViewAdapter = new NewsListViewAdapter(this);
        frameworkBlogNews = new BlogNews(this, frameworkNewsListView,frameworkBaseUrlString,frameworkCacheFileName,2,frameworkNewsListViewAdapter);
        frameworkBlogNews.init();
        programmingNewsListViewAdapter = new NewsListViewAdapter(this);
        programmingBlogNews = new BlogNews(this, programmingNewsListView,programmingBaseUrlString,programmingCacheFileName,3,programmingNewsListViewAdapter);
        programmingBlogNews.init();
        internetNewsListViewAdapter = new NewsListViewAdapter(this);
        internetBlogNews = new BlogNews(this, internetNewsListView,internetBaseUrlString,internetCacheFileName,4,internetNewsListViewAdapter);
        internetBlogNews.init();
        databaseNewsListViewAdapter = new NewsListViewAdapter(this);
        databaseBlogNews = new BlogNews(this, databaseNewsListView,databaseBaseUrlString,databaseCacheFileName,5,databaseNewsListViewAdapter);
        databaseBlogNews.init();
        systemNewsListViewAdapter = new NewsListViewAdapter(this);
        systemBlogNews = new BlogNews(this, systemNewsListView,systemBaseUrlString,systemCacheFileName,6,systemNewsListViewAdapter);
        systemBlogNews.init();
        cloudNewsListViewAdapter = new NewsListViewAdapter(this);
        cloudBlogNews = new BlogNews(this, cloudNewsListView,cloudBaseUrlString,cloudCacheFileName,7,cloudNewsListViewAdapter);
        cloudBlogNews.init();
        researchNewsListViewAdapter = new NewsListViewAdapter(this);
        researchBlogNews = new BlogNews(this, researchNewsListView,researchBaseUrlString,researchCacheFileName,8,researchNewsListViewAdapter);
        researchBlogNews.init();
        synthesizeNewsListViewAdapter = new NewsListViewAdapter(this);
        synthesizeBlogNews = new BlogNews(this, synthesizeNewsListView,synthesizeBaseUrlString,synthesizeCacheFileName,9,synthesizeNewsListViewAdapter);
        synthesizeBlogNews.init();
    }
    /**
     * 
     * ��ʼ������ר�Ҹ�������ĵ���¼�
     * 
     * */
    private void initExpertsClickEvent()
    {
    	//�ƶ�����
    	experts_mobile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_mobile;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(mobileFrameLayout);
				if(mobileExperts == null) mobileExperts = new Experts(MainActivity.this,mobileFrameLayout);
				mobileExperts.init(expertsMobileUrl,expertsMobileCacheFileName);
			}
		});
    	//Webǰ��
    	experts_web.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_web;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(webFrameLayout);
				if(webExperts == null) webExperts = new Experts(MainActivity.this,webFrameLayout);
				webExperts.init(expertsWebUrl,expertsWebCacheFileName);
			}
		});
    	//�ܹ����
    	experts_framework.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_framework;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(frameworkFrameLayout);
				if(frameworkExperts == null) frameworkExperts = new Experts(MainActivity.this,frameworkFrameLayout);
				frameworkExperts.init(expertsFrameworkUrl,expertsFrameworkCacheFileName);
			}
		});
    	//�������
    	experts_programming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_programming;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(programmingFrameLayout);
				if(programmingExperts == null) programmingExperts = new Experts(MainActivity.this,programmingFrameLayout);
				programmingExperts.init(expertsProgrammingUrl,expertsProgrammingCacheFileName);
			}
		});
    	//������
    	experts_internet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_internet;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(internetFrameLayout);
				if(internetExperts == null) internetExperts = new Experts(MainActivity.this,internetFrameLayout);
				internetExperts.init(expertsInternetUrl,expertsInternetCacheFileName);
			}
		});
    	//���ݿ�
    	experts_database.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_database;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(databaseFrameLayout);
				if(databaseExperts == null) databaseExperts = new Experts(MainActivity.this,databaseFrameLayout);
				databaseExperts.init(expertsDatabaseUrl,expertsDatabaseCacheFileName);
			}
		});
    	//ϵͳ��ά
    	experts_system.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_system;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(systemFrameLayout);
				if(systemExperts == null) systemExperts = new Experts(MainActivity.this,systemFrameLayout);
				systemExperts.init(expertsSystemUrl,expertsSystemCacheFileName);
			}
		});
    	//�Ƽ���
    	experts_cloud.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_cloud;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(cloudFrameLayout);
				if(cloudExperts == null) cloudExperts = new Experts(MainActivity.this,cloudFrameLayout);
				cloudExperts.init(expertsCloudUrl,expertsCloudCacheFileName);
			}
		});
    	//�з�����
    	experts_research.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#f5f6f8"));
				expertCurrentTextView = experts_research;
				expertCurrentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
				
				if(experts_grid_scrollview.getChildCount()>0) experts_grid_scrollview.removeAllViews();
				experts_grid_scrollview.addView(researchFrameLayout);
				if(researchExperts == null) researchExperts = new Experts(MainActivity.this,researchFrameLayout);
				researchExperts.init(expertsResearchUrl,expertsResearchCacheFileName);
			}
		});
    }
    /**
     * 
     * ��ʼ���O���x�
     * 
     * */
    private void initSettingTab()
    {
    	if(setting_check_update == null) 
    	{
    		setting_check_update = (RelativeLayout) MainActivity.this.findViewById(R.id.setting_check_update);
    		setting_check_update.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int type = event.getAction();
					if(type == MotionEvent.ACTION_DOWN) setting_check_update.setBackground(getResources().getDrawable(R.drawable.setting_item_bg_sel));
					else if(type == MotionEvent.ACTION_UP)
					{
						setting_check_update.setBackground(getResources().getDrawable(R.drawable.setting_item_bg_nor));
						Toast.makeText(MainActivity.this, "�ù�����δʵ��", Toast.LENGTH_SHORT).show();
					}
					return true;
				}
			});
    	}
    	if(setting_current_version == null)
    	{
    		setting_current_version = (TextView) MainActivity.this.findViewById(R.id.setting_current_version);
    		String versionName = FunctionUtils.getCurrentVersion(MainActivity.this);
    		if(versionName != null) setting_current_version.setText("V "+versionName);
    	}
    	if(setting_select_button == null) 
    	{
    		setting_select_button = (ImageView) MainActivity.this.findViewById(R.id.setting_select_button);
    		
    		if(setting.OnlyShowPictureInWifi == true) setting_select_button.setBackground(getResources().getDrawable(R.drawable.icon_switch_on));
    		else setting_select_button.setBackground(getResources().getDrawable(R.drawable.icon_switch_off));
    		
    		setting_select_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(setting.OnlyShowPictureInWifi == true)
					{
						setting.OnlyShowPictureInWifi = false;
						FunctionUtils.setSettingInfo(MainActivity.this, settingSharePreferenceFileName, setting);
						setting_select_button.setBackground(getResources().getDrawable(R.drawable.icon_switch_off));
					}
					else
					{
						setting.OnlyShowPictureInWifi = true;
						FunctionUtils.setSettingInfo(MainActivity.this, settingSharePreferenceFileName, setting);
						setting_select_button.setBackground(getResources().getDrawable(R.drawable.icon_switch_on));
					}
				}
			});
    	}
    	if(setting_clear_cache == null) 
    	{
    		setting_clear_cache = (RelativeLayout) MainActivity.this.findViewById(R.id.setting_clear_cache);
    		setting_clear_cache.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int type = event.getAction();
					if(type == MotionEvent.ACTION_DOWN) setting_clear_cache.setBackground(getResources().getDrawable(R.drawable.setting_item_bg_sel));
					else if(type == MotionEvent.ACTION_UP)
					{
						setting_clear_cache.setBackground(getResources().getDrawable(R.drawable.setting_item_bg_nor));
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage("�����Ҫɾ��������?");
						builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.i(tag,"delete cache");
								File file = new File(Environment.getExternalStorageDirectory(),"/CSDN/Cache");
								new CacheFileAsyncTask(MainActivity.this,setting_cache_size, CacheFileAsyncTask.TYPE_DELETE).execute(file);
							}
						});
						builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.i(tag,"cancel delete cache");
							}
						});
						builder.show();
					}
					return true;
				}
			});
    	}
    	if(setting_cache_size == null) setting_cache_size = (TextView) MainActivity.this.findViewById(R.id.setting_cache_size);
    	if(setting_about == null) 
    	{
    		setting_about = (TextView) MainActivity.this.findViewById(R.id.setting_about);
    		setting_about.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int type = event.getAction();
					if(type == MotionEvent.ACTION_DOWN) setting_about.setBackground(getResources().getDrawable(R.drawable.setting_item_bg_sel));
					else if(type == MotionEvent.ACTION_UP)
					{
						setting_about.setBackground(getResources().getDrawable(R.drawable.setting_item_bg_nor));
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage(getResources().getString(R.string.about_message));
						builder.show();
					}
					return true;
				}
			});
    	}
    	//��ȡ�����С
    	File file = new File(Environment.getExternalStorageDirectory(),"/CSDN/Cache");
    	new CacheFileAsyncTask(MainActivity.this,setting_cache_size, CacheFileAsyncTask.TYPE_SIZE).execute(file);
    }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i(tag,"requestCode:"+requestCode);
    	Log.i(tag,"resultCode:"+resultCode);
    	//���ƶ������������ķ���
		if(requestCode == 0 && resultCode == 100 && null != data)
		{
			if(mobileBlogNews != null) mobileBlogNews.handleForReadBlog(data);
		}
		//��Webǰ�˲������ķ���
		else if(requestCode == 1 && resultCode == 100 && null != data)
		{
			if(webBlogNews != null) webBlogNews.handleForReadBlog(data);
		}
		//�Ӽܹ���Ʋ������ķ���
		else if(requestCode == 2 && resultCode == 100 && null != data)
		{
			if(frameworkBlogNews != null) frameworkBlogNews.handleForReadBlog(data);
		}
		//�ӱ�����Բ������ķ���
		else if(requestCode == 3 && resultCode == 100 && null != data)
		{
			if(programmingBlogNews != null) programmingBlogNews.handleForReadBlog(data);
		}
		//�ӻ������������ķ���
		else if(requestCode == 4 && resultCode == 100 && null != data)
		{
			if(internetBlogNews != null) internetBlogNews.handleForReadBlog(data);
		}
		//�����ݿⲩ�����ķ���
		else if(requestCode == 5 && resultCode == 100 && null != data)
		{
			if(databaseBlogNews != null) databaseBlogNews.handleForReadBlog(data);
		}
		//��ϵͳ��ά�������ķ���
		else if(requestCode == 6 && resultCode == 100 && null != data)
		{
			if(systemBlogNews != null) systemBlogNews.handleForReadBlog(data);
		}
		//���Ƽ��㲩�����ķ���
		else if(requestCode == 7 && resultCode == 100 && null != data)
		{
			if(cloudBlogNews != null) cloudBlogNews.handleForReadBlog(data);
		}
		//���з����������ķ���
		else if(requestCode == 8 && resultCode == 100 && null != data)
		{
			if(researchBlogNews != null) researchBlogNews.handleForReadBlog(data);
		}
		//���ۺϲ������ķ���
		else if(requestCode == 5 && resultCode == 100 && null != data)
		{
			if(synthesizeBlogNews != null) synthesizeBlogNews.handleForReadBlog(data);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			//�����µĻ��汣�浽�ļ���
			if(mobileBlogNews != null) mobileBlogNews.saveCacheToFile();
			if(webBlogNews != null) webBlogNews.saveCacheToFile();
			if(frameworkBlogNews != null) frameworkBlogNews.saveCacheToFile();
			if(programmingBlogNews != null) programmingBlogNews.saveCacheToFile();
			if(internetBlogNews != null) internetBlogNews.saveCacheToFile();
			if(databaseBlogNews != null) databaseBlogNews.saveCacheToFile();
			if(systemBlogNews != null) systemBlogNews.saveCacheToFile();
			if(cloudBlogNews != null) cloudBlogNews.saveCacheToFile();
			if(researchBlogNews != null) researchBlogNews.saveCacheToFile();
			if(synthesizeBlogNews != null) synthesizeBlogNews.saveCacheToFile();
		}
		return super.onKeyDown(keyCode, event);
	}
}
