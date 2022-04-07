package com.example.shiyan7_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTextBatteryLevel;
    private BatteryLevelReceiver mBatteryLevelReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        registerBatteryReceiver();
    }

    private void initView() {
        mTextBatteryLevel = (TextView) this.findViewById(R.id.shiyan7_1);
    }

    /**
     * 注册广播
     */
    public void registerBatteryReceiver() {
        // 第二步，要收听的频道是:电量变化
        IntentFilter intentFilter = new IntentFilter();
        // 第三步，设置频道(即表明要监听什么广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        // 第四步，初始化接收者
        mBatteryLevelReceiver = new BatteryLevelReceiver();
        // 第五步，注册广播
        // 从第二步到第五步便是动态注册
        // You cannot receive this through components declared
        // in manifests, only by explicitly registering for it with
        // {@link Context#registerReceiver(BroadcastReceiver, IntentFilter)
        // Context.registerReceiver()}.
        this.registerReceiver(mBatteryLevelReceiver, intentFilter);
    }

    /**
     * 第一步，创建一个广播接收者,继承自BroadcastReceiver
     */
    private class BatteryLevelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // * Extra for {@link android.content.Intent#ACTION_BATTERY_CHANGED}:
            // * integer field containing the current battery level, from 0 to
            // * {@link #EXTRA_SCALE}.
            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                // 监听电量改变
                Log.d(TAG, "收到了电量广播 -- action is " + action);
                Log.d(TAG, "当前电量：" + batteryLevel);
                if (mTextBatteryLevel != null) {
                    mTextBatteryLevel.setText(Integer.toString(batteryLevel));
                }
                // 通过该方法来将电量转换成百分比
                int maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                float percent = batteryLevel * 1.0f / maxLevel * 100;
                Log.d(TAG, "当前电量百分比量：" + percent + "%");
            } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                // 监听充电状态
                Log.d(TAG, "充电器已经连接上");
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                // 监听充电线拔出状态
                Log.d(TAG, "充电器已经断开");
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 动态广播注册时要取消广播注册，否则会导致内存泄露
        if (mBatteryLevelReceiver != null) {
            this.unregisterReceiver(mBatteryLevelReceiver);
        }
    }
}