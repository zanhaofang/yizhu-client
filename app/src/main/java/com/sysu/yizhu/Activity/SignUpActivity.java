package com.sysu.yizhu.Activity;

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

import com.sysu.yizhu.R;
import com.sysu.yizhu.UserData;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/19.
 * Description: 注册界面Activity
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String getCodeUrl = "http://112.74.165.37:8080/user/sendSms/";
    private static final String signUpUrl = "http://112.74.165.37:8080/user/register";

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

    private void getCode() {
        HttpUtil.get(getCodeUrl + phoneNumText.getText().toString(), new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        Toast.makeText(SignUpActivity.this, "验证码已发送到您手机上", Toast.LENGTH_SHORT).show();
                        break;
                    case 400:
                        Toast.makeText(SignUpActivity.this, "手机号码已注册", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        Toast.makeText(SignUpActivity.this, "手机号码无效", Toast.LENGTH_SHORT).show();
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

    private void signUp() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", phoneNumText.getText().toString());
        params.put("password", passwordText.getText().toString());
        params.put("code", codeText.getText().toString());
        params.put("name", nameText.getText().toString());
        params.put("gender", gender);
        params.put("birthDate", birthDateText.getText().toString());
        params.put("location", locationText.getText().toString());
        HttpUtil.post(signUpUrl, params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        resultAnalysis(result);
                        Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(SignUpActivity.this, MainActivity.class);
                        SignUpActivity.this.startActivity(intent);
                        break;
                    case 400:
                        Toast.makeText(SignUpActivity.this, "手机号已被注册", Toast.LENGTH_SHORT).show();
                        break;
                    case 403:
                        Toast.makeText(SignUpActivity.this, "手机号码无效", Toast.LENGTH_SHORT).show();
                        break;
                    case 450:
                        Toast.makeText(SignUpActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
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

            UserData.getInstance().setUserId(phoneNumText.getText().toString());
            UserData.getInstance().setPassword(passwordText.getText().toString());
            UserData.getInstance().setLoginState(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
