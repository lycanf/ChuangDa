package com.chuangda.data;

import java.io.File;

import com.chuangda.common.DataNative;
import com.chuangda.common.FData;
import com.chuangda.common.FFile;
import com.chuangda.common.FLog;
import com.chuangda.common.FTime;
import com.chuangda.common.HandlePortData;
import com.chuangda.net.DataHttp;
import com.chuangda.widgets.MODBUS_ITEM;

public class FUser {

	public static String urlPay = "http://api.cdhkcn.com/Pay/Precreate";
	public static String urlPayQuery = "http://api.cdhkcn.com/Pay/Query";
	public static String urlPayFinish = "http://api.cdhkcn.com/Pay/Finish";
	public static String urlDeviceReg = "http://api.cdhkcn.com/Device/Reg";
	public static String urlDeviceState = "http://api.cdhkcn.com/Device/State";
	public static String urlDeviceMaintain = "http://api.cdhkcn.com/Device/Maintain";
	public static String urlDeviceConsume = "http://api.cdhkcn.com/Device/Consume";
	public static String urlVideoList = "http://api.cdhkcn.com/Device/Video";
	
	public static int DeviceNum = 16001;
	public static String MAC_ADDR = null;
	//
	public static String amount ="0.01";
	public static String cardno = "1005432456";
	public static String operatorcardno = "222999";
	public static String devicetype = "1";
	public static String paymode = "1";
	public static String timestamp ;
	public static String tradeno = "";
	
	public FUser() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getDeviceNum(){
		return "WP16"+String.format("%05d", DataNative.getDeviceNum())+MAC_ADDR;
	}
	
	public static String getQr(){
		tradeno = "";
		timestamp = FTime.getTimeString("yyyy-MM-dd HH:mm:ss");
		String ret = "amount="+amount
				+"&cardno="+cardno
				+"&deviceno="+getDeviceNum()
				+"&devicetype="+devicetype
				+"&timestamp="+timestamp
				+"&TestMode="+"1";
		FLog.v("str QR="+ret);
		return ret;
	}
	
	public static String getPayQuery(){
		String ret = "deviceno="+getDeviceNum()
				+"&timestamp="+timestamp
				+"&tradeno="+tradeno
				+"&TestMode="+"1";
		FLog.v("getPayQuery="+ret);
		return ret;
	}
	
	public static String getPayFinish(String status){
		String ret = "deviceno="+getDeviceNum()
				+"&devicetype="+devicetype
				+"&operatorcardno="+operatorcardno
				+"&paymode="+paymode
				+"&amount="+amount
				+"&timestamp="+timestamp
				+"&tradeno="+tradeno
				+"&status="+status
				+"&TestMode="+"1";
		FLog.v("getPayFinish="+ret);
		return ret;
	}
	
	//DeviceState
	public static String getDeviceState(){
		long totalFlow = DataNative.getTotalFlow();
		totalFlow += HandlePortData.getCurFlow();
		
		String ret = "deviceno="+getDeviceNum()
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&pulse="+MODBUS_ITEM.PULSE_L
				+"&unitprice="+String.format("%.2f", FData.getWaterPrice())
				+"&voltage="+MODBUS_ITEM.VOLTAGE
				+"&tds="+MODBUS_ITEM.TDS_OUT
				+"&power="+"0.0"
				+"&water="+totalFlow
				+"&TestMode="+"1";
		FLog.v("getDeviceState="+ret);
		return ret;
	}

	public static String sendDeviceState(){
		String ret = null;
		ret = DataHttp.sendHttpPost(urlDeviceState, getDeviceState());
		FLog.v("sendDeviceState ="+ret);
		return ret;
	}
	
	//DeviceConsume
	public static String getDeviceConsume(long total, String amount, String balance, long period ){
		String ret = "deviceno="+getDeviceNum()
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&cardno="+cardno
				+"&total="+total
				+"&amount="+amount
				+"&balance="+balance
				+"&period="+period
				+"&TestMode="+"1";
		FLog.v("getDeviceConsume="+ret);
		return ret;
	}
	
	//DeviceState
	public static String getDeviceMaintain(){
		String ret = "deviceno="+getDeviceNum()
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&level1time="+DataNative.getMaintainPPF()
				+"&level2time="+DataNative.getMaintainCTO()
				+"&level3time="+DataNative.getMaintainUDF()
				+"&rotime="+DataNative.getMaintainRO()
				+"&TestMode="+"1";
		FLog.v("getDeviceMaintain="+ret);
		FFile.record(ret);
		return ret;
	}

	public static String sendDeviceMaintain(){
		String ret = null;
		ret = DataHttp.sendHttpPost(urlDeviceMaintain, getDeviceMaintain());
		FLog.v("sendDeviceMaintain ="+ret);
		FFile.record(ret);
		return ret;
	}
	
	//DeviceState
	public static String getVideoList(){
		String list = "";
		File dir = new File(FData.VIDEO_PATH);
		if(dir!=null && dir.isDirectory()){
			String[] temp = dir.list();
			for(String f : temp){
				list += f+"#";
			}
		}
		String ret = "deviceno="+getDeviceNum()
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&videolist="+list
				+"&TestMode="+"1";
		FLog.v("getDeviceMaintain="+ret);
		return ret;
	}

	public static String sendVideoList(){
		String ret = null;
		ret = DataHttp.sendHttpPost(urlVideoList, getVideoList());
		FLog.v("sendVideoList ="+ret);
		return ret;
	}
}
