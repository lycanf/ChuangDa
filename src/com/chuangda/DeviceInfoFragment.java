package com.chuangda;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuangda.common.DataNative;
import com.chuangda.common.FCmd;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.common.FTime;
import com.chuangda.data.FUser;
import com.chuangda.widgets.MODBUS_ITEM;
import com.chuangda.widgets.customed.FMaintainView;




public class DeviceInfoFragment extends BaseFragment {

	public static final int MSG_INFO = 4000;
	
	public final int T_Quality = 100;
	public final int T_TotalFlow = 101;
	public final int T_Voltage = 102;
	public final int T_TotalPulse= 103;
	public final int T_Pulse_L = 104;
	public final int T_Volume = 105;
	public final int T_Dev_Num = 106;
	
	public final int T_MaintainPPF = 110;
	public final int T_MaintainCTO = 111;
	public final int T_MaintainUDF = 112;
	public final int T_MaintainRot = 113;
	
	class Item{
		FMaintainView maintain = null;
		TextView text = null;
		String type = "";
		int res ;
		public Item(int r, String t){
			res = r;
			type = t;
		}
		public void selected(boolean b){
			if(null != maintain){
				maintain.setSelected(b);
			}else if(null != text){
				text.setTextColor(b ? Color.RED : Color.WHITE);
			}
		}
	}
	
	Item[] mItems = {
		new Item (R.id.device_info_maintain_ppf,"PPF"),
		new Item (R.id.device_info_maintain_cto,"CTO"),
		new Item (R.id.device_info_maintain_udf,"UDF"),
		new Item (R.id.device_info_maintain_ro,"RO"),
		new Item (R.id.device_info_volume,"volume"),
		new Item (R.id.device_info_num,"device num"),
	};
	
	TextView mTextWaterQuality = null;
	TextView mTextFlow = null;
	TextView mTextVoltage = null;
	TextView mTextPulse = null;
	TextView mTextMaintain = null;
	
/*	FItemWidget[] mItems = {
		new FItemWidget("当前水质 : ",false, T_Quality),
		new FItemWidget("累积流量 : ",false, T_TotalFlow),
		new FItemWidget("供电电压 : ",false, T_Voltage),
		new FItemWidget("累积脉冲 : ",false, T_TotalPulse),
		new FItemWidget("每升脉冲 : ",false, T_Pulse_L),
		
		new FItemWidget("RO : ",true, T_MaintainRot),
		new FItemWidget("PPF : ",true, T_MaintainPPF),
		new FItemWidget("CTO: ",true, T_MaintainCTO),
		new FItemWidget("UDF : ",true, T_MaintainUDF),
		
		new FItemWidget("调整音量 : ",true, T_Volume),	
		new FItemWidget("设备编号 : ",true, T_Dev_Num),	
	};*/
	
	boolean isVisible = true;
	int mCurSelected = -1;
	int mCurUpdate = -1;
	AudioManager mAudioManager ;
//	FLinearLayout mlist = null;
	ImageView mImgArrow = null;
	
	
	public DeviceInfoFragment() {
	}
	
    static DeviceInfoFragment newInstance() {
    	DeviceInfoFragment f = new DeviceInfoFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.setting_device_info, container, false);
    	mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
    	mImgArrow = (ImageView) v.findViewById(R.id.device_info_arrow);
    	
    	mTextWaterQuality = (TextView) v.findViewById(R.id.device_info_text_water);
    	mTextFlow = (TextView) v.findViewById(R.id.device_info_text_flow);
    	mTextVoltage = (TextView) v.findViewById(R.id.device_info_text_voltage);
    	mTextPulse = (TextView) v.findViewById(R.id.device_info_text_pulse);
    	mTextMaintain = (TextView) v.findViewById(R.id.device_info_text_maintain);
    	for(int i=0; i<4; i++){
			mItems[i].maintain = (FMaintainView) v.findViewById(mItems[i].res);
		}
    	for(int i=4; i<6; i++){
			mItems[i].text = (TextView) v.findViewById(mItems[i].res);
		}
        return v;
    }

    class updateInfo extends Thread{
    	@Override
    	public void run() {
    		while(isVisible()){
    			FCmd.readAll();
    			Thread.currentThread();
    			try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    private void updateInfo(){
    	mTextWaterQuality.setText("当前水质 : "+MODBUS_ITEM.TDS_OUT);
    	mTextFlow.setText("当前流量 : "+MODBUS_ITEM.FLOW);
    	mTextVoltage.setText("供电电压 : "+MODBUS_ITEM.VOLTAGE);
    	mTextPulse.setText("当前脉冲 : "+MODBUS_ITEM.PULSE);
    	
    	mItems[4].text.setText(""+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    	mItems[5].text.setText(""+String.format("%05d", DataNative.getDeviceNum()));
    	
    	/*mlist.setText(T_Quality, "当前水质 : "+MODBUS_ITEM.TDS_OUT);
    	mlist.setText(T_TotalFlow, "当前流量 : "+MODBUS_ITEM.FLOW);
    	mlist.setText(T_Voltage, "供电电压 : "+MODBUS_ITEM.VOLTAGE);
    	mlist.setText(T_TotalPulse, "当前水质 : "+MODBUS_ITEM.TDS_OUT);
    	mlist.setText(T_Pulse_L, "当前脉冲 : "+MODBUS_ITEM.PULSE);
    	mlist.setText(T_Volume, "调整音量 : "+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    	mlist.setText(T_Dev_Num,  "设备编号 : "+String.format("%05d", DataNative.getDeviceNum()));*/
    }
    
    
    final  int COLOR_FOCUSED = Color.RED;
	final  int COLOR_UNFOCUSED = Color.DKGRAY;
	public void setBtnSelected(int position){
		mCurSelected = 0;
		for(int i=0; i < mItems.length; i++){
			if(i == position){
				mCurSelected = position;
				mItems[i].selected(true);
			}else{
				mItems[i].selected(false);
			}
		}
		
	}
	
	private void focusNext(boolean goToNext){
		mTextMaintain.setText("");
		
		if(mCurSelected < 0){
			setBtnSelected(0);
			return;
		}
		if(mCurSelected < (mItems.length-1) && goToNext){
			setBtnSelected(mCurSelected + 1);
		}else if(mCurSelected > 0 && !goToNext){
			setBtnSelected(mCurSelected - 1);
		}else{
			setBtnSelected(0);
		}
	}
	
	private void clickButton(){
		FLog.v("clickButton = " + mCurSelected);
		/*int left = -1;
		int top = -1;
		
		switch (mCurSelected) {
		case 4:
			left = mItems[mCurSelected].text.getRight()+20;
			top  = mItems[mCurSelected].text.getTop();
			break;
		case 5:
			left = mItems[mCurSelected].text.getRight()+20;
			top  = mItems[mCurSelected].text.getTop();
			break;
		}
		
		if(left >= 0){
			mImgArrow.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams pm = new RelativeLayout.LayoutParams(60, 60);
			pm.topMargin = top;
			pm.leftMargin = left;
			mImgArrow.setLayoutParams(pm);
			mCurUpdate = mCurSelected;
		}else{
			mImgArrow.setVisibility(View.INVISIBLE);
			mCurUpdate = -1;
		}*/
		
		mCurUpdate = -1;
		if(mCurSelected>=0 && mCurSelected< 4){
			mTextMaintain.setText(mItems[mCurSelected].type+"更换成功!");
			switch(mCurSelected){
			case 0:
				DataNative.setMaintainPPF(FTime.getTimeString("yyyy-MM-dd HH:mm:ss"));
				break;
			case 1:
				DataNative.setMaintainCTO(FTime.getTimeString("yyyy-MM-dd HH:mm:ss"));
				break;
			case 2:
				DataNative.setMaintainUDF(FTime.getTimeString("yyyy-MM-dd HH:mm:ss"));
				break;
			case 3:
				DataNative.setMaintainRO(FTime.getTimeString("yyyy-MM-dd HH:mm:ss"));
				break;
			}
			sendDeviceMaintain();
		}
		
		if(mCurSelected==4 || mCurSelected==5){
			mCurUpdate = mCurSelected;
		}
	}
	
	private void sendDeviceMaintain(){
		new Thread(){
			@Override
			public void run() {
				FUser.sendDeviceMaintain();
			}
		}.start();
	}
	
	private void quitUpdate(){
		mImgArrow.setVisibility(View.INVISIBLE);
		mCurUpdate = -1;
	}
	
	private void updateInfo(boolean bNext){
		switch (mCurSelected) {
		case 4:
			int tempVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			tempVolume += bNext ? 1 : -1;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, tempVolume , 0);
			break;
		case 5:
			int num = DataNative.getDeviceNum();
			num += bNext ? 1 : -1;
			if(num < 0){
				num = 0;
			}
			DataNative.setDeviceNum(num);
			break;
		}
		updateInfo();
	}
	
	private void updateMaintain(){
    	/*mlist.setText(T_MaintainRot,"RO :"+DataNative.getMaintainRO());
    	mlist.setText(T_MaintainPPF,"PPF:"+DataNative.getMaintainPPF());
    	mlist.setText(T_MaintainCTO,"CTO:"+DataNative.getMaintainCTO());
    	mlist.setText(T_MaintainUDF,"UDF:"+DataNative.getMaintainUDF());*/
		for(int i=0; i<4; i++){
			mItems[i].maintain.setTextType(mItems[i].type);
		}
	}
    @Override
    public void onPause() {
    	super.onPause();
    	isVisible = false;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	mCurSelected = -1;
    	isVisible = true;
    	updateInfo();
    	
    	updateMaintain();
    	new updateInfo().start();
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		if(msg.what == MODBUS_ITEM.MSG_MODBUS_INFO){
			updateInfo();
		}
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(KeyEvent.ACTION_UP == event.getAction() ){
//			FLog.v("user key up "+event.getKeyCode());
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				if(mCurUpdate >= 0){
					updateInfo(true);
				}else{
					focusNext(true);
				}
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				if(mCurUpdate >= 0){
					updateInfo(false);
				}else{
					focusNext(false);
				}
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
				clickButton();
			}
			
			if(FData.KEYCODE_WATER_START == event.getKeyCode()){
				
			}
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
				if(mCurUpdate >= 0){
					quitUpdate();
				}else{
					MainActivity.gHandle(MainActivity.MSG_POP_FRAGMENT);
				}
			}
		}
		return false;
	}

	@Override
	public void resetView() {
		// TODO Auto-generated method stub
		
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
