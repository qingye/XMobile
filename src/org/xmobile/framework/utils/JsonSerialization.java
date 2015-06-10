package org.xmobile.framework.utils;

import java.util.ArrayList;

import org.xmobile.framework.bean.BaseBean;
import org.xmobile.xjson.XJSon;
import org.xmobile.xjson.XJsonType;

public class JsonSerialization {

	public static <T> String toJson(T data){
		return new XJSon().toJson(data);
	}

	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> fromJsonList(String json){
		return (ArrayList<T>) new XJSon().fromJson(json, new XJsonType<ArrayList<T>>(){}.getType());
	}
	
	public static Object fromJsonString(String json, Class<?> clz){
		Object o = null;
		try {
			XJSon xj = new XJSon();
			o = (BaseBean) xj.fromJson(json, XJsonType.get(clz).getType());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return o;
	}
}
