package com.chuangda.widgets.customed;

import com.chuangda.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FMaintainView extends LinearLayout {

	private TextView mText0 = null;
	private TextView mText1 = null;
	private ImageView mImg0 = null;
	private ImageView mImg1 = null;
	private ImageView mImg2 = null;
	
	public FMaintainView(Context context) {
		super(context);
		init(context);
	}

	public FMaintainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FMaintainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundResource(R.drawable.bg_device_maintain);
		LinearLayout.LayoutParams pm;
	/*	pm = new LayoutParams(80,30);
		mText0 = new TextView(context);
		mText0.setText("¸ü»»");
		mText0.setTextColor(Color.WHITE);
		mText0.setTextSize(26);
		mText0.setGravity(Gravity.CENTER);
		pm.topMargin = 16;
		addView(mText0, pm);*/
		//
		int imgH = 16;
		int imgW = 56;
		pm = new LayoutParams(imgW,imgH);
		mImg2 = new ImageView(context);
		mImg2.setBackgroundColor(Color.rgb(200, 91, 2));
		pm.topMargin = 24;
		pm.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mImg2, pm);
		//
		pm = new LayoutParams(imgW,imgH);
		mImg1 = new ImageView(context);
		mImg1.setBackgroundColor(Color.rgb(58, 255, 19));
		pm.topMargin = 4;
		pm.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mImg1, pm);
		//
		pm = new LayoutParams(imgW,imgH);
		mImg0 = new ImageView(context);
		mImg0.setBackgroundColor(Color.rgb(255, 228, 0));
		pm.topMargin = 4;
		pm.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mImg0, pm);
		//
		pm = new LayoutParams(80,30);
		mText1 = new TextView(context);
		mText1.setText("PPF");
		mText1.setTextColor(Color.WHITE);
		mText1.setTextSize(26);
		mText1.setGravity(Gravity.CENTER);
		pm.topMargin = 2;
		addView(mText1, pm);
	}
	
	public void showTextChange(boolean change){
		mText0.setVisibility(change ? View.VISIBLE : View.GONE);
	}
	
	public void setTextType(String type){
		mText1.setText(type);
	}
	
	public void setSelected(boolean b){
		setBackgroundResource(b ? R.drawable.bg_device_maintain_selected : R.drawable.bg_device_maintain);
	}
}
