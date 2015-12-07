package com.chuangda.widgets;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.VideoView;

import com.chuangda.MainActivity;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;

public class VideoPlay extends VideoView implements OnErrorListener, OnPreparedListener{

	Context mContext = null;
	ArrayList<String> mPlayList = null;
	String mPlayPath = null;
	MediaController mMediaController = null;
	
	public VideoPlay(Context context) {
		super(context);
		init(context);
	}

	public VideoPlay(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoPlay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context ctx){
		mContext = ctx;
		setOnErrorListener(this);
		setOnPreparedListener(this);
		/*mMediaController = new MediaController(mContext);
		setMediaController(mMediaController);
		mMediaController.setMediaPlayer(this); */
		
		setVideoList();
	}
	
	void setVideoList(){
		File videoFile = new File(FData.VIDEO_PATH);
		if(!videoFile.exists() && !videoFile.mkdirs()){
			return ;
		}
		mPlayList = new ArrayList<String>();
		String[] fileList = null; 
		fileList = videoFile.list();
		for(String s : fileList){
			FLog.v("setVideoList="+FData.VIDEO_PATH+s);
			mPlayList.add(FData.VIDEO_PATH+s);
		}
	}
	
	public int getVideoCount(){
		int ret = 0;
		if(mPlayList != null){
			mPlayList.clear();
			mPlayList = null;
		}
		setVideoList();
		if(mPlayList != null){
			ret = mPlayList.size();
		}
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_VIDEO_COUNT,ret,0);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 100);
		FLog.v("getVideoCount="+ret);
		return ret;
	}

	void playContinue(){
		setVideoList();
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_PLAY_NEXT);
		MainActivity.gUIHandler.sendMessageDelayed(msg, 100);
	}
	
	public void playTest(){
		Uri uri = Uri.parse("rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp");
//		setVideoURI(uri);
		setVideoPath("/mnt/sdcard/video/test1.mp4");
		start();
	}
	
	public void playNext(){
		if(getVideoCount() <= 0){
			return;
		}
		if(TextUtils.isEmpty(mPlayPath)){
			mPlayPath = mPlayList.get(0);
		}
		FLog.v("playNext now="+mPlayPath);
		int position = mPlayList.indexOf(mPlayPath);
		position = (position+1)%mPlayList.size();
		
		mPlayPath = mPlayList.get(position);
		FLog.v("playNext play="+mPlayPath);
		
		File videoFile = new File(mPlayPath);
		if(!videoFile.exists()){
			FLog.v("no file="+mPlayPath);
			playContinue();
			return;
		}
		
		setVideoPath(mPlayPath);
		start();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		FLog.v("play error ! what="+what+" extra="+extra);
		if(null != mPlayPath ){
			File delFile = new File(mPlayPath);
			FLog.v("delFile="+mPlayPath);
			if(delFile.exists()){
				delFile.delete();
			}
		}
//		playContinue();
		return true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		FLog.v("play onPrepared getVideoWidth="+mp.getVideoWidth());
	}
	
}
