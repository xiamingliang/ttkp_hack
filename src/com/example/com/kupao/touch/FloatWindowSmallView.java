package com.example.com.kupao.touch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import com.example.com.kupao.touch.MyWindowManager;

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
	
	long downClickTime = 0; 
	long upClickTime = 0;   
	
	
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
			downClickTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - getStatusBarHeight();
			// 手指移动的时候更新小悬浮窗的位置
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			//如果down和up事件时间超过两秒，认为是长按，关闭悬浮框
			upClickTime = System.currentTimeMillis();
			long temp = upClickTime-downClickTime;
			Log.d("zhangqian","temp="+temp+" downClickTime="+downClickTime+"  upClickTime="+upClickTime);
			if((upClickTime-downClickTime)>1000)
			{
				if (tpthread != null)
				{
				    tpthread.stopthread();
				}
				MyWindowManager.removeSmallWindow(mcontext);
			}
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
			else if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
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
				
			}else{
				
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
		Log.i("zhangqian", "run blinker="+blinker);  

        while (blinker != null) {
            quicktouch();
            try {
                sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
        DataOutputStream os = new DataOutputStream(su.getOutputStream());
		try {
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        
	}
	
	public void stopthread() {  
	    blinker = null;  
	}  
	
	public void quicktouch(/*int posx, int posy, int count*/){
		Process su = null;
		DataOutputStream os = null;
		int count = 0;

		// x:502 y:441
		String[] touchEvent = { 
				"/system/bin/sendevent /dev/input/event3 1 330 1\n",
				"/system/bin/sendevent /dev/input/event3 3 48 20\n",
				"/system/bin/sendevent /dev/input/event3 3 53 502\n",
				"/system/bin/sendevent /dev/input/event3 3 54 441\n",
				"/system/bin/sendevent /dev/input/event3 3 57 0\n",
				"/system/bin/sendevent /dev/input/event3 0 0002 0\n",
				"/system/bin/sendevent /dev/input/event3 0 0000 0\n",
				"/system/bin/sendevent /dev/input/event3 1 330 0\n",
				"/system/bin/sendevent /dev/input/event3 0 2 0\n",
				"/system/bin/sendevent /dev/input/event3 0 0 0\n",
		};
		try {
			su = Runtime.getRuntime().exec("su");
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
        os = new DataOutputStream(su.getOutputStream());
        try {
            for(int i = 0; i < touchEvent.length; i++){   
                os.writeBytes(touchEvent[i]);
            }
            os.flush();
            int ret =  su.waitFor();
            Log.d("xiamingliang", "ret="+ret);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        	 e.printStackTrace();
        } finally {
            su.destroy();
        }
    }	    
}
}
