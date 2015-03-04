package com.example.myclass;

import android.widget.LinearLayout;

public class CacheView {
	private final static String tag = "CacheView";
	
	public int position = 0;
	public LinearLayout linearLayout = null;
	
	/***
	 * ¹¹Ôìº¯Êý
	 * */
	public CacheView(){}
	public CacheView(int position,LinearLayout linearLayout)
	{
		this.position = position;
		this.linearLayout = linearLayout;
	}
}
