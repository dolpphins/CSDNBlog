package com.example.myclass;

import java.io.Serializable;

import android.graphics.Bitmap;

public class News implements Serializable{
	public String headPictureUrl = "#";//ͷ������
	public String title;//����
	public String summary;//ժҪ
	public String textUrl;//��������
	public String author;//������
	public String publishTime;//����ʱ��
	public boolean hasRead = false;//�Ƿ��Ѷ� 
}
