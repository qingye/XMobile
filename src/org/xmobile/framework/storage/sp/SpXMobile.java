package org.xmobile.framework.storage.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpXMobile {
	
	private final static String SP_XMobile = "SP_XMobile";
	
	/***************************************************************************************
	 *  Set & Get
	 ***************************************************************************************/
	public static void setValue(Context context, String key, String value){
		SharedPreferences sp=context.getSharedPreferences(SP_XMobile, Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getValue(Context context, String key){
		SharedPreferences sp = context.getSharedPreferences(SP_XMobile, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}
}
