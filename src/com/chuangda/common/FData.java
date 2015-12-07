package com.chuangda.common;

import android.graphics.Color;
import android.view.KeyEvent;


public class FData {

	public static final int KEYCODE_PRE = KeyEvent.KEYCODE_F1;
	public static final int KEYCODE_NEXT = KeyEvent.KEYCODE_F2;
	public static final int KEYCODE_ENTER = KeyEvent.KEYCODE_F3;
	public static final int KEYCODE_BACK = KeyEvent.KEYCODE_BACK;
	
	public final static int COLOR_FOCUSED = Color.RED;
	public final static int COLOR_UNFOCUSED = Color.GRAY;
	
	public final static String VIDEO_PATH = "/mnt/sdcard/video/";
	
	private static int flow_data = 450;
	private static Double water_price = 4.51;
	private static String device_info = " 1...\r\n 2...\r\n 3...\r\n";
	
	public static void reset(){
		flow_data = 450;
		water_price = 4.51;
		device_info = " 1...\r\n 2...\r\n 3...\r\n";
	}
	
	// Flow Data
	public static void setFlowData(int data){
		flow_data = data;
	}
	
	public static int getFlowData(){
		return flow_data;
	}
	
	// Water Price
	public static void setWaterPrice(Double data){
		water_price = data;
	}
	
	public static Double getWaterPrice(){
		return water_price;
	}
	
	// Device Info
	public static void setDeviceInfo(String data){
		device_info = data;
	}
	
	public static String getDeviceInfo(){
		return device_info;
	}
}
