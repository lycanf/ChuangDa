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
	/*public synchronized void addSingle(FItemQueue item){
		mQueue.clear();
		SingleCmdHead = null;
		mQueue.add(item);
		int needCheck = 0;
		boolean isOK = false;
		for(int i=0; i<TryNum && !isOK; i++){
			needCheck = 0;
			while(needCheck < CheckNum){
				Thread.currentThread();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(item.cmd_head == SingleCmdHead){
					FLog.v("addSingle OK");
					isOK = true;
					break;
				}
				needCheck++;
			}
			if(needCheck >= CheckNum && (i<TryNum-1)){
				FLog.v("addSingle try="+i);
				mQueue.add(item);//one more try
			}else{
				FLog.v("addSingle out");
				break;
			}
		}
		FLog.v("addSingle end "+isOK);
		
	}*/
	
	public void add(FItemQueue item){
		if(item.tryCmdCount > 0){
			mQueue.clear();
		}
		mQueue.add(item);
	}
	
	long TRY_CMD_TIME = 100;
	Thread mThread = new Thread(){
		public void run() {
			Process.setThreadPriority(Thread.MAX_PRIORITY);
			Thread.currentThread();
			while(true){
				try {
					FItemQueue item = mQueue.take();
					FLog.t(item.name+" start ********************* mQueue size="+mQueue.size());
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
								if(tryCount > 3){
									FLog.t("FQueue try="+tryCount+" name="+item.name);
								}
							}
							if(runOk){
								break;
							}else{
								FLog.t("FQueue error !!!!!!!!!!!!!!!! "+item.name);
							}
						}
					}
					mLastRunTime = System.currentTimeMillis();
					Thread.sleep(item.interval);
				} catch (InterruptedException e) {
					FLog.e("FQueue error = "+e.getMessage());
					e.printStackTrace();
				}finally{
				}
			}
		};
	};
}
