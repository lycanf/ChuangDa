package com.chuangda.common;

import com.chuangda.widgets.MODBUS_ITEM;

import android.graphics.Color;
import android.view.KeyEvent;


public class FData {

	public static final int KEYCODE_PRE = 132;
	public static final int KEYCODE_NEXT = 122;
	public static final int KEYCODE_ENTER = 131;
	public static final int KEYCODE_WATER_START = 133;
	public static final int KEYCODE_WATER_STOP = 4;
	
	public final static int COLOR_FOCUSED = Color.RED;
	public final static int COLOR_UNFOCUSED = Color.LTGRAY;
	
	public final static String VIDEO_PATH = "/mnt/sdcard/cdhkmedia/";
	public final static String APK_PATH = "/mnt/sdcard/cdhkapk/";
	
	public static String device_info = " 1...\r\n 2...\r\n 3...\r\n";
	public static int    WATER_QUALITY = 1;
	
	public static void reset(){
		device_info = " 1...\r\n 2...\r\n 3...\r\n";
	}
	
	
	// Flow Data
	public static void setFlowData(int data){
		FCmd.setPulse(data);
	}
	
	public static int getFlowData(){
		return DataNative.getRateFlow();
	}
	
	// Water Price
	public static void setWaterPrice(float data){
		DataNative.setRateWater(data);
	}
	
	public static float getWaterPrice(){
		return DataNative.getRateWater();
	}
	
	// Device Info
	public static void setDeviceInfo(){
		device_info = "TDS : "+MODBUS_ITEM.TDS_OUT
				+"\n累积流量 : "+MODBUS_ITEM.FLOW
				+"\n供电电压 : "+MODBUS_ITEM.VOLTAGE
				+"\n累积脉冲 : "+MODBUS_ITEM.PULSE
				+"\n每升脉冲 : "+MODBUS_ITEM.PULSE_L;
	}
	public static void setDeviceInfo(String data){
		device_info = data;
	}
	
	public static String getDeviceInfo(){
		return device_info;
	}
}
