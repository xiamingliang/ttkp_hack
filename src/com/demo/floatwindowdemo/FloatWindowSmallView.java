package com.demo.floatwindowdemo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatWindowSmallView extends LinearLayout {

	/**
	 * 记录小悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录小悬浮窗的高度
	 */
	public static int viewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	 private static int statusBarHeight;

	/**
	 * 用于更新小悬浮窗的位置
	 */
	private WindowManager windowManager;

	/**
	 * 小悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * 记录当前手指位置在屏幕上的横坐标值
	 */
	private float xInScreen;

	/**
	 * 记录当前手指位置在屏幕上的纵坐标值
	 */
	private float yInScreen;

	/**
	 * 记录手指按下时在屏幕上的横坐标的值
	 */
	private float xDownInScreen;

	/**
	 * 记录手指按下时在屏幕上的纵坐标的值
	 */
	private float yDownInScreen;

	/**
	 * 记录手指按下时在小悬浮窗的View上的横坐标的值
	 */
	private float xInView;

	/**
	 * 记录手指按下时在小悬浮窗的View上的纵坐标的值
	 */
	private float yInView;

	
	TouchScreenThread tpthread = null;
	
	TextView percentView;
	
	Context mcontext;
	
	public FloatWindowSmallView(Context context) {
		super(context);
		mcontext = context;
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		percentView = (TextView) findViewById(R.id.percent);
		//percentView.setText(MyWindowManager.getUsedPercentValue(context));
		percentView.setText("open");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - getStatusBarHeight();
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			// 手指移动的时候更新小悬浮窗的位置
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
				//openBigWindow();
				Log.d("zhangqian","tpthread="+tpthread);
				if (tpthread == null){
				    tpthread = new TouchScreenThread();
					tpthread.start();
					percentView.setText("close");
				}else{
				    tpthread.stopthread();
				    tpthread = null;
				    percentView.setText("open");
				}
				
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
	 * 
	 * @param params
	 *            小悬浮窗的参数
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * 更新小悬浮窗在屏幕中的位置。
	 */
	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 打开大悬浮窗，同时关闭小悬浮窗。
	 */
	private void openBigWindow() {
		MyWindowManager.createBigWindow(getContext());
		MyWindowManager.removeSmallWindow(getContext());
	}

	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}


private class TouchScreenThread extends Thread{ 
	private volatile Thread blinker = Thread.currentThread();
	@Override
	public void run() {
		//Thread thisThread = Thread.currentThread(); 
		Log.i("zhangqian", "run blinker="+blinker);  
        while (blinker != null) { //(blinker == thisThread) {  
		    quicktouch();
		    /*try {
				sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }
}

public void stopthread() {  
    blinker = null;  
}  

public void quicktouch(/*int posx, int posy, int count*/){
	Process process = null;
	DataOutputStream os = null;
	String[] touchEvent = { 
			"sendevent /dev/input/event3 1 330 1\n",
			"sendevent /dev/input/event3 3 48 8\n",
			"sendevent /dev/input/event3 3 57 0\n",
			"sendevent /dev/input/event3 3 53 207\n",
			"sendevent /dev/input/event3 3 54 613\n",
			"sendevent /dev/input/event3 0 2 0\n",
			"sendevent /dev/input/event3 0 0 0\n",
			"sendevent /dev/input/event3 3 48 8\n",
			"sendevent /dev/input/event3 3 57 0\n",
			"sendevent /dev/input/event3 3 53 207\n",
			"sendevent /dev/input/event3 3 54 613\n",
			"sendevent /dev/input/event3 0 2 0\n",
			"sendevent /dev/input/event3 0 0 0\n",
			"sendevent /dev/input/event3 3 48 8\n",
			"sendevent /dev/input/event3 3 57 0\n",
			"sendevent /dev/input/event3 3 53 207\n",
			"sendevent /dev/input/event3 3 54 613\n",
			"sendevent /dev/input/event3 0 2 0\n",
			"sendevent /dev/input/event3 0 0 0\n",
			"sendevent /dev/input/event3 3 48 8\n",
			"sendevent /dev/input/event3 3 57 0\n",
			"sendevent /dev/input/event3 3 53 207\n",
			"sendevent /dev/input/event3 3 54 613\n",
			"sendevent /dev/input/event3 0 2 0\n",
			"sendevent /dev/input/event3 0 0 0\n",
			"sendevent /dev/input/event3 3 48 8\n",
			"sendevent /dev/input/event3 3 57 0\n",
			"sendevent /dev/input/event3 3 53 207\n",
			"sendevent /dev/input/event3 3 54 613\n",
			"sendevent /dev/input/event3 0 2 0\n",
			"sendevent /dev/input/event3 0 0 0\n",
			"sendevent /dev/input/event3 1 330 0\n",
			"sendevent /dev/input/event3 0 2 0\n",
			"sendevent /dev/input/event3 0 0 0\n"
            };

     try{
         //Thread.sleep(2000);
         process = Runtime.getRuntime().exec("su");
         os = new DataOutputStream(process.getOutputStream()); 
         for(int i = 0; i < touchEvent.length; i++){
	        // Log.i("zhangqian", touchEvent[i]);        
	         os.writeBytes(touchEvent[i]);
         }	     
         os.writeBytes("exit\n");
	     os.flush();         
	     process.waitFor();
		} catch (IOException e) {
		Log.e("zhangqian", "Runtime problems\n");
		e.printStackTrace();
		} catch (SecurityException se){
		se.printStackTrace();
		} catch (InterruptedException e) {
		e.printStackTrace();
		} finally {  
            try {  
                if (os != null) {  
                    os.close();  
                }  
                process.destroy();  
            } catch (Exception e) { 
            	e.printStackTrace();
            } 
		}
   }
 }
}