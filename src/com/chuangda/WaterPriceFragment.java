package com.chuangda;

import java.text.DecimalFormat;

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
    	
    	Double data = FData.getWaterPrice();
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

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_DOWN == event.getAction()){
			Double data = FData.getWaterPrice();
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				data = data + 0.01;
				FData.setWaterPrice(data);
				mWaterPrice.setText(new DecimalFormat("##.00").format(data));
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				data = data - 0.01;
				FData.setWaterPrice(data);
				mWaterPrice.setText(new DecimalFormat("##.00").format(data));
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
			}
		}
		return false;
	}
}
