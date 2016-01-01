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
//	public final static int MSG_SHOW_WATER_VOLUME= 9006;
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
	private static Double mUserMoney = (double) 0;
	private static Double mUserMoneyOri = (double) 0;
	public static String mUserMoneyStr = "-1";
	
	enum ViewFragment {
		USER, SETTING,CHANGE_PASSWD,CALIBRATE_FLOW,WATER_PRICE,DEVICE_INFO
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
				while(true){
					isRightRun = false;
					try {
						mMC.close();
						mMC.connect();
						mCardOn = true;
						if(!IS_ADMIN){
							handleWater();
							if((HandlePortData.getCurFlow()-lastFlow) > 0){
								WaterMgr.checkWater();
								lastFlow = HandlePortData.getCurFlow();
								float flow = HandlePortData.getCurFlow()/(float)1000;
								float cost = (float) (mUserMoneyOri - FData.getWaterPrice() * flow);
								setMoney(cost);
								float[] waterPrice = {cost,flow};
								mUIHandler.obtainMessage(MSG_SHOW_WATER_PRICE, waterPrice).sendToTarget();
							}
						}
						
						mMC.close();
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
			if(HandlePortData.WATER_ON){
				WaterMgr.WATER_STATE = WaterMgr.WATER_STATE_READ;
			}else{
				WaterMgr.start();
			}
			break;
		case WaterMgr.WATER_STATE_OFF:
			WaterMgr.stop();
			break;
		case WaterMgr.WATER_STATE_READ:
			FCmd.readWater();
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
	
	public void startWater(){
		
	}
	public void clearData(){
		FCmd.clearCmd(); //should use in main thread ?
		HandlePortData.clear();
		mUserMoneyOri = (double) 0;
	}
	
	public static boolean IS_ADMIN = false;
	public static boolean modifyPW() throws Exception{
		boolean auth1 = false;
		if(null == mMC || !mMC.isConnected()){
			return false;
		}
		auth1 = mMC.authenticateSectorWithKeyA(1, keyA);
		if (auth1) {
			//Log.e("d","cno_read_ture");
			byte[] response_0 = mMC.readBlock(4);
			String str = ToolClass.bytesToHexString(response_0);
			FLog.v("set KEY A !!!!!!!!!!!!!!! ="+str);
			String str1 = AesTool.decrypt(SEED,ToolClass.bytesToHexString(response_0));
			FLog.v("set KEY A !!!!!!!!!!!!!!! str1="+str1);
			keyA = ToolClass.hexStringToBytes(str1);
			DataNative.setKeyA(str1);
			return true;
		}
		return false;
	}
	
	public synchronized void readBalance() {
		boolean bCanReadMoney = false;
		boolean auth = false;
		boolean auth0 = false;
		IS_ADMIN = false;
		try {
			mMC.close();
			mMC.connect();
			//read card number
			auth0 = mMC.authenticateSectorWithKeyA(0, keyA);//MifareClassic.KEY_DEFAULT
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
			
//			setMoney(3000);
			
			//read money
			auth = mMC.authenticateSectorWithKeyA(7, keyA);
			FLog.v("readBalance read 7 "+auth);
			if (auth) {
				byte[] response0 = mMC.readBlock(28);
				String value = AesTool.decrypt(SEED,
						ToolClass.bytesToHexString(response0));
				mUserMoneyOri = mUserMoney = Double.valueOf(value);
				FLog.v("readBalance value="+value);
				FLog.v("readBalance mUserMoney="+mUserMoneyOri);
				bCanReadMoney = true;
				
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
	
	private synchronized void setMoney(double setMoney){
		if(setMoney < 0 || setMoney > 10000){
			FLog.v("set money error "+setMoney);
			return;
		}
		boolean auth2 = false;
		byte[] testdata = null;
		try {
			auth2 = mMC.authenticateSectorWithKeyA(7, keyA);
			if (auth2) {
				String ienstr = Double.toString(setMoney);
				String instr = AesTool.encrypt(SEED, ienstr);
				if(instr.length() == 32) {
					testdata = ToolClass.hexStringToBytes(instr);
					mMC.writeBlock(28, testdata);
					FLog.v("set Money charge done !" );
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
	}
	
	public static synchronized boolean changeMoney(double iadd){
		/*if(iadd > 0){
			return false;
		}*/
		boolean auth2 = false;
		boolean auth =false;
		byte[] testdata = null;
		boolean ret = false;
		try {
//			mMC.close();
//			mMC.connect();
			FLog.v("isconected "+mMC.isConnected());
			auth2 = mMC.authenticateSectorWithKeyA(7, keyA);
			if (auth2) {
				byte[] resold = mMC.readBlock(28);
				// 原来的值加充值的值等于现在卡里的值
				double iold = Double.valueOf(AesTool.decrypt(SEED,
						ToolClass.bytesToHexString(resold)));
//				FLog.v("changeMoney ori money "+iold);
				
				double f = iold + iadd;
				BigDecimal b = new BigDecimal(f);  
				double inew = b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
				
				String ienstr = Double.toString(inew);
//				FLog.v("changeMoney total money "+inew);
				
				String instr = AesTool.encrypt(SEED, ienstr);
				if ((inew >= envalue_MIN)
						&& (inew <= envale_MAX)
						&& (instr.length() == 32)) {
					// String ien=inew+"";
					testdata = ToolClass.hexStringToBytes(instr);
					mMC.writeBlock(28, testdata);
//					FLog.v("changeMoney charge done !" );
					
					auth = mMC.authenticateSectorWithKeyA(7, keyA);
					if (auth) {
						byte[] response0 = mMC.readBlock(28);
						String value = AesTool.decrypt(SEED,
								ToolClass.bytesToHexString(response0));
						mUserMoney = Double.valueOf(value);
						mUserMoneyStr = value;
//						gUIHandler.obtainMessage(MSG_SHOW_WATER_PRICE, value).sendToTarget();
						FLog.v("changeMoney success value="+value);
						ret = true;
					}
				} else {
					FLog.v("changeMoney set error ! inew="+inew+" length="+instr.length());
				}
			}
//			mMC.close();
		} catch (IOException e) {
			FLog.v("changeMoney no card !");
			e.printStackTrace();
		} catch (Exception e) {
			FLog.v("changeMoney Exception="+e.getMessage());
			e.printStackTrace();
		}finally {
//			FLog.v("changeMoney finally");
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
				
/*				try {
					mMC.close();
					mMC.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				clearData();// should 
				readBalance();
				new CheckCardOn().start();
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
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}
	
	boolean isOpen = false;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
//		FLog.v("dispatchKeyEvent action="+event.getAction()+" code="+event.getKeyCode());
		if(isCardOn()){
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
//				mVideoPlay.playTest();
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
		// TODO Auto-generated method stub
		mUIHandler.sendMessageDelayed(msg, 10);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			mOutputStream.write(cmd);
//			mOutputStream.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		for(int i=0; i<size; i++){
//			FLog.t(String.format("xxx %2d 0x%02x", FCmd.testNum, buffer[i] & 0xff));
			HandlePortData.handleByte(buffer[i]);
			FCmd.testNum++;
		}
		
	}
}
