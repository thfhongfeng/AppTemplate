package com.pine.base.component.image_selector;

import android.app.Activity;
import android.content.Intent;

import com.pine.base.component.image_selector.bean.ImageItemBean;
import com.pine.base.component.image_selector.ui.ImageDisplayActivity;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/11/2
 */

public class ImageViewer {
    public static final String INTENT_CUR_POSITION = "intent_cur_position";
    public static final String INTENT_SELECTED_IMAGE_LIST = "intent_selected_image_list";
    public static final String INTENT_MAX_SELECTED_COUNT = "intent_max_select_count";
    public static final String INTENT_IMAGE_LIST = "intent_image_list";
    public static final String INTENT_IMAGE_BEAN_LIST = "intent_image_bean_list";
    public static final String INTENT_CAN_SELECT = "intent_can_select";
    public static final String INTENT_RETURN_TYPE = "intent_return_type";

    private int mMaxCount = 9;
    private int mCurPosition = 0;
    private boolean mCanSelect = false;
    private ArrayList<String> mOriginData;
    private ArrayList<ImageItemBean> mOriginBeanData;
    private ArrayList<String> mSelectedData;

    private ImageViewer() {
    }

    public static ImageViewer create() {
        return new ImageViewer();
    }

    public ImageViewer canSelect(boolean canSelect) {
        mCanSelect = canSelect;
        return this;
    }

    public ImageViewer position(int position) {
        mCurPosition = position;
        return this;
    }

    public ImageViewer count(int count) {
        mMaxCount = count;
        return this;
    }

    public ImageViewer origin(ArrayList<String> images) {
        mOriginData = images;
        return this;
    }

    public ImageViewer originBean(ArrayList<ImageItemBean> beans) {
        mOriginBeanData = beans;
        return this;
    }

    public ImageViewer selected(ArrayList<String> images) {
        mSelectedData = images;
        return this;
    }

    public void start(Activity activity) {
        if (mOriginData != null || mOriginBeanData != null || mSelectedData != null) {
            Intent intent = new Intent(activity, ImageDisplayActivity.class);
            if (mCanSelect) {
                intent.putExtra(INTENT_CAN_SELECT, mCanSelect);
                intent.putExtra(INTENT_MAX_SELECTED_COUNT, mMaxCount);
                intent.putStringArrayListExtra(INTENT_SELECTED_IMAGE_LIST, mSelectedData);
            }
            if (mOriginBeanData != null) {
                intent.putExtra(INTENT_IMAGE_BEAN_LIST, mOriginBeanData);
            } else if (mOriginData != null) {
                intent.putExtra(INTENT_IMAGE_LIST, mOriginData);
            }
            intent.putExtra(INTENT_CUR_POSITION, mCurPosition);
            activity.startActivity(intent);
        }
    }

    public void start(Activity activity, int requestCode) {
        if (mOriginData != null || mOriginBeanData != null || mSelectedData != null) {
            Intent intent = new Intent(activity, ImageDisplayActivity.class);
            if (mCanSelect) {
                intent.putExtra(INTENT_CAN_SELECT, mCanSelect);
                intent.putExtra(INTENT_MAX_SELECTED_COUNT, mMaxCount);
                intent.putStringArrayListExtra(INTENT_SELECTED_IMAGE_LIST, mSelectedData);
            }
            if (mOriginBeanData != null) {
                intent.putExtra(INTENT_IMAGE_BEAN_LIST, mOriginBeanData);
            } else if (mOriginData != null) {
                intent.putExtra(INTENT_IMAGE_LIST, mOriginData);
            }
            intent.putExtra(INTENT_CUR_POSITION, mCurPosition);
            activity.startActivityForResult(intent, requestCode);
        }
    }
}