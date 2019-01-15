package com.lb.cstview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((EditText)findViewById(R.id.edit)).addTextChangedListener(mTextWatcher);
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        private boolean mSelfChanged;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mSelfChanged) {
                return;
            }
            String val = s.toString();
            if (val.length() >= 3) {
                mSelfChanged = true;
                s.clear();
                int i = 0;
                try {
                    i = Integer.valueOf(val);
                } catch (Exception e) {
                    //ignore;
                }
                if (i > 100) {
                    s.append(String.valueOf (100));
                } else {
                    s.append(String.valueOf(i));
                }
                mSelfChanged = false;
            }
        }
    };
}
