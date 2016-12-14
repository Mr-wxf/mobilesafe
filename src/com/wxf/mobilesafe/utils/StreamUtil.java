package com.wxf.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	public static String streamToString(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int temp = -1;
		try {
			while ((temp = is.read(buffer)) != -1) {
				os.write(buffer, 0, temp);
			}
			return os.toString();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}

}
