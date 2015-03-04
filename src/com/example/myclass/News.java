package com.example.myclass;

import java.io.Serializable;

import android.graphics.Bitmap;

public class News implements Serializable{
	public String headPictureUrl = "#";//头像链接
	public String title;//标题
	public String summary;//摘要
	public String textUrl;//正文链接
	public String author;//发布者
	public String publishTime;//发布时间
	public boolean hasRead = false;//是否已读 
}
