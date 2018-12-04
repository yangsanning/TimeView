package ysn.com.timeview.app;

import android.app.Application;
import android.content.Context;

import ysn.com.timeview.util.ResUtil;

/**
 * @Author yangsanning
 * @ClassName MyApplication
 * @Description 一句话概括作用
 * @Date 2018/12/4
 * @History 2018/12/4 author: description:
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initialize();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void initialize() {
        ResUtil.inject(mInstance);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
