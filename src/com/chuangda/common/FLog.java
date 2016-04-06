package com.chuangda.common;

import android.util.Log;

public class FLog {

	public static boolean SHOW = true;
	public static String  TAG  = "fq";
	public static String  TAG_T  = "temp";
	public static String  TAG_M  = "mark";
	public static String  TAG_TH  = "thread";
	
	public static void v(String str){
		if(!SHOW) return;
//		Log.v(TAG,str);
	}
	
	public static void t(String str){
		if(!SHOW) return;
		Log.v(TAG_T,str);
	}
	
	public static void m(String str){
		if(!SHOW) return;
		Log.v(TAG_M,str);
	}
	
	public static void t(byte[] str){
		if(!SHOW) return;
		String ret = "";
		for(byte b : str){
			ret += String.format("0x%02x ", (b & 0xff));
		}
		Log.v(TAG_T,ret);
	}
	
	public static void v(String t, String str){
		if(!SHOW) return;
		Log.v(t,str);
	}
	
	public static void e(String str){
		if(!SHOW) return;
		Log.e(TAG,str);
	}
	
	public static void showClassName(){
		String clazzName2 = new Throwable().getStackTrace()[1].getClassName(); 
		FLog.v("showClassName="+clazzName2);
	}
	
	public static void th(String str){
		if(!SHOW) return;
		Log.v(TAG_TH,str);
	}
	
}
