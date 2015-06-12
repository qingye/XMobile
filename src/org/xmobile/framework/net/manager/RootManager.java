package org.xmobile.framework.net.manager;

import org.xmobile.framework.net.DataPacket;
import org.xmobile.framework.net.engine.AsyncNetEngine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class RootManager{

	private static final int MSG_ID_NET_REQ 		= 0x01;
	private static final int MSG_ID_NET_RESP 		= 0x02;
	private static final int MSG_ID_NET_CANCEL 		= 0x03;
	private static final int MSG_ID_NET_CANCEL_ALL	= 0x04;

	static class ManagerHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {

			switch(msg.what){
			case MSG_ID_NET_REQ:
				DataPacket<?> packet = (DataPacket<?>) msg.obj;
				AsyncNetEngine.getInstance().addTask(packet.getContext(), this, packet);
				break;

			case MSG_ID_NET_CANCEL:
				AsyncNetEngine.getInstance().cancelTask((Context)msg.obj, true);
				break;
				
			case MSG_ID_NET_CANCEL_ALL:
				AsyncNetEngine.getInstance().cancelAllTask();
				break;
				
			default:
				super.handleMessage(msg);
			}
		}
	}
	
	private ManagerHandler mHandler = new ManagerHandler(){
		@Override
		public void handleMessage(Message msg) {

			switch(msg.what){
			case MSG_ID_NET_RESP:
				onResult((DataPacket<?>) msg.obj);
				break;
				
			default:
				super.handleMessage(msg);
			}
		}
	};

	private void onResult(DataPacket<?> packet){
		Parser.dataParser(packet);
		((PostResListener) packet.getContext()).onResult((Object)packet);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	private static RootManager mInstance = null;
	public synchronized static RootManager getInstance(){
		if(mInstance == null){
			mInstance = new RootManager();
		}
		return mInstance;
	}
	
	private RootManager(){
	}
	
	public void postRequest(Object obj){
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_ID_NET_REQ;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}
	public void postRequestCancel(Context context){
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_ID_NET_CANCEL;
		msg.obj = context;
		mHandler.sendMessage(msg);
	}
	public void postRequestCancelAll(){
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_ID_NET_CANCEL_ALL;
		mHandler.sendMessage(msg);
	}
}
