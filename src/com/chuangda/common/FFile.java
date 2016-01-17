package com.chuangda.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.chuangda.data.FUser;

public class FFile {

	public FFile() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void record(String stock){
		String path = FFilePath.getStoragePath();
		path += "cdhkrecord";
		String fileName = FTime.getTimeString("yyyy-MM")+".txt";
		String mark = FTime.getTimeString("dd-HH:mm:ss")+" d="+FUser.deviceno+" n="+FUser.cardno+" a="+FUser.amount+" ->";
		stock = mark+stock+"\n";
		try {
			FFile.writeToFile(stock.getBytes(), path, fileName, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean writeToFile(byte[] bytes,String parentPath ,String filename,boolean writeAdd) throws IOException
	{
		
		File parent = new File(parentPath);
		if(!parent.isDirectory()){
			if(!parent.mkdirs()){
				return false;
			}
		}
		
		File file = new File(parentPath + File.separator + filename);
		if(!file.exists()){
			boolean result = file.createNewFile();
			if( !result ) return result;	
		}
		
		FileOutputStream fOs = new FileOutputStream(file, writeAdd);
		fOs.write(bytes);
		fOs.flush();
		fOs.close();
		return true;
	}
}
