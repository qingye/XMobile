package org.xmobile.framework.storage.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBLocalHelper extends SQLiteOpenHelper {  
    public static final int DB_LOCAL_VERSION = 1;
    public static final String DB_LOCAL_NAME = "xmobile.db";

    public DBLocalHelper(Context context) {  
        super(context, DB_LOCAL_NAME, null, DB_LOCAL_VERSION);
    }  

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == newVersion) {
			return;
		} else {
			dropTables(db);
		}
		createTables(db);
	}
	
	private void createTables(SQLiteDatabase db) {
		for(int i = 0; i < DBTables.Tables.length; i ++){
			db.execSQL(DBTables.Tables[i]);
		}
	}

	private void dropTables(SQLiteDatabase db) {
		for(int i = 0; i < DBTables.TableNames.length; i ++){
			db.execSQL("DROP TABLE IF EXISTS " + DBTables.TableNames[i]);
		}
	}
}
