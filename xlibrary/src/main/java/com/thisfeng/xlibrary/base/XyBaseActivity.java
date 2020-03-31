package com.thisfeng.xlibrary.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.thisfeng.xlibrary.adapter.CustomHolder;
import com.thisfeng.xlibrary.annotation.annotationHelper.StateBinder;
import com.thisfeng.xlibrary.interfaces.Interfaces;
import com.thisfeng.xlibrary.interfaces.PermissionListener;
import com.thisfeng.xlibrary.okHttp.CallItem;
import com.thisfeng.xlibrary.okHttp.OkHttp;
import com.thisfeng.xlibrary.takephoto.model.TImage;
import com.thisfeng.xlibrary.utils.LogUtil.LogLayout;
import com.thisfeng.xlibrary.unit.MsgEvent;
import com.thisfeng.xlibrary.utils.LogUtil.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public abstract class XyBaseActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GOT_RESULT = 100;
    public static final int REQUEST_CODE_PHOTO_SELECT = 202;
    public static final int REQUEST_CODE_MULTI_PHOTO_SELECT = 203;
    public static final int REQUEST_CODE_GOT_PHONE_NUMBER = 301;
    private static final int REQUEST_PEIMISSION_CODE = 1000;

    protected static List<Activity> activities = new LinkedList<>();

    private static AlertDialog loadingDialog;

    //    private static AlertDialog loadingDialog;
    private static boolean loadingDialogShowManual = false;

    protected List<CallItem> requestList = new ArrayList<>();

    private BroadcastReceiver finishReceiver;
    private XyBaseActivity thisActivity;

    public static final String ACTION_FINISH_ACTIVITY = "FinishBaseActivity";
    protected LogLayout logLayout;

    private CustomHolder rootHolder;
    private int activityLayout = 0;
    private boolean firstShowOnStart = true;

    private PermissionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        addActivity(this);
        activityLayout = setActivityLayout();
        if (activityLayout != 0) {
            setContentView(activityLayout);
        }
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        initOnCreate(savedInstanceState);
    }

    protected abstract int setActivityLayout();

    /**
     * onCreate方法时进行初始化操作
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initOnCreate(Bundle savedInstanceState);

    protected void showOnStart(boolean firstShow) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (L.showLog() && logLayout == null) {
            logLayout = LogLayout.attachLogLayoutToActivity(getThis());
        }
        showOnStart(firstShowOnStart);
        if (firstShowOnStart) {
            firstShowOnStart = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (logLayout != null) {
            logLayout.removeLayout();
            logLayout = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        for (CallItem call : requestList) {
            if (call != null && call.getCall() != null) {
                call.getCall().cancel();
            }
        }
        dismissLoadingDialog();
        removeActivity(this);
        super.onDestroy();
    }


    public CustomHolder rootHolder() {
        if (rootHolder == null) {
            rootHolder = new CustomHolder(getWindow().getDecorView().getRootView());
        }
        return rootHolder;
    }

    protected XyBaseActivity getThis() {
        return this;
    }

    public void start(Class<? extends Activity> activityClass) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        this.startActivity(intent);
    }

    public void start(Class<? extends Activity> activityClass, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        this.startActivityForResult(intent, requestCode);
    }

    public void start(Class<? extends Activity> activityClass, BaseIntent baseIntent) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        baseIntent.setIntent(intent);
        this.startActivity(intent);
    }

    public void start(Class<? extends Activity> activityClass, BaseIntent baseIntent, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        baseIntent.setIntent(intent);
        this.startActivityForResult(intent, requestCode);
    }

    protected static List<Activity> getActivities() {
        return activities;
    }

    public boolean isLoadingDialogShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    public void showLoadingDialog() {
        showDialog(false);
    }

    public void showLoadingDialogManualDismiss() {
        showDialog(true);
    }

    private void showDialog(boolean manualDismiss) {
        loadingDialogShowManual = manualDismiss;
        if (loadingDialog != null) loadingDialog.dismiss();
        loadingDialog = setLoadingDialog();
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public static void dismissLoadingDialog() {
        loadingDialogShowManual = false;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public static void dismissLoadingDialogByManualState() {
        if (!loadingDialogShowManual && loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        StateBinder.saveState(this, outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        StateBinder.bindState(this, savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void hideSoftInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void registerFinishReceiver() {
        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FINISH_ACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(finishReceiver, filter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface BaseIntent {
        void setIntent(Intent intent);
    }


    /**
     * static methods
     */


    public static void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
        StringBuilder sb = new StringBuilder("addActivity: [");
        for (int i = 0; i < activities.size(); i++) {
            sb.append(activities.get(i).getClass().getSimpleName()).append(i < activities.size() - 1 ? ", " : "]");
        }
        L.i(sb.toString());
    }

    public static void removeActivity(Activity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
        StringBuilder sb = new StringBuilder("removeActivity: [");
        for (int i = 0; i < activities.size(); i++) {
            sb.append(activities.get(i).getClass().getSimpleName()).append(i < activities.size() - 1 ? ", " : "]");
        }
        L.i(sb.toString());
    }

    public static void finishAllActivity() {
        for (final Activity activity : activities) {
            if (activity != null) {
                activity.runOnUiThread(() -> activity.finish());
            } else {
                activities.remove(activity);
            }
        }
    }

    public static void exitApplication() {
        finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * judge a activity is on foreground
     *
     * @param activity
     */
    public static boolean isForeground(Activity activity) {
        if (TextUtils.isEmpty(activity.getClass().getName())) {
            return false;
        }
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activity.getClass().getName().equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Activity getForegroundActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            Log.e(" getForegroundActivity ", cpn.getClassName());
            for (Activity activity : activities) {
                if (activity.getClass().getName().equals(cpn.getClassName())) {
                    Log.e(" getActivity ", activity.getClass().getName());
                    return activity;
                }
            }
        }
        return null;
    }

    public static Activity getActivityByClassName(String className) {
        for (Activity activity : activities) {
            if (activity.getClass().getName().contains(className)) {
                Log.e("ActivityClassName ", activity.getClass().getName());
                return activity;
            }
        }
        return null;
    }

    /**
     * EventBus
     */
    protected boolean useEventBus() {
        return logLayout != null;
    }

    public void postEvent(String eventName) {
        postEvent(eventName, null, null);
    }

    public void postEvent(String eventName, Interfaces.FeedBack feedBack) {
        postEvent(eventName, null, feedBack);
    }

    public void postEvent(String eventName, Object object) {
        postEvent(eventName, object, null);
    }

    public void postEvent(String eventName, String object) {
        postEvent(eventName, object, null);
    }

    public void postEvent(String eventName, Object object, Interfaces.FeedBack feedBack) {
        EventBus.getDefault().post(new MsgEvent(eventName, object, feedBack));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEvent event) {
        if (logLayout != null && event.getEventName().equals(L.EVENT_LOG)) {
            logLayout.refreshData();
        }
    }

    @Subscribe
    public void onEventBackground(MsgEvent event) {

    }

    /**
     * okHttp request
     */
    public CallItem newCall() {
        CallItem call = OkHttp.newCall(getThis());
        requestList.add(call);
        return call;
    }

    /**
     * Results return
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK && data != null && data.getBooleanExtra(PhotoSelectBaseActivity.SELECT_SUCCESS, false)) {
            ArrayList<TImage> images = (ArrayList<TImage>) data.getSerializableExtra(PhotoSelectBaseActivity.IMAGES);
            if(images == null) images = new ArrayList<>();
            TImage image = images.size() > 0 ? images.get(0) : null;
            onPhotoSelectResult(data, images, image);
        }
    }

    protected void onPhotoSelectResult(Intent data, ArrayList<TImage> images, TImage image) {

    }

    protected abstract AlertDialog setLoadingDialog();

    public AlertDialog getLoadingDialog() {
        return loadingDialog;
    }

    protected interface WindowMode {
        // 输入适应
        int INPUT_ADJUST = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
    }

    /**
     * 设置输入法模式
     *
     * @param windowMode 在 WindowMode 中选择相应选项，或从WindowManager.LayoutParams中选择
     */
    protected void setWindowMode(int windowMode) {
        getWindow().setSoftInputMode(windowMode);
    }


    /**
     * 权限封装处理
     */
    public void requestRuntimePermissions(String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            /* 检测是否授权，没有授权添加入list中去授权*/
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        /* 如果有未授权的则去请求权限 */
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionList.toArray(new String[permissionList.size()]),
                    REQUEST_PEIMISSION_CODE);
        } else {
            mListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PEIMISSION_CODE:
                if (grantResults.length > 0) {
                    /* 存储拒绝的权限 */
                    List<String> deniedPermission = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            deniedPermission.add(permissions[i]);
                        }
                    }
                    if (deniedPermission.isEmpty()) {
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermission);
                    }
                }
                break;
            default:
                break;
        }
    }


}
