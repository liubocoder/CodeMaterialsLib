package com.lb.deskshortcut;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
        if (bundle != null) {
            int data = bundle.getInt("data");
            textView.setText("拿到的数据是：："+data);
            if (data == 1) {

            } else {
                finish();
            }
        } else {
            textView.setText("bundle is null");
        }
    }
}
