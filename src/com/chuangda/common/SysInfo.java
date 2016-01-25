package com.chuangda.common;

import com.chuangda.data.FUser;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SysInfo {

	public SysInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public static void getLocalMacAddress(Context cnt) {  
        WifiManager wifi = (WifiManager) cnt.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        String add = info.getMacAddress(); 
        FUser.MAC_ADDR = add.replace(":", "");
        return ;
    }  

}
