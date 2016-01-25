package com.chuangda.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FLinearLayout extends LinearLayout {

	ArrayList<FItemWidget> mList = new ArrayList<FItemWidget>();
	Context mContext = null;
	int width,height,textSize,marginTop;
	
	public FLinearLayout(Context context) {
		super(context);
		init(context);
	}

	public FLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context cnt){
		mContext = cnt;
	}

	public void init(FItemWidget[] l, int w, int h, int s, int marTop){
		width = w;
		height = h;
		textSize = s;
		marginTop = marTop;
		
		for(FItemWidget i : l ){
			addItem(i);
		}
	}
	
	public void addItem(FItemWidget item){
		mList.add(item);
		if(null == item.text){
			item.text = new TextView(mContext);
			item.text.setFocusable(true);
			item.text.setSingleLine(true);
			item.text.setEllipsize(TruncateAt.MARQUEE);
		}
		if(item.text != null){
			item.text.setText(item.name+item.value);
			item.text.setTextSize(textSize);
			item.text.setGravity(Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams pm = new LayoutParams(width,height);
			pm.topMargin = marginTop;
			addView(item.text, pm);
		}
	}
	
	public void setText(int type, String str){
		for(FItemWidget item : mList){
			if(type == item.type && null != item.text){
				item.text.setText(str);
			}
		}
	}
	
	public int getItemCount(){
		return mList.size();
	}
	
	public final ArrayList<FItemWidget> getItems(){
		return mList;
	}
	
	private void selectItem(int position){
		
	}
	
	private void doItem(int position){
		
	}
}
