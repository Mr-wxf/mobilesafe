package com.wxf.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
private static SharedPreferences sp;

//  读
	public static boolean getBoolean(Context context,String key,boolean defValue){
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}
		return sp.getBoolean(key, defValue);
		
	}
//	写
	public static void setBoolean(Context context,String key,boolean value){
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}

	public static String getPsd(Context context,String key, String defValue){
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}
		return sp.getString(key, defValue);
		
	}
//	写
	public static void setPsd(Context context,String key,String value){
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	
	public static int getInt(Context context,String key, int defValue){
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

		}
		return sp.getInt(key, defValue);
		
	}
//	写
	public static void setInt(Context context,String key,int value){
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	public static void romove(Context context, String number) {
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().remove(number).commit();	
	}
}

