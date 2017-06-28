package com.sysu.yizhu.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/27.
 * Description: App启动页面
 */
public class LaunchActivity extends AppCompatActivity {
    private static final String url = "http://112.74.165.37:8080/user/login";

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);

        AppManager.getAppManager().addActivity(LaunchActivity.this);
        preference = getSharedPreferences("info", MODE_PRIVATE);
        editor = preference.edit();

        Integer time = 2000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时跳转
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //已登录进入主页面，否则登录界面
                if (preference.getString("state", "").equals("login")) {
                    autoSignIn();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(LaunchActivity.this, SignInActivity.class);
                    LaunchActivity.this.startActivity(intent);
                }
                AppManager.getAppManager().finishActivity();
            }
        }, time);
    }

    private void autoSignIn() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", preference.getString("username", ""));
        params.put("password", preference.getString("password", ""));
        HttpUtil.post(url, "", params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        resultAnalysis(result);
                        Intent intent = new Intent();
                        intent.setClass(LaunchActivity.this, MainActivity.class);
                        LaunchActivity.this.startActivity(intent);
                        Toast.makeText(LaunchActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case 400:
                        Toast.makeText(LaunchActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        Toast.makeText(LaunchActivity.this, "用户名不是手机号或参数格式错误！", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(String result, Exception e) {

            }
        });
    }

    private void resultAnalysis(String string) { //解析JSON数据或更新UI
        JSONObject object = null;
        try {
            object = new JSONObject(string);

            editor.putString("state", "login");
            editor.putString("jsessionid", object.optString("jsessionid"));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}