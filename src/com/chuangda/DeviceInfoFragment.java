package com.chuangda;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuangda.common.FData;




public class DeviceInfoFragment extends BaseFragment {

	private TextView mDeviceInfo = null;
	
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

    @Override
    public void onResume() {
    	super.onResume();
    	
    	mDeviceInfo.setText(FData.getDeviceInfo());
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_WATER_START == event.getKeyCode()){
				Message msg = MainActivity.gUIHandler.obtainMessage(
						MainActivity.MSG_POP_FRAGMENT);
				MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
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
