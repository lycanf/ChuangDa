package com.chuangda.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataNative {

	public static String NAME = "data_native";
	
	public static final String RATE_WATER = "RATE_WATER";
	public static final String RATE_FLOW = "RATE_FLOW";
	public static final String KEY_A = "KEY_A";
	
	public static SharedPreferences sp;
	public static final float water_price = 4.51f;
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
	
	public static void setKeyA(String key){
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_A, key);
		editor.commit();
	}
	
	public static String getKeyA(){
		String ret = sp.getString(KEY_A, DefaltKeyA);
		return ret;
	}
}
