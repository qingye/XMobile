package org.xmobile.framework.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.net.DataPacket;
import org.xmobile.framework.net.engine.HttpTools;
import org.xmobile.framework.net.manager.PostResListener;
import org.xmobile.framework.storage.db.DBOpType;
import org.xmobile.framework.storage.db.ISQListener;
import org.xmobile.framework.storage.db.SqlResult;
import org.xmobile.framework.storage.db.base.IDaoBaseSql;
import org.xmobile.framework.utils.CReflection;
import org.xmobile.framework.utils.Log;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

public abstract class BaseService extends Service implements PostResListener, ISQListener {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected final boolean isNetCanUse(){
		if(HttpTools.NETWORK_STATUS_NOT_AVAILABLE == HttpTools.getNetworkStatus(getApplicationContext())){
			Log.d("@ Chris.yang @ say: no available network!");
			return false;
		}
		return true;
	}
	
	/*****************************************************************************
	 * Network Engine Interfaces
	 * 
	 * If an activity has more than one request, then you can set tag to
	 * distinguish the response data for which request.
	 *****************************************************************************/
	protected final <T> void postRequest(String service, T req){
		postRequestWithTag(service, req, 0);
	}
	
	protected final <T> void postRequestWithTag(String service, T req, int tag){
		DataPacket<T> packet = new DataPacket<T>(this, service, req, tag);
		BaseApplication.getInstance().postRequest(packet);
	}
	
	protected final void postRequestCancel(){
		BaseApplication.getInstance().postRequestCancel(this);
	}
	
	@Override
	public void onResult(Object result) {
	}
	
	/***********************************************************************************
	 * DB Interfaces
	 ***********************************************************************************/
	private void checkUiThread(){
		if(Looper.myLooper() != Looper.getMainLooper()){
			new Throwable("call db must in UIThread!");
		}
	}
	
	protected final void dbRequest(int tag, Class<?> clz, DBOpType type, String sql){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql);
	}
	
	protected final void dbRequest(int tag, Class<?> clz, DBOpType type, ArrayList<String> sql){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql);
	}
	
	protected final void dbRequest(int tag, Class<?> clz, DBOpType type, String sql, String[] params){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql, params);
	}
	
	protected final void addDBRequest(int tag, Class<?> clz, DBOpType type, ArrayList<String> sql, ArrayList<String[]> params){
		checkUiThread();
		BaseApplication.getInstance().dbRequest(this, tag, clz, type, sql, params);
	}
	
	@Override
	public void onDbResult(SqlResult sqlResult) {
	}

	/***********************************************************************************
	 * SQLite - Insert into
	 ***********************************************************************************/
	private String genInsertSql(Object obj, HashMap<String, Object> map){
		IDaoBaseSql sql = ServiceHelper.getDaoInstance(obj);
		return sql.insert(map);
	}
	
	public String genInsertSql(Object obj){
		HashMap<String, Object> map = new HashMap<String, Object>();
		CReflection.getAllAttr(obj, obj.getClass(), map);
		return genInsertSql(obj, map);
	}
	
	public ArrayList<String> genInsertSql(ArrayList<Object> list){
		ArrayList<String> sqlist = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i ++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				CReflection.getAllAttr(list.get(i), list.get(i).getClass(), map);
				sqlist.add(genInsertSql(list.get(i), map));
			}
		}
		return sqlist;
	}
	
	/***********************************************************************************
	 * SQLite - Replace into
	 ***********************************************************************************/
	private String genReplaceSql(Object obj, HashMap<String, Object> map){
		IDaoBaseSql sql = ServiceHelper.getDaoInstance(obj);
		return sql.replace(map);
	}
	
	public String genReplaceSql(Object obj){
		HashMap<String, Object> map = new HashMap<String, Object>();
		CReflection.getAllAttr(obj, obj.getClass(), map);
		return genReplaceSql(obj, map);
	}
	
	public ArrayList<String> genReplaceSql(ArrayList<Object> list){
		ArrayList<String> sqlist = new ArrayList<String>();
		for(int i = 0; i < list.size(); i ++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			CReflection.getAllAttr(list.get(i), list.get(i).getClass(), map);
			sqlist.add(genReplaceSql(list.get(i), map));
		}
		return sqlist;
	}
}
