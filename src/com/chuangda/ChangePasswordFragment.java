package com.chuangda;

import com.chuangda.MainActivity.ViewFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChangePasswordFragment extends BaseFragment {

	
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
        return v;
    }

    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	
    	AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.msg_update_password);
		builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updatePasswd();
			}
		});
		builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Message msg = MainActivity.gUIHandler.obtainMessage(
						MainActivity.MSG_POP_FRAGMENT);
				MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
				
				Message msg1 = MainActivity.gUIHandler.obtainMessage(
						MainActivity.MSG_SHOW_TOAST,"pop fragment");
				MainActivity.gUIHandler.sendMessageDelayed(msg1, 10);
			}
		});
		builder.create().show();
    }
    
    private void updatePasswd(){
    	
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
