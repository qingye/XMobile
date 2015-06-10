package org.xmobile.framework.net.engine;

import org.xmobile.framework.bean.BaseBean;
import org.xmobile.framework.net.DataPacket;

import android.os.Handler;

public class AsyncRequest implements Runnable{

	private Handler handler = null;
	private DataPacket<?> packet = null;
	
	public AsyncRequest(Handler handler, DataPacket<?> packet){
		this.handler = handler;
		this.packet = packet;
	}

	@Override
	public void run() {

		String method = packet.getRequest().getClass().getSimpleName();
		String json = ((BaseBean)packet.getRequest()).toJson();
		String result = HttpTools.post(
				packet.getContext(), 
				Protocols.getUrl(packet.getService(), method),
				json);
		
		if(handler != null){
			packet.setJson(result);
			handler.obtainMessage(0, packet).sendToTarget();
		}
	}
}
