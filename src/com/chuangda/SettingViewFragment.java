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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.chuangda.MainActivity.ViewFragment;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;

public class SettingViewFragment extends BaseFragment {

	public static final int ITEM_TEXT_SIZE = 40;
	
	private ListView mListView ;
	private Context  mContext;
	SettingItem[] SettingItems = {
		new SettingItem(R.string.msg_modify_password,ViewFragment.CHANGE_PASSWD),
		new SettingItem(R.string.msg_calibrate_flow,ViewFragment.CALIBRATE_FLOW),
		new SettingItem(R.string.msg_water_price,ViewFragment.WATER_PRICE),
		new SettingItem(R.string.msg_device_info,ViewFragment.DEVICE_INFO),
	};
	
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
			Button view = new Button(mContext);
			view.setText(getItemString(position));
			view.setTextSize(ITEM_TEXT_SIZE);
			view.setGravity(Gravity.CENTER);
			view.setTag(position);
			SettingItems[position].btn = view;
			
			if(0 == position){
				view.requestFocus();
			}
			return view;
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
	
	class SettingItem{
		public int text;
		public ViewFragment fragment;
		public Button btn;
		public SettingItem(int t, ViewFragment f){
			text = t;
			fragment = f;
		}
	}
	
	private void focusNext(boolean goToNext){
		int position = 0;
		for(; position < SettingItems.length; position++){
			if(SettingItems[position].btn.isFocused()){
				break;
			}
		}
		if(goToNext && position < (SettingItems.length-1)){
			SettingItems[++position].btn.requestFocus();
		}else if(!goToNext && position > 0){
			SettingItems[--position].btn.requestFocus();
		}else{
			SettingItems[0].btn.requestFocus();
		}
		
	}
	
	private void clickButton(){
		int position = 0;
		for(; position < SettingItems.length; position++){
			if(SettingItems[position].btn.isFocused()){
				break;
			}
		}
		if(position >= SettingItems.length){
			return;
		}
		FLog.v("item = "+position);
		ViewFragment fragment = SettingItems[position].fragment;
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_CHANGE_FRAGMENT,fragment);
		
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
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
				MainActivity.MSG_CHANGE_FRAGMENT,ViewFragment.USER);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 10);
	}
}
