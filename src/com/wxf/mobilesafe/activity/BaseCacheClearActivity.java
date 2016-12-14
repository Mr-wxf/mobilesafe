package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;

import android.app.Fragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost.TabSpec;

public class BaseCacheClearActivity extends TabActivity {
  @Override
protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_base_claer_cache);
//    	创建一个选项卡
    	TabSpec tab1 = getTabHost().newTabSpec("claer_cache").setIndicator("缓存清理");
    	TabSpec tab2 = getTabHost().newTabSpec("sd_claer_cache").setIndicator("SD卡缓存清理");
    	
    	tab1.setContent(new Intent(this,CacheClearActivity.class));
    	tab2.setContent(new Intent(this,SDcardCacheClearActivity.class));
    	
    	getTabHost().addTab(tab1);
    	getTabHost().addTab(tab2);
    	
}
}
