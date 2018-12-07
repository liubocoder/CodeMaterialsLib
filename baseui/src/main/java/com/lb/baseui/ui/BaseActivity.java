package com.lb.baseui.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.lb.baseui.log.Log;

/**
 * @author LiuBo
 * @date 2018-12-07
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.Activity.d("sInstt"+savedInstanceState+", taskId=%d", getTaskId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.Activity.d("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.Activity.d("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.Activity.d("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.Activity.d("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.Activity.d("onDestroy");
    }
}
