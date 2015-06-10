package org.xmobile.framework.bean;

public class ExceptionInfo extends BaseBean {

	private static final long serialVersionUID = 4400900058327763704L;
	private int code = 0;
	private String message = null;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
