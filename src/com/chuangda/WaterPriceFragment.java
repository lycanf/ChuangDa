package com.chuangda;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chuangda.common.FData;
import com.chuangda.common.FLog;

public class WaterPriceFragment extends BaseFragment {

	private TextView mWaterPrice = null;
	private Button   mPriceUp = null;
	private Button   mPriceDown = null;
	
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
    	mPriceUp   = (Button) v.findViewById(R.id.water_price_up);
    	mPriceDown = (Button) v.findViewById(R.id.water_price_down);
        return v;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	
    	mWaterPrice.setText(String.valueOf(FData.water_price));
    	/*mFlowUp.setOnClickListener(mOnClickListener);
    	mFlowDown.setOnClickListener(mOnClickListener);
    	
    	mFlowUp.setOnLongClickListener(mOnLongClickListener);
    	mFlowDown.setOnLongClickListener(mOnLongClickListener);*/
    	
    	mPriceUp.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				FLog.v("touch "+event.getAction());
				return false;
			}
		});
    	
    }
    
    OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int count = 0;
			count = v.getId() == R.id.flow_up ? 1 : -1;
			FData.flow_data += count;
			mWaterPrice.setText(String.valueOf(FData.flow_data));
		}
	};
    
	OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			int count = 0;
			count = v.getId() == R.id.flow_up ? 10 : -10;
			FData.flow_data += count;
			mWaterPrice.setText(String.valueOf(FData.flow_data));
			return true;
		}
	};
	
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
