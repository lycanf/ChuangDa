package com.chuangda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuangda.MainActivity.ViewFragment;
import com.chuangda.widgets.WaterHealthBar;

public class UserViewFragment extends BaseFragment {

	public final static int MSG_UPDATE_HEALTH_BAR = 1000;

	public final static String TEXT_COST = "水费:";
	public final static String TEXT_WATER = "水量 :";
	public final static String TEXT_3L = "3L";
	public final static String TEXT_5L = "5L";
	public final static String TEXT_CHARGE = "充";
	
	WaterHealthBar mWaterHealthBar;
	TextView mViewCost = null;
	TextView mViewWater = null;
	TextView mView3L = null;
	TextView mView5L = null;
	TextView mViewCharge= null;

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
		mViewCost = (TextView) v.findViewById(R.id.user_cost);
		mViewWater = (TextView) v.findViewById(R.id.user_water);
		mView3L = (TextView) v.findViewById(R.id.user_3l);
		mView5L = (TextView) v.findViewById(R.id.user_5l);
		mViewCharge = (TextView) v.findViewById(R.id.user_charge);

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
		
		mViewCost.setText(TEXT_COST);
		mViewWater.setText(TEXT_WATER);
		mView3L.setText(TEXT_3L);
		mView5L.setText(TEXT_5L);
		mViewCharge.setText(TEXT_CHARGE);
		
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

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
