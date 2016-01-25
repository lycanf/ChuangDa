package com.chuangda.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataNative {

	public static String NAME = "data_native";
	public static String Creat_Time = FTime.getTimeString("yyyy-MM-dd HH:mm:ss");
	
	public static final String RATE_WATER = "RATE_WATER";
	public static final String RATE_FLOW = "RATE_FLOW";
	public static final String KEY_A = "KEY_A";
	public static final String DEVICE_NUM= "DEVICE_NUM";
	public static final String DEVICE_TOTAL_FLOW = "DEVICE_TOTAL_FLOW";
	
	public static final String MAINTAIN_RO = "DEVICE_MAINTAIN_RO";
	public static final String MAINTAIN_PPF = "MAINTAIN_PPF";
	public static final String MAINTAIN_CTO = "MAINTAIN_CTO";
	public static final String MAINTAIN_UDF = "MAINTAIN_UDF";
	
	public static SharedPreferences sp;
	public static final float water_price = 0.4f;
	public static final int flow_data = 450;
	static final String DefaltKeyA = "CD8888888888";
//	static final String DefaltKeyA = "ffffffffffff";
	
	public static boolean IS_SUPPORT_NFC = false;
	public static void init(Context context) {
		sp = context.getApplicationContext().getSharedPreferences(NAME, Activity.MODE_PRIVATE);
	}

	public static void setRateWater(float rate){
		SharedPreferences.Editor editor = sp.edit();
		editor.putFloat(RATE_WATER, rate);
		editor.commit();
	}
	
	public static float getRateWater(){
		float ret = sp.getFloat(RATE_WATER, water_price);
		return ret;
	}
	
	public static void setRateFlow(int rate){
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(RATE_FLOW, rate);
		editor.commit();
	}
	
	public static int getRateFlow(){
		int ret = sp.getInt(RATE_FLOW, flow_data);
		return ret;
	}
	
	public static void setTotalFlow(long flow){
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(DEVICE_TOTAL_FLOW, flow);
		editor.commit();
	}
	
	public static long getTotalFlow(){
		long ret = sp.getLong(DEVICE_TOTAL_FLOW, 0);
		return ret;
	}
	
	public static void setDeviceNum(int num){
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(DEVICE_NUM, num);
		editor.commit();
		device_num = sp.getInt(DEVICE_NUM, 1);
	}
	
	private static int device_num = -1;
	public static int getDeviceNum(){
		if(device_num < 0){
			device_num = sp.getInt(DEVICE_NUM, 1);
		}
		return device_num;
	}
	
	public static void setKeyA(String key){
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_A, key);
		editor.commit();
	}
	
	public static String getKeyA(){
		String ret = sp.getString(KEY_A, DefaltKeyA);
		return ret;
	}
	
	//maintain
	public static void setMaintainRO(String str){
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(MAINTAIN_RO, str);
		editor.commit();
	}
	public static String getMaintainRO(){
		String ret = sp.getString(MAINTAIN_RO, Creat_Time);
		return ret;
	}
	public static void setMaintainPPF(String str){
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(MAINTAIN_PPF, str);
		editor.commit();
	}
	public static String getMaintainPPF(){
		String ret = sp.getString(MAINTAIN_PPF, Creat_Time);
		return ret;
	}
	public static void setMaintainCTO(String str){
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(MAINTAIN_CTO, str);
		editor.commit();
	}
	public static String getMaintainCTO(){
		String ret = sp.getString(MAINTAIN_CTO, Creat_Time);
		return ret;
	}
	public static void setMaintainUDF(String str){
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(MAINTAIN_UDF, str);
		editor.commit();
	}
	public static String getMaintainUDF(){
		String ret = sp.getString(MAINTAIN_UDF, Creat_Time);
		return ret;
	}
}
