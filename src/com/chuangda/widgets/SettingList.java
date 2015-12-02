package com.chuangda.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SettingList extends ScrollView {
	Context mContext; 
	LinearLayout mMainLayout = null;
	
	public SettingList(Context context) {
		super(context);
		init(context);
	}

	public SettingList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SettingList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		mContext = context;
		mMainLayout = new LinearLayout(mContext);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		addView(mMainLayout);
		
		
	}
}
