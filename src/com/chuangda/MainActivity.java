package com.chuangda;

import com.chuangda.common.FLog;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static int MSG_CHANGE_FRAGMENT = 9000;
	public final static int MSG_POP_FRAGMENT = 9001;
	public final static int MSG_SHOW_TOAST = 9002;
	
	public static Handler gUIHandler = null;
	static BaseFragment mCurBaseFragment = null;
	
	//common 
	public static int COMMON_HEALTH_STATE = 1;

	enum ViewFragment {
		USER, SETTING,CHANGE_PASSWD,CALIBRATE_FLOW,WATER_PRICE,DEVICE_INFO
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gUIHandler = mUIHandler;

		if (savedInstanceState == null) {
			// Do first time initialization -- add initial fragment.
			UserViewFragment mUserFragment = UserViewFragment.newInstance();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.main_frame, mUserFragment).commit();
//			translateFragment(ViewFragment.USER);
		} else {

		}
	}

	private void translateFragment(ViewFragment type) {
		BaseFragment newFragment = null;
		// Add the fragment to the activity, pushing this transaction
		// on to the back stack.
		switch (type) {
		case USER:
			newFragment = UserViewFragment.newInstance();
			break;
		case SETTING:
			newFragment = SettingViewFragment.newInstance();
			break;
		case CHANGE_PASSWD:
			newFragment = ChangePasswordFragment.newInstance();
			break;
		case CALIBRATE_FLOW:
			newFragment = ModifyFlowFragment.newInstance();
			break;
		case WATER_PRICE:
			newFragment = WaterPriceFragment.newInstance();
			break;
		case DEVICE_INFO:
			newFragment = DeviceInfoFragment.newInstance();
			break;
		default:
			break;
		}
		if(null != newFragment){
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.main_frame, newFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.addToBackStack(null);
			ft.commit();
		}

	}

	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		FLog.v("dispatchKeyEvent="+event.getAction());
		return super.dispatchKeyEvent(event);
	}



	Handler mUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_CHANGE_FRAGMENT:
				translateFragment((ViewFragment) msg.obj);
				break;
			case MSG_POP_FRAGMENT:
				getFragmentManager().popBackStack();
				break;
			case MSG_SHOW_TOAST:
				String str = (String) msg.obj;
				Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
				break;
			default:
				mCurBaseFragment.handleUI(msg);
			}
		};
	};

}
