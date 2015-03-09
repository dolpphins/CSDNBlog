package com.example.csdn_blog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity {
	private final static String tag = "SplashActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		//无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置布局
		this.setContentView(R.layout.splash_layout);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					Intent intent = new Intent(SplashActivity.this,MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finish();
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}).start();
		
	}
}
