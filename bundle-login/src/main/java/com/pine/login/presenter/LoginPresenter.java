package com.pine.login.presenter;

import android.text.TextUtils;

import com.pine.base.mvp.presenter.BasePresenter;
import com.pine.login.R;
import com.pine.login.contract.ILoginContract;
import com.pine.login.manager.LoginManager;
import com.pine.tool.util.RegexUtils;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class LoginPresenter extends BasePresenter<ILoginContract.Ui> implements ILoginContract.Presenter {

    @Override
    public void login() {
        String mobile = getUi().getUserMobile();
        String pwd = getUi().getUserPassword();
        if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd)) {
            getUi().showLoginResultToast(getContext().getString(R.string.login_input_empty_msg));
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(mobile)) {
            getUi().showLoginResultToast(getContext().getString(R.string.login_mobile_incorrect_format));
            return;
        }
        LoginManager.login(mobile, pwd, new LoginManager.Callback() {
            @Override
            public void onLoginResponse(boolean isSuccess, String msg) {
                if (isUiAlive()) {
                    if (!isSuccess) {
                        getUi().showLoginResultToast(getContext().getString(R.string.login_login_fail));
                    } else {
                        finishUi();
                    }
                }
            }
        });
    }

    @Override
    public void onUiState(int state) {

    }
}
