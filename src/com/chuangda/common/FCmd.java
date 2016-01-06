package com.chuangda.common;

import com.chuangda.MainActivity;
import com.chuangda.data.FItemQueue;
import com.chuangda.data.FQueue;

public class FCmd {

	private static FQueue mFQueue = new FQueue();
	public FCmd() {
		// TODO Auto-generated constructor stub
	}

	public static int testNum = 0;
	public static boolean tesrOn = false;
	public static void test(){
		FLog.v("do test");
		testNum = 0;
		
		tesrOn = !tesrOn;
		MainActivity.sendData(tesrOn ? FConst.CMD_ON : FConst.CMD_OFF);
	}
	
	public static void setPulse(int pulse){
//		pulse = 1000;
		int p0 = pulse/256;
		int p1 = pulse - p0 * 256;
		byte b0 = (byte) (p0 & 0xFF);
		byte b1 = (byte) (p1 & 0xFF);
		FConst.CMD_SET19[4] = b0;
		FConst.CMD_SET19[5] = b1;
		byte[] crc = CRC16.calcCrc16(FConst.CMD_SET19,0,6);
		FConst.CMD_SET19[6] = crc[0];
		FConst.CMD_SET19[7] = crc[1];
		
		FItemQueue item = FItemQueue.getDefault(FConst.CMD_SET19, FConst.HEAD_SET19);
		item.tryCmdCount = 3;
		item.name = "setPulse";
		mFQueue.add(item);
	}
	
	public static void clearCmd(){
		FItemQueue item = FItemQueue.getDefault(FConst.CMD_CLEAR,FConst.HEAD_CLEAR);
		item.tryCmdCount = 3;
		item.name = "clearCmd";
		mFQueue.add(item);
	}
	
	public static void readAll(){
		FItemQueue item = FItemQueue.getDefault(FConst.CMD_READ_ALL, FConst.HEAD_READ_ALL);
		item.name = "readAll";
		mFQueue.add(item);
	}
	
	public static void readTDS(){
		FItemQueue item = FItemQueue.getDefault(FConst.CMD_READ_TDS, FConst.HEAD_READ_TDS);
		item.name = "readTDS";
		mFQueue.add(item);
	}
	
	public static void readWater(){
		FItemQueue item = FItemQueue.getDefault(FConst.CMD_READ_FLOW, FConst.HEAD_READ_FLOW);
		item.name = "readWater";
		mFQueue.add(item);
	}
	
	public static void waterOpen(boolean bOn){
		byte[] cmd_water = bOn ? FConst.CMD_ON : FConst.CMD_OFF;
		byte[] head_water = bOn ? FConst.HEAD_ON : FConst.HEAD_OFF;
		FItemQueue item = FItemQueue.getDefault(cmd_water, head_water);
		item.tryCmdCount = 3;
		item.name = "waterOpen "+bOn;
		mFQueue.add(item);
	}
}
