package com.thisfeng.test;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.thisfeng.xlibrary.base.XyBaseActivity;
import com.thisfeng.xlibrary.interfaces.Interfaces;
import com.thisfeng.xlibrary.uiKit.views.nicespinner.NiceSpinner;
import com.thisfeng.xlibrary.utils.Tools;
import com.thisfeng.xlibrary.utils.imageCompress.ImageCompressTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/26 0026.
 */
public class TestA extends XyBaseActivity implements ImageCompressTask.CompressListener{
    NiceSpinner<String> one;
    NiceSpinner<String> two;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int setActivityLayout() {
        return 0;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {
//one = (NiceSpinner) findViewById(R.id.test_one);
        //two = (NiceSpinner) findViewById(R.id.test_two);
        // button = (Button) findViewById(R.id.btn);
        // button.setOnClickListener((view) -> PhotoSelectActivity.startForResult(getThis(), PhotoSelectActivity.class, PhotoSelectBaseActivity.PhotoParam.out(0, 0)));
        List<String> list = new ArrayList<>();
        list.add("或在在要要在");
        list.add("在在要要在");
        list.add("或要要在");
        list.add("或要要在");
        list.add("或要要在");
        list.add("或在在要在");
        list.add("要");
        one.setDataList(list, data -> data);
        List<String> list2 = new ArrayList<>();
        list2.add("或在在要要在");
        list2.add("在在要要在");
        list2.add("或要要在");
        list2.add("或要要在");
        list2.add("或要要在");
        list2.add("或在在要在");
        list2.add("要");
        two.setDataList(Tools.getStringDataList(list2, new Interfaces.OnStringData<String>() {
            @Override
            public String getDataString(String data) {
                return data;
            }
        }));
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

 /*   @Override
    protected void onPhotoSelectResult(int resultCode, final Uri uri) {
        super.onPhotoSelectResult(resultCode, uri);
        showLoadingDialog();
        if (resultCode == RESULT_OK) {
            new Thread(new ImageCompressTask(getThis(), uri, App.getInstance().getCacheDir().getAbsolutePath()+ File.separator + "transfer" + File.separator, TestA.this)).start();
        }
        //setMultiView(uri);
    }*/

    @Override
    public void done(List<File> files, List<File> fails, boolean allSuccess) {

    }

    @Override
    public void error(Exception e) {

    }
}
