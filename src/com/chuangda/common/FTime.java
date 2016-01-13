package com.chuangda.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.TextUtils;

public class FTime {

	public FTime() {
		// TODO Auto-generated constructor stub
	}

	public static String getTimeString(){
		return getTimeString(null);
	}
	
	public static String getTimeString(String format){
		long currentTime = System.currentTimeMillis();
		return convert2Time(currentTime,format);
	}
	
	public static String convert2Time(long timeMillis, String format){
		String ret = null;
		if(TextUtils.isEmpty(format)){
			format = "yyyy-MM-dd-HH:mm:ss";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = new Date(timeMillis);
		ret = formatter.format(date);
		return ret;
	}
}
