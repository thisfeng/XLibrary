package com.thisfeng.test;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;


import com.thisfeng.test.items.MyFragmentAdapter;
import com.thisfeng.xlibrary.utils.ImageUtils;
import com.thisfeng.xlibrary.utils.toast.TS;

public class New1Activity extends ABaseActivity {

    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_new1;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {
        Button btn = (Button) findViewById(R.id.btn);
        vp = (ViewPager) findViewById(R.id.vpMain);

        HotItem hotItem = new HotItem();
        hotItem.setAge(10);
        hotItem.setName("Acil");

        vp.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));

        new Thread(() -> {
            ImageUtils.loadBitmapFromFresco(Uri.parse("https://members.mytaoheung.com//files////afd98eadd2394913bb40d3ade0100c3e//image//2017_10_12//D585B1DD9C8A705F.jpg"), bitmap1 -> {
                Bitmap bmp = ImageUtils.doGaussianBlur(bitmap1, 30, false, 120);
                runOnUiThread(() -> {
                    ((ImageView) findViewById(R.id.iv)).setImageBitmap(bmp);
                });
            });
        }).start();
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hotItem.setAge(hotItem.getAge()+1);
                        TS.show(hotItem.toString());
                      /*  postEvent("anEventName", "ABC", obj -> {
                            L.e((String) obj);
                            return "AA";
                        });*/
                    }
                }
        );
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }


    public void lookPhoto(View view) {
        start(PhotoDemoActivity.class);
    }
}
