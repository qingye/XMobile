package org.xmobile.framework.service;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.bean.BaseBean;
import org.xmobile.framework.storage.db.base.EntityBaseObject;
import org.xmobile.framework.storage.db.base.IDaoBaseSql;
import org.xmobile.framework.utils.CReflection;

public class ServiceHelper {

	public static final String[] Upload = {
	};
	
	public static final String[] Query = {
	};
	/*******************************************************************************
	 * DAO Instance
	 *******************************************************************************/
	public static IDaoBaseSql getDaoInstance(Object obj){
		return getDaoInstance(obj.getClass().getSimpleName());
	}
	
	public static IDaoBaseSql getDaoInstance(String clzName){
		IDaoBaseSql sql = null;
		try {
			clzName = clzName.replace("Entity", "");
			Class<?> clz = Class.forName(BaseApplication.DB_DAO_PATH + clzName + "Dao");
			sql = (IDaoBaseSql) clz.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return sql;
	}
	
	/*******************************************************************************
	 * Entity -> CreateOrUpdateXXX
	 *******************************************************************************/
	public static BaseBean entityToBean(EntityBaseObject entity, String clzName){
		BaseBean base = null;
		try {
			base = (BaseBean) Class.forName(clzName).newInstance();
			CReflection.getAndSetAllAttr(base, base.getClass(), entity, entity.getClass());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return base;
	}
}
