package com.wxf.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxf.mobilesafe.db.BlackNumberHelper;
import com.wxf.mobilesafe.db.bean.BlackNumberBean;

public class BlcakNumberDAO {
	private BlackNumberHelper blackNumberHelper;

	private BlcakNumberDAO(Context context) {
		blackNumberHelper = new BlackNumberHelper(context);
	}

	private static BlcakNumberDAO blcakNumberDAO = null;

	public static BlcakNumberDAO getInstance(Context context) {
		if (blcakNumberDAO == null) {
			blcakNumberDAO = new BlcakNumberDAO(context);
		}
		return blcakNumberDAO;
	}

	public void insert(String phone, String mode) {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("phone", phone);
		contentValues.put("mode", mode);
		db.insert("blacknumber", null, contentValues);
		db.close();
	}

	public void delete(String phone) {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		db.delete("blacknumber", "phone=?", new String[] { phone });
		db.close();
	}

	public void update(String phone, String mode) {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		db.update("blacknumber", contentValues, "phone=?",
				new String[] { phone });
		db.close();
	}

	public List<BlackNumberBean> query() {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		Cursor query = db.query("blacknumber",
				new String[] { "phone", "mode" }, null, null, null, null,
				"_id desc");

		List<BlackNumberBean> numberBeans = new ArrayList<BlackNumberBean>();
		while (query.moveToNext()) {
			BlackNumberBean blackNumberBean = new BlackNumberBean();
			blackNumberBean.phone = query.getString(0);
			blackNumberBean.mode = query.getString(1);
			numberBeans.add(blackNumberBean);
		}
		db.close();
		return numberBeans;
	}

	public List<BlackNumberBean> find(int index) {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		Cursor query = db.rawQuery(
				"select phone,mode from blacknumber order by _id desc limit ?,20;",
				new String[] { index + "" });

		List<BlackNumberBean> numberBeans = new ArrayList<BlackNumberBean>();
		while (query.moveToNext()) {
			BlackNumberBean blackNumberBean = new BlackNumberBean();
			blackNumberBean.phone = query.getString(0);
			blackNumberBean.mode = query.getString(1);
			numberBeans.add(blackNumberBean);
		}
		db.close();
		return numberBeans;
	}

	public int getCount() {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		Cursor query = db.rawQuery("select count(*)from blacknumber", null);
		int count = 0;
		while (query.moveToNext()) {
			count = query.getInt(0);
		}
		db.close();

		return count;
	}

	public int getMode(String phone) {
		SQLiteDatabase db = blackNumberHelper.getWritableDatabase();
		Cursor query = db.query("blacknumber", new String[]{"mode"}, "phone=?", new String[]{phone}, null, null, null);
		int count = 0;
		while (query.moveToNext()) {
			count = query.getInt(0);
		}
		db.close();

		return count;		
	}
}
