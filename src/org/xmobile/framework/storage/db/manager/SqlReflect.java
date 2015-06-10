/*
 * File: Cson.java
 * Author: Chris.yang@generalbiologic.com
 * Date: 2014.3.31
 * 
 * Use java reflect method to generate the related data
 */

package org.xmobile.framework.storage.db.manager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.xmobile.framework.utils.JsonSerialization;
import org.xmobile.framework.utils.Utils;

import android.database.Cursor;

public class SqlReflect {

	public final <T> ArrayList<T> toList(Cursor cursor, Class<?> c){
		ArrayList<T> list = null;
		if(cursor != null){
			list = new ArrayList<T>();
			while(cursor.moveToNext()){
				T obj = setByReflect(cursor, c, null);
				if(obj != null){
					list.add(obj);
				}
			}
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T setByReflect(Cursor cursor, Class<?> c, T obj){
		try {
			if(obj == null){
				obj = (T) c.newInstance();
			}
			Field[] fields = c.getDeclaredFields();
			for(Field field : fields){
				if(field.getName().equals("serialVersionUID")){
					continue;
				}
				int idx = cursor.getColumnIndex(field.getName());
				if(idx < 0){
					continue;
				}
				
				field.setAccessible(true);
				String md = "set" + Utils.capitalize(field.getName());
				Method method = c.getDeclaredMethod(md, field.getType());
				if(field.getType().getName().equals("java.lang.String")){
					String val = cursor.getString(idx);
					method.invoke(obj, val);
				}else if(field.getType().getName().equals("java.lang.Integer") ||
						 field.getType().getName().equals("int")){
					Integer val = cursor.getInt(idx);
					method.invoke(obj, val);
				}else{
					method.invoke(obj, JsonSerialization.fromJsonString(cursor.getString(idx), field.getType()));
				}
			}
			
			if(c.getGenericSuperclass() != null){
				setByReflect(cursor, c.getSuperclass(), obj);
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return obj;
	}
}
