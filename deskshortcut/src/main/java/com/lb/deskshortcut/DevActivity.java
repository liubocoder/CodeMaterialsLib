package com.lb.deskshortcut;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * @author LiuBo
 * @date 2018-12-07
 */
public class DevActivity extends AppCompatActivity {
    public static final String TAG = "DevActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: taskId="+getTaskId());
        TextView textView = new TextView(this);
        textView.setText("DEV_ACTIVITY");
        setContentView(textView);
    }
}
