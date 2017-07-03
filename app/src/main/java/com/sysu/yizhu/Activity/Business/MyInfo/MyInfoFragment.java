package com.sysu.yizhu.Activity.Business.MyInfo;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.yizhu.R;
import com.sysu.yizhu.Util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/30.
 * Description: 个人信息fragment
 */
public class MyInfoFragment extends Fragment {
    private static final String getInfoUrl = "http://112.74.165.37:8080/user/info";
    private static final String modifyInfoUrl = "http://112.74.165.37:8080/user/modifyInfo";

    //存储当前年月日
    private int year;
    private int month;
    private int day;

    private int gender_choice;

    private String userName = "";
    private String gender = "";
    private String birthDate = "";
    private String location = "";
    private String phoneNum = "";

    private RelativeLayout my_info_userName;
    private RelativeLayout my_info_gender;
    private RelativeLayout my_info_birthDate;
    private RelativeLayout my_info_location;

    private TextView my_info_userName_content;
    private TextView my_info_gender_content;
    private TextView my_info_birthDate_content;
    private TextView my_info_location_content;
    private TextView my_info_phoneNum_content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_info_layout, container, false);

        my_info_userName = (RelativeLayout) view.findViewById(R.id.my_info_userName);
        my_info_gender = (RelativeLayout) view.findViewById(R.id.my_info_gender);
        my_info_birthDate = (RelativeLayout) view.findViewById(R.id.my_info_birthDate);
        my_info_location = (RelativeLayout) view.findViewById(R.id.my_info_location);

        my_info_userName_content = (TextView) view.findViewById(R.id.my_info_userName_content);
        my_info_gender_content = (TextView) view.findViewById(R.id.my_info_gender_content);
        my_info_birthDate_content = (TextView) view.findViewById(R.id.my_info_birthDate_content);
        my_info_location_content = (TextView) view.findViewById(R.id.my_info_location_content);
        my_info_phoneNum_content = (TextView) view.findViewById(R.id.my_info_phoneNum_content);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        my_info_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    View dialogView = factory.inflate(R.layout.my_info_modify_layout, null);

                    final EditText my_info_modify_content = (EditText) dialogView.findViewById(R.id.my_info_modify_content);
                    my_info_modify_content.setText(userName);

                    new AlertDialog.Builder(getActivity())
                            .setTitle("姓名")
                            .setView(dialogView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userName = my_info_modify_content.getText().toString();
                                    modifyInfo();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            }
        });

        my_info_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    if (gender.equals("male")) {
                        gender_choice = 0;
                    } else {
                        gender_choice = 1;
                    }

                    new AlertDialog.Builder(getActivity())
                            .setTitle("性别")
                            /*.setIcon(R.drawable.add)*/
                            .setSingleChoiceItems(R.array.gender, gender_choice, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    gender_choice = which;
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (gender_choice) {
                                        case 0:
                                            gender = "male";
                                            break;
                                        case 1:
                                            gender = "female";
                                            break;
                                        default:
                                            break;
                                    }
                                    modifyInfo();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            }
        });

        my_info_birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date date = new Date(year - 1900, month, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        birthDate = format.format(date);
                        modifyInfo();
                    }
                }, year, month, day);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        my_info_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    View dialogView = factory.inflate(R.layout.my_info_modify_layout, null);

                    final EditText my_info_modify_content = (EditText) dialogView.findViewById(R.id.my_info_modify_content);
                    my_info_modify_content.setText(location);

                    new AlertDialog.Builder(getActivity())
                            .setTitle("常居地")
                            .setView(dialogView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    location = my_info_modify_content.getText().toString();
                                    modifyInfo();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            }
        });

        getInfo(); //默认显示个人信息

        return view;
    }

    private void resultAnalysis(String string) { //解析JSON数据并更新UI
        JSONObject object = null;
        try {
            object = new JSONObject(string);

            userName = object.optString("name");
            gender = object.optString("gender");
            birthDate = object.optString("birthDate");
            location = object.optString("location");
            phoneNum = object.optString("userId");

            my_info_phoneNum_content.setText(phoneNum);
            my_info_userName_content.setText(userName);
            if(gender.equals("male")) {
                my_info_gender_content.setText("男");
            } else {
                my_info_gender_content.setText("女");
            }
            my_info_birthDate_content.setText(birthDate);
            my_info_location_content.setText(location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        HttpUtil.post(getInfoUrl, params, new HttpUtil.HttpResponseCallBack() {
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
        params.put("name", userName);
        params.put("gender", gender);
        params.put("birthDate", birthDate);
        params.put("location", location);
        HttpUtil.post(modifyInfoUrl, params, new HttpUtil.HttpResponseCallBack() {
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
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "修改失败！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String result, Exception e) {

            }
        });
    }
}
