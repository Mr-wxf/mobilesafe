package com.wxf.mobilesafe.db.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
   public  String name;
   public  String packageName;
   public  Drawable icon;
   public  boolean isSDCard;
   public  boolean isSystem;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getPackageName() {
	return packageName;
}
public void setPackageName(String packageName) {
	this.packageName = packageName;
}
public Drawable getIcon() {
	return icon;
}
public void setIcon(Drawable icon) {
	this.icon = icon;
}
public boolean isSDCard() {
	return isSDCard;
}
public void setSDCard(boolean isSDCard) {
	this.isSDCard = isSDCard;
}
public boolean isSystem() {
	return isSystem;
}
public void setSystem(boolean isSystem) {
	this.isSystem = isSystem;
}
   
   
}
