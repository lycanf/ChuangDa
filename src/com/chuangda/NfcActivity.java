package com.chuangda;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class NfcActivity extends Activity{
	private Tag tag;
	MifareClassic mc;

	byte keyA[] = { (byte) 0x88, (byte) 0x88, (byte) 0x88, (byte) 0x88,
			(byte) 0x88, (byte) 0x88 };

	 String keyBstr = null;
	 byte keyB[]=null;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		
		TextView text = new TextView(this);
		text.setText("xxx");
		setContentView(text);
		
		Log.v("fq","NfcActivity onCreate");
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); 
		Log.v("fq","NfcActivity onNewIntent "+tag);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
	


