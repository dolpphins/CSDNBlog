package com.example.myclass;

import java.io.Serializable;

/**
 * 
 * ��ʾÿһר������
 * 
 * */
public class ColumnItem implements Serializable{
	public String ColumnName = "";//ר����
	public String ColumnAuthor = "";//ר������
	public String ColumnCreateTime = "";//ר������ʱ��
	public int ColumnNumberOfPassage = 0;//ר��������
	public int ColumnPageView = 0;//ר�������
	public String ColumnUrl = "";//ר����ַ
	
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
