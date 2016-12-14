package com.wxf.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackupEngine {
	
	private static int index=0;
	private static Cursor cursor;
	private static FileOutputStream fos;

	public static void backupSms(Context context ,String path,CallBack callBack) {
		 try {
 		 File file = new File(path);
 		 cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
 				 new String[]{"address","date ","type","body"}, null, null, null);
 		
			fos = new FileOutputStream(file);
			XmlSerializer serializer=Xml.newSerializer();
			serializer.setOutput(fos, "utf-8");
			serializer.startDocument("utf-8", true);
			serializer.startTag(null, "smss");
//			pd.setMax(cursor.getCount()) ;
			callBack.setMax(cursor.getCount());
			while(cursor.moveToNext()){
				serializer.startTag(null, "sms");
				
				serializer.startTag(null, "date");
				serializer.text(cursor.getString(1));
				serializer.endTag(null, "date");				
				
				serializer.startTag(null, "type");
				serializer.text(cursor.getString(2));
				serializer.endTag(null, "type");				
				
				
				serializer.startTag(null, "body");
				serializer.text(cursor.getString(3));
				serializer.endTag(null, "body");				
				serializer.endTag(null, "sms");
				
				serializer.startTag(null, "sms");
				serializer.startTag(null, "address");
				serializer.text(cursor.getString(0));
				serializer.endTag(null, "address");	
				
				serializer.endTag(null, "sms");
				index++;
			    Thread.sleep(500);
//				pd.setProgress(index);
			    callBack.setProgress(index);
			}
			
			serializer.endTag(null, "smss");
			
			
			serializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null&&fos!=null){
				cursor.close();
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	


 public  interface  CallBack{
	public void setMax(int max); 
	public void setProgress(int index); 
}

}