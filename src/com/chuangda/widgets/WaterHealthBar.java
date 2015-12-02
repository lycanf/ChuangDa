package com.chuangda.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class WaterHealthBar extends LinearLayout {

	Context mContext; 
	
	public WaterHealthBar(Context context) {
		super(context);
		init(context);
	}

	public WaterHealthBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WaterHealthBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		mContext = context;
		setOrientation(LinearLayout.HORIZONTAL);
	}
	
	public void setBar(int width, int color){
		removeAllViews();
		View view = new View(mContext);
		view.setBackgroundColor(color);
		LinearLayout.LayoutParams pm = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		addView(view, pm);
	}
}
