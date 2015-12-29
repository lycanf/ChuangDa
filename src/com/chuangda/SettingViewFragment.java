package com.chuangda;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.chuangda.MainActivity.ViewFragment;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;

public class SettingViewFragment extends BaseFragment {

	public static final int ITEM_TEXT_SIZE = 40;
	
	private ListView mListView ;
	private Context  mContext;
	SettingItem[] SettingItems = {
		new SettingItem(R.string.msg_device_info,ViewFragment.DEVICE_INFO),
		new SettingItem(R.string.msg_calibrate_flow,ViewFragment.CALIBRATE_FLOW),
		new SettingItem(R.string.msg_water_price,ViewFragment.WATER_PRICE),
		new SettingItem(R.string.msg_modify_password,ViewFragment.CHANGE_PASSWD),
	};
	
	private int mCurSelected = 0;
	
	public SettingViewFragment() {
	}
	
    static SettingViewFragment newInstance() {
    	SettingViewFragment f = new SettingViewFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	mContext = getActivity();
    	View v = inflater.inflate(R.layout.setting_view, container, false);
    	mListView =  (ListView) v.findViewById(R.id.setting_list);
    	mListView.setDivider(null);
    	mListView.setAdapter(myAdapter);
        return v;
    }

    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void handleUI(Message msg) {
		// TODO Auto-generated method stub
		
	}
	
	BaseAdapter myAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RelativeLayout layout = new RelativeLayout(mContext);
			RelativeLayout.LayoutParams pm = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			pm.topMargin = 4;
			
			Button view = new Button(mContext);
			view.setText(getItemString(position));
			view.setTextSize(ITEM_TEXT_SIZE);
			view.setGravity(Gravity.CENTER);
			view.setTag(position);
			view.setOnClickListener(mOnClickListener);
			SettingItems[position].btn = view;
			setBtnSelected(mCurSelected);
			
			layout.addView(view, pm);
			
			return layout;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return SettingItems[position];
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return SettingItems.length;
		}
	};
	
	private String getItemString(int position){
		String ret = "";
		Activity act = getActivity();
		if(null != act){
			Resources res = act.getResources();
			if(null != res){
				ret = res.getString(SettingItems[position].text);
			}
		}
		return ret;
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			setBtnSelected(position);
			clickButton(position);
		}
	};
	
	public void setBtnSelected(int position){
		mCurSelected = 0;
		for(int i=0; i < SettingItems.length; i++){
			if(SettingItems[i].btn == null){
				continue;
			}
			if(i == position){
				SettingItems[i].btn.setBackgroundColor(FData.COLOR_FOCUSED);
				mCurSelected = position;
			}else{
				SettingItems[i].btn.setBackgroundColor(FData.COLOR_UNFOCUSED);
			}
		}
	}
	
	class SettingItem{
		public int text;
		public ViewFragment fragment;
		public Button btn = null;
		public SettingItem(int t, ViewFragment f){
			text = t;
			fragment = f;
		}
	}
	
	private void focusNext(boolean goToNext){
		if(mCurSelected < (SettingItems.length-1) && goToNext){
			setBtnSelected(mCurSelected + 1);
		}else if(mCurSelected > 0 && !goToNext){
			setBtnSelected(mCurSelected - 1);
		}else{
			setBtnSelected(0);
		}
	}
	
	private void clickButton(int position){
		FLog.v("clickButton = "+mCurSelected);
		if(position >= SettingItems.length){
			return;
		}
		ViewFragment fragment = SettingItems[mCurSelected].fragment;
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_CHANGE_FRAGMENT,fragment);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(FData.KEYCODE_WATER_START == event.getKeyCode()){
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
				clickButton(mCurSelected);
			}
		}
		return false;
	}
	
	
	private void test(){
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_CHANGE_FRAGMENT,ViewFragment.USER);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
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
