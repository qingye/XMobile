package org.xmobile.framework.net.manager;

import java.util.ArrayList;

import org.xmobile.framework.BaseApplication;
import org.xmobile.framework.bean.BaseBean;
import org.xmobile.framework.net.DataPacket;
import org.xmobile.framework.net.engine.HttpTools;
import org.xmobile.xjson.XJSon;
import org.xmobile.xjson.XJsonType;

public class Parser {

	protected final static String COMMON_RESPONSE = "CommonResp";
	
	// For Activity
	public static void dataParser(DataPacket<?> packet){
		BaseBean resp = null;
		if(checkJson(packet.getJson())){
			String clz = BaseApplication.BEAN_RESPONSE_PATH;

			String clzName = getClassName(packet.getRequest());
			if(isClassExist(clz + clzName)){
				clz += clzName;
			}else{
				clz += COMMON_RESPONSE;
			}
			resp = parse(clz, packet.getJson());
		}
		
		packet.setResponse(resp);
	}
	
	// For SyncService
	public static BaseBean dataParser(Object bean, String json){
		BaseBean resp = null;
		if(checkJson(json)){
			String path = BaseApplication.BEAN_RESPONSE_PATH;
			String clz = path;
			
			String clzName = getClassName(bean);
			if(isClassExist(clz + clzName)){
				clz += clzName;
			}else{
				clz += COMMON_RESPONSE;
			}
			resp = parse(clz, json);
		}
		return resp;
	}
	
	private static boolean checkJson(String json){
		boolean result = true;
		if(json.equals(HttpTools.NETWORK_NOT_AVAILABLE) ||
		   json.equals(HttpTools.SERVER_ERR) ||
		   json.equals(HttpTools.TIME_OUT)){
			result = false;
		}
		return result;
	}
	                                
	private static boolean isClassExist(String clz){
		boolean result = false;
		try {
			if(Class.forName(clz) != null){
				result = true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static <T> String getClassName(T instance){
		String name = "";
		if(instance instanceof ArrayList){
			ArrayList<?> list = (ArrayList<?>) instance;
			name = list.get(0).getClass().getSimpleName();
		}else{
			name = instance.getClass().getSimpleName();
		}
		return name + "Resp";
	}
	
	public static BaseBean parse(String clz, String json){
		BaseBean resp = null;
		try {
			XJSon xj = new XJSon();
			resp = (BaseBean) xj.fromJson(json, XJsonType.get(Class.forName(clz)).getType());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return resp;
	}
}
