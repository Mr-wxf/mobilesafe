package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {
	private Button bt_bottom;
	private Button bt_top;
	private ImageView iv_drag;
	private WindowManager mWm;
	private int screenWidth;
	private int screenHeight;
	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}

	private void initUI() {
		mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
		screenWidth = mWm.getDefaultDisplay().getWidth();
		screenHeight = mWm.getDefaultDisplay().getHeight();
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		int locationX = SpUtil.getInt(getApplicationContext(),
				ConstaxtValuse.LOCATION_TOAST_X, 0);
		int locationY = SpUtil.getInt(getApplicationContext(),
				ConstaxtValuse.LOCATION_TOAST_Y, 0);
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = locationX;
		layoutParams.topMargin = locationY;
		iv_drag.setLayoutParams(layoutParams);
		if (locationY > screenHeight / 2) {

			bt_top.setVisibility(View.VISIBLE);
			bt_bottom.setVisibility(View.INVISIBLE);
		} else {
			bt_top.setVisibility(View.INVISIBLE);
			bt_bottom.setVisibility(View.VISIBLE);
		}
		iv_drag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0,
						mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock
						.uptimeMillis();
				if (mHits[mHits.length - 1] - mHits[0] < 500) {
					iv_drag.layout(
							screenWidth / 2 - iv_drag.getWidth()/2,
							screenHeight / 2 - iv_drag.getHeight()/2,
							screenWidth / 2 +iv_drag.getWidth()/2,
							screenHeight / 2 + iv_drag.getHeight()/2);
				}
			}
		});

		iv_drag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int  moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					int endX = moveX - startX;
					int endY = moveY - startY;
					int left = endX + iv_drag.getLeft();
					int top = endY + iv_drag.getTop();
					int right = endX + iv_drag.getRight();
					int bottom = endY + iv_drag.getBottom();
					// 容错
					if (left < 0 || right > screenWidth || top < 0
							|| bottom > screenHeight - 22) {
						return true;
					}
					if (bottom > screenHeight / 2) {
						bt_top.setVisibility(View.VISIBLE);
						bt_bottom.setVisibility(View.INVISIBLE);
					} else {
						bt_top.setVisibility(View.INVISIBLE);
						bt_bottom.setVisibility(View.VISIBLE);
					}

					
					iv_drag.layout(left, top, right, bottom);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					SpUtil.setInt(getApplicationContext(),
							ConstaxtValuse.LOCATION_TOAST_X, iv_drag.getLeft());
					SpUtil.setInt(getApplicationContext(),
							ConstaxtValuse.LOCATION_TOAST_Y, iv_drag.getTop());
					break;
				}

				return false;
			}
		});
	}
}
