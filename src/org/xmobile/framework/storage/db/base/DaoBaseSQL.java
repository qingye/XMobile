package org.xmobile.framework.storage.db.base;

import java.util.HashMap;
import java.util.Iterator;

public abstract class DaoBaseSQL implements IDaoBaseSql {
	
	protected String query(String tableName){
		return "select * from " + tableName;
	}

	protected String insertSQL(String tableName, HashMap<String, Object> map){
		String sql = "insert into " + tableName + " ";
		String key = "(";
		String value = "values(";
		
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String k = it.next();
			String v = "";
			Object ov = map.get(k);
			if(ov == null){
				continue;
			}
			
			key += (k + ", ");
			v = "" + ov;
			v = v.replace("'", "''");
			value += ("'" + v + "', ");
		}
		key = key.substring(0, key.length()-2) + ") ";
		value = value.substring(0, value.length()-2) + ") ";
		sql = sql + key + value;

		return sql;
	}
	
	protected String updateSQL(String tableName, HashMap<String, Object> map, 
			HashMap<String, Object> condition){
		
		String sql = "update " + tableName + " set ";
		String keyAndValues = "";
		String where = "where ";
		
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String k = it.next();
			keyAndValues += (k + " = ");
			
			String v = "" + map.get(k);
			v = v.replace("'", "''");
			keyAndValues += ("'" + v + "', ");
		}
		keyAndValues = keyAndValues.substring(0, keyAndValues.length()-2) + " ";
		
		it = condition.keySet().iterator();
		while(it.hasNext()){
			String k = it.next();
			where += (k + " = ");
			
			String v = "" + condition.get(k);
			v = v.replace("'", "''");
			where += ("'" + v + "' and ");
		}
		where = where.substring(0, where.length()-5);
		sql = sql + keyAndValues + where;

		return sql;
	}
	
	protected String replaceSQL(String tableName, HashMap<String, Object> map){
		String sql = "replace into " + tableName + " ";
		String key = "(";
		String value = "values(";
		
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String k = it.next();
			String v = "";
			Object ov = map.get(k);
			if(ov == null){
				continue;
			}
			
			key += (k + ", ");
			v = "" + ov;
			v = v.replace("'", "''");
			value += ("'" + v + "', ");
		}
		key = key.substring(0, key.length()-2) + ") ";
		value = value.substring(0, value.length()-2) + ") ";
		sql = sql + key + value;

		return sql;
	}
	
	protected String deleteSql(String tableName, String condition){
		return "delete from " + tableName + condition;
	}
}
