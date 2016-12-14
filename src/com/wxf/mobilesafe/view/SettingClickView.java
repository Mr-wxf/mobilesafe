package com.wxf.mobilesafe.view;

import com.wxf.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {

	private TextView tv_des;
	private TextView tv_title;

	public SettingClickView(Context context) {
		this(context, null);

	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		View.inflate(getContext(), R.layout.setting_click_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setDes(String des) {
		tv_des.setText(des);
	}

}
