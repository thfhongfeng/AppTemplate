package com.pine.user.remote.atlas;

import android.os.Bundle;
import android.taobao.atlas.remote.IRemote;

import com.pine.router.impl.atlas.AtlasRouterBundleRemote;
import com.pine.user.remote.UserUiRemoteService;

import java.lang.reflect.Method;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class UserAtlasUiRemote extends AtlasRouterBundleRemote<UserUiRemoteService> implements IRemote {

    private static UserUiRemoteService mRemoteService;
    private static Method[] mMethods;

    static {
        mRemoteService = new UserUiRemoteService();
        Class clazz = mRemoteService.getClass();
        mMethods = clazz.getMethods();
    }

    @Override
    public Bundle call(String commandName, Bundle args, final IResponse callback) {
        return call(mRemoteService, mMethods, realHost, commandName, args, callback);
    }

    @Override
    public <T> T getRemoteInterface(Class<T> interfaceClass, Bundle args) {
        return null;
    }
}
