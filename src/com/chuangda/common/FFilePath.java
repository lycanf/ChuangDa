package com.chuangda.common;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FFilePath {

	public FFilePath() {
		// TODO Auto-generated constructor stub
	}

	public static void test(Context context){
		FLog.v("getAbsolutePath="+context.getApplicationContext().getFilesDir().getAbsolutePath());
		FLog.v("getPackageResourcePath="+context.getApplicationContext().getPackageResourcePath());
		FLog.v("sd="+Environment.getExternalStorageDirectory()); 
	}
	
	public static String getStoragePath(){
		String ret = null;
		boolean sdCardExist = Environment.getExternalStorageState()   
                .equals(Environment.MEDIA_MOUNTED); 
		if(!sdCardExist){
			return ret;
		}
		ret = Environment.getExternalStorageDirectory()+"/";
//		FLog.v("getStoragePath "+ret);
		return ret;
	}
	
	public static boolean isFileExit(String filePath){
		File f = new File(filePath);
		return f.exists();
	}
}
