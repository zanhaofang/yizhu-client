package com.sysu.yizhu.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.AppManager;

/**
 * Created by QianZixuan on 2017/4/19.
 * Description: 忘记密码Activity
 */
public class ForgotPwdActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pwd_layout);

        AppManager.getAppManager().addActivity(ForgotPwdActivity.this);
    }
}
