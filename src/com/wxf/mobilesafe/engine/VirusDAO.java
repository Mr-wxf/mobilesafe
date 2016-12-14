package com.wxf.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class VirusDAO {
	static String path="data/data/com.wxf.mobilesafe/files/antivirus.db";

public static List<String>  getVirusMD5(){
	   SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	   Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
	   List<String> viursList =new ArrayList<String>();
	   while(cursor.moveToNext()){
		   viursList.add( cursor.getString(0));
	   }
	return viursList;
}
	
}


