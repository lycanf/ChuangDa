package com.chuangda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chuangda.common.FLog;

public class MyReceiver extends BroadcastReceiver {

	static final String action_boot="android.intent.action.BOOT_COMPLETED";
	
	public MyReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		FLog.v("MyReceiver onReceive "+arg1.getAction());
		
		if (arg1.getAction().equals(action_boot)){
            Intent ootStartIntent=new Intent(arg0,MainActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            arg0.startActivity(ootStartIntent);
        }
	}

}
