package com.chuangda.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.chuangda.MainActivity;
import com.chuangda.common.FData;
import com.chuangda.common.FLog;
import com.chuangda.data.FUser;

public class VideoPlay extends VideoView implements OnErrorListener, OnPreparedListener, OnCompletionListener{

	Context mContext = null;
	ArrayList<String> mPlayList = new ArrayList<String>();
	String mPlayingPath = null;
	MediaController mMediaController = null;
	public static List<String> DEL_LIST = new ArrayList<String>();
	
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
		setOnCompletionListener(this);
		/*mMediaController = new MediaController(mContext);
		setMediaController(mMediaController);
		mMediaController.setMediaPlayer(this); */
		
		setVideoList(true);
	}
	
	void setVideoList(boolean bAll){
		File videoFile = new File(FData.VIDEO_PATH);
		if(!videoFile.exists() && !videoFile.mkdirs()){
			return ;
		}
		for(String filename : mPlayList){
			for(String delName : DEL_LIST){
				if(filename.contains(delName)){
					File delFile = new File(filename);
					if(delFile.exists()){
						delFile.delete();
						FLog.m("del video "+filename);
					}
				}
			}
		}

		for(String delName : DEL_LIST){
			mPlayList.remove(delName);
		}
		if(DEL_LIST != null && DEL_LIST.size()>0){
			Thread sendList = new Thread(){
				@Override
				public void run() {
					FUser.sendVideoList();
				}
			};
			sendList.start();
			
		}
		DEL_LIST.clear();
		
		if(bAll){
			String[] fileList = null; 
			fileList = videoFile.list();
			for(String s : fileList){
//				FLog.v("setVideoList="+FData.VIDEO_PATH+s);
				String tempS = FData.VIDEO_PATH+s;
				if(!mPlayList.contains(tempS)){
					mPlayList.add(FData.VIDEO_PATH+s);
				}
			}
			if(mPlayList.size() > 0){
				Collections.sort(mPlayList); 
				mPlayingPath = mPlayList.get(0);
			}
		}else{
			
		}
	}
	
	private void rePlay(){
		MainActivity.gHandle(MainActivity.MSG_PLAY_NEXT);
	}
	
	public int getVideoCount(){
		int ret = 0;
		if(mPlayList !=null){
			ret = mPlayList.size();
		}
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_VIDEO_COUNT,ret,0);
		MainActivity.gUIHandler.removeMessages(MainActivity.MSG_VIDEO_COUNT);
		MainActivity.gUIHandler.sendMessage(msg);
//		FLog.v("getVideoCount="+ret);
		return ret;
	}
	
	private void sendVideoCount(int ret){
		Message msg = MainActivity.gUIHandler.obtainMessage(
				MainActivity.MSG_VIDEO_COUNT,ret,0);
		MainActivity.gUIHandler.removeMessages(MainActivity.MSG_VIDEO_COUNT);
		MainActivity.gUIHandler.sendMessage(msg);
	}

	public void playTest(){
		Uri uri = Uri.parse("rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp");
//		setVideoURI(uri);
		setVideoPath("/mnt/sdcard/video/test1.mp4");
		start();
	}
	
	public boolean playNext(){
		int videoCount = getVideoCount();
		sendVideoCount(videoCount);
		if(videoCount <= 0){
			setVideoList(true);
			MainActivity.gUIHandler.sendEmptyMessageDelayed(MainActivity.MSG_PLAY_NEXT, 1000);
			return false;
		}
		if(TextUtils.isEmpty(mPlayingPath)){
			mPlayingPath = mPlayList.get(0);
		}
		int position = mPlayList.indexOf(mPlayingPath);
		FLog.m("playNext position="+position+" now="+mPlayingPath);
		position = Math.max(position, 0);
		if(position == 0){
			setVideoList(true);
		}
		position = (position+1)%mPlayList.size();
		
		mPlayingPath = mPlayList.get(position);
//		FLog.v("playNext position="+position+" play="+mPlayingPath);
		
		File videoFile = new File(mPlayingPath);
//		FLog.v("playNext canRead "+videoFile.canRead());
		
		if(!videoFile.exists()){
			FLog.v("no file="+mPlayingPath);
			mPlayList.remove(mPlayingPath);
			playNext();
		}
		setVideoPath(mPlayingPath);
		start();
		
		return true;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		FLog.v("play error ! what="+what+" extra="+extra);
		if(null != mPlayingPath ){
			File delFile = new File(mPlayingPath);
//			FLog.v("delFile="+mPlayingPath);
			if(delFile.exists()){
				delFile.delete();
				FLog.m("play error ! what="+what+" del="+mPlayingPath);
			}
		}
		setVideoList(true);
		rePlay();
		return true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
//		FLog.v("play onPrepared getVideoWidth="+mp.getVideoWidth());
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
//		FLog.v("video onCompletion="+mPlayingPath);
		rePlay();
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		// TODO Auto-generated method stub
		super.onWindowVisibilityChanged(visibility);
		FLog.v("onWindowVisibilityChanged "+visibility);
		if(View.VISIBLE == visibility){
//			setVideoList(true);
			playNext();
		}else{
			stopPlayback();
		}
	}
	
}
