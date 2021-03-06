package com.pine.mvc.remote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.mvc.ui.activity.MvcHomeActivity;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterAnnotation;
import com.pine.router.command.RouterMvcCommand;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvcUiRemoteService {

    @RouterAnnotation(CommandName = RouterMvcCommand.goMvcHomeActivity)
    public void goBusinessHomeActivity(@NonNull Activity activity, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(activity, MvcHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
