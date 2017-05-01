package com.sysu.yizhu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by QianZixuan on 2017/4/18.
 * Description: 登录界面Activity
 */
public class SignInActivity extends AppCompatActivity {
    private static final String url = "http://172.18.68.242:8080/user/login";

    //定义message.what的参数
    private static final int ERROR = 0;
    private static final int OK = 1;
    private static final int NOT_FOUND = 2;

    private EditText sign_in_username = null;
    private EditText sign_in_password = null;
    private Button sign_in = null;
    private TextView forgot_pwd = null;
    private TextView sign_up = null;

    //存储用户名密码
    private SharedPreferences preference;
    private  SharedPreferences.Editor editor;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { // 保存登录成功的用户名密码，并对UI操作
                case OK:
                    Toast.makeText(SignInActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    editor.putString("username", sign_in_username.getText().toString());
                    editor.putString("password", sign_in_password.getText().toString());
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(SignInActivity.this, MainActivity.class);
                    SignInActivity.this.startActivity(intent);
                    break;
                case NOT_FOUND:
                    Toast.makeText(SignInActivity.this, "登录失败，用户名或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(SignInActivity.this, "发生错误，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    //
    private void sendRequesttoserver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) (new URL(url.toString()).openConnection()); // 建立连接
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    final String cookieval = connection.getHeaderField("Set-Cookie");
                    if (cookieval != null) {
                        editor.putString("jsessionid", cookieval);
                        editor.commit();
                    }

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String request_username = sign_in_username.getText().toString();
                    String request_password = sign_in_password.getText().toString();
                    request_username = URLEncoder.encode(request_username, "utf-8"); // 设置编码
                    request_password = URLEncoder.encode(request_password, "utf-8");
                    out.writeBytes("userId=" + request_username + "&password=" + request_password); // 请求格式

                    int code = connection.getResponseCode();
                    Message msg = Message.obtain();
                    switch (code) {
                        case 200:
                            msg.what = OK;
                            handler.sendMessage(msg);
                            break;
                        case 404:
                            msg.what = NOT_FOUND;
                            handler.sendMessage(msg);
                            break;
                        default:
                            msg.what = ERROR;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect(); // 断开连接
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.sysu.yizhu.R.layout.sign_in_layout);

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
                    sendRequesttoserver();
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
}
