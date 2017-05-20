package com.sysu.yizhu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by QianZixuan on 2017/4/19.
 * Description: 主页面Activity
 */
public class MainActivity extends AppCompatActivity implements OnClickListener{
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
}
