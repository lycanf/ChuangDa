package com.chuangda;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.locks.ReentrantLock;

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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.SerialPort;

import com.chuangda.common.AesTool;
import com.chuangda.common.DataNative;
import com.chuangda.common.FCmd;
import com.chuangda.common.FConst;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.common.HandlePortData;
import com.chuangda.common.SysInfo;
import com.chuangda.common.ToolClass;
import com.chuangda.common.WaterMgr;
import com.chuangda.data.FItemCard;
import com.chuangda.data.FNetSetting;
import com.chuangda.data.FUser;
import com.chuangda.net.DataHttp;
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
	public final static int MSG_FORCE_STOP = 9012;
	public final static int MSG_INIT_VIEW = 9013;
	
	public final static String TEXT_NO_VIDEO = "没有视频";
	public static Handler gUIHandler = null;
	static BaseFragment mCurBaseFragment = null;
	Toast mToast = null;
	
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
	//common 
	public static int COMMON_HEALTH_STATE = 1;
	
	// read car
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	static MifareClassic mMC;
	static byte keyA[] = null;
	private static final ReentrantLock mLock = new ReentrantLock();  


	public static long INTERVAL_CHECK_ALL = 1000*60;
	public static long INTERVAL_RECORD = 1000*6;
	
	public static String SEED = "cdhk";
	public static double envalue_MIN = 0;
	public static double envale_MAX = 10000;

	private static double mUserMoneyBase = (double) 0;
	private static double mUserMoneyOri = (double) 0;
	public  static double UserMoneyCur = (double) 0;
	public static String mUserMoneyStr = "-1";
	
	public static FItemCard  CardNew = new FItemCard();
	public static FItemCard  CardCur = new FItemCard();
	
	enum ViewFragment {
		USER, SETTING,CHANGE_PASSWD,CALIBRATE_FLOW,WATER_PRICE,DEVICE_INFO,PAY,REGIST
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
        mTestText.setVisibility(View.GONE);
        
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
		
		SysInfo.getLocalMacAddress(this);
		
		startTDSCheck();
		new Record().start();
	}
	
	private void startTDSCheck(){
		if(System.currentTimeMillis() - mCheckTDSTime > 3000){
//			new CheckTDS().start();
			new CheckAll().start();
		}

	}
	
	private class CheckAll extends Thread{
		@Override
		public void run() {
			FLog.v("CheckAll start");
			while(!isCardOn()){
				Thread.currentThread();
				long curTime = System.currentTimeMillis();
				if(TextUtils.isEmpty(FUser.MAC_ADDR)){
					SysInfo.getLocalMacAddress(MainActivity.this);
				}
				if((curTime-FConst.LATEST_MAINTAIN) > FConst.INTERVAL_MAINTAIN){
					FUser.sendDeviceMaintain();
					FConst.LATEST_MAINTAIN = curTime;
				}
				try {
					gUIHandler.obtainMessage(MSG_INIT_VIEW).sendToTarget();
					FCmd.readAll();
					Thread.sleep(INTERVAL_CHECK_ALL);
					mCheckTDSTime = System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			FLog.v("CheckAll stop");
		}
	}
	
	private class Record extends Thread{
		@Override
		public void run() {
			while(true){
				Thread.currentThread();
				try {
					mCheckRecordTime = System.currentTimeMillis();
					String res = FUser.sendDeviceState();
					FNetSetting.parse(res);
					Thread.sleep(INTERVAL_RECORD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class SendConsume extends Thread{
		@Override
		public void run() {
			String ret = null;
			String  amount = null,  balance = null;
			long totalFlow = HandlePortData.getCurFlow();
			
//			total = String.format("%.2f", totalFlow);
			amount = String.format("%.2f", (mUserMoneyBase-UserMoneyCur));
			if(Math.abs(mUserMoneyBase-UserMoneyCur) <= 0.01 ){
				return;
			}
			balance = String.format("%.2f", UserMoneyCur);
			if(HandlePortData.isWaterOn()){
				HandlePortData.WATER_FLOW_TIME += System.currentTimeMillis()-HandlePortData.WATER_FLOW_TIME_ONCE;
			}
			ret = DataHttp.sendHttpPost(FUser.urlDeviceConsume,FUser.getDeviceConsume(totalFlow, amount, balance,HandlePortData.WATER_FLOW_TIME));
			FLog.v("sendDeviceConsume ="+ret);
		}
	}
	
	public static long mCheckTDSTime = 0;
	public static long mCheckRecordTime = 0;
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

	static int mLastFlow = 0;
	static boolean isConnected = false;
	public static boolean isConnectedCard(){
		return isConnected;
	}
	private class CheckCardOn extends Thread{
		public void run() {
			if(null != mMC){
				mLastFlow = 0;
				isConnected = false;
				mCardOn = true;
				while(!isForceStop()){
					try {
//						FLog.v("CheckCardOn 000");
						FLog.th("000 CheckCardOn in");
						mLock.lock();
						mMC.close();
						mMC.connect();
						mMC.close();
						isConnected = true;
//						FLog.v("CheckCardOn 001");
					} catch (IOException e) {
						e.printStackTrace();
						isConnected = false;
						FLog.v("CheckCardOn 100 "+e.getMessage());
					}catch (java.lang.IllegalStateException e) {
						e.printStackTrace();
						isConnected = false;
						FLog.v("CheckCardOn 101 "+e.getMessage());
					}finally{
						FLog.th("000 CheckCardOn out");
						mLock.unlock();
//						FLog.v("CheckCardOn 002");
						if(!isConnectedCard()){
							if (WaterMgr.isWaterTimer() && HandlePortData.isWaterOn()) {
								FLog.v("CheckCardOn 003 continue");
							}else{
								FLog.v("CheckCardOn 004 removedCard");	
								removedCard();
								break;
							}
						}
						
					}
					checkUserState();
					Thread.currentThread();
					try {
						Thread.sleep(FConst.CHECK_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
 				isConnected = false;
 				removedCard();
				FLog.v("get off CheckCardOn");
			}
		};
	};
	
	public static boolean isNOMoney(){
		return mUserMoneyBase <= 0;
	}
	private void checkUserState(){
		if (!IS_ADMIN) {
			if(isNOMoney()){
				return;
			}
			handleWater();
			if (HandlePortData.isWaterOn()) {
				if (WaterMgr.isWaterTimer()) {
					float flow = HandlePortData.getCurFlow()/ (float) 1000;
					mUIHandler.obtainMessage(MSG_SHOW_WATER_VOLUME,flow).sendToTarget();
					WaterMgr.checkWater();
				} else if ((HandlePortData.getCurFlow() - mLastFlow) > 0) {
					mLastFlow = HandlePortData.getCurFlow();
					if(isConnectedCard()){
						setCostMoney();
					}
				}
			} else {

			}
		}
	}
	
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
		if(!isCardOn()){
			return;
		}
		if(!IS_ADMIN){
			new SendConsume().start();
		}
		mCardOn = false;
		IS_ADMIN = false;
		clearData();
		WaterMgr.stop();
		mUIHandler.obtainMessage(MSG_CARD_OFF).sendToTarget();
		startTDSCheck();
		WaterMgr.init();
	}
	
	public static boolean isCardOn(){
		return mCardOn;
	}
	
	public static float getMoneyLeft(float totalFlow){
		float cost = (float) (mUserMoneyBase - FData.getWaterPrice() * totalFlow /(float)1000);
		return cost;
	}
	public static float[] setCostMoney(){
		float flow = HandlePortData.getCurFlow();
		float cost = getMoneyLeft(flow);
		if(cost < 0){
			cost = 0;
			forceStop("余额不足");
		}
		boolean bSet = setMoney(cost);
		if(bSet){
			float[] waterPrice = {cost,flow/(float)1000};
			gUIHandler.obtainMessage(MSG_SHOW_WATER_PRICE,0,0, waterPrice).sendToTarget();
			return waterPrice;
		}
		return null;
	}
	
	public static boolean isMoneyEnough(int flowcost){
		float flow = flowcost/(float)1000;
		float cost = (float) (mUserMoneyBase - FData.getWaterPrice() * flow);
		FLog.v("isMoneyEnough flowcost="+flowcost+" -- cost="+cost);
		return cost > 0;
	}
	
	public void startWater(){
		
	}
	public void clearData(){
		FCmd.clearCmd(); //should use in main thread ?
		HandlePortData.clear();
	}
	
	public static boolean IS_ADMIN = false;
	public static boolean modifyPW(){
		boolean auth1 = false;
		boolean ret = false;
		try {
			FLog.th("002 modifyPW in");
			mLock.lock();
			mMC.close();
			mMC.connect();
			auth1 = mMC.authenticateSectorWithKeyA(1, keyA);//keyA,MifareClassic.KEY_DEFAULT
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
			FLog.th("002 modifyPW out");
			mLock.unlock();
			return ret;
		}
	}
	
	public synchronized void readBalance() {
		boolean bCanReadMoney = false;
		boolean auth = false;
		boolean auth0 = false;
		IS_ADMIN = false;
		UserMoneyCur = 0;
		mUserMoneyBase = 0;
		HandlePortData.WATER_FLOW_TIME = 0;
		
//		setMoney(500);
		try {
			mMC.close();
			mMC.connect();
			FLog.v("readBalance keyA="+ToolClass.bytesToHexString(keyA));
			
			//read card number
			auth0 = mMC.authenticateSectorWithKeyA(0, keyA);//MifareClassic.KEY_DEFAULT
			FLog.v("readBalance read card number "+auth0);
			if (auth0) {
				//Log.e("d","cno_read_ture");
				byte[] response_0 = mMC.readBlock(0);
				byte[] response_no = { response_0[3], response_0[2],
						response_0[1], response_0[0] };
				DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
				df.setGroupingUsed(false);
				FUser.cardno = df.format(ToolClass.HexstrToDouble(ToolClass
						.bytesToHexString(response_no)));
				FLog.v("readBalance mCardNum="+FUser.cardno);
			}
			//read money
			auth = mMC.authenticateSectorWithKeyA(7, keyA);
			FLog.v("readBalance read 7 "+auth);
			if (auth) {
				byte[] response0 = mMC.readBlock(28);
				String value = AesTool.decrypt(SEED,
						ToolClass.bytesToHexString(response0));
				mUserMoneyBase = Double.valueOf(value);
				UserMoneyCur = mUserMoneyOri = mUserMoneyBase;
				FLog.v("readBalance value="+value);
				FLog.v("readBalance mUserMoneyBase="+mUserMoneyBase);
				bCanReadMoney = true;
				mbForceStop = false;
				if(isNOMoney()){
					showToast("请充值");
				}
				
				float[] waterPrice = {Float.valueOf(value), 0};
				mUIHandler.obtainMessage(MSG_SHOW_WATER_PRICE,1,0, waterPrice).sendToTarget();
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
	
	public static double getMoney(){
		boolean auth = false;
		double ret = 0;
		try {
			FLog.th("001 getMoney in");
			mLock.lock();
			mMC.close();
			mMC.connect();
			auth = mMC.authenticateSectorWithKeyA(7, keyA);
			if (auth) {
				byte[] response0 = mMC.readBlock(28);
				String value = AesTool.decrypt(SEED,
						ToolClass.bytesToHexString(response0));
				ret = Double.valueOf(value);
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
			FLog.th("001 getMoney out");
			mLock.unlock();
			return ret;
		}

	}
	
	public synchronized static boolean addMoney(double addM){
		boolean ret = false;
		double curM = getMoney();
		if(curM >= 0){
			double setM = curM+addM;
			mUserMoneyBase += addM;
			ret = setMoney(setM);
		}
		return ret;
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
			FLog.th("003 setMoney in");
			mLock.lock();
			mMC.close();
			mMC.connect();
			auth2 = mMC.authenticateSectorWithKeyA(7, keyA);//MifareClassic.KEY_DEFAULT
			FLog.v("set Money "+auth2+" setMoney="+setMoney);
			if (auth2) {
				String ienstr = String.format("%.2f", setMoney);
//				FLog.v("set Money ienstr="+ienstr);
				String instr = AesTool.encrypt(SEED, ienstr);
				if(instr.length() == 32) {
					testdata = ToolClass.hexStringToBytes(instr);
					mMC.writeBlock(28, testdata);
					UserMoneyCur = setMoney;
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
			FLog.th("003 setMoney out");
			mLock.unlock();
//			FLog.v("set money "+setMoney);
			return ret;
		}
	}
	

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(isCardOn()){
			showToast("正在放水，请先停水");
			return;
		}
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
				
//				setAdmin();
				clearData();// should 
				WaterMgr.init();
				readBalance();
				
				new CheckCardOn().start();
			}
		}
	}
	
	//test
	private static void setAdmin(){
		boolean auth2 = false;
		byte[] testdata = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
				0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,};;
			try {
				mMC.close();
				mMC.connect();
				auth2 = mMC.authenticateSectorWithKeyA(7, keyA);//MifareClassic.KEY_DEFAULT
				FLog.v("setAdmin auth2="+auth2);
				if (auth2) {
						mMC.writeBlock(28, testdata);
						FLog.v("setAdmin success");
				}
				mMC.close();
			} catch (IOException e) {
				FLog.v("setAdmin "+e.getMessage());
				e.printStackTrace();
			}

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
		case REGIST:
			newFragment = RegistFragment.newInstance();
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
		
		if(isCardOn() && !isForceStop() ){
			if(isConnectedCard()){
				mCurBaseFragment.dispatchKeyEvent(event);
			}else {
				if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
					mCurBaseFragment.dispatchKeyEvent(event);
				}else{
					showToast("卡片被拿走了");
				}
			}
		}else{
			showToast("请重新放置卡片");
		}
		return true;
	}
	
	public static void showToast(String str){
		gUIHandler.obtainMessage(MSG_SHOW_TOAST,str).sendToTarget();
	}
	
	public static void gHandle(int what){
		gHandle(what,null);
	}
	public static void gHandle(int what, Object obj){
		gHandle(what,0,0,obj);
	}
	public static void gHandle(int what, int arg1, int arg2, Object obj){
		gUIHandler.obtainMessage(what,arg1,arg2,obj).sendToTarget();
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			FLog.m("mUIHandler msg "+msg.what);
			FLog.m("getBackStackEntryCount "+getFragmentManager().getBackStackEntryCount());
			switch (msg.what) {
			case MSG_CHANGE_FRAGMENT:
				translateFragment((ViewFragment) msg.obj);
				break;
			case MSG_POP_FRAGMENT:
				getFragmentManager().popBackStack();
				break;
			case MSG_SHOW_TOAST:
				if(null != mToast){
					mToast.cancel();
				}
				String str = (String) msg.obj;
				mToast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);
				mToast.show();
				break;
			case MSG_TEST_TEXT:
				String strTest = (String) msg.obj;
				mTestText.setText(strTest);
				break;
			case MSG_PLAY_NEXT:
				mVideoPlay.playNext();
				break;
			case MSG_VIDEO_COUNT:
//				FLog.v("MSG_VIDEO_COUNT "+msg.arg1);
				mVideoText.setVisibility(msg.arg1 > 0 ? View.INVISIBLE : View.VISIBLE);
				mVideoText.setText(TEXT_NO_VIDEO);
				mVideoBg.setVisibility(msg.arg1 > 0 ? View.INVISIBLE : View.VISIBLE);
				break;
			case MainActivity.MSG_CARD_OFF:
				if(mCurViewFragment != ViewFragment.USER){
					popAll();
				}else{
					mCurBaseFragment.resetView();
				}
				mCurBaseFragment.onCardOff();
				break;
			default:
				mCurBaseFragment.handleUI(msg);
			}
		};
	};

	private void popAll(){
		for(int i=0; i<getFragmentManager().getBackStackEntryCount(); i++){
			getFragmentManager().popBackStack();
		}
	}
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
		FLog.v("forceStop !!!!!!!!!!! "+str);
		if(null == str){
			str = "请重新放置水卡";
		}
		WaterMgr.stop();
		mbForceStop = true;
		gUIHandler.obtainMessage(MSG_SHOW_TOAST,str).sendToTarget();
		gUIHandler.obtainMessage(MSG_FORCE_STOP).sendToTarget();
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
