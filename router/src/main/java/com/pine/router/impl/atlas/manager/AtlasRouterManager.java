package com.pine.router.impl.atlas.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.taobao.atlas.remote.IRemoteTransactor;
import android.taobao.atlas.remote.RemoteFactory;
import android.taobao.atlas.remote.transactor.RemoteTransactor;

import com.pine.config.switcher.ConfigBundleSwitcher;
import com.pine.router.IRouterCallback;
import com.pine.router.R;
import com.pine.router.RouterConstants;
import com.pine.router.impl.IRouterManager;
import com.pine.tool.util.LogUtils;

import static com.pine.router.RouterConstants.TYPE_DATA_COMMAND;
import static com.pine.router.RouterConstants.TYPE_OP_COMMAND;
import static com.pine.router.RouterConstants.TYPE_UI_COMMAND;

/**
 * Created by tanghongfeng on 2019/1/14
 */

public abstract class AtlasRouterManager implements IRouterManager {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());

    private void callCommand(final String commandType, final Activity activity, final String commandName,
                             final Bundle args, final IRouterCallback callback) {
        if (getRemoteIntent(commandType) == null) {
            LogUtils.releaseLog(TAG, "this intent of " + getBundleKey() + " is null");
            if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_INVALID, "intent is null")) {
                onCommandFail(commandType, activity, IRouterManager.FAIL_CODE_INVALID, "");
            }
            return;
        }
        if (!ConfigBundleSwitcher.isBundleOpen(getBundleKey())) {
            LogUtils.releaseLog(TAG, getBundleKey() + " is not opened");
            if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_INVALID,
                    activity.getString(R.string.router_bundle_not_open))) {
                onCommandFail(commandType, activity, IRouterManager.FAIL_CODE_INVALID, activity.getString(R.string.router_bundle_not_open));
            }
            return;
        }
        RemoteFactory.requestRemote(RemoteTransactor.class, activity, getRemoteIntent(commandType),
                new RemoteFactory.OnRemoteStateListener<RemoteTransactor>() {

                    @Override
                    public void onRemotePrepared(RemoteTransactor remote) {
                        remote.call(commandName, args, new IRemoteTransactor.IResponse() {
                            @Override
                            public void OnResponse(Bundle bundle) {
                                if (callback != null) {
                                    switch (bundle.getString(RouterConstants.REMOTE_CALL_STATE_KEY)) {
                                        case RouterConstants.ON_SUCCEED:
                                            callback.onSuccess(bundle.getBundle(RouterConstants.REMOTE_CALL_RESULT_KEY));
                                            break;
                                        default:
                                            if (!callback.onFail(IRouterManager.FAIL_CODE_ERROR,
                                                    bundle.getBundle(RouterConstants.REMOTE_CALL_RESULT_KEY)
                                                            .getString(RouterConstants.REMOTE_CALL_FAIL_MESSAGE_KEY))) {
                                                onCommandFail(commandType, activity, IRouterManager.FAIL_CODE_ERROR,
                                                        bundle.getBundle(RouterConstants.REMOTE_CALL_RESULT_KEY)
                                                                .getString(RouterConstants.REMOTE_CALL_FAIL_MESSAGE_KEY));
                                            }
                                            break;
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailed(String errorInfo) {
                        LogUtils.releaseLog(TAG, "request " + getBundleKey() + " onFailed");
                        if (callback != null && !callback.onFail(IRouterManager.FAIL_CODE_LOST, errorInfo)) {
                            onCommandFail(commandType, activity, IRouterManager.FAIL_CODE_LOST, errorInfo);
                        }
                    }
                });
    }

    @Override
    public void callUiCommand(final Activity activity, final String commandName,
                              final Bundle args, final IRouterCallback callback) {
        callCommand(TYPE_UI_COMMAND, activity, commandName, args, callback);
    }

    @Override
    public void callDataCommand(final Activity activity, final String commandName,
                                final Bundle args, final IRouterCallback callback) {
        callCommand(TYPE_DATA_COMMAND, activity, commandName, args, callback);
    }

    @Override
    public void callOpCommand(final Activity activity, final String commandName,
                              final Bundle args, final IRouterCallback callback) {
        callCommand(TYPE_OP_COMMAND, activity, commandName, args, callback);
    }

    public abstract String getBundleKey();

    protected abstract Intent getRemoteIntent(String commandType);

    protected abstract void onCommandFail(String commandType, Context context, int failCode, String message);
}
