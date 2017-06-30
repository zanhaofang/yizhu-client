package com.sysu.yizhu.Activity.Business;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVInstallation;
import com.sysu.yizhu.Activity.Business.AskHelp.AskHelpFragment;
import com.sysu.yizhu.Activity.Business.AskQuestion.AskQuestionFragment;
import com.sysu.yizhu.Activity.Business.HotkeyHelp.HotkeyHelpFragment;
import com.sysu.yizhu.Activity.Business.MyInfo.MyInfoFragment;
import com.sysu.yizhu.Activity.Login.SignInActivity;
import com.sysu.yizhu.R;
import com.sysu.yizhu.UserData;
import com.sysu.yizhu.Util.AppManager;
import com.sysu.yizhu.Util.HttpUtil;

import java.util.HashMap;

/**
 * Created by QianZixuan on 2017/4/19.
 * Description: 主页面Activity
 */
public class MainActivity extends AppCompatActivity implements OnClickListener{
    private static final String updateObjectIdUrl = "http://112.74.165.37:8080/user/updateObjectId";

    //下方bar的按钮
    private Button hotkey_help_button;
    private Button ask_help_button;
    private Button ask_question_button;
    private Button my_info_button;

    //fragment
    private HotkeyHelpFragment hotkey_help_fragment;
    private AskHelpFragment ask_help_fragment;
    private AskQuestionFragment ask_question_fragment;
    private MyInfoFragment my_info_fragment;

    //为main_content设置默认fragment
    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hotkey_help_fragment = new HotkeyHelpFragment();
        transaction.replace(R.id.main_content, hotkey_help_fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        AppManager.getAppManager().finishAllActivity();
        AppManager.getAppManager().addActivity(MainActivity.this);

        //初始化按钮并设置点击事件
        hotkey_help_button = (Button) findViewById(R.id.hotkey_help);
        ask_help_button = (Button) findViewById(R.id.ask_help);
        ask_question_button = (Button) findViewById(R.id.ask_question);
        my_info_button = (Button) findViewById(R.id.my_info);

        hotkey_help_button.setOnClickListener(this);
        ask_help_button.setOnClickListener(this);
        ask_question_button.setOnClickListener(this);
        my_info_button.setOnClickListener(this);

        setDefaultFragment();

        updateObjectId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                UserData.getInstance().setLoginState(false);
                Intent intent = new Intent();
                intent.setClass( MainActivity.this, SignInActivity.class);
                MainActivity.this.startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        //按照id处理点击事件
        switch (v.getId()) {
            case R.id.hotkey_help:
                if (hotkey_help_fragment == null)
                    hotkey_help_fragment = new HotkeyHelpFragment();
                transaction.replace(R.id.main_content, hotkey_help_fragment);
                break;
            case R.id.ask_help:
                if (ask_help_fragment == null)
                    ask_help_fragment = new AskHelpFragment();
                transaction.replace(R.id.main_content, ask_help_fragment);
                break;
            case R.id.ask_question:
                if (ask_question_fragment == null)
                    ask_question_fragment = new AskQuestionFragment();
                transaction.replace(R.id.main_content, ask_question_fragment);
                break;
            case R.id.my_info:
                if (my_info_fragment == null)
                    my_info_fragment = new MyInfoFragment();
                transaction.replace(R.id.main_content, my_info_fragment);
                break;
        }
        transaction.commit();
    }

    private void updateObjectId() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("objectId", AVInstallation.getCurrentInstallation().getObjectId());
        HttpUtil.post(updateObjectIdUrl, params, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(int code, String result) {
                switch (code) {
                    case 200:
                        Toast.makeText(MainActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(MainActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(MainActivity.this, "objectId不存在", Toast.LENGTH_SHORT).show();
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
