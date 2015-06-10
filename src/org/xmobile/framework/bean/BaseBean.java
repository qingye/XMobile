package org.xmobile.framework.bean;

import java.io.Serializable;

import org.xmobile.xjson.XJSon;

public class BaseBean implements Serializable {

	protected static final long serialVersionUID = -2592979375156733925L;

	public String toJson(){
		XJSon xj = new XJSon();
		return xj.toJson(this);
	}

	@Override
	public String toString() {
		return toJson();
	}
}
