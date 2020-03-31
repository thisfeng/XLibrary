package com.thisfeng.xlibrary.utils.crashUtil;

import androidx.annotation.LayoutRes;

/**
 * Created by XY on 2017-06-05.
 */

public interface ICrash {
    @LayoutRes
    int getLayoutId();

    void setViews(CrashActivity activity, CrashItem crashItem);

    /**
     * 是否保存写入本地data/data/com...文件
     *
     * @return true
     */
    boolean getIsSaveCrashLogFile();
}
