package com.sysu.yizhu.Activity.Login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sysu.yizhu.Activity.Business.MainActivity;
import com.sysu.yizhu.R;
import com.sysu.yizhu.UserData;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;
import com.sysu.yizhu.Util.PermissionsChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/27.
 * Description: App启动页面
 */
public class LaunchActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PermissionsChecker mPermissionsChecker;

    private static final String url = "http://112.74.165.37:8080/user/login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);

        AppManager.getAppManager().addActivity(LaunchActivity.this);

        mPermissionsChecker = new PermissionsChecker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            Integer time = 2000;    //设置等待时间，单位为毫秒
            Handler handler = new Handler();
            //当计时结束时跳转
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //已登录进入主页面，否则登录界面
                    if (UserData.getInstance().isLogin()) {
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
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            AppManager.getAppManager().finishActivity();
        }
    }

    private void autoSignIn() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", UserData.getInstance().getUserId());
        params.put("password", UserData.getInstance().getPassword());
        HttpUtil.post(url, params, new HttpUtil.HttpResponseCallBack() {
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

            UserData.getInstance().setLoginState(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}