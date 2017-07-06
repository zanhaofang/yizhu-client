package com.sysu.yizhu;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.sysu.yizhu.Activity.Business.MainActivity;

/**
 * Created by CrazeWong on 2017/6/29.
 */
public class MyApplication extends Application {
    private static final String LC_APP_ID = "CpwcohsMmxb9sUanQTOBD827-gzGzoHsz";
    private static final String LC_APP_KEY = "aOzT095oxFBfJMn8EptrQlFW";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        AVOSCloud.initialize(this, LC_APP_ID, LC_APP_KEY);

        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    System.out.println(installationId);
                    // 获取ObjectId
                    System.out.println(AVInstallation.getCurrentInstallation().getObjectId());
                } else {
                }
            }
        });

        PushService.setDefaultPushCallback(this, MainActivity.class);
    }

    public static Context getContext() {
        return context;
    }
}
