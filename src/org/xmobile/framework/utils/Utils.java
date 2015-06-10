package org.xmobile.framework.utils;

import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public static String getGUID(){
		String guid = UUID.randomUUID().toString();
		return guid;
	}
	
	public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }
	
	public static String getLocalLanguage(){
		String lan = Locale.getDefault().toString().toLowerCase(Locale.getDefault());
		return lan.replace('_', '-');
	}
	
	public static String getOSVersion(){
		return android.os.Build.VERSION.RELEASE;
	}
	
	public static String getNetworkStatus(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null){
			if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
				return "wifi";
			}else if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
				return "gprs"; // 2G/2.5G/3G
			}
		}
		return null;
	}
}
