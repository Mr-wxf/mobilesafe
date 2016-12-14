package com.wxf.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDAO {
	String path = "data/data/com.wxf.mobilesafe/files/commonnum.db";

	public List<Group> getGroup() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("classlist", new String[] { "name", "idx" },
				null, null, null, null, null);
		List<Group> groupList = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			Group group = new Group();

			group.name = cursor.getString(0);
			group.idx = cursor.getString(1);
			group.childList = getChildList(group.idx);
			groupList.add(group);
		}

		cursor.close();
		db.close();
		return groupList;
	}

	public List<ChildList> getChildList(String idx) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
		List<ChildList> ChildList = new ArrayList<ChildList>();
		while (cursor.moveToNext()) {
			ChildList child = new ChildList();
			child._id = cursor.getString(0);
			child.number = cursor.getString(1);
			child.name = cursor.getString(2);
			ChildList.add(child);
		}
		cursor.close();
		db.close();

		return ChildList;
	}

	public class Group {
		public String name;
		public String idx;
		public List<ChildList> childList;
	}

	public class ChildList {
		public String _id;
		public String number;
		public String name;
	}
}
