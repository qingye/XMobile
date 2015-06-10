package org.xmobile.framework.storage.sp;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.utils.JsonSerialization;
import org.xmobile.xjson.XJSon;

public class SpManager {

	public static <T> void setObject(String key, T object){
		XJSon xj = new XJSon();
		SpXMobile.setValue(BaseApplication.getInstance().getApplicationContext(), key, xj.toJson(object));
	}
	
	public static Object getObject(String key, Class<?> clz){
		String value = SpXMobile.getValue(BaseApplication.getInstance().getApplicationContext(), key);
		return JsonSerialization.fromJsonString(value, clz);
	}
}
