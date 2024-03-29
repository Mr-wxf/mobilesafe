package com.wxf.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class AddressDAO {

	private static String tag="AdressDAO";
	private static String mAddress="";

	public static String queryAddress(String phone) {
		String path="data/data/com.wxf.mobilesafe/files/address.db";
		  SQLiteDatabase db = SQLiteDatabase.openDatabase
				  (path, null, SQLiteDatabase.OPEN_READONLY);
		
			
//			手机号码正则表达式
			String regex="^1[3-8]\\d{9}";
			mAddress = "未知号码";
		
			if(phone.matches(regex)){
				phone = phone.substring(0,7);
				//3,数据库查询
				Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
				//4,查到即可
				if(cursor.moveToNext()){
					String outkey = cursor.getString(0);
					Log.i(tag, "outkey = "+outkey);
					//5,通过data1查询到的结果,作为外键查询data2
					Cursor indexCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
					if(indexCursor.moveToNext()){
						//6,获取查询到的电话归属地
						mAddress = indexCursor.getString(0);
						Log.i(tag, "address = "+mAddress);
					}
				}else{
					mAddress = "未知号码";
				}
			}else{
				int length = phone.length();
				switch (length) {
				case 3://119 110 120 114
					mAddress = "报警电话";
					break;
				case 4://119 110 120 114
					mAddress = "模拟器";
					break;
				case 5://10086 99555
					mAddress = "服务电话";
					break;
				case 7:
					mAddress = "本地电话";
					break;
				case 8:
					mAddress = "本地电话";
					break;
				case 11:
					//(3+8) 区号+座机号码(外地),查询data2
					String area = phone.substring(1, 3);
					Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
					if(cursor.moveToNext()){
						mAddress = cursor.getString(0);
					}else{
						mAddress = "未知号码";
					}
					break;
				case 12:
					//(4+8) 区号(0791(江西南昌))+座机号码(外地),查询data2
					String area1 = phone.substring(1, 4);
					Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
					if(cursor1.moveToNext()){
						mAddress = cursor1.getString(0);
					}else{
						mAddress = "未知号码";
					}
					break;
				}
			}
			return mAddress;
		}
}


