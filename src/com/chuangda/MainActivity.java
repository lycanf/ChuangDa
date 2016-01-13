package com.chuangda;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.SerialPort;

import com.chuangda.common.AesTool;
import com.chuangda.common.DataCal;
import com.chuangda.common.DataNative;
import com.chuangda.common.FCmd;
import com.chuangda.common.FConst;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.common.HandlePortData;
import com.chuangda.common.ToolClass;
import com.chuangda.common.WaterMgr;
import com.chuangda.data.FItemCard;
import com.chuangda.widgets.VideoPlay;

interface UICallBack{
	void sendMsg(Message msg);
}

public class MainActivity extends SerialPortActivity implements UICallBack{

	public final static int MSG_CHANGE_FRAGMENT = 9000;
	public final static int MSG_POP_FRAGMENT = 9001;
	public final static int MSG_SHOW_TOAST = 9002;
	public final static int MSG_PLAY_NEXT = 9003;
	public final static int MSG_VIDEO_COUNT = 9004;
	public final static int MSG_SHOW_WATER_PRICE= 9005;
	public final static int MSG_SHOW_WATER_VOLUME= 9006;
	public final static int MSG_CARD_OFF = 9007;
	public final static int MSG_CARD_ON= 9008;
	public final static int MSG_SHOW_TDS= 9009;
	public final static int MSG_TEST_TEXT = 9010;
	public final static int MSG_MODIFY_FLOW = 9011;
	
	public final static String TEXT_NO_VIDEO = "没有视频";
	public static Handler gUIHandler = null;
	static BaseFragment mCurBaseFragment = null;
	
	public static MyApplication mApplication;
	public static SerialPort mSerialPort;
	public static boolean mCardOn = false;
	
	private Tag mNewTag;
	TextView mVideoText ;
	TextView mTestText ;
	VideoPlay mVideoPlay;
	ImageView mVideoBg ;
	ViewFragment mCurViewFragment = ViewFragment.USER;
	
	WakeLock wakeLock;
	CheckTDS mCheckTDS;
	//common 
	public static int COMMON_HEALTH_STATE = 1;
	
	// read car
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	static MifareClassic mMC;
	static byte keyA[] = null;


	public static String SEED = "cdhk";
	public static double envalue_MIN = 0;
	public static double envale_MAX = 10000;

	private static String mCardNum = null;
	private static Double mUserMoneyOri = (double) 0;
	public static String mUserMoneyStr = "-1";
	
	public static FItemCard  CardNew = new FItemCard();
	public static FItemCard  CardCur = new FItemCard();
	
	enum ViewFragment {
		USER, SETTING,CHANGE_PASSWD,CALIBRATE_FLOW,WATER_PRICE,DEVICE_INFO,PAY
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gUIHandler = mUIHandler;
		DataNative.init(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;  
        getWindow().setAttributes(params);
        
        mTestText = (TextView) findViewById(R.id.main_test_text);
        
		mVideoText = (TextView) findViewById(R.id.main_video_text);
		mVideoText.setText(TEXT_NO_VIDEO);
		mVideoPlay = (VideoPlay) findViewById(R.id.main_video_play);
		mVideoBg = (ImageView) findViewById(R.id.main_video_bg);
		mUIHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT, 10);
			
		
		UserViewFragment mUserFragment = UserViewFragment.newInstance();
//		PayFragment mUserFragment = PayFragment.newInstance();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.main_frame, mUserFragment).commit();
		
		//read card
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		DataNative.IS_SUPPORT_NFC = false;
		if (mAdapter == null) {
			mUIHandler.obtainMessage(MSG_SHOW_TOAST,"没有支持NFC的硬件！").sendToTarget();
			FLog.v("device not support NFC！");
		} else if (!mAdapter.isEnabled()) {
			mUIHandler.obtainMessage(MSG_SHOW_TOAST,"NFC 未开启！").sendToTarget();
			FLog.v("please set NFC on !");
		} else {
			String key = DataNative.getKeyA();
			keyA = ToolClass.hexStringToBytes(key);
			DataNative.IS_SUPPORT_NFC = true;
			FLog.v("get keyA="+key);
			FLog.v("NFC can work ! " + getIntent().getAction());
			
			mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		}
		
		startTDSCheck();
	}
	
	private void startTDSCheck(){
		if(System.currentTimeMillis() - mCheckTDSTime > 3000){
			mCheckTDS = new CheckTDS();
			mCheckTDS.start();
			//test
//			new CheckAll().start();
		}

	}
	
	private class CheckAll extends Thread{
		@Override
		public void run() {
			while(!isCardOn()){
				Thread.currentThread();
				try {
					FCmd.readAll();
					Thread.sleep(1000);
					mCheckTDSTime = System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static long mCheckTDSTime = 0;
	private class CheckTDS extends Thread{
		@Override
		public void run() {
			while(!isCardOn()){
				Thread.currentThread();
				try {
					FCmd.readTDS();
					mCheckTDSTime = System.currentTimeMillis();
					Thread.sleep(FConst.CHECK_QULITY_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class CheckCardOn extends Thread{
		public void run() {
			if(null != mMC){
				boolean isRightRun = false;
				int lastFlow = 0;
				while(!isForceStop()){
					isRightRun = false;
					try {
						mMC.close();
						mMC.connect();
						mCardOn = true;
						mMC.close();
						
						if(!IS_ADMIN){
							handleWater();
							if(HandlePortData.isWaterOn()){
								if(WaterMgr.isWaterTimer()){
									float flow = HandlePortData.getCurFlow()/(float)1000;
									mUIHandler.obtainMessage(MSG_SHOW_WATER_VOLUME, flow).sendToTarget();
									WaterMgr.checkWater();
									
								}else if((HandlePortData.getCurFlow()-lastFlow) > 0){
									lastFlow = HandlePortData.getCurFlow();
									setCostMoney();
								}
							}else{
								
							}
						}
						
						Thread.currentThread();
						Thread.sleep(FConst.CHECK_INTERVAL);
						isRightRun = true;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						if(!isRightRun){
							removedCard();
							break;
						}
					}
				}
				FLog.v("the card is off ! MC="+mMC.isConnected());
				if(isRightRun){
					removedCard();
				}
			}
		};
	};
	
	private void handleWater(){
		switch(WaterMgr.WATER_STATE){
		case WaterMgr.WATER_STATE_ON:
			if(HandlePortData.isWaterOn()){
				WaterMgr.WATER_STATE = WaterMgr.WATER_STATE_READ;
			}else{
				WaterMgr.start();
			}
			break;
		case WaterMgr.WATER_STATE_OFF:
			if(HandlePortData.isWaterOn()){
				WaterMgr.stop();
			}else{
				
			}
			break;
		case WaterMgr.WATER_STATE_READ:
			if(HandlePortData.isWaterOn()){
				FCmd.readWater();
			}else{
				
			}
			break;
		}
	}
	
	private void removedCard(){
		FLog.v("removedCard ");
		mCardOn = false;
		IS_ADMIN = false;
		clearData();
		WaterMgr.stop();
		mUIHandler.obtainMessage(MSG_CARD_OFF).sendToTarget();
		startTDSCheck();
	}
	
	public static boolean isCardOn(){
		return mCardOn;
	}
	
	public static float getMoneyLeft(float totalFlow){
		float cost = (float) (mUserMoneyOri - FData.getWaterPrice() * totalFlow /(float)1000);
		return cost;
	}
	public static float[] setCostMoney(){
		float flow = HandlePortData.getCurFlow();
		float cost = getMoneyLeft(flow);
		if(cost <= 0){
			cost = 0;
			forceStop("余额不足");
		}
		setMoney(cost);
		float[] waterPrice = {cost,flow/(float)1000};
		gUIHandler.obtainMessage(MSG_SHOW_WATER_PRICE, waterPrice).sendToTarget();
		return waterPrice;
	}
	
	public static boolean isMoneyEnough(int flowcost){
		float flow = flowcost/(float)1000;
		float cost = (float) (mUserMoneyOri - FData.getWaterPrice() * flow);
		FLog.v("isMoneyEnough flowcost="+flowcost+" -- cost="+cost);
		return cost > 0;
	}
	
	public void startWater(){
		
	}
	public void clearData(){
		FCmd.clearCmd(); //should use in main thread ?
		HandlePortData.clear();
		mUserMoneyOri = (double) 0;
	}
	
	public static boolean IS_ADMIN = false;
	public static boolean modifyPW(){
		boolean auth1 = false;
		boolean ret = false;
		try {
			mMC.close();
			mMC.connect();
			auth1 = mMC.authenticateSectorWithKeyA(1, MifareClassic.KEY_DEFAULT);//keyA,MifareClassic.KEY_DEFAULT
			if (auth1) {
				//Log.e("d","cno_read_ture");
				byte[] response_0 = mMC.readBlock(4);
				String str = ToolClass.bytesToHexString(response_0);
				FLog.v("set KEY A !!!!!!!!!!!!!!! ="+str);
				String str1 = AesTool.decrypt(SEED,ToolClass.bytesToHexString(response_0));
				FLog.v("set KEY A !!!!!!!!!!!!!!! str1="+str1);
				keyA = ToolClass.hexStringToBytes(str1);
				DataNative.setKeyA(str1);
				ret = true;
			}
			mMC.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return ret;
		}
	}
	
	public synchronized void readBalance() {
		boolean bCanReadMoney = false;
		boolean auth = false;
		boolean auth0 = false;
		IS_ADMIN = false;
		
//		setMoney(0.5);
		try {
			mMC.close();
			mMC.connect();
			FLog.v("readBalance keyA="+ToolClass.bytesToHexString(keyA));
			//read card number
			auth0 = mMC.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT);//MifareClassic.KEY_DEFAULT
			FLog.v("readBalance read card number "+auth0);
			if (auth0) {
				//Log.e("d","cno_read_ture");
				byte[] response_0 = mMC.readBlock(0);
				byte[] response_no = { response_0[3], response_0[2],
						response_0[1], response_0[0] };
				DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
				df.setGroupingUsed(false);
				mCardNum = df.format(ToolClass.HexstrToDouble(ToolClass
						.bytesToHexString(response_no)));
				FLog.v("readBalance mCardNum="+mCardNum);
			}
			
			//read money
			auth = mMC.authenticateSectorWithKeyA(7, keyA);
			FLog.v("readBalance read 7 "+auth);
			if (auth) {
				byte[] response0 = mMC.readBlock(28);
				String value = AesTool.decrypt(SEED,
						ToolClass.bytesToHexString(response0));
				mUserMoneyOri = Double.valueOf(value);
				FLog.v("readBalance value="+value);
				FLog.v("readBalance mUserMoneyOri="+mUserMoneyOri);
				bCanReadMoney = true;
				mbForceStop = false;
				
				float[] waterPrice = {Float.valueOf(value), 0};
				mUIHandler.obtainMessage(MSG_SHOW_WATER_PRICE, waterPrice).sendToTarget();
				mUIHandler.obtainMessage(MSG_CARD_ON, IS_ADMIN).sendToTarget();
			}else{
				IS_ADMIN = mMC.authenticateSectorWithKeyA(1, keyA);
			}
			
			//is admin
			if(!bCanReadMoney && auth){
				IS_ADMIN = mMC.authenticateSectorWithKeyA(1, keyA);
				FLog.v("I'm adimin !");
			}
			
			if(!auth && !auth0){
				mMC.close();
				mUIHandler.obtainMessage(MSG_SHOW_TOAST,"非授权卡！").sendToTarget();
			}
			mMC.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			FLog.v("readBalance no card !");
			e.printStackTrace();
		} catch (Exception e) {
			FLog.v("readBalance Exception="+e.getMessage());
			e.printStackTrace();
		} finally {
			FLog.v("readBalance finally bCanReadMoney "+bCanReadMoney);
			if(IS_ADMIN || (!bCanReadMoney && auth)){
				FLog.v("readBalance goto setting");
				mUIHandler.obtainMessage(MSG_CHANGE_FRAGMENT,ViewFragment.SETTING).sendToTarget();
			}
		}
	}
	
	public static String getMoney(){
		boolean auth = false;
		String ret = null;
		try {
			mMC.close();
			mMC.connect();
			auth = mMC.authenticateSectorWithKeyA(7, keyA);
			if (auth) {
				byte[] response0 = mMC.readBlock(28);
				ret = AesTool.decrypt(SEED,
						ToolClass.bytesToHexString(response0));
				FLog.v("getMoney "+ret);
			}
			mMC.close();
		} catch (IOException e) {
			FLog.v("getMoney "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			FLog.v("getMoney "+e.getMessage());
			e.printStackTrace();
		}finally{
			return ret;
		}

	}
	
	@SuppressWarnings("finally")
	public synchronized static boolean setMoney(double setMoney){
		boolean ret = false;
		if(setMoney < 0 || setMoney > 10000){
			FLog.v("set money error "+setMoney);
			return ret;
		}
		boolean auth2 = false;
		byte[] testdata = null;
		try {
			mMC.close();
			mMC.connect();
			auth2 = mMC.authenticateSectorWithKeyA(7, keyA);//MifareClassic.KEY_DEFAULT
			FLog.v("set Money "+auth2+" setMoney="+setMoney);
			if (auth2) {
				String ienstr = String.format("%.3f", setMoney);
//				FLog.v("set Money ienstr="+ienstr);
				String instr = AesTool.encrypt(SEED, ienstr);
				if(instr.length() == 32) {
					testdata = ToolClass.hexStringToBytes(instr);
					mMC.writeBlock(28, testdata);
					FLog.v("set Money charge done !!! " +ienstr);
					ret = true;
//					readMoney();
				}else{
					FLog.v("set Money error "+instr.length());
				}
			}
			mMC.close();
		} catch (IOException e) {
			FLog.e("set Money "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			FLog.e("set Money "+e.getMessage());
			e.printStackTrace();
		}finally{
//			FLog.v("set money "+setMoney);
			return ret;
		}
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (mAdapter != null) {
			mNewTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); 
			FLog.v("NFC onNewIntent tag="+mNewTag.toString());
			if(null != mNewTag){
				FLog.v("onNewIntent get tag !");
				mAdapter = NfcAdapter.getDefaultAdapter(this);
				mPendingIntent = PendingIntent.getActivity(this, 0,
						new Intent(this, getClass())
								.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
				mMC = MifareClassic.get(mNewTag);
				
				
				clearData();// should 
				WaterMgr.init();
				readBalance();
				
				if(mUserMoneyOri > 0 || IS_ADMIN){
					new CheckCardOn().start();
				}else{
					mUIHandler.obtainMessage(MSG_SHOW_TOAST,"请充值！").sendToTarget();
				}
			}
		}
		
		startTDSCheck();
	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mAdapter != null){
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					 mTechLists);
		}
	}
	
	private void translateFragment(ViewFragment type) {
		BaseFragment newFragment = null;
		mCurViewFragment = type;
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
		case PAY:
			newFragment = PayFragment.newInstance();
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	boolean isOpen = false;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
//		FLog.v("dispatchKeyEvent action="+event.getAction()+" code="+event.getKeyCode());
		
		if(KeyEvent.ACTION_UP == event.getAction() && FData.KEYCODE_PRE == event.getKeyCode()){
//			FCmd.test();
		}
		
		if(isCardOn() && !isForceStop()){
			mCurBaseFragment.dispatchKeyEvent(event);
		}
		return true;
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			FLog.m("mUIHandler msg "+msg.what);
			switch (msg.what) {
			case MSG_CHANGE_FRAGMENT:
				translateFragment((ViewFragment) msg.obj);
				break;
			case MSG_POP_FRAGMENT:
				getFragmentManager().popBackStack();
				break;
			case MSG_SHOW_TOAST:
				String str = (String) msg.obj;
				Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
				break;
			case MSG_TEST_TEXT:
				String strTest = (String) msg.obj;
				mTestText.setText(strTest);
				break;
			case MSG_PLAY_NEXT:
				mVideoPlay.playNext();
				break;
			case MSG_VIDEO_COUNT:
				FLog.v("MSG_VIDEO_COUNT "+msg.arg1);
				mVideoText.setVisibility(msg.arg1 > 0 ? View.INVISIBLE : View.VISIBLE);
				mVideoText.setText(TEXT_NO_VIDEO);
				mVideoBg.setVisibility(msg.arg1 > 0 ? View.INVISIBLE : View.VISIBLE);
				break;
			case MainActivity.MSG_CARD_OFF:
				if(mCurViewFragment != ViewFragment.USER){
					getFragmentManager().popBackStack();
					translateFragment(ViewFragment.USER);
				}else{
					mCurBaseFragment.resetView();
				}
				break;
			default:
				mCurBaseFragment.handleUI(msg);
			}
		};
	};

	@Override
	public void sendMsg(Message msg) {
		mUIHandler.sendMessageDelayed(msg, 10);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
	private static boolean mbForceStop = false;
	public static boolean isForceStop(){
		return mbForceStop;
	}
	public static void forceStop(String str){
		FLog.v("forceStop !!!!!!!!!!!");
		if(null == str){
			str = "请重新放置水卡";
		}
		WaterMgr.stop();
		mbForceStop = true;
		gUIHandler.obtainMessage(MSG_SHOW_TOAST,str).sendToTarget();
	}
	
	public static void sendData(String str){
		try {
			mOutputStream.write(str.getBytes());
			mOutputStream.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendData(byte[] cmd){
		try {
			if(null != mOutputStream){
				mOutputStream.write(cmd);
			}
//			costsend "+ToolClass.bytesToHexString(cmd));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
//		FLog.t("datareceice="+ToolClass.bytesToHexString(buffer));
		for(int i=0; i<size; i++){
			HandlePortData.handleByte(buffer[i]);
		}
		
	}
}
