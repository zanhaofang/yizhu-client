package com.sysu.yizhu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;
import android.graphics.Color;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by QianZixuan on 2017/4/19.
 * Description: 注册界面Activity
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String getCodeUrl = "http://172.18.68.242:8080/user/sendSms/:";
    private static final String signUpUrl = "http://172.18.68.242:8080/user/register";

    //定义message.what的参数
    private static final int ERROR = 0;
    private static final int GET_CODE_OK = 1;
    private static final int GET_CODE_FAILED = 2;
    private static final int GET_CODE_FORBIDDEN = 3;
    private static final int SIGN_UP_OK = 4;
    private static final int SIGN_UP_FAILED = 5;
    private static final int SIGN_UP_FORBIDDEN = 6;
    private static final int SIGN_UP_MISS = 7;

    private EditText nameText;
    private Spinner genderSpinner;
    private TextView birthDateText;
    private EditText locationText;
    private EditText phoneNumText;
    private Button getCodeButton;
    private EditText codeText;
    private Button signUpButton;
    private EditText passwordText;
    private EditText retypePasswordText;
    //存储当前年月日
    private int year;
    private int month;
    private int day;

    private String gender = "male";

    //存储用户名密码
    private SharedPreferences preference;
    private  SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        AppManager.getAppManager().addActivity(SignUpActivity.this);

        nameText = (EditText) findViewById(R.id.sign_up_name);
        genderSpinner = (Spinner) findViewById(R.id.sign_up_gender);
        birthDateText = (TextView) findViewById(R.id.sign_up_birthDateText);
        locationText = (EditText) findViewById(R.id.sign_up_location);
        phoneNumText = (EditText) findViewById(R.id.sign_up_phoneNum);
        codeText = (EditText) findViewById(R.id.sign_up_code);
        getCodeButton = (Button) findViewById(R.id.sign_up_getCode);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        passwordText = (EditText) findViewById(R.id.sign_up_password);
        retypePasswordText = (EditText) findViewById(R.id.sign_up_retypePassword);

        //sharedpreference初始化
        preference = getSharedPreferences("info", MODE_PRIVATE);
        editor = preference.edit();

        //初始化年月日
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        setBirthDateText();

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (genderSpinner.getSelectedItem().toString().equals("男"))
                    gender = "male";
                else
                    gender = "female";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        birthDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SignUpActivity.this.year = year;
                        SignUpActivity.this.month = month;
                        SignUpActivity.this.day = dayOfMonth;
                        setBirthDateText();
                    }
                }, year, month, day).show();
            }
        });

        getCodeButton.setOnClickListener(new View.OnClickListener() { //获取验证码-按钮点击事件
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneNumText.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                } else {
                    //发送GET请求验证码
                    getCode();
                    //按钮60s倒计时
                    TimeCount time = new TimeCount(60000, 1000);
                    time.start();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneNumText.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(codeText.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passwordText.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(retypePasswordText.getText())) {
                    Toast.makeText(SignUpActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
                } else if (!passwordText.getText().toString().equals(retypePasswordText.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    //发送POST
                    signUp();
                }
            }
        });
    }
    //显示BirthDate
    private void setBirthDateText() {
        String date = year + "-";
        if (month < 10)
            date = date + "0";
        date = date + (month + 1) + "-";
        if (day < 10)
            date = date + "0";
        date = date + day;
        birthDateText.setText(date);
    }


    class TimeCount extends CountDownTimer { //按钮倒计时类
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getCodeButton.setBackgroundColor(Color.parseColor("#808080"));
            getCodeButton.setClickable(false);
            getCodeButton.setText(millisUntilFinished / 1000 +"秒后重新发送");
        }

        @Override
        public void onFinish() {
            getCodeButton.setText("重新获取验证码");
            getCodeButton.setClickable(true);
            getCodeButton.setBackgroundColor(Color.parseColor("#87CEFA"));

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { // 保存登录成功的用户名密码，并对UI操作
                case ERROR:
                    Toast.makeText(SignUpActivity.this, "发生错误，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case GET_CODE_OK:
                    Toast.makeText(SignUpActivity.this, "验证码已发送到您手机上", Toast.LENGTH_SHORT).show();
                    break;
                case GET_CODE_FAILED:
                    Toast.makeText(SignUpActivity.this, "手机号码已注册", Toast.LENGTH_SHORT).show();
                    break;
                case GET_CODE_FORBIDDEN:
                    Toast.makeText(SignUpActivity.this, "手机号码无效", Toast.LENGTH_SHORT).show();
                    break;
                case SIGN_UP_OK:
                    Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    editor.putString("username", phoneNumText.getText().toString());
                    //editor.putString("password", sign_in_password.getText().toString());
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(SignUpActivity.this, MainActivity.class);
                    SignUpActivity.this.startActivity(intent);
                    break;
                case SIGN_UP_FAILED:
                    Toast.makeText(SignUpActivity.this, "手机号已被注册", Toast.LENGTH_SHORT).show();
                    break;
                case SIGN_UP_FORBIDDEN:
                    Toast.makeText(SignUpActivity.this, "手机号码无效", Toast.LENGTH_SHORT).show();
                    break;
                case SIGN_UP_MISS:
                    Toast.makeText(SignUpActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void getCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) (new URL(getCodeUrl + phoneNumText.getText().toString()).openConnection()); // 建立连接
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);

                    int code = connection.getResponseCode(); // 获取服务器响应
                    Message msg = Message.obtain();
                    switch (code) {
                        case 200:
                            msg.what = GET_CODE_OK;
                            handler.sendMessage(msg);
                            break;
                        case 400:
                            msg.what = GET_CODE_FAILED;
                            handler.sendMessage(msg);
                            break;
                        case 403:
                            msg.what = GET_CODE_FORBIDDEN;
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

    private void signUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    // 请求数据格式
                    String data = "userId=" + URLEncoder.encode(phoneNumText.getText().toString(), "utf-8")
                            + "&password=" + URLEncoder.encode(passwordText.getText().toString(), "utf-8")
                            + "&code=" + URLEncoder.encode(codeText.getText().toString(), "utf-8")
                            + "&name=" + URLEncoder.encode(nameText.getText().toString(), "utf-8")
                            + "&gender=" + URLEncoder.encode(gender, "utf-8")
                            + "&birthDate=" + URLEncoder.encode(birthDateText.getText().toString(), "utf-8")
                            + "&location=" + URLEncoder.encode(locationText.getText().toString(), "utf-8");

                    connection = (HttpURLConnection) (new URL(signUpUrl).openConnection()); // 建立连接
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);

                    // 设置请求的头
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));

                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    /*final String cookieval = connection.getHeaderField("Set-Cookie");
                    if (cookieval != null) {
                        editor.putString("jsessionid", cookieval);
                        editor.commit();
                    }*/

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(data.getBytes());
                    out.flush();
                    out.close();

                    int code = connection.getResponseCode(); // 获取服务器响应
                    Message msg = Message.obtain();
                    switch (code) {
                        case 200:
                            msg.what = SIGN_UP_OK;
                            handler.sendMessage(msg);
                            break;
                        case 400:
                            msg.what = SIGN_UP_FAILED;
                            handler.sendMessage(msg);
                            break;
                        case 403:
                            msg.what = SIGN_UP_FORBIDDEN;
                            handler.sendMessage(msg);
                            break;
                        case 450:
                            msg.what = SIGN_UP_MISS;
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

}
