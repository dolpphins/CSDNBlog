package com.example.column;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.csdn_blog.R;
import com.example.myclass.ColumnItem;
import com.example.util.ColumnDetailListAdapter;
import com.example.util.FunctionUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ColumnItemActivity extends Activity {
	private final static String tag = "ColumnItemActivity";

	private PullToRefreshListView columnItemListView = null;
	private TextView column_detail = null;
	
	private ColumnItem columnItem = null;
	private ColumnDetail columnDetail = null;
	private ColumnDetailListAdapter columnDetailListAdapter = null;
	private String columnsBaseUrlString = "";
	protected String cacheFilePath = "/CSDN/Cache/ColumnDetail/";//�����ļ�·��
	private String columnsCacheFileName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		//�ޱ���
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//���ò���
		this.setContentView(R.layout.column_detail_layout);
		
		column_detail = (TextView) this.findViewById(R.id.column_detail);
		columnItemListView = (PullToRefreshListView) this.findViewById(R.id.columnItemListView);
		
		columnItem = (ColumnItem) getIntent().getSerializableExtra("columnItem");
		columnsBaseUrlString = columnItem.ColumnUrl;
		columnsCacheFileName = FunctionUtils.getColumnNameByUrl(columnItem.ColumnUrl);
		
		columnDetailListAdapter = new ColumnDetailListAdapter(this);
		columnDetail = new ColumnDetail(this,columnItemListView,columnsBaseUrlString,columnsCacheFileName,columnDetailListAdapter);
		columnDetail.setCacheFolder(cacheFilePath);
		columnDetail.init();
		
		column_detail.setText("ר��:"+columnItem.ColumnName);
	}
}
