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


public class ModifyFlowFragment extends BaseFragment {

	private TextView mFlowData = null;
	
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
    	
        return v;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	mFlowData.setText(String.valueOf(FData.getFlowData()));
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
		if(KeyEvent.ACTION_DOWN == event.getAction()){
			int data = FData.getFlowData();
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				FData.setFlowData(++data);
				mFlowData.setText(String.valueOf(data));
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				FData.setFlowData(--data);
				mFlowData.setText(String.valueOf(data));
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
			}
		}
		return false;
	}
}
