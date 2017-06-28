package com.sysu.yizhu.Activity;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 个人信息fragment
 */
public class MyInfoFragment extends Fragment {
    private static final String getInfoUrl = "http://112.74.165.37:8080/user/info";
    private static final String modifyInfoUrl = "http://112.74.165.37:8080/user/modifyInfo";

    private static final int DISPLAY = 0;
    private static final int MODIFY = 1;

    private int stateFlag;

    private EditText nameText;
    private Spinner genderSpinner;
    private TextView birthDateText;
    private EditText locationText;
    private EditText phoneNumText;
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

        stateFlag = DISPLAY; //按钮flag初始化

        //初始化控件
        nameText = (EditText) view.findViewById(R.id.my_info_name);
        genderSpinner = (Spinner) view.findViewById(R.id.my_info_gender);
        birthDateText = (TextView) view.findViewById(R.id.my_info_birthDateText);
        locationText = (EditText) view.findViewById(R.id.my_info_location);
        phoneNumText = (EditText) view.findViewById(R.id.my_info_phoneNum);
        myInfoButton = (Button) view.findViewById(R.id.my_info_button);

        //sharedpreference初始化
        preference = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        editor = preference.edit();

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
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        MyInfoFragment.this.year = year;
                        MyInfoFragment.this.month = month;
                        MyInfoFragment.this.day = dayOfMonth;
                        setBirthDateText();
                    }
                }, year, month, day).show();
            }
        });

        myInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateFlag == DISPLAY) { //开始编辑个人信息
                    stateFlag = MODIFY;

                    nameText.setEnabled(true);
                    genderSpinner.setEnabled(true);
                    birthDateText.setEnabled(true);
                    locationText.setEnabled(true);

                    myInfoButton.setText("保存");
                } else if (stateFlag == MODIFY) { //提交保存个人信息
                    stateFlag = DISPLAY;

                    modifyInfo();
                    getInfo();

                    myInfoButton.setText("编辑");
                }
            }
        });

        getInfo(); //默认显示个人信息

        return view;
    }

    private void setBirthDateText() {
        String date = year + "-";
        if (month < 10)
            date = date + "0";
        date = date + (month + 1) + "-";
        if (day < 10)
            date = date + "0";
        date = date + day;
        birthDateText.setText(date);
    };

    private void resultAnalysis(String string) { //解析JSON数据
        JSONObject object = null;
        try {
            object = new JSONObject(string);
            phoneNumText.setText(object.optString("userId"));
            nameText.setText(object.optString("name"));

            if(object.optString("gender").equals("male")) {
                genderSpinner.setSelection(0);
            } else {
                genderSpinner.setSelection(1);
            }

            birthDateText.setText(object.optString("birthDate"));
            locationText.setText(object.optString("location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getInfo() {
        nameText.setEnabled(false);
        genderSpinner.setEnabled(false);
        birthDateText.setEnabled(false);
        locationText.setEnabled(false);
        phoneNumText.setEnabled(false);

        HashMap<String, String> params = new HashMap<String, String>();
        HttpUtil.post(getInfoUrl, preference.getString("jsessionid", ""), params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        resultAnalysis(result);
                        /*if (getActivity() != null)
                            Toast.makeText(getActivity(), "获取成功！", Toast.LENGTH_SHORT).show();*/
                        break;
                    case 401:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "未登录！请重新登录", Toast.LENGTH_SHORT).show();
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

    private void modifyInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", nameText.getText().toString());
        params.put("gender", gender);
        params.put("birthDate", birthDateText.getText().toString());
        params.put("location", locationText.getText().toString());
        HttpUtil.post(modifyInfoUrl, preference.getString("jsessionid", ""), params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        getInfo();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "未登录！", Toast.LENGTH_SHORT).show();
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
}
