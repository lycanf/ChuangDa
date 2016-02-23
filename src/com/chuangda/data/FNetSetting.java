package com.chuangda.data;

import com.chuangda.common.FLog;
import com.chuangda.common.FTime;
import com.chuangda.net.DataHttp;

public class FNetSetting {

	static String STR_SUC = "1";
	
	public static String urlSettingVideo = "http://api.cdhkcn.com/Device/Video";
	public static String urlSettingSetReply = "http://api.cdhkcn.com/Device/SetReply";
	
	public static String urlSettingGetIntervalPara = "http://api.cdhkcn.com/Device/GetIntervalPara";
	public static String urlSettingGetVideoList = "http://api.cdhkcn.com/Device/GetVideoList";
	public static String urlSettingGetMeasurePara = "http://api.cdhkcn.com/Device/GetMeasurePara";
	public static String urlSettingGetUpdateUrl = "http://api.cdhkcn.com/Device/GetUpdateUrl";
	
	public static String getPara(){
		String ret = "deviceno="+FUser.getDeviceNum()
				+"&timestamp="+FTime.getTimeString("yyyy-MM-dd HH:mm:ss")
				+"&TestMode="+"1";
		FLog.v("getPara="+ret);
		return ret;
	}
	
	public FNetSetting() {
		// TODO Auto-generated constructor stub
	}

	public static void parse(String res){
		getUpdateUrl();
		FJson jobj = new FJson(res);
		String ret = jobj.getString("result");
		if(ret.equals(STR_SUC)){
			ret = jobj.getString("setinteval");
			if(ret.equals(STR_SUC)){
				getSetinteval();
			}
			ret = jobj.getString("setvideo");
			if(ret.equals(STR_SUC)){
				getVideoList();
			}
			ret = jobj.getString("setmeasure");
			if(ret.equals(STR_SUC)){
				getMeasurePara();
			}
			ret = jobj.getString("setupdate");
			if(ret.equals(STR_SUC)){
				getUpdateUrl();
			}
		}
	}
	
	private static void getSetinteval(){
		String ret = DataHttp.sendHttpPost(urlSettingGetIntervalPara, getPara());
		FLog.v("getSetinteval="+ret);
	}
	
	private static void getVideoList(){
		String ret = DataHttp.sendHttpPost(urlSettingGetVideoList, getPara());
		FLog.v("getVideoList="+ret);
	}
	
	private static void getMeasurePara(){
		String ret = DataHttp.sendHttpPost(urlSettingGetMeasurePara, getPara());
		FLog.v("getMeasurePara="+ret);
	}
	
	private static void getUpdateUrl(){
		String ret = DataHttp.sendHttpPost(urlSettingGetUpdateUrl, getPara());
		FLog.v("getUpdateUrl="+ret);
	}
}
