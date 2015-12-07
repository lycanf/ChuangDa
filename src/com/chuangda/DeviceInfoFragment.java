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
		return false;
	}
}
