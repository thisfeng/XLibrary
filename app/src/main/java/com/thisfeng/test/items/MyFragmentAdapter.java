package com.thisfeng.test.items;

import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thisfeng.test.FragmentNew;


/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {

    public static final int FRAGMENT_MY_MERCHANT = 0;
    public static final int FRAGMENT_RECEIVE = 1;
    private SparseArray<FragmentNew> fragments;

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new SparseArray<>();
    }

    @Override
    public Fragment getItem(int position) {
        FragmentNew fragment = fragments.get(position);
        if (fragment == null) {
            fragment = new FragmentNew();
            fragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case FRAGMENT_MY_MERCHANT:
                return "11111";
            case FRAGMENT_RECEIVE:
            default:
                return "22222";
        }
    }


}
