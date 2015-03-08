package com.example.myclass;

import java.io.Serializable;


public class Expert implements Serializable{
	public String headPictureUrl = "#";//头像链接
	public String name;//名字
	public String blogUrl = "#";//博客主頁
	public int pageview = 0;//访问次数
	public int score = 0;//积分
	public int rank = 0;//排名
	public int originalPassage = 0;//原创文章数
	public int transshipmentPassage = 0;//转载文章数
	public int translationPassage = 0;//译文文章数
}
