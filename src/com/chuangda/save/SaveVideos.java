package com.chuangda.save;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
				List<String> fileList = getDownloadFiles(urls);
				for(String f : fileList){
					DataHttp.saveFile(f, "cdhkmedia");
				}
				isSaving = false;
			}
		};
		thread.start();

		return true;
	}
	
	public static List<String> getDownloadFiles(String[] urls){
		File file = new File(FData.VIDEO_PATH);
		List<String> urlList = new ArrayList<String>();
		String USED = "used";
		if(file != null && file.isDirectory() && urls != null && urls.length >0){
			VideoPlay.DEL_LIST.clear();
			String[] nativeList = file.list();
			for(String temp : urls){
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
			for(String temp : nativeList){
				if(!USED.equalsIgnoreCase(temp)){
					FLog.v("del "+temp);
					VideoPlay.DEL_LIST.add(temp);
				}
			}
		}
		return urlList;
	}
}
