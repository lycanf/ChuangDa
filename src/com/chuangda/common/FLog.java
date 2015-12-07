package com.chuangda.common;

import android.util.Log;

public class FLog {

	public static boolean SHOW = true;
	public static String  TAG  = "fq";
	
	public static void v(String str){
		if(!SHOW) return;
		Log.v(TAG,str);
	}
	
	public static void v(String t, String str){
		if(!SHOW) return;
		Log.v(t,str);
	}
	
	public static void showClassName(){
		String clazzName2 = new Throwable().getStackTrace()[1].getClassName(); 
		FLog.v("showClassName="+clazzName2);
	}
}
