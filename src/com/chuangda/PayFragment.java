package com.chuangda;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.data.FUser;
import com.chuangda.net.DataHttp;




public class PayFragment extends BaseFragment {

	public static final int MSG_TEXT = 3500;
	public static final int MSG_QR = 3501;
	class ItemMoney{
		public TextView textView = null;
		public int res = 0;
		public int money = 0;
		public ItemMoney(int r, int m){
			res = r;
			money = m;
		}
	}
	private ItemMoney[] mItemMoneys = {
		new ItemMoney(R.id.money10,10), 
		new ItemMoney(R.id.money50,50), 
		new ItemMoney(R.id.money100,100), 
		new ItemMoney(R.id.money200,200), 
		new ItemMoney(R.id.money300,300), 
		new ItemMoney(R.id.money500,500), 
		new ItemMoney(R.id.money_pay,120), 
		new ItemMoney(R.id.money_msg,0), 
	};
	
	int mCurSelected = 0;
	boolean isConnecting = false;
	ImageView mQrImg ;
	Bitmap    mQrBmp = null;
	
	
	public PayFragment() {
	}
	
    static PayFragment newInstance() {
    	PayFragment f = new PayFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.pay_view, container, false);
    	for(int i=0; i<mItemMoneys.length; i++){
    		mItemMoneys[i].textView = (TextView) v.findViewById(mItemMoneys[i].res);
    		mItemMoneys[i].textView.setOnClickListener(mOnClickListener);
    		mItemMoneys[i].textView.setTag(i);
    	}
    	mQrImg = (ImageView) v.findViewById(R.id.qr_img);
    	mQrImg.setVisibility(View.INVISIBLE);
        return v;
    }

    public boolean isPaying(){
    	return isConnecting ;
    }
    
    public void setMessage(String str){
    	mItemMoneys[7].textView.setVisibility(View.VISIBLE);
    	mItemMoneys[7].textView.setText(str);
    	mQrImg.setVisibility(View.INVISIBLE);
    }
    
    public void setQrImg(){
    	new Thread(){
    		public void run() {
    			isConnecting = true;
    			String ret = null;
    			FUser.amount = mItemMoneys[mCurSelected].money + ".00";
    			//test
    			if(mCurSelected == 3){
    				FUser.amount = "0.01";
    			}
    			FLog.v(FUser.amount);
    			ret = DataHttp.sendHttpPost(FUser.urlPay, FUser.getQr());
    			String qrUrl = null;
    			try {
					JSONObject jsonObject = new JSONObject(ret);
					if(jsonObject.has("qr_code")){
						qrUrl = jsonObject.getString("qr_code");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
    			if(qrUrl == null){
    				MainActivity.gUIHandler.obtainMessage(MSG_TEXT, "获取二维码失败").sendToTarget();
    				isConnecting = false;
    				FLog.v("获取二维码失败");
    				return;
    			}else{
    				MainActivity.gUIHandler.obtainMessage(MSG_TEXT, 
    						"为您充值 "+mItemMoneys[mCurSelected].money+" 元\n"
    				+"生成订单中。。。").sendToTarget();
    			}
    			FLog.v("qrUrl="+qrUrl);
    			mQrBmp = DataHttp.getHttpBitmap(qrUrl);
    			if(mQrBmp == null){
    				MainActivity.gUIHandler.obtainMessage(MSG_TEXT, "获取二维码失败").sendToTarget();
    			}else{
    				MainActivity.gUIHandler.obtainMessage(MSG_QR).sendToTarget();
    			}
    			FLog.v("mQrBmp="+mQrBmp);
    			isConnecting = false;
    		};
    	}.start();
    }
    
    OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int p = (Integer) v.getTag();
			if(p<0 || p > 6){
				return;
			}
			mCurSelected = p;
			doSelected();
		}
	};
	
    
    @Override
    public void onResume() {
    	super.onResume();
    	setSelected();
//    	setQrImg();
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		switch(msg.what){
		case MSG_TEXT:
			setMessage((String) msg.obj);
			break;
		case MSG_QR:
			mItemMoneys[7].textView.setVisibility(View.INVISIBLE);
			mQrImg.setVisibility(View.VISIBLE);
			mQrImg.setImageBitmap(mQrBmp);
			break;
		}
	}
	
	public void doSelected(){
		if(0<=mCurSelected && 6>=mCurSelected){
			setMessage("为您充值 "+mItemMoneys[mCurSelected].money+" 元\n"+"连接中。。。");
			setQrImg();
		}
	}

	public void setSelected(){
		if(mCurSelected < 0 || mCurSelected > 6){
			return;
		}
		for(int i=0 ; i<7; i++){
			if(i==mCurSelected){
				mItemMoneys[i].textView.setBackgroundColor(Color.RED);
			}else{
				mItemMoneys[i].textView.setBackgroundColor(Color.GREEN);
			}
		}
		if(0<=mCurSelected && 6>=mCurSelected){
			setMessage("按确定充值 "+mItemMoneys[mCurSelected].money+" 元");
		}
	}
	private void focusNext(boolean goToNext){
//		FLog.t("cur="+mCurSelected+" "+goToNext);
		if(goToNext){
			if(mCurSelected < 6){
				mCurSelected++;
			}
		}else{
			if(mCurSelected > 0 && mCurSelected < 6){
				mCurSelected--;
			}
		}
		setSelected();
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_UP == event.getAction()){
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				focusNext(false);
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				focusNext(true);
			}
			if(FData.KEYCODE_ENTER == event.getKeyCode()){
				doSelected();
			}
			if(FData.KEYCODE_WATER_STOP == event.getKeyCode()){
				if(mCurSelected == 6){
					mCurSelected = 5;
					setSelected();
				}else{
					MainActivity.gUIHandler.obtainMessage(MainActivity.MSG_POP_FRAGMENT).sendToTarget();
				}
				
			}
		}
		
		if(KeyEvent.ACTION_DOWN == event.getAction() && mCurSelected == 6){
			int customMoney = mItemMoneys[6].money;
			if(FData.KEYCODE_PRE == event.getKeyCode()){
				customMoney--;
			}
			if(FData.KEYCODE_NEXT == event.getKeyCode()){
				customMoney++;
			}
			mItemMoneys[6].money = customMoney;
			String moneyStr = "-"+customMoney+"+";
			mItemMoneys[6].textView.setText(moneyStr);
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
