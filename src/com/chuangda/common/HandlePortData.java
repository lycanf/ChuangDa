package com.chuangda.common;

import com.chuangda.MainActivity;
import com.chuangda.data.FQueue;
import com.chuangda.widgets.MODBUS_ITEM;

class CMD_ITEM{
	public final byte[] cmd_head ;
	public byte[] cmd_read ;
	public byte[] cmd_crc;
	public boolean readEnd = false;
	public boolean crcRight = false;
	public CMD_ITEM( byte[] h, int lr){
		cmd_head = h;
		cmd_read = new byte[lr];
		cmd_crc = new byte[h.length+lr-2];
	}
	public byte[] getCrc(){
		System.arraycopy(cmd_head, 0, cmd_crc, 0, cmd_head.length);  
        System.arraycopy(cmd_read, 0, cmd_crc, cmd_head.length, cmd_read.length-2);
		return cmd_crc;
	}
}

public class HandlePortData {
	public static byte[] DATA_CMD_HEAD = new byte[8] ;
	public static int 		mCmdLine = -1;
	public static int mCurCmdNum = 0;
	private static int mFlowCur = 0;
	public static int mFlowPre = 0;
	public static boolean mRecognizeCmd = true;
	private static boolean WATER_ON = false;
	
	private static boolean isAnswer = false;
	
	public static CMD_ITEM[] mReadCmds = {
		new CMD_ITEM(FConst.HEAD_READ_FLOW, 8),//water flow
		new CMD_ITEM(FConst.HEAD_READ_ALL, 42),//read 20 Bits
		new CMD_ITEM(FConst.HEAD_READ_TDS, 12),//read TDS
		new CMD_ITEM(FConst.HEAD_ON, 2),//water open
		new CMD_ITEM(FConst.HEAD_SET19, 4),//set 19 pulse
		new CMD_ITEM(FConst.HEAD_CLEAR, 2),//set 19 pulse
		new CMD_ITEM(FConst.HEAD_OFF, 2),//water off
	};
	
	public static boolean getAnswerState(){
		return isAnswer;
	}
	public static void setAnswerState(){
		isAnswer = false;
	}
	
	public static boolean isWaterOn(){
		return WATER_ON;
	}
	public static int getCurFlow(){
		return mFlowCur;
	}
	
	public static void clear(){
		clearItemCmd();
		mFlowCur = 0;
	}
	
	public static void clearItemCmd(){
		for(byte b : DATA_CMD_HEAD){
			b = (byte)0x00;
		}
		for(CMD_ITEM i : mReadCmds){
			i.crcRight = false;
			i.readEnd = false;
		}
		mCurCmdNum 	= -1;
		mCmdLine	= -1;
		mRecognizeCmd = true;
	}
	
	public static void recordCmd(byte input){
		for(int i=0 ; i < DATA_CMD_HEAD.length-1 ; i++){
			DATA_CMD_HEAD[i] = DATA_CMD_HEAD[i+1];
		}
		DATA_CMD_HEAD[DATA_CMD_HEAD.length-1] = input;
	}
	
	public static boolean isCmd(byte[] cmdHead){
		boolean ret = false;
		if(null == cmdHead){
			
		}else{
			ret = true;
			int offset = DATA_CMD_HEAD.length - cmdHead.length;
			for(int i=cmdHead.length-1; i >=0; i--){
				if(DATA_CMD_HEAD[i+offset] != cmdHead[i]){
					ret = false;
				}
			}
		}
		return ret;
	}
	
	public static synchronized void handleByte(byte singleByte){
		int singleInt = singleByte & 0xFF;
//		FLog.v(" handleByte byte="+singleByte+" int="+singleInt);
		/*mDataReceived.append(singleInt);mDataReceived.append(",");
		FLog.v("handleByte="+mDataReceived.toString());*/
		
		//record
		if(mRecognizeCmd){
			recordCmd(singleByte);
			for(int i=0; i<mReadCmds.length; i++){
				CMD_ITEM item = mReadCmds[i];
				if(isCmd(item.cmd_head)){
					FLog.t("get cmd "+i);
					mRecognizeCmd = false;
					mCmdLine = i;
					mCurCmdNum = -1;
					break;
				}
			}
		}else{
			mCurCmdNum++;
			
			switch(mCmdLine){
			case 0:
				parseCmdFlow(singleInt, singleByte);
				break;
			case 1:
				parseCmdAll(singleInt, singleByte);
				break;
			case 2:
				parseCmdTDS(singleInt, singleByte);
				break;
			case 3:
				parseCmdWaterOn(singleInt, singleByte);
				break;
			case 4:
				parseCmd19(singleInt, singleByte);
				break;
			case 5:
				parseCmdClear(singleInt, singleByte);
				break;
			case 6:
				parseCmdWaterOff(singleInt, singleByte);
				break;
			}
		}

		
	}
	
	public static int getCmdLine(){
		return mCmdLine;
	}
	
	private static void parseCmdClear(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
//		FLog.v("parseCmdClear  "+vint);
		if(item.crcRight){
			FLog.v("parseCmdClear success ");
		}
		if(item.readEnd){
			FLog.v("parseCmdClear end ");
			parseCmdEnd(item);
		}
	}
	
	private static void parseCmd19(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
//		FLog.v("parseCmd19  "+vint);
		if(item.crcRight){
			int h = item.cmd_read[0] & 0xff;
			int l = item.cmd_read[1] & 0xff;
			int val = h*256 + l;
			DataNative.setRateFlow(val);
			FLog.v("parseCmd19 success "+val);
			MainActivity.gUIHandler.obtainMessage(
					MainActivity.MSG_MODIFY_FLOW,val,0).sendToTarget();
		}
		if(item.readEnd){
			parseCmdEnd(item);
		}
	}
	
	private static void parseCmdWaterOn(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
		if(item.crcRight){
//			FLog.t("parseCmdWaterOn success ");
			WATER_ON = true;
		}
		if(item.readEnd){
			parseCmdEnd(item);
		}
	}
	
	private static void parseCmdWaterOff(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
		if(item.crcRight){
//			FLog.t("parseCmdWaterOff success ");
			WATER_ON = false;
		}
		if(item.readEnd){
			parseCmdEnd(item);
		}
	}
	
	private static void parseCmdTDS(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
		if(item.crcRight){
//			FLog.t("parseCmdTDS success ");
//			FLog.t(item.cmd_read);
			MODBUS_ITEM.setTDS(item.cmd_read);
		}
		if(item.readEnd){
			parseCmdEnd(item);
		}
	}
	
	private static void parseCmdAll(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
		if(item.crcRight){
//			FLog.t("parseCmdAll success ");
//			FLog.t(item.cmd_read);
			MODBUS_ITEM.setItem(item.cmd_read);
		}
		if(item.readEnd){
			parseCmdEnd(item);
		}
	}
	
	private static void parseCmdFlow(int vint, byte vbyte){
		CMD_ITEM item = parseCmd(vbyte);
		if(item.crcRight){
			int flowl = DataCal.getFrom2Bytes(item.cmd_read[2], item.cmd_read[3]);
			int flowh = DataCal.getFrom2Bytes(item.cmd_read[4], item.cmd_read[5]);
//			setFlowAdd(DataCal.getFrom2Bits(flowh,flowl));
			int flow = DataCal.getFrom2Bits(flowh,flowl);
			if(flow >=0 && flow < 10000000){
				mFlowCur = flow;
			}
		}
		if(item.readEnd){
			parseCmdEnd(item);
		}
	}
	
	private static CMD_ITEM parseCmd(byte vbyte){
		if(mCmdLine < 0 || mCmdLine >= mReadCmds.length ){
			FLog.e("parseCmdFlow error mCmdLine="+mCmdLine);
		}
		CMD_ITEM item = mReadCmds[mCmdLine];
		if(mCurCmdNum < 0 || mCurCmdNum >= item.cmd_read.length){
			FLog.e("parseCmdFlow error mCurCmdNum="+mCurCmdNum);
		}
		item.cmd_read[mCurCmdNum] = vbyte;
//		FLog.t("parseCmdFlow "+mCurCmdNum+" = "+vint);
		if(mCurCmdNum == item.cmd_read.length-1){
//			FLog.t(item.cmd_read);
			byte[] crc = CRC16.calcCrc16(item.getCrc());
			int len = item.cmd_read.length-1;
			byte[] readCrc = {item.cmd_read[len-1], item.cmd_read[len]};
			item.crcRight = crc[0] == readCrc[0] && crc[1] == readCrc[1];
			item.readEnd = true;
		}
		return item;
	}
	
	private static void parseCmdEnd(CMD_ITEM item){
		FLog.t("parseCmdEnd ");
		clearItemCmd();
		FQueue.SingleCmdHead = item.cmd_head;
		isAnswer = true;
	}
}
