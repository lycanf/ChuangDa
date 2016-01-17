package com.chuangda;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuangda.common.FCmd;
import com.chuangda.common.FData;




public class DeviceInfoFragment extends BaseFragment {

	public static final int MSG_INFO = 4000;
	
	private TextView mDeviceInfo = null;
	boolean isVisible = true;
	
	public DeviceInfoFragment() {
	}
	
    static DeviceInfoFragment newInstance() {
    	DeviceInfoFragment f = new DeviceInfoFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.setting_device_info, container, false);
    	mDeviceInfo = (TextView) v.findViewById(R.id.text_device_info);
        return v;
    }
    

    class updateInfo extends Thread{
    	@Override
    	public void run() {
    		while(isVisible()){
    			MainActivity.gHandle(MSG_INFO);
    			FCmd.readAll();
    			Thread.currentThread();
    			try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	isVisible = false;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	isVisible = true;
    	mDeviceInfo.setText(FData.getDeviceInfo());
    	new updateInfo().start();
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		if(msg.what == MSG_INFO){
			mDeviceInfo.setText(FData.getDeviceInfo());
		}
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
				MainActivity.gHandle(MainActivity.MSG_POP_FRAGMENT);
			}
		}
		return false;
	}

	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCardOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCardOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startWater() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopWater() {
		// TODO Auto-generated method stub
		
	}
}
