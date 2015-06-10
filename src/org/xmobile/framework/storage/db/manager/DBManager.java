package org.xmobile.framework.storage.db.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.storage.db.DBOpType;
import org.xmobile.framework.storage.db.ISQListener;
import org.xmobile.framework.storage.db.SqlResult;
import org.xmobile.framework.utils.Log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class DBManager {

	private static DBManager mInstance = null;
	private BaseSQLHelper mSqlInstance = null;

	public class DaoHandle{
		public Class<?> mEntity = null;
		public boolean isInUse = false; 
	}
	private HashMap<Class<?>, DaoHandle> mDaoMap = null;

	public class LStruct{
		public ISQListener listener = null;
		public int iUseCount = 0;
	}
	private HashMap<Context, LStruct> mListenerMap = null;
	
	private DBManager(){
		if(mDaoMap == null){
			mDaoMap = new HashMap<Class<?>, DaoHandle>();
		}
		
		if(mSqlInstance == null){
			mSqlInstance = new BaseSQLHelper(BaseApplication.getInstance().getApplicationContext());
		}
		
		if(mListenerMap == null){
			mListenerMap = new HashMap<Context, LStruct>();
		}
		
		if(mWorkerThread == null){
			mWorkerThread = new WorkerThread();
			mWorkerThread.start();
		}
	}
	
	public static DBManager getInstance(){
		if(mInstance == null){
			mInstance = new DBManager();
		}
		return mInstance;
	}

	public void closeDBManager(){
		if(mDaoMap != null){
			mDaoMap.clear();
			mDaoMap = null;
		}

		if(mListenerMap != null){
			mListenerMap.clear();
			mListenerMap = null;
		}
		
		if(mWorkerThread != null){
			WorkerThread temp = mWorkerThread;
			mWorkerThread = null;
			temp.interrupt();
		}
		
		mSqlInstance = null;
		mInstance = null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	private DaoHandle getDaoHandle(Class<?> clz){
		DaoHandle handle = mDaoMap.get(clz);
		if(handle == null){
			handle = new DaoHandle();
			try {
				handle.mEntity = Class.forName(BaseApplication.DB_ENTITY_PATH + clz.getSimpleName().replace("Dao", "Entity"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			mDaoMap.put(clz, handle);
		}
		return handle;
	}
	
	private void putDaoHandle(Class<?> clz, DaoHandle handle){
		mDaoMap.put(clz, handle);
	}
	
	//////////////////////////////////////////// Listener ////////////////////////////////////////////
	private void addListener(Context context){
		LStruct ls = mListenerMap.get(context);
		if(ls == null){
			ls = new LStruct();
			ls.listener = (ISQListener) context;
		}
		ls.iUseCount ++;
		mListenerMap.put(context, ls);
	}
	
	private LStruct getLStruct(Context context){
		LStruct ls = mListenerMap.get(context);
		if(ls != null){
			ls.iUseCount --;
			if(ls.iUseCount == 0){
				mListenerMap.remove(context);
			}
		}
		return ls;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	private int[] mDBPrior = {0, 0, 0, 0};
	private final int PRIOR_DB_ACTIVITY_NONQUERY = 0x0;
	private final int PRIOR_DB_SERVICE_NONQUERY = 0x1;
	private final int PRIOR_DB_ACTIVITY_QUERY = 0x2;
	private final int PRIOR_DB_SERVICE_QUERY = 0x3;
	
	private LinkedList<DBStruct> mQueue = new LinkedList<DBStruct>();
	private WorkerThread mWorkerThread = null;

	public void addDBRequest(Context context, int tag, Class<?> clz, DBOpType type, String sql){
		addDBRequest(context, tag, clz, type, sql, null);
	}
	
	public void addDBRequest(Context context, int tag, Class<?> clz, DBOpType type, ArrayList<String> sql){
		addDBRequest(context, tag, clz, type, sql, null);
	}
	
	public void addDBRequest(Context context, int tag, Class<?> clz, DBOpType type, String sql, String[] params){
		if(sql == null || sql == ""){
			new Throwable("sql can't be null");
		}
		
		ArrayList<String> sqlist = new ArrayList<String>();
		sqlist.add(sql);
		
		ArrayList<String[]> paramslist = new ArrayList<String[]>();
		if(params != null){
			paramslist.add(params);
		}
		
		addDBRequest(context, tag, clz, type, sqlist, paramslist);
	}
	
	public void addDBRequest(Context context, int tag, Class<?> clz, DBOpType type, ArrayList<String> sql, ArrayList<String[]> params){
		if(sql == null || sql.size() == 0){
			new Throwable("sql can't be null");
		}

		mWorkerThread.addNewDBRequest(MSG_ID_INQUEUE, new DBStruct(context, tag, clz, type, sql, params));
	}

	private int getIndex(int prior){
		int index = 0;
		for(int i = 0; i < mDBPrior.length; i ++){
			index += mDBPrior[i];
			if(prior == i){
				break;
			}
		}
		
		return index;
	}
	
	private void inQueue(DBStruct db, boolean bReAdd){
		Context ctx = db.getContext();
		int prior = 0;
		if(ctx instanceof Activity){
			if(db.getOpType() == DBOpType.NONQUERY){
				prior = PRIOR_DB_ACTIVITY_NONQUERY;
			}else{
				prior = PRIOR_DB_ACTIVITY_QUERY;
			}
		}else if(ctx instanceof Service){
			if(db.getOpType() == DBOpType.NONQUERY){
				prior = PRIOR_DB_SERVICE_NONQUERY;
			}else{
				prior = PRIOR_DB_SERVICE_QUERY;
			}
		}else{
			new Throwable("Haven't Realization");
		}

		// Add to Queue and Increase
		mQueue.add(getIndex(prior), db);
		mDBPrior[prior] ++;

		if(!bReAdd){
			addListener(ctx);
		}
	}
	
	private DBStruct deQueue(){
		DBStruct db = null;
		if(mQueue.size() > 0){
			try {
				db = mQueue.removeFirst();
			} catch (NoSuchElementException e) {
				
			}
			for(int i = 0; i < mDBPrior.length; i ++){
				if(mDBPrior[i] != 0){
					mDBPrior[i] --;
					break;
				}
			}
		}
		return db;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	private static final int MSG_ID_SUCCESS = 0x0;
	private static final int MSG_ID_INQUEUE = 0x1000;
	private static final int MSG_ID_REINQUEUE = 0x1001;
	private static final int MSG_ID_DEQUEUE = 0x1002;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_ID_SUCCESS:
				Bundle bundle = msg.getData();
				DBStruct db = (DBStruct) bundle.getSerializable("db");
				SqlResult result = (SqlResult) bundle.getSerializable("result");
				LStruct ls = getLStruct(db.getContext());
				if(ls != null){
					ls.listener.onDbResult(result);
				}
				break;

			default:
				break;
			}
		}
	};
	
	public class WorkerThread extends Thread{
		public Handler mWorkerHandler = null;
		
		private void initHandler(){
			mWorkerHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					boolean bReAdd = true;
					switch(msg.what){
					case MSG_ID_SUCCESS:
						Message m = mHandler.obtainMessage();
						m.what = MSG_ID_SUCCESS;
						m.setData(msg.getData());
						mHandler.sendMessage(m);
						sendEmptyMessage(MSG_ID_DEQUEUE);
						break;
					
					case MSG_ID_INQUEUE:
						bReAdd = false;
					case MSG_ID_REINQUEUE:
						inQueue((DBStruct) msg.obj, bReAdd);
						sendEmptyMessage(MSG_ID_DEQUEUE);
						break;
						
					case MSG_ID_DEQUEUE:
						deQ();
						break;
						
					default:
						break;
					}
				}
			};
		}
		
		private DaoHandle isConfict(DBStruct db){
			DaoHandle handle = getDaoHandle(db.getmDaoClass());
			if(handle == null){
				Log.e("Bad dao !");
			}
			
			if(handle.isInUse){
				Message msg = mWorkerHandler.obtainMessage();
				msg.what = MSG_ID_REINQUEUE;
				msg.obj = db;
				mWorkerHandler.sendMessage(msg);
				handle = null;
			}else{
				handle.isInUse = true;
				putDaoHandle(db.getmDaoClass(), handle);
			}
			return handle;
		}
		
		private void resetDaoHandle(DBStruct db, DaoHandle handle){
			if(handle == null){
				return;
			}
			
			handle.isInUse = false;
			putDaoHandle(db.getmDaoClass(), handle);
		}
		
		private void deQ(){
			if(mQueue.size() == 0){
				return;
			}
			
			DBStruct db = deQueue();
			if(db == null){
				return;
			}
			
			DaoHandle handle = isConfict(db);
			if(handle == null){
				return;
			}
			
			runSQL(db, handle);
		}
		
		private void runSQL(DBStruct db, DaoHandle handle){
			SqlResult result = null;
			switch(db.getOpType()){
			case NONQUERY:
				result = mSqlInstance.operationCUD(handle, db);
				break;
				
			case QUERY:
				result = mSqlInstance.operationQ(handle, db);
				break;
				
			default:
				break;
			}
			
			// reset handle
			resetDaoHandle(db, handle);
			
			Bundle bundle = new Bundle();
			bundle.putSerializable("db", db);
			bundle.putSerializable("result", result);
			Message msg = mWorkerHandler.obtainMessage();
			msg.what = MSG_ID_SUCCESS;
			msg.setData(bundle);
			mWorkerHandler.sendMessage(msg);
		}
		
		public void addNewDBRequest(int what, DBStruct db){
			mWorkerHandler.obtainMessage(MSG_ID_INQUEUE, db).sendToTarget();
		}
		
		@Override
		public void run() {
			Looper.prepare();
			if(mWorkerHandler == null){
				initHandler();
			}
			Looper.loop();
		}
	}
}
