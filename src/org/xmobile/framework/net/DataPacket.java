package org.xmobile.framework.net;

import org.xmobile.framework.bean.BaseBean;

import android.content.Context;

public class DataPacket<T> {

	private Context context = null;
	private String service = null;
	private T request = null;
	private String json = null;
	private BaseBean response = null;
	private int tag = 0;
	
	public DataPacket(Context c, String s, T r, int t){
		context = c;
		service = s;
		request = r;
		tag = t;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public T getRequest() {
		return request;
	}
	public void setRequest(T request) {
		this.request = request;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public BaseBean getResponse() {
		return response;
	}
	public void setResponse(BaseBean response) {
		this.response = response;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
}
