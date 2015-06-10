package org.xmobile.framework.storage.db.base;

import java.util.HashMap;

public interface IDaoBaseSql {

	public String query();
	public String replace(HashMap<String, Object> map);
	public String insert(HashMap<String, Object> map);
	public String update(HashMap<String, Object> map, HashMap<String, Object> condition);
	public String delete(String condition);

}
