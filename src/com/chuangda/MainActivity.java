package com.chuangda;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

	public final static int MSG_CHANGE_FRAGMENT = 9000;
	public final static int MSG_POP_FRAGMENT = 9001;
	
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
		default:
			break;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.main_frame, newFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
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
			default:
				mCurBaseFragment.handleUI(msg);
			}
		};
	};

}
