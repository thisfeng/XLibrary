package com.thisfeng.xlibrary.takephoto.permission;

import com.thisfeng.xlibrary.takephoto.model.InvokeParam;

/**
 * 授权管理回调
 */
public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}
