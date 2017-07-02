package com.sysu.yizhu.Activity.Business;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.avos.avoscloud.AVInstallation;
import com.sysu.yizhu.Activity.Business.AskHelp.AskHelpFragment;
import com.sysu.yizhu.Activity.Business.AskQuestion.AskQuestionFragment;
import com.sysu.yizhu.Activity.Business.Sos.SosFragment;
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
public class MainActivity extends AppCompatActivity{
    private static final String updateObjectIdUrl = "http://112.74.165.37:8080/user/updateObjectId";

    //fragment
    private SosFragment sos_fragment;
    private AskHelpFragment ask_help_fragment;
    private AskQuestionFragment ask_question_fragment;
    private MyInfoFragment my_info_fragment;

    //为main_content设置默认fragment
    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        sos_fragment = new SosFragment();
        transaction.replace(R.id.main_content, sos_fragment);
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

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.sos, "求救"))
                .addItem(new BottomNavigationItem(R.drawable.help, "求助"))
                .addItem(new BottomNavigationItem(R.drawable.question, "提问"))
                .addItem(new BottomNavigationItem(R.drawable.myinfo, "我"))
                .setActiveColor(R.color.colorPrimary)
                .setFirstSelectedPosition(0)
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                switch (position) {
                    case 0:
                        if (sos_fragment == null)
                            sos_fragment = new SosFragment();
                        transaction.replace(R.id.main_content, sos_fragment);
                        break;
                    case 1:
                        if (ask_help_fragment == null)
                            ask_help_fragment = new AskHelpFragment();
                        transaction.replace(R.id.main_content, ask_help_fragment);
                        break;
                    case 2:
                        if (ask_question_fragment == null)
                            ask_question_fragment = new AskQuestionFragment();
                        transaction.replace(R.id.main_content, ask_question_fragment);
                        break;
                    case 3:
                        if (my_info_fragment == null)
                            my_info_fragment = new MyInfoFragment();
                        transaction.replace(R.id.main_content, my_info_fragment);
                        break;
                }
                transaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

        setDefaultFragment();

        updateObjectId();

        AppManager.getAppManager().finishAllActivity();
        AppManager.getAppManager().addActivity(MainActivity.this);
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
