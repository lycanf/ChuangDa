package com.chuangda.widgets;

import android.widget.TextView;

public class FItemWidget {
	private int    id = 0;
	private boolean isSelected = false;
	private boolean isClickable = false;
	
	//public
	public String name = "";
	public String value = "";
	public int resId = 0;
	public int type = -1;
	public TextView text = null;
	public Object obj = null;
	
	//cdhk
	public FItemWidget(String n, boolean click ,int t) {
		type = t;
		isClickable = click;
		name = n;
	}
	
	public FItemWidget(String n, boolean click) {
		name = n;
		isClickable = click;
	}
	public FItemWidget(String n, String v) {
		name = n;
		value = v;
	}
	public FItemWidget(){
		
	}

	//data
	public void setValue(String v){
		value = v;
	}
	public boolean isClickable(){
		return isClickable;
	}
	//UI
	public void setSelected(boolean b){
		isSelected = b;
	}
	
	public boolean isSelected(){
		return isSelected;
	}
}
