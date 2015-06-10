package org.xmobile.framework.storage.db;

import java.io.Serializable;
import java.util.ArrayList;

import org.xmobile.framework.storage.db.base.EntityBaseObject;

public class SqlResult implements Serializable {

	private static final long serialVersionUID = -924926277846314903L;
	private int tag = -1;
	private boolean result = false;
	private ArrayList<EntityBaseObject> list = null;
	
	public SqlResult(){
	}
	public SqlResult(int tag, boolean result){
		this.tag = tag;
		this.result = result;
		this.list = null;
	}
	public SqlResult(int tag, boolean result, ArrayList<EntityBaseObject> list){
		this.tag = tag;
		this.result = result;
		this.list = list;
	}
	
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public ArrayList<EntityBaseObject> getList() {
		return list;
	}
	public void setList(ArrayList<EntityBaseObject> list) {
		this.list = list;
	}
}
