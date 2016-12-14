package com.wxf.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
     TextView textView = new TextView(getApplicationContext()); 
     textView.setText("TestActivity");
	setContentView(textView);
      
}
}
