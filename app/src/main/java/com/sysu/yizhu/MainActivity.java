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
    private Button myinfo_button;

    //fragment
    private MyinfoFragment myinfo_fragment;

    //为main_content设置默认fragment
    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        myinfo_fragment = new MyinfoFragment();
        transaction.replace(R.id.main_content, myinfo_fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        AppManager.getAppManager().finishAllActivity();
        AppManager.getAppManager().addActivity(MainActivity.this);

        //初始化按钮并设置点击事件
        myinfo_button = (Button) findViewById(R.id.my_info);
        myinfo_button.setOnClickListener(this);

        setDefaultFragment();
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        //按照id处理点击事件
        switch (v.getId()) {
            case R.id.my_info:
                if (myinfo_fragment == null)
                    myinfo_fragment = new MyinfoFragment();
                transaction.replace(R.id.main_content, myinfo_fragment);
                break;
        }
        transaction.commit();
    }
}
