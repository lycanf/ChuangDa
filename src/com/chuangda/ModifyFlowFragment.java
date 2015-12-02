package com.chuangda;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ModifyFlowFragment extends BaseFragment {

	private ListView mListView ;
	
	public ModifyFlowFragment() {
	}
	
    static ModifyFlowFragment newInstance() {
    	ModifyFlowFragment f = new ModifyFlowFragment();
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
