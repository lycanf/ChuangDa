package com.chuangda.data;

import com.chuangda.common.FLog;
import com.chuangda.common.FTime;

public class FUser {

	public static String urlPay = "http://api.cdhkcn.com/Pay/Precreate";
	
	//
	public static String amount ="0.01";
	public static String cardno = "1005432456";
	public static String deviceno = "0934ED34";
	public static String devicetype = "1";
	public static String timestamp = "2015-12-11 19:11:11";
	
	public FUser() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getQr(){
		String ret = "amount="+amount
				+"&cardno="+cardno
				+"&deviceno="+deviceno
				+"&devicetype="+devicetype
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&TestMode="+"1";
		FLog.v("str QR="+ret);
		return ret;
	}

}
