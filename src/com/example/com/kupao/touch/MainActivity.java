package com.example.com.kupao.touch;

import com.example.com.kupao.touch.MyWindowManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyWindowManager.createSmallWindow(getApplicationContext());
		finish();
    }
    
}
