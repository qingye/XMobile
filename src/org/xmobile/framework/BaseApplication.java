package org.xmobile.framework;

import java.util.ArrayList;

import org.xmobile.framework.cache.AsyncImageLoader;
import org.xmobile.framework.net.manager.RootManager;
import org.xmobile.framework.storage.db.DBOpType;
import org.xmobile.framework.storage.db.manager.DBManager;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;

public class BaseApplication extends Application {
	public static final String APP_PACKAGE_NAME = "org.xmobile.framework";
	public static final int SDK_VER_CODE = 1;

	// Reflection path
	public final static String DB_DAO_PATH = APP_PACKAGE_NAME + ".storage.db.dao.";
	public static final String DB_ENTITY_PATH = APP_PACKAGE_NAME + ".storage.db.entity.";
	public final static String BEAN_RESPONSE_PATH = APP_PACKAGE_NAME + ".bean.resp.";

	// Action
	public static final String ACTION_CONNECTION= APP_PACKAGE_NAME + ".intent.CONNECTION";
	
	// SDcard Path
	public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String XMOBILE_ROOT_DIR = SDCARD + "/xmobile";
	public static final String FILE_PATH_DIR = XMOBILE_ROOT_DIR + "/File";
	public static final String IMAGE_PATH_DIR = XMOBILE_ROOT_DIR + "/Image";
	
	/*****************************************************************************
	 * Application's Instance
	 *****************************************************************************/
	private static BaseApplication mInstance = null;
	private static AsyncImageLoader mAyncImageLoader = null;
	private ArrayList<Activity> actList = null;
	
	public static synchronized BaseApplication getInstance(){
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		actList = new ArrayList<Activity>();
		DBManager.getInstance();
		mAyncImageLoader = new AsyncImageLoader(this);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	/*****************************************************************************
	 * Network Engine Interfaces
	 *****************************************************************************/
	public void postRequest(Object packet){
		RootManager.getInstance().postRequest(packet);
	}
	public void postRequestCancel(Context context){
		RootManager.getInstance().postRequestCancel(context);
	}
	
	/*****************************************************************************
	 * DB Interfaces
	 *****************************************************************************/
	public void dbRequest(Context context, int tag, Class<?> clz, DBOpType type, String sql){
		DBManager.getInstance().addDBRequest(context, tag, clz, type, sql);
	}
	public void dbRequest(Context context, int tag, Class<?> clz, DBOpType type, ArrayList<String> sql){
		DBManager.getInstance().addDBRequest(context, tag, clz, type, sql);
	}
	public void dbRequest(Context context, int tag, Class<?> clz, DBOpType type, String sql, String[] params){
		DBManager.getInstance().addDBRequest(context, tag, clz, type, sql, params);
	}
	public void dbRequest(Context context, int tag, Class<?> clz, DBOpType type, ArrayList<String> sql, ArrayList<String[]> params){
		DBManager.getInstance().addDBRequest(context, tag, clz, type, sql, params);
	}
	
	/*****************************************************************************
	 * Load bitmap for image view
	 *****************************************************************************/
	public Object loadBitmap(Object context, Object view, String url){
		return mAyncImageLoader.loadBitmap(context, view, url);
	}
	
	/*****************************************************************************
	 * Exit the whole APK
	 *****************************************************************************/
	public void quit() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);

		for (Activity act : actList) {
			if (!act.isFinishing())
				act.finish();
		}
		actList.clear();
		
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
		System.exit(0);
	}

	/*****************************************************************************
	 * Add to Activity list
	 *****************************************************************************/
	public void addActivitToStack(Activity act) {
		for(int i = 0; i < actList.size(); i ++){
			if(actList.get(i).equals(act)){
				return;
			}
		}
		actList.add(act);
	}
	
	/*****************************************************************************
	 * Remove from Activity list
	 *****************************************************************************/
	public void removeActivityToStack(Activity act){
		for(int i = 0; i < actList.size(); i ++){
			if(actList.get(i).equals(act)){
				actList.remove(i);
			}
		}
	}
	
	/*****************************************************************************
	 * Remove from Activity list by name
	 * Input: Simple class name
	 *****************************************************************************/
	public void removeActivityToStackByName(String activityName){
		for(Activity activity : actList){
			String pcName = activity.getClass().getPackage().getName();
			String className = activity.getClass().getName().replace(pcName.concat("."), "");
			if(activityName.equals(className)){
				activity.finish();
			}
		}
	}
}
