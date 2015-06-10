package org.xmobile.framework.storage.db.manager;

import java.util.ArrayList;

import org.xmobile.framework.storage.db.SqlResult;
import org.xmobile.framework.storage.db.base.EntityBaseObject;
import org.xmobile.framework.storage.db.manager.DBManager.DaoHandle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseSQLHelper {

	private SQLiteOpenHelper mDbHelper = null;
	private SqlReflect mSqlReflectInstance = null;
	
	public BaseSQLHelper(Context context){
		if(mDbHelper == null){
			mDbHelper = new DBLocalHelper(context);
		}
	}
	
	private SqlReflect getSqlReflectInstance(){
		if(mSqlReflectInstance == null){
			mSqlReflectInstance = new SqlReflect();
		}
		return mSqlReflectInstance;
	}
	
	///////////////////////////////// Readable & Writable /////////////////////////////////
	private final SQLiteDatabase getReadableDB() {
		return mDbHelper.getReadableDatabase();
	}
	
	private final SQLiteDatabase getWrittableDB() {
		return mDbHelper.getWritableDatabase();
	}

	///////////////////////////////////////////////////////////////////////////////////////
	private final void closeDB(SQLiteDatabase db) {
		if (db != null && db.isOpen()){
			try {
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closeCursorAndDB(Cursor c, SQLiteDatabase db) {
    	try {
    		if (c != null && !c.isClosed()) {
    			c.close();
    		}
    		closeDB(db);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
	public final void closeHelp() {
		if (mDbHelper != null){
			mDbHelper.close();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Insert / Update / Delete
	public SqlResult operationCUD(DaoHandle handle, DBStruct db){
		boolean ret = true;
		SQLiteDatabase mDb = getWrittableDB();
		try {
			mDb.beginTransaction();
			for(String sql : db.getSqlExec()){
				mDb.execSQL(sql);
			}
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		} finally {
			mDb.endTransaction();
			closeDB(mDb);
		}
		
		return new SqlResult(db.getTag(), ret);
	}
	
	// Query
	public SqlResult operationQ(DaoHandle handle, DBStruct db){
		SQLiteDatabase mDb = getReadableDB();
		
		String[] param = null;
		if(db.getSqlParams().size() > 0){
			param = db.getSqlParams().get(0);
		}

		Cursor c = mDb.rawQuery(db.getSqlExec().get(0), param);
		SqlReflect sr = getSqlReflectInstance();
		ArrayList<EntityBaseObject> list = sr.toList(c, handle.mEntity);
		closeCursorAndDB(c, mDb);
		
		return new SqlResult(db.getTag(), true, list);
	}
}
