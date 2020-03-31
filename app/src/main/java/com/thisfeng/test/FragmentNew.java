package com.thisfeng.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.thisfeng.xlibrary.base.BaseLazyFragment;

/**
 * Created by XY on 2017-6-29.
 */

public class FragmentNew extends BaseLazyFragment {
    @Override
    protected boolean useEventBus() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, container, false);
        return view;
    }
    @Override
    protected void onFirstShow() {

    }
}
