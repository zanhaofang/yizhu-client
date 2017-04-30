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
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by QianZixuan on 2017/4/19.
 * Description: 注册界面Activity
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String url = "http://172.18.68.242:8080/user/register";

    //定义message.what的参数
    private static final int ERROR = 0;
    private static final int OK = 1;
    private static final int FORBIDDEN = 2;

    private EditText sign_up_username = null;
    private EditText sign_up_password = null;
    private EditText retype_sign_up_password = null;
    private Button sign_up_submit = null;

    //sharedpreference用以存储
    private SharedPreferences preference;
    private  SharedPreferences.Editor editor;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { //保存登录成功的用户名密码，并对UI操作
                case OK:
                    Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    editor.putString("username", sign_up_username.getText().toString());
                    editor.putString("password", sign_up_password.getText().toString());
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(SignUpActivity.this, MainActivity.class);
                    SignUpActivity.this.startActivity(intent);
                    break;
                case FORBIDDEN:
                    Toast.makeText(SignUpActivity.this, "用户名不是手机号", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(SignUpActivity.this, "发生错误，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void sendRequesttoserver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) (new URL(url.toString()).openConnection()); //建立连接
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    final String cookieval = connection.getHeaderField("Set-Cookie");
                    if (cookieval != null) {
                        editor.putString("jsessionid", cookieval);
                        editor.commit();
                    }

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String request_username = sign_up_username.getText().toString();
                    String request_password = sign_up_password.getText().toString();
                    request_username = URLEncoder.encode(request_username, "utf-8");
                    request_password = URLEncoder.encode(request_password, "utf-8");
                    out.writeBytes("userId=" + request_username + "&password=" + request_password); // 请求格式

                    int code = connection.getResponseCode();
                    Message msg = Message.obtain();
                    switch (code) {
                        case 200:
                            msg.what = OK;
                            handler.sendMessage(msg);
                            break;
                        case 403:
                            msg.what = FORBIDDEN;
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
        setContentView(com.sysu.yizhu.R.layout.sign_up_layout);

        AppManager.getAppManager().addActivity(SignUpActivity.this);

        sign_up_username = (EditText) findViewById(R.id.sign_up_username);
        sign_up_password = (EditText) findViewById(R.id.sign_up_password);
        retype_sign_up_password = (EditText) findViewById(R.id.retype_sign_up_password);
        sign_up_submit = (Button) findViewById(R.id.sign_up_submit);

        preference = getSharedPreferences("info", MODE_PRIVATE);
        editor = preference.edit();

        sign_up_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sign_up_username.getText())) {
                    Toast.makeText(SignUpActivity.this, "请填写用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sign_up_password.getText())) {
                    Toast.makeText(SignUpActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(retype_sign_up_password.getText())) {
                    Toast.makeText(SignUpActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
                } else if (!sign_up_password.getText().toString().equals(retype_sign_up_password.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "两次填写密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    sendRequesttoserver();
                }
            }
        });
    }
}
