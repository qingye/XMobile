package org.xmobile.framework.net.engine;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class AsyncResponse extends Handler{

	private WeakReference<Handler> mNotifyRef = null;
	public AsyncResponse(Handler notifyHandler){
		mNotifyRef = new WeakReference<Handler>(notifyHandler);
	}

	@Override
	public void handleMessage(Message msg) {
		Handler handler = mNotifyRef.get();
		if(handler == null){
			return;
		}

		switch(msg.what){
		case 0:
			msg.what = 0x02;
			handler.dispatchMessage(msg);
			break;
			
		default:
			break;
		}
	}
}
