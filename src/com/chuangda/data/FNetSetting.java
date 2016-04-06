package com.chuangda.data;

import com.chuangda.MainActivity;
import com.chuangda.common.DataNative;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.common.FTime;
import com.chuangda.net.DataHttp;
import com.chuangda.save.SaveApk;
import com.chuangda.save.SaveVideos;

public class FNetSetting {

	public static String taskid;
	
	private static int MIN_INTERVAL = 3;
	private static String STR_SUC = "0";
	private static String STR_UPDATE = "1";
	private static String urlSettingSetReply = "http://api.cdhkcn.com/Device/SetReply";
	
	private static String urlSettingGetIntervalPara = "http://api.cdhkcn.com/Device/GetIntervalPara";
	private static String urlSettingGetVideoList = "http://api.cdhkcn.com/Device/GetVideoList";
	private static String urlSettingGetMeasurePara = "http://api.cdhkcn.com/Device/GetMeasurePara";
	private static String urlSettingGetUpdateUrl = "http://api.cdhkcn.com/Device/GetUpdateUrl";
	
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
//		sendReply(999,0);
		FJson jobj = new FJson(res);
		String ret = jobj.getString("result");
		
		if(ret.equals(STR_SUC)){
			ret = jobj.getString("setinteval");
			if(ret.equals(STR_UPDATE)){
				getSetinteval();
			}
			ret = jobj.getString("setvideo");
			if(ret.equals(STR_UPDATE)){
				getVideoList();
			}
			ret = jobj.getString("setmeasure");
			if(ret.equals(STR_UPDATE)){
				getMeasurePara();
			}
			ret = jobj.getString("setupdate");
			if(ret.equals(STR_UPDATE)){
//				getUpdateUrl();
			}
			
			//test
			/*int taskid = jobj.getInt("taskid");
			sendReply(taskid,0);*/
		}
	}
	
	public static void sendReply(int taskid, int result){
		String res = getPara()+"&result="+result+"&taskid="+taskid;
		String ret = DataHttp.sendHttpPost(urlSettingSetReply, res);
		FLog.v("ret="+ret);
		FLog.m("sendReply ret="+ret);
	}
	
	private static void getSetinteval(){
		String ret = DataHttp.sendHttpPost(urlSettingGetIntervalPara, getPara());
		FJson jobj = new FJson(ret);
		int res = jobj.getInt("result");
		if(res == 0){
			int intevalstate = jobj.getInt("stateinteval");
			if(intevalstate > MIN_INTERVAL){
				DataNative.setStateInterval(intevalstate);
			}
			int intevalMaintain = jobj.getInt("maintaininteval");
			if(intevalMaintain > MIN_INTERVAL){
				DataNative.setMaintainInterval(intevalMaintain);
			}
			int intevalTds = jobj.getInt("tdsinteval");
			if(intevalTds > MIN_INTERVAL){
				DataNative.setTdsInterval(intevalTds);
			}
			int intevalVideo= jobj.getInt("videointeval");
			if(intevalVideo > MIN_INTERVAL){
				DataNative.setVideoInterval(intevalVideo);
			}
			
			int taskid = jobj.getInt("taskid");
			sendReply(taskid,0);
		}
		FLog.v("getSetinteval="+ret);
	}
	
	private static void getVideoList(){
		String ret = DataHttp.sendHttpPost(urlSettingGetVideoList, getPara());
		FLog.m("getVideoList="+ret);
		FJson jobj = new FJson(ret);
		int res = jobj.getInt("result");
		if(res == 0){
			 /*String[] list = jobj.getStringArray("videolist");
			 SaveVideos.save(list);*/
			String temp = jobj.getString("videolist");
			temp = temp.replace(" ", "");
			temp = temp.replace("\\", "");
			temp = temp.replace("[", "").replace("]", "");
			temp = temp.replace("\"", "");
			String[] list = temp.split(",");
			SaveVideos.save(list);
			
			int tempId = jobj.getInt("taskid");
			sendReply(tempId,0);
		}
	}
	
	private static void getMeasurePara(){
		String ret = DataHttp.sendHttpPost(urlSettingGetMeasurePara, getPara());
		FLog.v("getMeasurePara="+ret);
		FJson jobj = new FJson(ret);
		if(!jobj.canUse){
			FLog.v("getMeasurePara error");
			return;
		}
		int res = jobj.getInt("result");
		if(res == 0){
			float unitprice = jobj.getFloat("unitprice");
			if(unitprice > 0){
				FData.setWaterPrice(unitprice);
			}
			int pulse = jobj.getInt("pulse");
			if(pulse > 0){
				FData.setFlowData(pulse);
			}
			int resetpower = jobj.getInt("resetpower");
			if(resetpower == 1){
				
			}
			int resetwater = jobj.getInt("resetwater");
			if(resetwater == 1){
				
			}
			
			int taskid = jobj.getInt("taskid");
			sendReply(taskid,0);
		}
	}
	
	private static void getUpdateUrl(){
		String ret = DataHttp.sendHttpPost(urlSettingGetUpdateUrl, getPara());
		FLog.v("getUpdateUrl="+ret);
		FJson jobj = new FJson(ret);
		int res = jobj.getInt("result");
		if(res == 0){
			String url = jobj.getString("updateurl");
			FLog.v("getUpdateUrl="+url);
			SaveApk.save(url);
			
			int taskid = jobj.getInt("taskid");
			sendReply(taskid,0);
		}
	}
}
