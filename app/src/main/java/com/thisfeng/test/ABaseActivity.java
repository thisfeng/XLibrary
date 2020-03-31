package com.thisfeng.test;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.thisfeng.xlibrary.base.XyBaseActivity;

/**
 * Created by XY on 2016-11-01.
 */

public  class ABaseActivity extends XyBaseActivity {
    @Override
    protected int setActivityLayout() {
        return 0;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {

    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }
}
