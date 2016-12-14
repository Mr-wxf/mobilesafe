package com.wxf.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

	public static String encoder(String str) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");

			byte[] digest = instance.digest(str.getBytes());
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				// System.out.println(hexString);
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				stringBuffer.append(hexString);
			}
			String psdMd5 = stringBuffer.toString();
			return psdMd5;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
