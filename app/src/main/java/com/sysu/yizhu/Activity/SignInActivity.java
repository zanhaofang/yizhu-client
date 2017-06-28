package com.sysu.yizhu.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/18.
 * Description: 登录界面Activity
 */
public class SignInActivity extends AppCompatActivity {
    private static final String url = "http://112.74.165.37:8080/user/login";

    private EditText sign_in_username = null;
    private EditText sign_in_password = null;
    private Button sign_in = null;
    private TextView forgot_pwd = null;
    private TextView sign_up = null;

    //存储用户名密码
    private SharedPreferences preference;
    private  SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sysu.yizhu.R.layout.sign_in_layout);

        AppManager.getAppManager().finishAllActivity();
        AppManager.getAppManager().addActivity(SignInActivity.this);
        //控件初始化
        sign_in_username = (EditText) findViewById(R.id.sign_in_username);
        sign_in_password = (EditText) findViewById(R.id.sign_in_password);
        sign_in = (Button) findViewById(R.id.sign_in);
        forgot_pwd = (TextView) findViewById(R.id.forgot_pwd);
        sign_up = (TextView) findViewById(R.id.sign_up);
        //sharedpreference初始化
        preference = getSharedPreferences("info", MODE_PRIVATE);
        editor = preference.edit();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sign_in_username.getText())) {
                    Toast.makeText(SignInActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sign_in_password.getText())) {
                    Toast.makeText(SignInActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    signIn();
                }
            }
        });

        forgot_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, ForgotPwdActivity.class);
                SignInActivity.this.startActivity(intent);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, SignUpActivity.class);
                SignInActivity.this.startActivity(intent);
            }
        });
    }

    private void signIn() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", sign_in_username.getText().toString());
        params.put("password", sign_in_password.getText().toString());
        HttpUtil.post(url, "", params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        resultAnalysis(result);
                        Intent intent = new Intent();
                        intent.setClass(SignInActivity.this, MainActivity.class);
                        SignInActivity.this.startActivity(intent);
                        Toast.makeText(SignInActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case 400:
                        Toast.makeText(SignInActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        Toast.makeText(SignInActivity.this, "用户名不是手机号或参数格式错误！", Toast.LENGTH_SHORT).show();
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

            editor.putString("username", sign_in_username.getText().toString());
            editor.putString("password", sign_in_password.getText().toString());
            editor.putString("state", "login");
            editor.putString("jsessionid", object.optString("jsessionid"));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
