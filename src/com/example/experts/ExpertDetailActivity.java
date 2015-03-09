package com.example.experts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.blog.BlogDetailActivity;
import com.example.csdn_blog.R;
import com.example.myclass.Expert;
import com.example.myclass.News;
import com.example.network.ExpertDetailAsyncTask;
import com.example.util.ExpertDetailListViewAdapter;
import com.example.util.FunctionUtils;

public class ExpertDetailActivity extends Activity{
	private final static String tag = "ExpertDetailActivity";

	private TextView expert_detail = null;
	private ImageView expert_detail_show_icon = null;
	private ListView expert_article_listview = null;
	private TextView expert_detail_icon = null;
	private List<News> expertDetailList = new ArrayList<News>();
	private ExpertDetailListViewAdapter expertDetailListViewAdapter = null;
	
	private Expert expert = null;
	private int currentPageIndex = 0;
	private String urlString = "";
	
	public boolean isLoading = false;
	/**
	 * 
	 * 侧栏控件
	 * 
	 * */
	private RelativeLayout expert_detail_sidelayout = null;
	private ImageView expert_detail_headpicture = null;
	private TextView expert_detail_name = null;
	private TextView expert_detail_pageview = null;
	private TextView expert_detail_score = null;
	private TextView expert_detail_rank = null;
	private TextView expert_detail_original = null;
	private TextView expert_detail_transshipment = null;
	private TextView expert_detail_translation = null;
	
	//屏幕大小
	private int screenWidth = 0;
	private int screenHeight = 0;
	
	private boolean isShow = false;//标记侧栏布局是否显示 
	//boolean down = false;
	//Point downPoint = new Point();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置布局
		this.setContentView(R.layout.expert_detail_layout);
		//获取屏幕大小
		getScreenSize();
		
		expert_detail = (TextView) this.findViewById(R.id.expert_detail);
		expert_detail_show_icon = (ImageView) this.findViewById(R.id.expert_detail_show_icon);
		expert_article_listview = (ListView) this.findViewById(R.id.expert_article_listview);
		expert_detail_icon = (TextView) this.findViewById(R.id.expert_detail_icon);
		expert_detail_sidelayout = (RelativeLayout) this.findViewById(R.id.expert_detail_sidelayout);
		expert_detail_headpicture = (ImageView) this.findViewById(R.id.expert_detail_headpicture);
		expert_detail_name = (TextView) this.findViewById(R.id.expert_detail_name);
		expert_detail_pageview = (TextView) this.findViewById(R.id.expert_detail_pageview);
		expert_detail_score = (TextView) this.findViewById(R.id.expert_detail_score);
		expert_detail_rank = (TextView) this.findViewById(R.id.expert_detail_rank);
		expert_detail_original = (TextView) this.findViewById(R.id.expert_detail_original);
		expert_detail_transshipment = (TextView) this.findViewById(R.id.expert_detail_transshipment);
		expert_detail_translation = (TextView) this.findViewById(R.id.expert_detail_translation);
		
		
		expert = (Expert) this.getIntent().getSerializableExtra("expert");
		
		expert_detail.setText(expert.name+"的博客");
		
		currentPageIndex++;
		if(currentPageIndex<=1) urlString = expert.blogUrl;
		else urlString = expert.blogUrl + "/article/list/" + currentPageIndex;
		expertDetailListViewAdapter = new ExpertDetailListViewAdapter(this);
		expertDetailListViewAdapter.setDataSet(expertDetailList);
		expert_article_listview.setAdapter(expertDetailListViewAdapter);
		isLoading = true;
		ExpertDetailAsyncTask expertDetailAsyncTask = new ExpertDetailAsyncTask(ExpertDetailActivity.this,expert_article_listview,expert_detail_icon,expertDetailList,expertDetailListViewAdapter,expert);
		expertDetailAsyncTask.init(expert_detail_headpicture, expert_detail_name,expert_detail_pageview,expert_detail_score,expert_detail_rank,expert_detail_original,expert_detail_transshipment,expert_detail_translation);
		expertDetailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString);
		
		//FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		//params.setMargins(screenWidth-10, 0, 0, 0);
		//expert_detail_sidelayout.setLayoutParams(params);
		
		expert_detail_show_icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isShow)
				{
					isShow = true;
					float fromXDelta = FunctionUtils.dip2px(ExpertDetailActivity.this, 126.0f); 
					Animation anim = new TranslateAnimation(fromXDelta, 0, 0, 0);
					anim.setDuration(300);
					anim.setInterpolator(new AccelerateDecelerateInterpolator());
					anim.setFillAfter(true);
					expert_detail_sidelayout.setVisibility(View.VISIBLE);
					expert_detail_sidelayout.startAnimation(anim);
				}
				else
				{
					isShow = false;
					float fromXDelta = FunctionUtils.dip2px(ExpertDetailActivity.this, 1000.0f);
					Animation anim = new TranslateAnimation(0, fromXDelta, 0, 0);
					anim.setDuration(1000);
					anim.setInterpolator(new AccelerateDecelerateInterpolator());
					expert_detail_sidelayout.setVisibility(View.VISIBLE);
					expert_detail_sidelayout.startAnimation(anim);
					
					anim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {	}
						@Override
						public void onAnimationRepeat(Animation animation) {}
						@Override
						public void onAnimationEnd(Animation animation) {
							expert_detail_sidelayout.setVisibility(View.GONE);
						}
					});
				}
			}
		});
		
		expert_article_listview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState)
				{
				//不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					//最后一项可见时
					if(view.getLastVisiblePosition() == view.getCount() - 1)
					{
						currentPageIndex++;
						if(currentPageIndex<=1) urlString = expert.blogUrl;
						else urlString = expert.blogUrl + "/article/list/" + currentPageIndex;
						if(!isLoading)
						{
							isLoading = true;
							ExpertDetailAsyncTask expertDetailAsyncTask = new ExpertDetailAsyncTask(ExpertDetailActivity.this,expert_article_listview,expert_detail_icon,expertDetailList,expertDetailListViewAdapter,expert);
							expertDetailAsyncTask.init(expert_detail_headpicture, expert_detail_name,expert_detail_pageview,expert_detail_score,expert_detail_rank,expert_detail_original,expert_detail_transshipment,expert_detail_translation);
							expertDetailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlString);
						}
						else
						{
							expert_detail_icon.setVisibility(View.VISIBLE);
							Animation anim = AnimationUtils.loadAnimation(ExpertDetailActivity.this,R.anim.loading_icon_anim);
							if(expert_detail_icon.getAnimation() == null) expert_detail_icon.startAnimation(anim);
						}
					}
					break;
					//滚动时
					case OnScrollListener.SCROLL_STATE_FLING:
						expert_detail_icon.clearAnimation();
						expert_detail_icon.setVisibility(View.GONE);
						break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		expert_article_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ExpertDetailActivity.this,BlogDetailActivity.class);
				expertDetailList.get(position).headPictureUrl = expert.headPictureUrl;
				expertDetailList.get(position).author = expert.name;
				intent.putExtra("news", expertDetailList.get(position));
				intent.putExtra("position", position);
				String cacheFileName = FunctionUtils.getIdByUrl(expertDetailList.get(position).textUrl);
				intent.putExtra("cacheFileName", cacheFileName);
				startActivity(intent);
			}
		});
		
		expert_article_listview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isShow) return true;
				else return false;
			}
		});
	}

	/**
	 * 
	 * 获取屏幕大小
	 * 
	 * */
	public void getScreenSize()
	{
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		screenHeight = outMetrics.heightPixels;
	}
}
