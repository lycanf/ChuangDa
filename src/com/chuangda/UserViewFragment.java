package com.chuangda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.chuangda.MainActivity.ViewFragment;
import com.chuangda.widgets.WaterHealthBar;

public class UserViewFragment extends BaseFragment {

	public final static int MSG_UPDATE_HEALTH_BAR = 1000;

	WaterHealthBar mWaterHealthBar;

	public UserViewFragment() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static UserViewFragment newInstance() {
		UserViewFragment f = new UserViewFragment();
		// Bundle args = new Bundle();
		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.user_view, container, false);
		mWaterHealthBar = (WaterHealthBar) v.findViewById(R.id.main_health_bar);

		// test
		mWaterHealthBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showManagerDialog();
				/*Message msg = MainActivity.gUIHandler.obtainMessage(
						MainActivity.MSG_CHANGE_FRAGMENT,ViewFragment.SETTING);
				MainActivity.gUIHandler.sendMessageDelayed(msg, 10);*/
			}
		});
		return v;
	}
	
	private void showManagerDialog(){
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.smg_enter_system);
		builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Message msg = MainActivity.gUIHandler.obtainMessage(
						MainActivity.MSG_CHANGE_FRAGMENT,ViewFragment.SETTING);
				MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
			}
		});
		builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MSG_UPDATE_HEALTH_BAR, Color.RED, 1);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 100);
	}

	@Override
	public void handleUI(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MSG_UPDATE_HEALTH_BAR:
			int width = mWaterHealthBar.getWidth() / 3 * msg.arg2;
			mWaterHealthBar.setBar(width, msg.arg1);
			break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
