package com.chuangda.common;

import com.chuangda.MainActivity;

public class WaterMgr {
	public static final int WATER_LEVEL_3 = 3000;
	public static final int WATER_LEVEL_5 = 5000;
	public static final int WATER_LEVEL_0 = 0;
	
	private static boolean WATER_TIMER = false;
	
	public static final int WATER_STATE_READ = 1;
	public static final int WATER_STATE_ON = 2;
	public static final int WATER_STATE_OFF = 3;
	public static int WATER_STATE = WATER_STATE_READ;
	
	private static int      START_WATER_FLOW = HandlePortData.getCurFlow();
	private static int 		mWaterLevel = 0;
	private static int 		mWaterLevelStart = 0;
	private static int 		mWaterLeft = 0;
	private static int 		mStartWaterFlow3L= 0;
	private static int 		mStartWaterFlow5L= 0;

	public static String TAG = "watermgr";
	
	public static int getWaterLevelStart(){
		int ret = mWaterLevelStart;
		mWaterLevelStart = 0;
		return ret;
	}
	public static int setWaterLevelStart(int level){
		mWaterLevelStart = level;
		return mWaterLevelStart;
	}
	
	public static int getWaterLevel(){
		return mWaterLevel;
	}
	
	public static int getWaterLeft(){
		return mWaterLeft;
	}
	
	public static int getStartWaterFlow(){
		return START_WATER_FLOW;
	}
	public static boolean isWaterTimer(){
		return WATER_TIMER;
	}
	public static int getStartWaterFlow3L(){
		return mStartWaterFlow3L;
	}
	public static int getStartWaterFlow5L(){
		return mStartWaterFlow5L;
	}
	
	public static void checkWater(){
		if(!HandlePortData.isWaterOn()){
			return;
		}
		if(isWaterTimer()){
			int waterUsed = HandlePortData.getCurFlow() - getStartWaterFlow();
			mWaterLeft = mWaterLevel - waterUsed;
			FLog.v(TAG,"checkWater "+mWaterLevel+" -- "+mWaterLeft);
			if(mWaterLeft < 0){
//				MainActivity.showToast("checkWater stop "+mWaterLevel);
				stop();
				init();
			}
		}
	}
	
	public static void init(){
		WATER_TIMER  = false;
		mWaterLevel = 0;
	}
	
	public static void start(){
		FLog.v(TAG,"start "+HandlePortData.isWaterOn());
		if(HandlePortData.isWaterOn()){
			return;
		}
		FCmd.waterOpen(true);
	}
	
	public static void stop(){
		FLog.v(TAG,"stop");
		FCmd.waterOpen(false);
//		MainActivity.sendData(CMD_OFF);
	}
	
	public static boolean start3L(){
		FLog.v(TAG,"start3L");
		boolean is3LNew = mWaterLevel != WATER_LEVEL_3;
		if(is3LNew){
			START_WATER_FLOW = HandlePortData.getCurFlow();
			mStartWaterFlow3L = START_WATER_FLOW;
		}
//		start();
		WATER_STATE = WATER_STATE_ON;
		WATER_TIMER = true;
		mWaterLevel = WATER_LEVEL_3;
		return is3LNew;
	}
	
	public static boolean start5L(){
		FLog.v(TAG,"start5L");
		boolean is5LNew = mWaterLevel != WATER_LEVEL_5;
		if(is5LNew){
			START_WATER_FLOW = HandlePortData.getCurFlow();
			mStartWaterFlow5L = START_WATER_FLOW;
		}
//		start();
		WATER_STATE = WATER_STATE_ON;
		WATER_TIMER = true;
		mWaterLevel = WATER_LEVEL_5;
		return is5LNew;
	}
	
}
