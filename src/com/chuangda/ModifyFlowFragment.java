package com.chuangda;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.chuangda.common.FCmd;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;


public class ModifyFlowFragment extends BaseFragment {

	private TextView mFlowData = null;
	private int mFlowValue ;
	
	public ModifyFlowFragment() {
	}
	
    static ModifyFlowFragment newInstance() {
    	ModifyFlowFragment f = new ModifyFlowFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.modify_flow, container, false);
    	mFlowData = (TextView) v.findViewById(R.id.flow_data);
    	AssetManager assets = getActivity().getAssets();
		final Typeface font = Typeface.createFromAsset(assets, "fonts/digital-7.ttf");
		mFlowData.setTypeface(font);
		mFlowData.setTextColor(Color.rgb(234, 191, 25));
        return v;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	mFlowValue = getData();
    	mFlowData.setText(String.valueOf(mFlowValue));
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what == MainActivity.MSG_MODIFY_FLOW){
			mFlowData.setText(String.valueOf(msg.arg1));
		}
	}

	public void setData(int data){
		FData.setFlowData(data);
//		FCmd.readAll();
	}
	
	public int getData(){
		int ret = 0;
		ret = FData.getFlowData();
		return ret;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(KeyEvent.ACTION_DOWN == event.getAction()){
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				++mFlowValue;
				mFlowData.setText(String.valueOf(mFlowValue));
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				--mFlowValue;
				mFlowData.setText(String.valueOf(mFlowValue));
			}
		}
		
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
				MainActivity.gHandle(MainActivity.MSG_POP_FRAGMENT);
			}
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				setData(mFlowValue);
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				setData(mFlowValue);
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
