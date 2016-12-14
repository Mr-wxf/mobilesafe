package com.wxf.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.wxf.mobilesafe.db.AppLockHelper;
import com.wxf.mobilesafe.db.BlackNumberHelper;
import com.wxf.mobilesafe.db.bean.BlackNumberBean;

public class AppLockrDAO {
	private AppLockHelper appLockHelper;
	private Context context;

	private AppLockrDAO(Context context) {
		appLockHelper = new AppLockHelper(context);
		this.context=context;
	}

	private static AppLockrDAO appLockrDAO = null;

	public static AppLockrDAO getInstance(Context context) {
		if (appLockrDAO == null) {
			appLockrDAO = new AppLockrDAO(context);
		}
		return appLockrDAO;
	}

	public void insert(String packagename) {
		SQLiteDatabase db = appLockHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("packagename", packagename);
		db.insert("applock", null, contentValues);
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("contant://applock/skip"), null);
		
	}

	public void delete(String packagename) {
		SQLiteDatabase db = appLockHelper.getWritableDatabase();

		db.delete("applock", "packagename=?", new String[] {packagename});
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("contant://applock/skip"), null);
	}

	public List<String> findAll() {
		SQLiteDatabase db = appLockHelper.getWritableDatabase();
		List<String> lockPackagenameList = new ArrayList<String>();
		Cursor cursor = db.query("applock", new String[] { "packagename" },
				null, null, null, null, null);
		while (cursor.moveToNext()) {
			String string = cursor.getString(0);
			lockPackagenameList.add(string);
		}
		cursor.close();
		db.close();
		return lockPackagenameList;
	}
}
