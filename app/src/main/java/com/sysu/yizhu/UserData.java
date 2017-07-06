package com.sysu.yizhu;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户数据的类
 * Created by QianZixuan on 2017/6/30.
 */
public class UserData {
    private static SharedPreferences preference;
    private static SharedPreferences.Editor editor;

    private static UserData instance;

    private static String UserId;
    private static String Password;
    private static String JsessionId;
    private static boolean LoginState;

    private UserData() {
        preference = MyApplication.getContext().getSharedPreferences("userData", Context.MODE_PRIVATE);//存储的文件名
        editor = preference.edit();

        UserId = preference.getString("UserId", "");
        Password = preference.getString("Password", "");
        JsessionId = preference.getString("JsessionId", "");
        LoginState = preference.getBoolean("LoginState", false);
    }

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public String getUserId() {
        return UserId;
    }

    public String getPassword() {
        return Password;
    }

    public String getJsessionId() {
        return JsessionId;
    }

    public boolean isLogin() {
        return LoginState;
    }

    public void setUserId(String userId) {
        UserId = userId;
        editor.putString("UserId", userId);
        editor.commit();
    }

    public void setPassword(String password) {
        Password = password;
        editor.putString("Password", password);
        editor.commit();
    }

    public void setJsessionId(String jsessionId) {
        JsessionId = jsessionId;
        editor.putString("JsessionId", jsessionId);
        editor.commit();
    }

    public void setLoginState(boolean loginState) {
        LoginState = loginState;
        editor.putBoolean("LoginState", loginState);
        editor.commit();
    }
}
