package com.thisfeng.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.thisfeng.xlibrary.base.BaseFlowTagLayout;

import java.util.List;

/**
 * Created by XY on 2016-08-17.
 */
public class TagLayout extends BaseFlowTagLayout {

    public TagLayout(Context context) {
        super(context, null);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View bindChildView(View contentView, List list, int position) {

        switch (tagType) {
            case 0:
                break;
            default:
                break;
        }

        TextView tv = (TextView) contentView.findViewById(R.id.tv);
        tv.setText(((String) list.get(position)));
        return contentView;
    }

    @Override
    protected int getLayoutId() {
        switch (tagType) {
            case 0:
                return R.layout.tag_layout;
            default:
                return R.layout.layout_blank;
        }
    }


}
