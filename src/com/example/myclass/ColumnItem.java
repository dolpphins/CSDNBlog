package com.example.myclass;

import java.io.Serializable;

/**
 * 
 * 表示每一专栏的类
 * 
 * */
public class ColumnItem implements Serializable{
	public String ColumnName = "";//专栏名
	public String ColumnAuthor = "";//专栏作者
	public String ColumnCreateTime = "";//专栏创建时间
	public int ColumnNumberOfPassage = 0;//专栏文章数
	public int ColumnPageView = 0;//专栏浏览数
	public String ColumnUrl = "";//专栏地址
	
	public ColumnItem(){}
	
	public ColumnItem(String ColumnName,String ColumnAuthor,String ColumnCreateTime,int ColumnNumberOfPassage,int ColumnPageView,String ColumnUrl)
	{
		this.ColumnName = ColumnName;
		this.ColumnAuthor = ColumnAuthor;
		this.ColumnCreateTime = ColumnCreateTime;
		this.ColumnNumberOfPassage = ColumnNumberOfPassage;
		this.ColumnPageView = ColumnPageView;
		this.ColumnUrl = ColumnUrl;
	}
}
