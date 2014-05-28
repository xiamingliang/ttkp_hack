package com.demo.floatwindowdemo;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatWindowBigView extends LinearLayout {

	/**
	 * 记录大悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录大悬浮窗的高度
	 */
	public static int viewHeight;

	TouchScreenThread tpthread;

	public FloatWindowBigView(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = findViewById(R.id.big_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button touch = (Button) findViewById(R.id.touch);
		Button back = (Button) findViewById(R.id.back);
		Button close = (Button) findViewById(R.id.close);
		touch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
              //快速点击悬浮框的坐标
				tpthread = new TouchScreenThread();
				tpthread.start();
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击返回的时候，移除大悬浮窗，创建小悬浮窗
				if (tpthread != null)
				{
				    tpthread.stopthread();
				}
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.createSmallWindow(context);
			}
		});
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
				if (tpthread != null)
				{
				    tpthread.stopthread();
				}
				MyWindowManager.removeBigWindow(context);
				MyWindowManager.removeSmallWindow(context);
				Intent intent = new Intent(getContext(), FloatWindowService.class);
				context.stopService(intent);
			}
		});
	}
	
    void quicktouch(/*int posx, int posy, int count*/){
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
   
    public class TouchScreenThread extends Thread{ 
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
  }
}
