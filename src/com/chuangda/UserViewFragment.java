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

import com.chuangda.MainActivity.ViewFragment;
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
	
	final int color_water_normal = Color.rgb(255, 210, 0);
	final int color_water_selected = Color.BLUE;
	
	final int BTN_NOW = 0;
	final int BTN_5L = 1;
	final int BTN_3L = 2;
	final int BTN_PAY = 3;
	
//	WaterHealthBar mWaterHealthBar;
	TextView mViewCost = null;
	TextView mViewWater = null;
	ITEM_BTN mBtnWter[] = {
		new ITEM_BTN(R.id.user_water, 0, BTN_NOW),
		new ITEM_BTN(R.id.user_5l, R.drawable.water1,BTN_5L),
		new ITEM_BTN(R.id.user_3l, R.drawable.water0, BTN_3L ),
		new ITEM_BTN(R.id.user_charge, R.drawable.pay, BTN_PAY),

	};
	ITEM_BTN mBtnQuality[] = {
		new ITEM_BTN(R.id.user_good0, R.drawable.good0, 0),
		new ITEM_BTN(R.id.user_good1, R.drawable.good1, 1),
		new ITEM_BTN(R.id.user_good2, R.drawable.good2, 2),
	};
	
	private int mCurSelected = 0;
	private int mCurMode = 0;
	
	View mMainLayout = null;
	Animation mAminAlpha = null;
	

	class ITEM_BTN{
		public int id;
		public int res;
		public int position;
		public Button btn;
		public TextView text;
		public boolean isSelected = false;
		public boolean canAnim = false;
		public ITEM_BTN(int i, int r, int p){
			id = i; res = r; position = p;
		}
		public void setSelected(boolean s){
			isSelected = s;
			if(null != text){
//				text.setTextColor(s ? color_water_selected : color_water_normal);
			}else if(null != btn){
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
		
		mBtnWter[BTN_NOW].text = mViewWater;
		mBtnWter[BTN_NOW].text.setTag(BTN_NOW);
		for(int i=BTN_5L; i < mBtnWter.length; i++){
			mBtnWter[i].btn = (Button) mMainLayout.findViewById(mBtnWter[i].id);
			mBtnWter[i].setSelected(false);
			mBtnWter[i].btn.setTag(i);
			mBtnWter[i].btn.setOnClickListener(mOnClickListener);
		}
		for(int i=0; i < mBtnQuality.length; i++){
			mBtnQuality[i].btn = (Button) mMainLayout.findViewById(mBtnQuality[i].id);
			mBtnQuality[i].setSelected(false);
			mBtnQuality[i].btn.setTag(i);
			mBtnQuality[i].btn.setOnClickListener(mOnClickListener);
		}
		
		mAminAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.alphaout);
		return mMainLayout;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(MainActivity.isCardOn()){
			MainActivity.setCostMoney();
		}
		setBtnSelected(BTN_NOW);
//		MainActivity.gHandle(MSG_UPDATE_HEALTH_BAR,Color.RED, FData.WATER_QUALITY, null);
	}
	
	public void setBtnSelected(int position){
		for(int i=0; i<mBtnWter.length; i++){
			if(i == position){
				mBtnWter[i].setSelected(true);
				mCurSelected = i;
			}else{
				mBtnWter[i].setSelected(false);
			}
		}
	}
	public void setQuality(int position){
		for(int i=0; i<mBtnQuality.length; i++){
			if(i == position){
				mBtnQuality[i].setSelected(true);
			}else{
				mBtnQuality[i].setSelected(false);
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
		case MainActivity.MSG_SHOW_WATER_VOLUME:
			float valueVolume = (Float) msg.obj;
			String costVolume = String.format("%.2f", valueVolume);
			mViewWater.setText(costVolume);
			break;
		case MainActivity.MSG_SHOW_TDS:
			handleTDS();
			break;
		case MainActivity.MSG_FORCE_STOP:
			initView();
			break;
		case MainActivity.MSG_INIT_VIEW:
			initView();
			break;
		}
	}
	
	private void handleTDS(){
		int tds = MODBUS_ITEM.TDS_OUT;
		int type = 0;
		if(0 < tds && tds <= 120){
			type = 0;
		}
		if(120 < tds && tds <= 360){
			type = 1;
		}
		if(360 < tds && tds <= 500){
			type = 2;
		}
		setQuality(type);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void focusNext(boolean goToNext){
		int next = 0;
		if(goToNext){
			next = (getCurSelected() + 1)%mBtnWter.length;
			if(HandlePortData.isWaterOn() && next == BTN_PAY){
				next = BTN_NOW;
			}
		}else{
			next = getCurSelected()-1;
			if(next < 0){
				next = BTN_PAY;
			}
			if(HandlePortData.isWaterOn() && next == BTN_PAY){
				next = BTN_3L;
			}
		}
		setBtnSelected(next);
		mViewWater.setTextColor(next==BTN_NOW ? color_water_selected : color_water_normal);
	}
	
	int testNum = 0;
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			FLog.v("onclick "+position);
			if(MainActivity.isCardOn()){
				setBtnSelected(position);
			}
		}
	};
	
	private int getFlowAgain(){
		int ret = -1;
		int curFlow = HandlePortData.getCurFlow();
		if(getCurSelected() == BTN_3L){
			if(WaterMgr.getWaterLevel() == WaterMgr.WATER_LEVEL_3){
				if(HandlePortData.isWaterOn()){
					return ret;
				}else{
					ret = curFlow + WaterMgr.getWaterLeft();
					return ret;
				}
			}else if(WaterMgr.getWaterLevel() == WaterMgr.WATER_LEVEL_5
					|| WaterMgr.getWaterLevel() == WaterMgr.WATER_LEVEL_0){
				ret = curFlow + WaterMgr.WATER_LEVEL_3;
				return ret;
			}
		}else if(getCurSelected() == BTN_5L){
			if(WaterMgr.getWaterLevel() == WaterMgr.WATER_LEVEL_5){
				if(HandlePortData.isWaterOn()){
					return ret;
				}else{
					ret = curFlow + WaterMgr.getWaterLeft();
					return ret;
				}
			}else if(WaterMgr.getWaterLevel() == WaterMgr.WATER_LEVEL_3
					|| WaterMgr.getWaterLevel() == WaterMgr.WATER_LEVEL_0){
				ret = curFlow + WaterMgr.WATER_LEVEL_5;
				return ret;
			}
		}else{
			
		}
		return ret;
	}
	
	private void beginSetWater(){
		FLog.v("beginWater "+getCurSelected());
		int flowTotal = 0;
		float moneyLeft = 0;
		float moneyCur = 0;
		String costStr = "";
		boolean isMoneyEnough = false;
		flowTotal = getFlowAgain();
		FLog.v("beginWater flowTotal="+flowTotal);
		if(flowTotal<0){
			return;
		}
		moneyLeft = MainActivity.getMoneyLeft(flowTotal);
		isMoneyEnough = moneyLeft > 0;
		FLog.v("clickButton moneyCur="+moneyCur+" moneyCost="+moneyLeft);
		
		if(isMoneyEnough){
			if(MainActivity.setMoney(moneyLeft)){
				if(getCurSelected() == BTN_3L){
					WaterMgr.start3L();
				}else if(getCurSelected() == BTN_5L){
					WaterMgr.start5L();
				}
				costStr = String.format("%.2f", moneyLeft);
				mViewCost.setText(costStr);
			}else{
				MainActivity.forceStop("begin water fail");
			}
		}else{
			MainActivity.gUIHandler.obtainMessage(
					MainActivity.MSG_SHOW_TOAST,"余额不足").sendToTarget();
		}
	}
	
	long mKeyInteral = 0;
	private boolean btnCanPress(){
		long time = System.currentTimeMillis() - mKeyInteral;
		boolean ret = time > 600;
		ret = ret && MainActivity.isCardOn();
//		FLog.v("btnCanPress "+time+" ret="+ret);
		if(ret){
			mKeyInteral = System.currentTimeMillis();
		}else{
			MainActivity.showToast("太快了，会把我按爆的-_-");
		}
		return ret;
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

		if(KeyEvent.ACTION_UP == event.getAction() ){
			FLog.v("user key up "+event.getKeyCode());
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				focusNext(false);
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				focusNext(true);
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode() && btnCanPress()){
				if(getCurSelected() == BTN_PAY){
					if(HandlePortData.isWaterOn()){
						MainActivity.gUIHandler.obtainMessage(
								MainActivity.MSG_SHOW_TOAST,"请停水后，进行充值").sendToTarget();
					}else{
						MainActivity.gUIHandler.obtainMessage(MainActivity.MSG_CHANGE_FRAGMENT,
								ViewFragment.PAY).sendToTarget();
					}
				}
			}
			
			if(FData.KEYCODE_WATER_START == event.getKeyCode() && btnCanPress()){
				if(HandlePortData.isWaterOn()){
					if(getCurSelected() == BTN_3L || getCurSelected() == BTN_5L){
						beginSetWater();
					}else{
						payBackMoney();
					}
				}else{
					if(getCurMode() == getCurSelected()){
						
					}else{
						mCurMode = getCurSelected();
						WaterMgr.init();
					}
					if(isSetMode()){
						beginSetWater();
					}else{
						WaterMgr.WATER_STATE = WaterMgr.WATER_STATE_ON;
					}
				}
			}
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode() && btnCanPress()){
				WaterMgr.WATER_STATE = WaterMgr.WATER_STATE_OFF;
				WaterMgr.stop();
				if(!WaterMgr.isWaterTimer() || MainActivity.isConnectedCard()){
					payBackMoney();
				}else{
//					WaterMgr.init();
				}
				
			}
		}

		return false;
	}
	
	private void payBackMoney(){
		int curFlow = HandlePortData.getCurFlow();
		float moneyLeft = MainActivity.getMoneyLeft(curFlow);
		String costStr = null;
		if(MainActivity.setMoney(moneyLeft)){
			costStr = String.format("%.2f", moneyLeft);
			mViewCost.setText(costStr);
		}else{
			MainActivity.forceStop("stop water fail");
		}
	}
	
	private boolean isSetMode(){
		return getCurSelected() == BTN_3L || getCurSelected() == BTN_5L;
	}
	
	private int getCurSelected(){
		return mCurSelected;
	}
	
	private int getCurMode(){
		return mCurMode;
	}
	
	private void initView(){
		mViewCost.setText(TEXT_COST);
		mViewWater.setText(TEXT_WATER);
		mViewWater.setTextColor(color_water_normal);
		setBtnSelected(BTN_NOW);
	}
	@Override
	public void resetView() {

	}

	@Override
	public void onCardOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCardOff() {
		// TODO Auto-generated method stub
		initView();
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
