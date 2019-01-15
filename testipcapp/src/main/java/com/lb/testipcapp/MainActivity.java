package com.lb.testipcapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.szhklt.aidl.wuJiaSmartHome.ISpeechAsr;
import com.szhklt.aidl.wuJiaSmartHome.ISpeechAsrListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSpeechAsr != null) {
                    Log.i(TAG, "has bindService ");
                    return;
                }
                String serviceName = "android.intent.action.START_B_SERVICE";
                Intent intent = new Intent(serviceName);
                boolean ret = bindService(createExplicitFromImplicitIntent(getApplicationContext(),intent), mService, Context.BIND_AUTO_CREATE);
                Log.i(TAG, "bindService " + serviceName + " " + ret);
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSpeechAsr != null) {
                    try {
                        Log.d(TAG, "register="+iSpeechAsr.startListening(new MySpeaker()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "onClick: unregister");
                }
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iSpeechAsr != null) {
                    try {
                        iSpeechAsr.startSpeaking("一二三四五，上山打老虎");
                        Log.d(TAG, "call spc text is :一二三四五，上山打老虎");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "onClick: unregister");
                }
            }
        });

    }
    private ISpeechAsr iSpeechAsr;

    private ServiceConnection mService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iSpeechAsr = ISpeechAsr.Stub.asInterface(service);
            Log.d(TAG, "onServiceConnected: "+iSpeechAsr);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: "+name);
            iSpeechAsr = null;
        }
    };

    private static class MySpeaker extends ISpeechAsrListener.Stub {

        @Override
        public boolean onResult(String result) throws RemoteException {
            Log.d(TAG, "onResult: "+result);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ("你好".equals(result)) {
                return true;
            }
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mService);
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
