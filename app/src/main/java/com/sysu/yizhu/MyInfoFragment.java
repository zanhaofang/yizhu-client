package com.sysu.yizhu;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 个人信息fragment
 */
public class MyInfoFragment extends Fragment {
    private static final String getInfoUrl = "http://172.18.68.242:8080/user/info";
    private static final String modifyInfoUrl = "http://172.18.68.242:8080/user/modifyInfo";

    //定义message.what的参数
    private static final int ERROR = 0;
    private static final int GET_INFO_OK = 1;
    private static final int GET_INFO_FAILED = 2;
    private static final int GET_INFO_FORBIDDEN = 3;
    private static final int MODIFY_INFO_OK = 4;
    private static final int MODIFY_INFO_FAILED = 5;
    private static final int MODIFY_INFO_FORBIDDEN = 6;

    private EditText nameText;
    private Spinner genderSpinner;
    private TextView birthDateText;
    private EditText locationText;
    private EditText phoneNumText;
    private EditText passwordText;
    private EditText retypePasswordText;
    private Button myInfoButton;

    //存储当前年月日
    private int year;
    private int month;
    private int day;

    private String gender = "male";

    //存储用户名密码
    private SharedPreferences preference;
    private  SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_info_layout, container, false);

        //初始化控件
        nameText = (EditText) view.findViewById(R.id.my_info_name);
        genderSpinner = (Spinner) view.findViewById(R.id.my_info_gender);
        birthDateText = (TextView) view.findViewById(R.id.my_info_birthDateText);
        locationText = (EditText) view.findViewById(R.id.my_info_location);
        phoneNumText = (EditText) view.findViewById(R.id.my_info_phoneNum);
        passwordText = (EditText) view.findViewById(R.id.my_info_password);;
        retypePasswordText = (EditText) view.findViewById(R.id.my_info_retypePassword);;
        myInfoButton = (Button) view.findViewById(R.id.my_info_button);

        getInfo();

        return view;
    }

    private void getInfo() {

    }
}
