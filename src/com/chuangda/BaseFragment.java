package com.chuangda;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.chuangda.common.FLog;
public abstract class BaseFragment extends Fragment {

	public BaseFragment() {
		// TODO Auto-generated constructor stub
	}
	

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FLog.v("onCreate "+getClass().getName());
    }


    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	FLog.v("ondestroy "+getClass().getName());
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	FLog.v("onResume "+getClass().getName());
    	super.onResume();
    	MainActivity.mCurBaseFragment = this;
    }
    abstract public void handleUI(Message msg);
    abstract public boolean dispatchKeyEvent(KeyEvent event);
}
