package com.chuangda;

import android.R.color;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.chuangda.common.FData;

public class ChangePasswordFragment extends BaseFragment {

	
	private LinearLayout[] mButtons = new LinearLayout[2];
	
	public ChangePasswordFragment() {
	}
	
    static ChangePasswordFragment newInstance() {
    	ChangePasswordFragment f = new ChangePasswordFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.change_passwd, container, false);
    	mButtons[0] = (LinearLayout) v.findViewById(R.id.change_passwd_yes);
    	mButtons[1] = (LinearLayout) v.findViewById(R.id.change_passwd_no);
    	for(int i=0; i<mButtons.length; i++){
    		mButtons[i].setTag(i);
    		mButtons[i].setOnClickListener(mOnClickListener);
    	}
        return v;
    }

    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	setBtnSelected(1);
    }
    
    private void updatePasswd(){
    	boolean ret = false;
    	ret = MainActivity.modifyPW();
    	String str = ret ? "修改成功" : "修改失败";
    	Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_SHOW_TOAST,str);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
		
		msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_POP_FRAGMENT);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
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
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			setBtnSelected(position);
			clickButton();
		}
	};
	
	int mCurSelected = 0;
	public void setBtnSelected(int position){
		mCurSelected = 0;
		for(int i=0; i < mButtons.length; i++){
			if(i == position){
				mButtons[i].setBackgroundColor(Color.RED);
				mCurSelected = position;
			}else{
				mButtons[i].setBackgroundColor(Color.WHITE);
			}
		}
	}
	
	private void clickButton(){
		if(mCurSelected == 1){
			goBack();
		}
		
		if(mCurSelected == 0){
			updatePasswd();
		}
	}
	
	private void goBack(){
		MainActivity.gHandle(MainActivity.MSG_POP_FRAGMENT);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				setBtnSelected(0);
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				setBtnSelected(1);
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
				clickButton();
			}
			if(FData.KEYCODE_WATER_START == event.getKeyCode()){
				goBack();
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
