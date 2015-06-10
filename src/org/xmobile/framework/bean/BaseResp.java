package org.xmobile.framework.bean;

public class BaseResp<T> extends BaseBean {

	private static final long serialVersionUID = 1325002920459714515L;

	private boolean result = false;
	private ExceptionInfo info = null;
	private T data = null;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public ExceptionInfo getInfo() {
		return info;
	}
	public void setInfo(ExceptionInfo info) {
		this.info = info;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
