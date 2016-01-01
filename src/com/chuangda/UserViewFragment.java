package com.chuangda;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.chuangda.common.DataCal;
import com.chuangda.common.FCmd;
import com.chuangda.common.FConst;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.common.HandlePortData;
import com.chuangda.common.WaterMgr;
import com.chuangda.widgets.MODBUS_ITEM;


public class UserViewFragment extends BaseFragment {

	public final static int MSG_UPDATE_HEALTH_BAR = 1000;

	public final static String TEXT_DEFAULT = "888888888";
	public final static String TEXT_COST = "0.0";
	public final static String TEXT_WATER =  "0.0";
	
//	WaterHealthBar mWaterHealthBar;
	TextView mViewCost = null;
	TextView mViewWater = null;
	ITEM_BTN mButtons[] = {
		new ITEM_BTN(R.id.user_3l, R.drawable.water0, true),
		new ITEM_BTN(R.id.user_5l, R.drawable.water1, true),
		new ITEM_BTN(R.id.user_charge, R.drawable.pay, true),
		new ITEM_BTN(R.id.user_good0, R.drawable.good0, false),
		new ITEM_BTN(R.id.user_good1, R.drawable.good1, false),
		new ITEM_BTN(R.id.user_good2, R.drawable.good2, false),
	};
	private int mCurSelected = 0;
	private int mCurTDS = 0;
	
	View mMainLayout = null;
	Animation mAminAlpha = null;
	

	class ITEM_BTN{
		public int id;
		public int res;
		public Button btn;
		public boolean isSelected = false;
		public boolean canAnim = false;
		public ITEM_BTN(int i, int r, boolean anim){
			id = i; res = r; canAnim = anim;
		}
		public void setSelected(boolean s){
			isSelected = s;
			if(null != btn){
//				btn.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
				btn.setAlpha(s ? 1 : 0);
				if(canAnim){/*
					if(s){
						btn.startAnimation(mAminAlpha);
					}else{
						btn.clearAnimation();
					}
				*/}
			}
		}
	}
	
	public UserViewFragment() {
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
//		mWaterHealthBar = (WaterHealthBar) mMainLayout.findViewById(R.id.main_health_bar);
		mViewCost = (TextView) mMainLayout.findViewById(R.id.user_cost);
		mViewWater = (TextView) mMainLayout.findViewById(R.id.user_water);
		
		AssetManager assets = getActivity().getAssets();
		final Typeface font = Typeface.createFromAsset(assets, "fonts/digital-7.ttf");
		
		mViewCost.setTypeface(font);
		mViewCost.setText(TEXT_COST);
//		mViewCost.setTextColor(Color.rgb(131, 98, 192));
		mViewCost.setTextColor(Color.rgb(255, 210, 0));
		
		mViewWater.setTypeface(font);
		mViewWater.setText(TEXT_WATER);
//		mViewWater.setTextColor(Color.rgb(55, 206, 185));
		mViewWater.setTextColor(Color.rgb(255, 210, 0));
		
/*		mViewCost.setTextColor(Color.rgb(131, 98, 192));
		mViewWater.setTextColor(Color.rgb(55, 206, 185));*/
		
		for(int i=0; i < mButtons.length; i++){
			mButtons[i].btn = (Button) mMainLayout.findViewById(mButtons[i].id);
			mButtons[i].setSelected(false);
			mButtons[i].btn.setTag(i);
			mButtons[i].btn.setOnClickListener(mOnClickListener);
		}
		setBtnSelected(4);
		mAminAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.alphaout);
		return mMainLayout;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		resetView();
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MSG_UPDATE_HEALTH_BAR, Color.RED, FData.WATER_QUALITY);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 100);
		
	}
	
	public static int LINE_LEN_WATER = 3;
	public void setBtnSelected(int position){
		if(position < 0){
			mCurSelected = -1;
			for(int i=0; i < LINE_LEN_WATER; i++){
				mButtons[i].setSelected(false);
			}
		}
		if(position == mCurSelected || position == mCurTDS){
			FLog.e("return setBtnSelected "+mCurSelected+" mCurTDS="+mCurTDS);
			return;
		}
		if(position < 0 || position >= mButtons.length){
			FLog.e("return setBtnSelected position="+position);
			return;
		}
		//select water
		if(position < LINE_LEN_WATER){
			mCurSelected = position;
			for(int i=0; i < LINE_LEN_WATER; i++){
				if(i == position){
					mButtons[i].setSelected(true);
				}else{
					mButtons[i].setSelected(false);
				}
			}
		}else{
			mCurTDS = position;
			for(int i=LINE_LEN_WATER; i < mButtons.length; i++){
				if(i == position){
					mButtons[i].setSelected(true);
				}else{
					mButtons[i].setSelected(false);
				}
			}
		}
	}

	@Override
	public void handleUI(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MSG_UPDATE_HEALTH_BAR:
			break;
		case MainActivity.MSG_CARD_ON:
			onCardOn();
			break;
		case MainActivity.MSG_SHOW_WATER_PRICE:
			float[] value = (float[]) msg.obj;
			String cost = String.format("%.2f", value[0]);
			String flow = String.format("%.2f", value[1]);
			FLog.v("cost="+cost+" flow="+flow);
			mViewCost.setText(cost);
			mViewWater.setText(flow);
/*			mViewCost.setText(String.valueOf(value[0]));
			mViewWater.setText(String.valueOf(value[1]));*/
			break;
		case MainActivity.MSG_SHOW_TDS:
			handleTDS();
			break;
		}
	}
	
	private void handleTDS(){
		int tds = MODBUS_ITEM.TDS_OUT;
		int type = 3;
		if(0 < tds && tds <= 120){
			type = 3;
		}
		if(120 < tds && tds <= 360){
			type = 4;
		}
		if(360 < tds && tds <= 500){
			type = 5;
		}
		setBtnSelected(type);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void focusNext(boolean goToNext){
//		FLog.t("cur="+mCurSelected+" "+goToNext);
		if(mCurSelected >= 0 && mCurSelected < (LINE_LEN_WATER-1) && goToNext){
			setBtnSelected(mCurSelected + 1);
		}else if(mCurSelected > 0 && !goToNext){
			setBtnSelected(mCurSelected - 1);
		}else{
			setBtnSelected(0);
		}
	}
	
	int testNum = 0;
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			FLog.v("onclick "+position);
			if(MainActivity.isCardOn()){
				setBtnSelected(position);
				clickButton(position);
			}
		}
	};
	private void clickButton(int position){
		FLog.v("clickButton "+position);
		if(mCurSelected == 0){
			WaterMgr.start3L();
		}else if(mCurSelected == 1){
			WaterMgr.start5L();
		}else if(mCurSelected == 2){
		}
	}
	
	long mKeyInteral = 0;
	private boolean btnCanPress(){
		boolean ret = System.currentTimeMillis() - mKeyInteral > 512;
		
		ret = MainActivity.isCardOn();
		return ret;
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

		if(KeyEvent.ACTION_UP == event.getAction()){
			FLog.v("user key up "+event.getKeyCode());
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				focusNext(false);
//				FCmd.test();
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				focusNext(true);
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
				if(btnCanPress()){
					clickButton(mCurSelected);
				}
			}
			
			if(FData.KEYCODE_WATER_START == event.getKeyCode()){
				if(btnCanPress() && !HandlePortData.WATER_ON){
//					WaterMgr.start();
					WaterMgr.WATER_STATE = WaterMgr.WATER_STATE_ON;
				}
			}
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
				WaterMgr.WATER_STATE = WaterMgr.WATER_STATE_OFF;
				if(!MainActivity.mCardOn){
					WaterMgr.stop();
				}
			}
		}

		return false;
	}
	
	@Override
	public void resetView() {
		mViewCost.setText(TEXT_COST);
		mViewWater.setText(TEXT_WATER);
		setBtnSelected(-1);
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
