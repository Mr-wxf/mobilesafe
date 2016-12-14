package com.wxf.mobilesafe.activity;

import com.wxf.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (e1.getRawX() - e2.getRawX() > 100) {
							// 象左滑动
							showNext();
							overridePendingTransition(R.anim.next_in_anim,
									R.anim.next_out_anim);
						}
						if (e2.getRawX() - e1.getRawX() > 100) {
							// 向右滑动
							showPre();
							overridePendingTransition(R.anim.pre_in_anim,
									R.anim.pre_out_anim);
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});

	}

	public void bt_next(View view) {
		showNext();
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

	public void bt_pre(View view) {
		showPre();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	public abstract void showNext();

	public abstract void showPre();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
