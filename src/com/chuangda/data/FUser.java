package com.chuangda.data;

import com.chuangda.MainActivity;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.common.FTime;
import com.chuangda.net.DataHttp;
import com.chuangda.widgets.MODBUS_ITEM;

public class FUser {

	public static String urlPay = "http://api.cdhkcn.com/Pay/Precreate";
	public static String urlPayQuery = "http://api.cdhkcn.com/Pay/Query";
	public static String urlPayFinish = "http://api.cdhkcn.com/Pay/Finish";
	public static String urlPayReg = "http://api.cdhkcn.com/Device/Reg";
	public static String urlDeviceState = "http://api.cdhkcn.com/Device/State";
	public static String urlDeviceMaintain = "http://api.cdhkcn.com/Device/Maintain";
	public static String urlDeviceConsume = "http://api.cdhkcn.com/Device/Consume";
	
	//
	public static String amount ="0.01";
	public static String cardno = "1005432456";
	public static String deviceno = "0934ED34";
	public static String operatorcardno = "222999";
	public static String devicetype = "1";
	public static String paymode = "1";
	public static String timestamp ;
	public static String tradeno = "";
	
	public FUser() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getQr(){
		tradeno = "";
		timestamp = FTime.getTimeString("yyyy-MM-dd HH:mm:ss");
		String ret = "amount="+amount
				+"&cardno="+cardno
				+"&deviceno="+deviceno
				+"&devicetype="+devicetype
				+"&timestamp="+timestamp
				+"&TestMode="+"1";
		FLog.v("str QR="+ret);
		return ret;
	}
	
	public static String getPayQuery(){
		String ret = "deviceno="+deviceno
				+"&timestamp="+timestamp
				+"&tradeno="+tradeno
				+"&TestMode="+"1";
		FLog.v("getPayQuery="+ret);
		return ret;
	}
	
	public static String getPayFinish(String status){
		String ret = "deviceno="+deviceno
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
		String ret = "deviceno="+deviceno
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&pulse="+MODBUS_ITEM.PULSE
				+"&unitprice="+FData.getWaterPrice()
				+"&voltage="+MODBUS_ITEM.VOLTAGE
				+"&tds="+MODBUS_ITEM.TDS_OUT
				+"&power="+"0.0"
				+"&water="+MODBUS_ITEM.FLOW
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
	public static String getDeviceConsume(String total, String amount, String balance ){
		String ret = "deviceno="+deviceno
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&cardno="+cardno
				+"&total="+total
				+"&amount="+amount
				+"&balance="+balance
				+"&TestMode="+"1";
		FLog.v("getDeviceConsume="+ret);
		return ret;
	}
	
	//DeviceState
	public static String getDeviceMaintain(){
		String ret = "deviceno="+deviceno
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&level1time="+"2015-12-01 23:07:50"
				+"&level2time="+"2015-12-01 23:07:50"
				+"&level3time="+"2015-12-01 23:07:50"
				+"&rotime="+"2015-12-01 23:07:50"
				+"&TestMode="+"1";
		FLog.v("getDeviceMaintain="+ret);
		return ret;
	}

	public static String sendDeviceMaintain(){
		String ret = null;
		ret = DataHttp.sendHttpPost(urlDeviceMaintain, getDeviceMaintain());
		FLog.v("sendDeviceMaintain ="+ret);
		return ret;
	}
}
