package com.loupe.project.camare;

import android.content.Context;
import android.os.Build;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

/**
 * @author 郭翰林
 * @date 2018/9/18 0018 17:54
 * 注释: Android权限申请工具类
 */
public class PermissionUtils {
    public interface PermissionListener {
        /**
         * 成功
         */
        void onSuccess(Context context);

        /**
         * 失败
         */
        void onFailed(Context context);
    }

    /**
     * 注释：应用相关组权限
     * 时间：2018/9/18 0018 17:57
     * 作者：郭翰林
     */
    public static void applicationPermissions(final Context context, final PermissionListener listener, String[]... permissions) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!AndPermission.hasPermissions(context, permissions)) {
                AndPermission.with(context)
                        .runtime()
                        .permission(permissions)
                        .rationale(new Rationale<List<String>>() {
                            @Override
                            public void showRationale(Context mContext, List<String> data, RequestExecutor executor) {
                                //选择显示提示弹窗
                                executor.execute();
                            }
                        })
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> permission) {
                                listener.onSuccess(context);
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> permission) {
                                listener.onFailed(context);
                            }
                        })
                        .start();
            } else {
                listener.onSuccess(context);
            }
        } else {
            listener.onSuccess(context);
        }
    }

    /**
     * 注释：应用相关单个权限
     * 时间：2018/9/18 0018 17:57
     * 作者：郭翰林
     */
    public static void applicationPermissions(Context context, PermissionListener listener, String... permissions) {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            if (!AndPermission.hasPermissions(context, permissions)) {
//                AndPermission.with(context)
//                        .runtime()
//                        .permission(permissions)
//                        .rationale((mContext, data, executor) -> {
//                            //选择显示提示弹窗
//                            executor.execute();
//                        })
//                        .onGranted((permission) -> {
//                            listener.onSuccess(context);
//                        })
//                        .onDenied((permission) -> {
//                            listener.onFailed(context);
//                        })
//                        .start();
//            } else {
//                listener.onSuccess(context);
//            }
//        } else {
//            listener.onSuccess(context);
//        }
    }

}
