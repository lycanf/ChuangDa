package com.chuangda.common;

public class FConst {

	public static final long CHECK_INTERVAL = 512;
	public static final long CHECK_QULITY_INTERVAL = 2048;
	
	public static final int CMD_WATER_ON = 255;
	public static final int CMD_WATER_OFF = 0;
	
	//modbus command
	public final static byte[] CMD_READ_FLOW 	= {(byte)0x01, (byte)0x03 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x03 , (byte)0x05 , (byte)0xCB};
	public final static byte[] CMD_READ_ALL 	= {(byte)0x01, (byte)0x03 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x14 , (byte)0x45 , (byte)0xC5};
	public final static byte[] CMD_READ_TDS 	= {(byte)0x01, (byte)0x03 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x05 , (byte)0x85 , (byte)0xC9};
	public final static byte[] CMD_ON 	= {(byte)0x01, (byte)0x05 ,(byte)0x00 , (byte)0x00 , (byte)0xFF , (byte)0x00 , (byte)0x8C , (byte)0x3A};
	public final static byte[] CMD_OFF 	= {(byte)0x01, (byte)0x05 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0xCD , (byte)0xCA};
	public final static byte[] CMD_SET19 	= {(byte)0x01, (byte)0x06 ,(byte)0x00 , (byte)0x13 , (byte)0x02 , (byte)0xC2 , (byte)0xF8 , (byte)0x0E};
	public final static byte[] CMD_CLEAR 	= {(byte)0x01, (byte)0x06 ,(byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x01 , (byte)0x48 , (byte)0x0A};
	
	//read head
	public final static byte[] HEAD_READ_FLOW 	= {(byte)0x01, (byte)0x03 ,(byte)0x06};
	public final static byte[] HEAD_READ_ALL 	= {(byte)0x01, (byte)0x03 ,(byte)0x28};
	public final static byte[] HEAD_READ_TDS	= {(byte)0x01, (byte)0x03 ,(byte)0x0a};
	public final static byte[] HEAD_ON 			= {(byte)0x01,(byte)0x05 ,(byte)0x00 ,(byte)0x00 ,(byte)0xFF ,(byte)0x00 };
	public final static byte[] HEAD_OFF			= {(byte)0x01,(byte)0x05 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 };
	public final static byte[] HEAD_SET19 		= {(byte)0x01, (byte)0x06 ,(byte)0x00, (byte)0x13};
	public final static byte[] HEAD_CLEAR 		= {(byte)0x01, (byte)0x06 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x01};

}
