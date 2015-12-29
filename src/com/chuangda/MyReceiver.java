package com.chuangda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chuangda.common.FLog;

public class MyReceiver extends BroadcastReceiver {

	public MyReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		FLog.v("MyReceiver onReceive");
	}

}
