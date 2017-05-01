package com.sysu.yizhu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by QianZixuan on 2017/4/27.
 * Description: App启动页面
 */
public class LaunchActivity extends AppCompatActivity {
    private SharedPreferences preference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);

        AppManager.getAppManager().addActivity(LaunchActivity.this);
        preference = getSharedPreferences("info", MODE_PRIVATE);

        Integer time = 2000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时跳转
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //已登录进入主页面，否则登录界面
                Intent intent = new Intent();
                if (preference.getString("username", "").equals("")) {
                    intent.setClass(LaunchActivity.this, SignInActivity.class);
                    LaunchActivity.this.startActivity(intent);
                } else {
                    intent.setClass(LaunchActivity.this, MainActivity.class);
                    LaunchActivity.this.startActivity(intent);
                }
                AppManager.getAppManager().finishActivity();
            }
        }, time);
    }
}