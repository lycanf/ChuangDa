package com.chuangda.common;

import com.chuangda.MainActivity;

public class FCmd {

	public static final int CMD_WATER_ON = 255;
	public static final int CMD_WATER_OFF = 0;
	
	//modbus command
	public static byte[] CMD_ON 	= {(byte)0x01, (byte)0x05 ,(byte)0x00 , (byte)0x00 , (byte)0xFF , (byte)0x00 , (byte)0x8C , (byte)0x3A};
	public static byte[] CMD_OFF 	= {(byte)0x01, (byte)0x05 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0xCD , (byte)0xCA};
	public static byte[] CMD_READ_FLOW 	= {(byte)0x01, (byte)0x03 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x03 , (byte)0x05 , (byte)0xCB};
	public static byte[] CMD_READ_TDS 	= {(byte)0x01, (byte)0x03 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x05 , (byte)0x85 , (byte)0xC9};

	public static byte[] CMD_READ_ALL 	= {(byte)0x01, (byte)0x03 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x14 , (byte)0x45 , (byte)0xC5};
	public static byte[] CMD_SET19 	= {(byte)0x01, (byte)0x06 ,(byte)0x00 , (byte)0x13 , (byte)0x02 , (byte)0xC2 , (byte)0xF8 , (byte)0x0E};
	public static byte[] CMD_CLEAR 	= {(byte)0x01, (byte)0x06 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x01 , (byte)0x48 , (byte)0x0A};
	
	public FCmd() {
		// TODO Auto-generated constructor stub
	}

	public static int testNum = 0;
	public static void test(){
		FLog.v("do test");
		testNum = 0;
		readAll();
	}
	
	public static void setPulse(int pulse){
//		pulse = 1000;
		int p0 = pulse/256;
		int p1 = pulse - p0 * 256;
		byte b0 = (byte) (p0 & 0xFF);
		byte b1 = (byte) (p1 & 0xFF);
		CMD_SET19[4] = b0;
		CMD_SET19[5] = b1;
		byte[] crc = CRC16.calcCrc16(CMD_SET19,0,6);
		CMD_SET19[6] = crc[0];
		CMD_SET19[7] = crc[1];
		MainActivity.sendData(CMD_SET19);
	}
	
	public static void clearCmd(){
		MainActivity.sendData(CMD_CLEAR);
	}
	
	public static void readAll(){
		MainActivity.sendData(CMD_READ_ALL);
	}
	
	public static void readTDS(){
		MainActivity.sendData(CMD_READ_TDS);
	}
	
	public static void readWater(){
//		FLog.v("readWater");
		MainActivity.sendData(CMD_READ_FLOW);
	}
	
	public static void waterOpen(boolean bOn){
		MainActivity.sendData(bOn ? CMD_ON : CMD_OFF);
	}
}
