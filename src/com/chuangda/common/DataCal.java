package com.chuangda.common;

import android.os.Message;

import com.chuangda.MainActivity;

public class DataCal {

	public DataCal() {
		// TODO Auto-generated constructor stub
	}
	
	public  static int getFrom2Bytes(byte h, byte l){
		int rh = h & 0xff;
		int rl = l & 0xff;
		return getFrom2Bytes(rh, rl);
	}
	public  static int getFrom2Bytes(int h, int l){
		int ret = 0;
		ret = h*256 +l;
		return ret;
	}
	
	public  static int getFrom2Bits(int h, int l){
		int ret = 0;
		ret = h*256*256 +l;
		return ret;
	}
}
