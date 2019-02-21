package com.pine.template;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.taobao.atlas.framework.Atlas;
import android.util.Log;

import com.pine.base.BaseApplication;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * Created by tanghongfeng on 2018/7/3.
 */

public class TemplateApplication extends Application {
    private static final String TAG = LogUtils.makeLogTag(TemplateApplication.class);

    @Override
    public void onCreate() {
        super.onCreate();

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        LogUtils.setDebuggable(AppUtils.isApkDebuggable(this));

        Atlas.getInstance().addBundleListener(new BundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
                Log.d(TAG, "event Type: " + event.getType());
                Log.d(TAG, "event Bundle: Location=" + event.getBundle().getLocation() +
                        ", BundleId=" + event.getBundle().getBundleId() + ", State=" + event.getBundle().getState());
                switch (event.getType()) {
                    case BundleEvent.INSTALLED:
                        break;
                }
            }
        });

        BaseApplication.init(this);
    }

    @Override
    public void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
    }
}
