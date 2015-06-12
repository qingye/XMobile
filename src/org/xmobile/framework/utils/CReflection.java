package org.xmobile.framework.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.xmobile.xjson.XJSon;

public class CReflection {

	private static Class<?> getDestFieldType(Class<?> c, String fieldName){
		Class<?> type = null;
		
		Field[] fields = c.getDeclaredFields();
		for(int i = 0; i < fields.length; i ++){
			if(fields[i].getName().equals(fieldName)){
				type = fields[i].getType();
				break;
			}
		}
		
		if(type == null && c.getGenericSuperclass() != null){
			type = getDestFieldType(c.getSuperclass(), fieldName);
		}
		
		return type;
	}

	/***************************************************************************************
	 * Only getValue
	 ***************************************************************************************/
	public static void getAllAttr(Object src, Class<?> srcClz, HashMap<String, Object> map){
		Field[] fields = srcClz.getDeclaredFields();
		for(Field field : fields){
			if(field.getName().equals("serialVersionUID")){
				continue;
			}

			field.setAccessible(true);
			String md = "get" + Utils.capitalize(field.getName());
			try {
				Method method = srcClz.getDeclaredMethod(md);
				Object obj = method.invoke(src);
				if(obj != null){
					if(obj instanceof List<?>){
						obj = new XJSon().toJson(obj);
					}
					map.put(field.getName(), obj);
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		if(srcClz.getGenericSuperclass() != null){
			getAllAttr(src, srcClz.getSuperclass(), map);
		}
	}
	
	/***************************************************************************************
	 * Get value from destination field, and set to the same field source field
	 ***************************************************************************************/
	private static Method getDestDeclareMethod(Class<?> clz, String method, Class<?> type){
		Method m = null;
		try {
			m = clz.getDeclaredMethod(method, type);
		} catch (NoSuchMethodException e) {
		}
		
		if(m == null && clz.getGenericSuperclass() != null){
			m = getDestDeclareMethod(clz.getSuperclass(), method, type);
		}
		return m;
	}
	public static void getAndSetAllAttr(Object dest, Class<?> destClz, Object src, Class<?> srcClz){
		Field[] fields = srcClz.getDeclaredFields();
		for(Field field : fields){
			if(field.getName().equals("serialVersionUID") ||
			   field.getName().equals("id")){
				continue;
			}
			field.setAccessible(true);
			String strMethodGet = "get" + Utils.capitalize(field.getName());
			String strMethodSet = "set" + Utils.capitalize(field.getName());
			
			try {
				// get source
				Method methodGet = srcClz.getDeclaredMethod(strMethodGet);
				Object obj = methodGet.invoke(src);
				
				// set destination
				Class<?> destFieldType = getDestFieldType(destClz, field.getName());
				Method methodSet = getDestDeclareMethod(destClz, strMethodSet, destFieldType);
				if(destFieldType.equals(field.getType())){
					methodSet.invoke(dest, obj);
				}
				
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		if(srcClz.getGenericSuperclass() != null){
			getAndSetAllAttr(dest, destClz, src, srcClz.getSuperclass());
		}
	}
}
