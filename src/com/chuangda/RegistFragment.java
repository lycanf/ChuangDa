package com.chuangda;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.data.FUser;
import com.chuangda.net.DataHttp;


public class RegistFragment extends BaseFragment {

	private static final int MSG_TEXT = 6500; 
	private static final int MSG_QR = 6501; 
	
	private ImageView mRegistQr = null;
	private TextView  mRegistMsg = null;
	Bitmap    mQrBmp = null;
	
	public RegistFragment() {
	}
	
    static RegistFragment newInstance() {
    	RegistFragment f = new RegistFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.regist_view, container, false);
    	mRegistQr = (ImageView) v.findViewById(R.id.regist_qr);
    	mRegistMsg = (TextView) v.findViewById(R.id.regist_msg);
        return v;
    }

    @Override
    public void onResume() {
    	super.onResume();
    	new GetRegistQr().start();
    }
	
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case MSG_QR:
			mRegistQr.setImageBitmap(mQrBmp);
			break;
		case MSG_TEXT:
			mRegistMsg.setText((String) msg.obj);
			break;
		}
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
				MainActivity.gHandle(MainActivity.MSG_POP_FRAGMENT);
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
	
	class GetRegistQr extends Thread{
		@Override
		public void run() {
			String ret = null;
			String retResult = "";
			String getRegQr = "";
			getRegQr = "deviceno="+FUser.getDeviceNum()
					+"&cardno="+FUser.cardno;
			ret = DataHttp.sendHttpPost(FUser.urlDeviceReg, getRegQr);
			FLog.v("GetRegistQr:"+ret);
			try {
				JSONObject jsonObject = new JSONObject(ret);
				if(jsonObject.has("result")){
					if(Integer.valueOf(jsonObject.getString("result")) >= 0){
						if(jsonObject.has("qr_code")){
							String url = jsonObject.getString("qr_code");
							mQrBmp = DataHttp.getHttpBitmap(url);
			    			if(mQrBmp == null){
			    				retResult = "¶şÎ¬Âë»ñÈ¡Ê§°Ü";
			    			}else{
			    				retResult = "ÇëÉ¨Âë×¢²á";
			    				MainActivity.gHandle(MSG_QR);
			    			}
						}else{
							retResult = "¶şÎ¬ÂëÇëÇóÊ§°Ü";
						}
					}else{
						retResult = "É¨ÂëÇëÇóÊ§°Ü";
					}
				}
				if(jsonObject.has("tradeno")){
					FUser.tradeno = jsonObject.getString("tradeno");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}finally{
				MainActivity.gHandle(MSG_TEXT,retResult);
			}
		}
	}
}
