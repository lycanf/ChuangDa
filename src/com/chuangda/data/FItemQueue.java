package com.chuangda.data;

public class FItemQueue {
	public static final int TimeInterval = 500;
	
	public byte[] cmd = null;
	public byte[] cmd_head = null;
	public long createTime ;
	public int  interval ;
	public int tryCmdCount = 1;
	public String name;
	
	public static FItemQueue getDefault(byte[] b, byte[] h){
		FItemQueue item = new FItemQueue();
		item.cmd = b;
		item.cmd_head = h;
		item.createTime = System.currentTimeMillis();
		item.interval = TimeInterval;
		return item;
	}
	
	public FItemQueue() {
		// TODO Auto-generated constructor stub
	}

}
