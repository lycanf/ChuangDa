package com.chuangda;

import java.text.DecimalFormat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuangda.common.FData;


public class WaterPriceFragment extends BaseFragment {

	private TextView mWaterPrice = null;
	
	public WaterPriceFragment() {
	}
	
    static WaterPriceFragment newInstance() {
    	WaterPriceFragment f = new WaterPriceFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.water_price, container, false);
    	mWaterPrice = (TextView) v.findViewById(R.id.text_water_price);
        return v;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	
    	float data = getData();
    	mWaterPrice.setText(new DecimalFormat("##.00").format(data));
    	
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
	
	public void setData(float data){
		FData.setWaterPrice(data);
		mWaterPrice.setText(new DecimalFormat("##.00").format(data));
	}
	
	public float getData(){
		float ret = 0;
		ret = FData.getWaterPrice();
		return ret;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_DOWN == event.getAction()){
			float data = getData();
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				data = data + 0.01f;
				setData(data);
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				data = data - 0.01f;
				setData(data);
			}
			
		}
		
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
