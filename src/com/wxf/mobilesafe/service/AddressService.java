package com.wxf.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.wxf.mobilesafe.R;
import com.wxf.mobilesafe.engine.AddressDAO;
import com.wxf.mobilesafe.utils.ConstaxtValuse;
import com.wxf.mobilesafe.utils.SpUtil;

public class AddressService extends Service {
	private TextView tv_showtoast;
	private TelephonyManager mTM;
	private MyPhoneStateListener myPhoneStateListener;
	private WindowManager mWM;
	private View view;
	private String address;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			tv_showtoast.setText(address);

		};
	};
	
	private int toast_style;
	private InnerOutCallReceiver innerOutCallReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		innerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(innerOutCallReceiver, intentFilter);

	}
 class InnerOutCallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
//	获取电话号码的方法
		String incomingNumber = getResultData();
	

		showToast(incomingNumber);		
		
	}
	 
 }
	class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			// 空闲
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null && mTM != null) {
					mWM.removeView(view);
				}
				break;
			// 通话
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			// 响铃
			case TelephonyManager.CALL_STATE_RINGING:

				showToast(incomingNumber);
				break;
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTM != null && myPhoneStateListener != null) {
			mTM.listen(myPhoneStateListener, TelephonyManager.PHONE_TYPE_NONE);
		}
		if(innerOutCallReceiver!=null){
			unregisterReceiver(innerOutCallReceiver);
		}
	}

	public void showToast(String incomingNumber) {
		
		
		final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ; 
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE   不能被点击
        params.gravity=Gravity.LEFT+Gravity.TOP;
   
        
        view = View.inflate(this, R.layout.showtoast_item, null);
       
        
        tv_showtoast = (TextView) view.findViewById(R.id.tv_showtoast);
        view.setOnTouchListener(new OnTouchListener() {
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
					params.x=	params.x+endX;
					params.y=	params.y+endY;
					// 容错
					if (params.x < 0)  {
						params.x=0;
					}
					if(params.y<0){
						params.y=0;
					}
					if(params.x>mWM.getDefaultDisplay().getWidth()-view.getWidth()){
						params.x=mWM.getDefaultDisplay().getWidth()-view.getWidth();
					}
                     if(params.y>mWM.getDefaultDisplay().getHeight()-view.getHeight()){
                    	 params.y=mWM.getDefaultDisplay().getHeight()-view.getHeight();
                     }
					
                     mWM.updateViewLayout(view, params);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					SpUtil.setInt(getApplicationContext(), ConstaxtValuse.LOCATION_TOAST_X, params.x);
					SpUtil.setInt(getApplicationContext(), ConstaxtValuse.LOCATION_TOAST_Y, params.y);
				break;
				}
			
				return true;
			}
		});
      int[]showToastColor=  new int[]{R.drawable.call_locate_white,
    		  R.drawable.call_locate_orange,
    		  R.drawable.call_locate_blue,
    		  R.drawable.call_locate_gray 
    		  ,R.drawable.call_locate_green};
          toast_style = SpUtil.getInt(getApplicationContext(), ConstaxtValuse.TOAST_STYLE, 0); 
          tv_showtoast.setBackgroundResource(showToastColor[toast_style]);
          
          params.x = SpUtil.getInt(getApplicationContext(), ConstaxtValuse.LOCATION_TOAST_X, 0);
          params.y = SpUtil.getInt(getApplicationContext(), ConstaxtValuse.LOCATION_TOAST_Y, 0);
          
          mWM.addView(view, params);
                 query(incomingNumber);
       
        
	}

	private void query(final String incomingNumber) {
		new Thread(){
			

			public void run() {
			address = AddressDAO.queryAddress(incomingNumber); 
				 handler.sendEmptyMessage(0);	
			};
			
		}.start();
			
	}
}
