package org.xmobile.framework.receiver;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)){
			if(Utils.getNetworkStatus(context) != null){
				Intent it = new Intent(BaseApplication.ACTION_CONNECTION);
				context.sendBroadcast(it);
			}
		}
	}

}
