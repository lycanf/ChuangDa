package com.chuangda.data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Process;

import com.chuangda.MainActivity;
import com.chuangda.common.FLog;
import com.chuangda.common.HandlePortData;

public class FQueue {
	public static long CMD_TIME ;
	public static byte[] SingleCmdHead;
	
	public long mLastRunTime = System.currentTimeMillis();
	private BlockingQueue<FItemQueue> mQueue = new LinkedBlockingQueue<FItemQueue>(10);
	
	public FQueue() {
		mThread.start();
	}

	public void clearQueue(){
		mQueue.clear();
	}
	
	final int CheckNum = 10;
	final int TryNum = 3;
	
	public void add(FItemQueue item){
		if(item.tryCmdCount > 1){
//			FLog.v("FQueue clear     "+item.name);
			mQueue.clear();
		}
//		FLog.t("FQueue add     "+item.name);
		mQueue.add(item);
	}
	
	long TRY_CMD_TIME = 100;
	Thread mThread = new Thread(){
		public void run() {
			Process.setThreadPriority(Thread.MAX_PRIORITY);
			Thread.currentThread();
			long curTime = System.currentTimeMillis();
			while(true){
				try {
					FItemQueue item = mQueue.take();
//					FLog.v(item.name+" start "+item.name+" ******** mQueue size="+mQueue.size());
					SingleCmdHead = null;
					if(null != item.cmd){
						CMD_TIME = item.createTime;
//						FLog.v("sendData="+item.cmd.toString());
						for(int i=0; i< item.tryCmdCount; i++){
							int tryCount = 0;
							boolean runOk = false;
							while(tryCount < 5){
								MainActivity.sendData(item.cmd);
								Thread.sleep(TRY_CMD_TIME);
								if(item.cmd_head == SingleCmdHead){
									runOk = true;
//									FLog.t("FQueue getAnswerState out");
									break;
								}
								tryCount++;
							}
							if(runOk){
//								FLog.t("FQueue do command "+item.name);
								break;
							}else{
								FLog.e("FQueue error !!!!!!!!!!!!!!!! "+item.name);
								if(System.currentTimeMillis() - curTime > 5000){
									curTime = System.currentTimeMillis();
									MainActivity.showToast("µ×²ã¶ÁÈ¡³ö´í "+item.name);
								}
							}
						}
					}
					mLastRunTime = System.currentTimeMillis();
				} catch (InterruptedException e) {
					FLog.e("FQueue error = "+e.getMessage());
					e.printStackTrace();
				}finally{
				}
			}
		};
	};
}
