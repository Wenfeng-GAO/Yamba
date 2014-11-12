package com.wenfeng.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public static final String TAG = DBHelper.class.getSimpleName();

	public DBHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}

	// Called only the first time we create the table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s (%s int primary key, %s text, %s text, %s int)", 
				StatusContract.TABLE, 
				StatusContract.Column.ID, 
				StatusContract.Column.USER, 
				StatusContract.Column.MESSAGE, 
				StatusContract.Column.CREATED_AT);
		Log.d(TAG, "onCreate with SQL: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + StatusContract.TABLE);
		onCreate(db);
	}

}
