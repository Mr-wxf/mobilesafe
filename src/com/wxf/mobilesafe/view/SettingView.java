package com.wxf.mobilesafe.view;

import com.wxf.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout {

	private static final String namespace = "http://schemas.android.com/apk/res/com.wxf.mobilesafe";
	private static final String tag = "SettingView";
	private CheckBox cb_box;
	private TextView tv_des;
	private TextView tv_title;
	private String desoff;
	private String deson;
	private String destitle;

	public SettingView(Context context) {
		this(context, null);

	}

	public SettingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		View.inflate(getContext(), R.layout.setting_item_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		initAttrs(attrs);
	}

	private void initAttrs( AttributeSet attrs) {
		destitle = attrs.getAttributeValue(namespace, "destitle");
		desoff = attrs.getAttributeValue(namespace, "desoff");
		deson = attrs.getAttributeValue(namespace, "deson");
		Log.i(tag, destitle);
		Log.i(tag, desoff);
		Log.i(tag, deson);
		tv_title.setText(destitle);
	}

	public boolean isCheck() {

		return cb_box.isChecked();
		
		
	}

	public void setCheck(boolean isCheck) {

		if (isCheck) {
			tv_des.setText(deson);
		} else {
			tv_des.setText(desoff);
		}
		cb_box.setChecked(isCheck);
	}
}
