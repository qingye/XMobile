package org.xmobile.framework.storage.db.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.xmobile.framework.storage.db.DBOpType;

import android.content.Context;

public class DBStruct implements Serializable {

	private static final long serialVersionUID = 6920064515332720037L;
	private Context context = null;
	private int tag = -1;
	private Class<?> mDaoClass = null;
	private DBOpType opType = DBOpType.NONE;
	private ArrayList<String> sqlExec = null;
	private ArrayList<String[]> sqlParams = null;

	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public Class<?> getmDaoClass() {
		return mDaoClass;
	}
	public void setmDaoClass(Class<?> mDaoClass) {
		this.mDaoClass = mDaoClass;
	}
	public DBOpType getOpType() {
		return opType;
	}
	public void setOpType(DBOpType opType) {
		this.opType = opType;
	}
	public ArrayList<String> getSqlExec() {
		return sqlExec;
	}
	public void setSqlExec(ArrayList<String> sqlExec) {
		this.sqlExec = sqlExec;
	}
	public ArrayList<String[]> getSqlParams() {
		return sqlParams;
	}
	public void setSqlParams(ArrayList<String[]> sqlParams) {
		this.sqlParams = sqlParams;
	}

	private DBStruct(){
		sqlExec = new ArrayList<String>();
		sqlParams = new ArrayList<String[]>();
	}
	
	public DBStruct(Context context, int tag, Class<?> clz, DBOpType type, String sql){
		this(context, tag, clz, type, sql, null);
	}
	
	public DBStruct(Context context, int tag, Class<?> clz, DBOpType type, String sql, String[] params){
		this();
		
		this.context = context;
		this.tag = tag;
		mDaoClass = clz;
		opType = type;
		sqlExec.add(sql);
		if(params != null){
			sqlParams.add(params);
		}
	}
	
	public DBStruct(Context context, int tag, Class<?> clz, DBOpType type, ArrayList<String> sql){
		this(context, tag, clz, type, sql, null);
	}
	
	public DBStruct(Context context, int tag, Class<?> clz, DBOpType type, ArrayList<String> sql, ArrayList<String[]> params){
		this.context = context;
		this.tag = tag;
		mDaoClass = clz;
		opType = type;
		sqlExec = sql;
		sqlParams = params;
	} 
}
