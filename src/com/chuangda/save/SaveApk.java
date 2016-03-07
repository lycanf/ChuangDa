package com.chuangda.save;

import java.io.File;

import android.text.TextUtils;

import com.chuangda.MainActivity;
import com.chuangda.common.FFilePath;
import com.chuangda.common.FLog;
import com.chuangda.net.DataHttp;

public class SaveApk {

	public SaveApk() {
		// TODO Auto-generated constructor stub
	}

	static boolean isSaving = false;
	public static boolean save(final String url){
		if(isSaving){
			FLog.v("saving !");
			return false;
		}
		Thread thread = new Thread(){
			@Override
			public void run() {
				isSaving = true;
				String savepath = FFilePath.getStoragePath();
	            savepath += "cdhkapk";
				String fileName = DataHttp.saveFile(url, "cdhkapk");
				if(TextUtils.isEmpty(fileName)){
					MainActivity.gHandle(MainActivity.MSG_UPDATE_APK, savepath+","+fileName);
				}
				isSaving = false;
			}
		};
		thread.start();

		return true;
	}
}
