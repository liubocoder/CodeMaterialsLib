package com.lb.deskshortcut;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * @author LiuBo
 * @date 2018-12-06
 */
public class Shortcut1Activity extends AppCompatActivity {

    private static final String TAG = "Shortcut1Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);

        TextView textView = findViewById(R.id.txt1);
        Bundle bundle = getIntent().getExtras();

        Log.d(TAG, "onCreate: "+bundle+", taskId="+getTaskId());
        if (bundle != null) {
            int data = bundle.getInt("data");
            textView.setText("拿到的数据是：："+data);
            if (data == 1) {
                Intent intent = new Intent(this, DevActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        } else {
            textView.setText("bundle is null");
        }
    }
}
