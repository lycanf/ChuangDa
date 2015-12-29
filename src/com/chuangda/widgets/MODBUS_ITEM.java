package com.chuangda.widgets;

import com.chuangda.MainActivity;
import com.chuangda.common.DataCal;
import com.chuangda.common.FLog;


public class MODBUS_ITEM {
	
	public static int FLOW = 0;
	public static int TDS_OUT = 0;
	public static int VOLTAGE = 0;//mv
	public static int PULSE   = 0;
	public static int PULSE_L = 0;
	
	private static int mLastTDS_OUT = 0;
	
	public MODBUS_ITEM() {
		
	}
	
	public static void setTDS(byte[] cmd){
		TDS_OUT = DataCal.getFrom2Bytes(cmd[8], cmd[9]);
		FLog.m(System.currentTimeMillis()+" MODBUS_ITEM TDS_OUT="+TDS_OUT );
		
		if(TDS_OUT != mLastTDS_OUT){
			mLastTDS_OUT = TDS_OUT;
			MainActivity.gUIHandler.obtainMessage(
					MainActivity.MSG_SHOW_TDS).sendToTarget();
		}
		
	}
	
	public static void setItem(byte[] cmd){
		int len = cmd.length;
		//tds
		if(len > 9){
			TDS_OUT = DataCal.getFrom2Bytes(cmd[8], cmd[9]);
		}
		
		//voltage
		if(len > 19){
			VOLTAGE = DataCal.getFrom2Bytes(cmd[18], cmd[19]);
		}
		
		int low, high;
		//pulse
		if(len > 27){
			low = DataCal.getFrom2Bytes(cmd[24], cmd[25]);
			high = DataCal.getFrom2Bytes(cmd[26], cmd[27]);
			PULSE =  DataCal.getFrom2Bits(high,low);
		}

		//pulse per l
		if(len > 39){
			PULSE_L = DataCal.getFrom2Bytes(cmd[38], cmd[39]);
		}
		
		//flow
		if(len > 5){
			low = DataCal.getFrom2Bytes(cmd[2], cmd[3]);
			high = DataCal.getFrom2Bytes(cmd[4], cmd[5]);
			FLOW =  DataCal.getFrom2Bits(high,low);
		}
		String str = "\n\rTDS_OUT="+TDS_OUT+"\n\rVOLTAGE="+VOLTAGE+"\n\rPULSE="+PULSE+"\n\rPULSE_L="+PULSE_L+"\n\rFLOW="+FLOW;
		FLog.m(str);
		MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_TEST_TEXT, str).sendToTarget();
	}

	
}
