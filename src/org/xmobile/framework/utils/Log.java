package org.xmobile.framework.utils;

public final class Log {
	
	private static final String TAG = "HealthCenter";
	private static boolean mbLoggable = true; 
	
	public static void setLoggable(boolean bLoggable){
		mbLoggable = bLoggable;
	}
	
	public static boolean isLoggable(){
		return mbLoggable;
	}
	
	public static boolean isDebuggable(){
		return mbLoggable;
	}
	
	public static int w(String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.w(TAG, msg);
    }
    public static int w(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.w(tag, msg);
    }
    public static int w(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.w(tag, msg, tr);
    }
    
    public static int v(String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.v(TAG, msg);
    }
    public static int v(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.v(tag, msg);
    }
    public static int v(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.v(tag, msg, tr);
    }
	
    public static int i(String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.i(TAG, msg);
    }
    public static int i(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.i(tag, msg);
    }
    public static int i(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.i(tag, msg, tr);
    }
    
    public static int d(String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.d(TAG, msg);
    }
    public static int d(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.d(tag, msg);
    }
    public static int d(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.d(tag, msg, tr);
    }
    
    public static int e(String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.e(TAG, msg);
    }
    public static int e(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.e(tag, msg);
    }
    public static int e(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.e(tag, msg, tr);
    }
}

