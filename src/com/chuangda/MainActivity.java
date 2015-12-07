package com.chuangda;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangda.common.FLog;
import com.chuangda.widgets.VideoPlay;

interface UICallBack{
	void sendMsg(Message msg);
}

public class MainActivity extends Activity implements UICallBack{

	public final static int MSG_CHANGE_FRAGMENT = 9000;
	public final static int MSG_POP_FRAGMENT = 9001;
	public final static int MSG_SHOW_TOAST = 9002;
	public final static int MSG_PLAY_NEXT = 9003;
	public final static int MSG_VIDEO_COUNT = 9004;
	
	public final static String TEXT_NO_VIDEO = "Ã»ÓÐÊÓÆµ";
	public static Handler gUIHandler = null;
	static BaseFragment mCurBaseFragment = null;
	
	TextView mVideoText ;
	VideoPlay mVideoPlay;
	
	WakeLock wakeLock;
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
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mVideoText = (TextView) findViewById(R.id.main_video_text);
		mVideoText.setText(TEXT_NO_VIDEO);
		mVideoPlay = (VideoPlay) findViewById(R.id.main_video_play);
		mUIHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT, 10);
		
		
		UserViewFragment mUserFragment = UserViewFragment.newInstance();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.main_frame, mUserFragment).commit();
		
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
		FLog.v("dispatchKeyEvent action="+event.getAction()+" code="+event.getKeyCode());
		if(mCurBaseFragment.dispatchKeyEvent(event)){
			return true;
		}
		return super.dispatchKeyEvent(event);
	}



	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			FLog.v("mUIHandler msg "+msg.what);
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
			case MSG_PLAY_NEXT:
//				mVideoPlay.playNext();
				mVideoPlay.playTest();
				break;
			case MSG_VIDEO_COUNT:
				mVideoText.setVisibility(msg.arg1 > 0 ? View.INVISIBLE : View.VISIBLE);
				break;
			default:
				mCurBaseFragment.handleUI(msg);
			}
		};
	};



	@Override
	public void sendMsg(Message msg) {
		// TODO Auto-generated method stub
		mUIHandler.sendMessageDelayed(msg, 10);
	}

}
