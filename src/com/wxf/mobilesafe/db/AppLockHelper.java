package com.wxf.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockHelper extends SQLiteOpenHelper {

	public AppLockHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock (_id integer primary" +
				" key autoincrement , packagename varchar(50))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
