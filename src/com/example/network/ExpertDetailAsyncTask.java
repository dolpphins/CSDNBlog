package com.example.network;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csdn_blog.R;
import com.example.experts.ExpertDetailActivity;
import com.example.myclass.Expert;
import com.example.myclass.News;
import com.example.util.ExpertDetailListViewAdapter;

public class ExpertDetailAsyncTask extends AsyncTask<String,Void,List<News>>{
	private final static String tag = "ExpertDetailAsyncTask";
	
	private Context context = null;
	private ListView expert_article_listview = null;
	private TextView expert_detail_icon = null;
	private ExpertDetailListViewAdapter expertDetailListViewAdapter = null;
	
	private String urlString = "";
	private List<News> expertDetailList = null;
	private Expert expert = null;
	
	private Network network = null;
	
	private ImageView expert_detail_headpicture = null;
	private TextView expert_detail_name = null;
	private TextView expert_detail_pageview = null;
	private TextView expert_detail_score = null;
	private TextView expert_detail_rank = null;
	private TextView expert_detail_original = null;
	private TextView expert_detail_transshipment = null;
	private TextView expert_detail_translation = null;
	
	public ExpertDetailAsyncTask(Context context,ListView expert_article_listview,TextView expert_detail_icon,List<News> expertDetailList,ExpertDetailListViewAdapter expertDetailListViewAdapter,Expert expert)
	{
		this.context = context;
		this.expert_article_listview = expert_article_listview;
		this.expert_detail_icon = expert_detail_icon;
		this.expertDetailList = expertDetailList;
		this.expertDetailListViewAdapter = expertDetailListViewAdapter;
		this.expert = expert;
		
		network = new Network();
	}
	/**
	 * 
	 * 初始化一些引用
	 * 
	 * */
	public void init(ImageView expert_detail_headpicture,TextView expert_detail_name,TextView expert_detail_pageview,
			TextView expert_detail_score,TextView expert_detail_rank,TextView expert_detail_original,TextView expert_detail_transshipment,
			TextView expert_detail_translation)
	{
		this.expert_detail_headpicture = expert_detail_headpicture;
		this.expert_detail_name = expert_detail_name;
		this.expert_detail_pageview = expert_detail_pageview;
		this.expert_detail_score = expert_detail_score;
		this.expert_detail_rank = expert_detail_rank;
		this.expert_detail_original = expert_detail_original;
		this.expert_detail_transshipment = expert_detail_transshipment;
		this.expert_detail_translation = expert_detail_translation;
		
	}
	
	@Override
	protected void onPreExecute() {
		expert_detail_icon.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(context,R.anim.loading_icon_anim);
		if(expert_detail_icon.getAnimation() == null) expert_detail_icon.startAnimation(anim);
		
		super.onPreExecute();
	}

	@Override
	protected List<News> doInBackground(String... params) {
		urlString = params[0];
		String html = network.getData(urlString);
		network.parseExpertInfo(html,expert);
		return network.parseExpertDetailHtml(html); 
	}
	
	@Override
	protected void onPostExecute(List<News> result) {
		expert_detail_icon.clearAnimation();
		expert_detail_icon.setVisibility(View.GONE);
		
		if(result == null)
			Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show();
		else if(result.size() == 0)
			Toast.makeText(context, "没有更多的内容了", Toast.LENGTH_SHORT).show();
		else
		{
			expertDetailList.addAll(result);
			Log.i(tag,"expertDetailList size is "+expertDetailList.size());
		}
		((ExpertDetailActivity)context).isLoading = false;
		expertDetailListViewAdapter.notifyDataSetChanged();
		
		//设置侧栏布局
		new PictureAsyncTask(context,expert_detail_headpicture).execute(expert.headPictureUrl);
		expert_detail_name.setText(expert.name);
		
		expert_detail_pageview.setText("访问:"+expert.pageview+"次");
		expert_detail_score.setText("积分:"+expert.score);
		expert_detail_rank.setText("排名:第"+expert.rank+"名");
		expert_detail_original.setText("原创:"+expert.originalPassage+"篇");
		expert_detail_transshipment.setText("转载:"+expert.transshipmentPassage+"篇");
		expert_detail_translation.setText("译文:"+expert.translationPassage+"篇");
		
		super.onPostExecute(result);
	}

}
