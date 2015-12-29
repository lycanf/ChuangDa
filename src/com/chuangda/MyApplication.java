/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.chuangda;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.InvalidParameterException;

import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

import com.chuangda.common.FLog;

public class MyApplication extends android.app.Application implements UncaughtExceptionHandler{

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;
	
	protected static MyApplication instance; 
	String mCrashMsg ;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		FLog.v("MyApplication onCreate");
		instance = this;
//		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Open the serial port */
			String path = "/dev/ttymxc2";
			File device = new File(path);
			if(!device.exists() || !device.canRead()){
				FLog.v("getSerialPort fail !");
				return null;
			}
			int baudrate = 9600;
			mSerialPort = new SerialPort(device, baudrate, 0);
		}
		return mSerialPort;
	}
	
    public void restartApp(){  
        Intent intent = new Intent(instance,MainActivity.class);  
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.startActivity(intent);  
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		FLog.v( "uncaughtException, thread: " + thread
                + " name: " + thread.getName() + " id: " + thread.getId() + "exception: "
                + ex);
        mCrashMsg = ex.toString();
        FLog.v("getMessage="+mCrashMsg);
        new Thread() {      
            @Override      
            public void run() {      
                Looper.prepare();      
                Toast.makeText(instance, "Oh ! It carshed ! \r\n"+mCrashMsg, Toast.LENGTH_SHORT).show();      
                Looper.loop();      
            }      
        }.start();  
        
        if(mCrashMsg.contains("NullPointerException")){
        	FLog.v("NullPointerException restart");
        	restartApp();
        }
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
