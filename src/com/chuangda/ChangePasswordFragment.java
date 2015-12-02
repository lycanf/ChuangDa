package com.chuangda;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ChangePasswordFragment extends BaseFragment {

	private ListView mListView ;
	
	public ChangePasswordFragment() {
	}
	
    static ChangePasswordFragment newInstance() {
    	ChangePasswordFragment f = new ChangePasswordFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	mListView = new ListView(getActivity());
        return mListView;
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
}
