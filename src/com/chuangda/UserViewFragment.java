package com.chuangda;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chuangda.MainActivity.ViewFragment;
import com.chuangda.common.FData;
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
	ITEM_BTN mButtons[] = {
		new ITEM_BTN(0, R.id.user_3l, TEXT_3L),
		new ITEM_BTN(1, R.id.user_5l, TEXT_5L),
		new ITEM_BTN(2, R.id.user_charge, TEXT_CHARGE),
	};
	private int mCurSelected = 0;
	
	View mMainLayout = null;
	

	class ITEM_BTN{
		public int id;
		public int num;
		public Button btn;
		public String text;
		public boolean isSelected = false;
		public ITEM_BTN(int n, int i, String t){
			num = n;
			id = i;
			text = t;
		}
	}
	
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
		View mMainLayout = inflater.inflate(R.layout.user_view, container, false);
		mWaterHealthBar = (WaterHealthBar) mMainLayout.findViewById(R.id.main_health_bar);
		mViewCost = (TextView) mMainLayout.findViewById(R.id.user_cost);
		mViewWater = (TextView) mMainLayout.findViewById(R.id.user_water);
		
		for(int i=0; i < mButtons.length; i++){
			mButtons[i].btn = (Button) mMainLayout.findViewById(mButtons[i].id);
			mButtons[i].btn.setText(mButtons[i].text);
		}
		return mMainLayout;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mViewCost.setText(TEXT_COST);
		mViewWater.setText(TEXT_WATER);
		
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MSG_UPDATE_HEALTH_BAR, Color.RED, 1);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 100);
		
		setBtnSelected(0);
	}
	
	public void setBtnSelected(int position){
		mCurSelected = 0;
		for(int i=0; i < mButtons.length; i++){
			if(i == position){
				mButtons[i].btn.setBackgroundColor(FData.COLOR_FOCUSED);
				mButtons[i].isSelected = true;
				mCurSelected = position;
			}else{
				mButtons[i].btn.setBackgroundColor(FData.COLOR_UNFOCUSED);
				mButtons[i].isSelected = false;
			}
		}
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

	private void focusNext(boolean goToNext){
		if(mCurSelected < (mButtons.length-1) && goToNext){
			setBtnSelected(mCurSelected + 1);
		}else if(mCurSelected > 0 && !goToNext){
			setBtnSelected(mCurSelected - 1);
		}else{
			setBtnSelected(0);
		}
	}
	
	private void clickButton(){
		if(mCurSelected == 0){
			test();
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(FData.KEYCODE_BACK == event.getKeyCode()){
			return true;
		}
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				focusNext(false);
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				focusNext(true);
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
				clickButton();
			}
		}

		return false;
	}
	
	
	private void test(){
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_CHANGE_FRAGMENT,ViewFragment.SETTING);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
	}
}
