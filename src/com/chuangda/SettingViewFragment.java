package com.chuangda;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.chuangda.MainActivity.ViewFragment;
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
			view.setOnClickListener(mOnClickListener);
			view.setTag(position);
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
	
	OnClickListener mOnClickListener = new OnClickListener(){
		
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			FLog.v("item = "+position);
		}
	};
	
	class SettingItem{
		public int text;
		public ViewFragment fragment;
		public SettingItem(int t, ViewFragment f){
			text = t;
			fragment = f;
		}
	}
}
