package com.chuangda.common;

import com.chuangda.MainActivity;

public class WaterMgr {
	public static int WATER_LEVEL_3 = 300;
	public static int WATER_LEVEL_5 = 500;
	public static boolean WATER_TIMER = false;
	
	public static final int WATER_STATE_READ = 1;
	public static final int WATER_STATE_ON = 2;
	public static final int WATER_STATE_OFF = 3;
	public static int WATER_STATE = WATER_STATE_READ;
	
	private static int      START_WATER_FLOW = HandlePortData.getCurFlow();
	private static int 		mWaterLevel = 0;

	public static String TAG = "watermgr";
	
	public static void checkWater(){
		if(!HandlePortData.isWaterOn()){
			return;
		}
		if(WATER_TIMER){
			FLog.v(TAG,"checkWater "+mWaterLevel+" -- "+HandlePortData.getCurFlow());
			if(mWaterLevel < HandlePortData.getCurFlow()){
				MainActivity.gUIHandler.obtainMessage(
						MainActivity.MSG_SHOW_TOAST,"checkWater stop "+mWaterLevel).sendToTarget();
				stop();
			}
		}
	}
	
	public static void start(){
		FLog.v(TAG,"start "+HandlePortData.isWaterOn());
		if(HandlePortData.isWaterOn()){
			return;
		}
//		isSetWaterOn = true;
		FCmd.waterOpen(true);
	}
	
	public static void stop(){
		FLog.v(TAG,"stop");
//		isSetWaterOn = false;
		WATER_TIMER  = false;
		mWaterLevel = 0;
		FCmd.waterOpen(false);
//		MainActivity.sendData(CMD_OFF);
	}
	
	public static void start3L(){
		FLog.v(TAG,"start3L");
		start();
		WATER_TIMER = true;
		mWaterLevel = WATER_LEVEL_3;
		START_WATER_FLOW = HandlePortData.getCurFlow();
	}
	
	public static void start5L(){
		FLog.v(TAG,"start5L");
		start();
		WATER_TIMER = true;
		mWaterLevel = WATER_LEVEL_5;
		START_WATER_FLOW = HandlePortData.getCurFlow();
	}
	
}
