package com.chuangda.save;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.net.DataHttp;
import com.chuangda.widgets.VideoPlay;

public class SaveVideos {

	public SaveVideos() {
		// TODO Auto-generated constructor stub
	}
	static boolean isSaving = false;
	public static boolean save(final String[] urls){
		if(isSaving){
			FLog.v("saving video !");
			return false;
		}
		Thread thread = new Thread(){
			@Override
			public void run() {
				isSaving = true;
				for(String ss : urls){
					FLog.m("shold save "+ss);
				}
				List<String> delList = new ArrayList<String>();
				List<String> fileList = getDownloadFiles(urls, delList);
				String savedVide = null;
				for(String f : fileList){
					if(!TextUtils.isEmpty(f)){
						savedVide = DataHttp.saveFile(f, "cdhkmedia");
						FLog.m("have save "+savedVide);
					}
				}
				isSaving = false;
				
				VideoPlay.DEL_LIST = delList;
				for(String ds : delList){
					FLog.m("should del "+ds);
				}
				
				
			}
		};
		thread.start();

		return true;
	}
	
	public static List<String> getDownloadFiles(String[] urls, List<String> delList){
		File file = new File(FData.VIDEO_PATH);
		List<String> urlList = new ArrayList<String>();
		String USED = "used";
		if(file != null && file.isDirectory() && urls != null && urls.length >0){
			String[] nativeList = file.list();
			for(String temp : urls){
				if(nativeList.length == 0){
					urlList.add(temp);
				}
				for(int i=0; i<nativeList.length; i++){
					if(temp.contains(nativeList[i])){
						nativeList[i] = USED;
						break;
					}
					if(i==nativeList.length-1){
						urlList.add(temp);
					}
				}
			}
			
			delList.clear();
			for(String temp : nativeList){
				if(!USED.equalsIgnoreCase(temp)){
					FLog.v("del "+temp);
//					VideoPlay.DEL_LIST.add(temp);
					delList.add(temp);
				}
			}
		}
		return urlList;
	}
}
