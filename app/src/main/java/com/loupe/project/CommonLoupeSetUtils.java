package com.loupe.project;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommonLoupeSetUtils {

    public static boolean canLightLoupeStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }

        try {
            String miuiVersionName = getLoupeSystemProperties("ro.miui.ui.version.name");
            if (miuiVersionName != null) {
                //miui v9以后的版本执行下面代码
                int version = Integer.valueOf(miuiVersionName.substring(1));
                if (version >= 9) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getLoupeSystemProperties(String key) {
        System.out.println("obtain key->" + key);
        String value = "";
        try {
            Resources.getSystem();
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getDeclaredMethod("get", String.class);
            value = (String) get.invoke(c, key);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("get " + key + " error...");
            value = "";
        }
        return value;
    }

    public static void onlyLoupeLightStatusbarTextDark(Window window, boolean dark) {

        if (MIUILoupeSetStatusBarLightMode(window, dark)) {
            return;
        }

        if (isLoupeFlyme()) {
            StatusBarLoupeColorUtil.setStatusBarDarkIcon(window, dark);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                window.setStatusBarColor(Color.TRANSPARENT);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.setStatusBarColor(Color.TRANSPARENT);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            }
        }

    }

    public static boolean MIUILoupeSetStatusBarLightMode(Window window, boolean dark) {
        return MIUILoupeSetStatusBarLightMode(window, dark, false);
    }

    public static boolean MIUILoupeSetStatusBarLightMode(Window window, boolean dark, boolean transBlack) {

        if (window == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.BRAND != null && Build.BRAND.toLowerCase().trim().equals("xiaomi")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (dark) {
                    try {
                        String miuiVersionName = getLoupeSystemProperties("ro.miui.ui.version.name");
                        if (miuiVersionName != null) {
                            //miui v9以后的版本执行下面代码
                            int version = Integer.valueOf(miuiVersionName.substring(1));

                            if (version >= 9) {
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                //  window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                                return true;
                            }
                        } else {

                            window.setStatusBarColor(Color.WHITE);
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                        }

                    } catch (Exception e) {

                    }
                } else {
                    if (transBlack) {//透明状态栏+黑字
                        window.setStatusBarColor(Color.TRANSPARENT);
                        //SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 黑字
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {//透明状态栏+白字
                        window.setStatusBarColor(Color.TRANSPARENT);
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                }

                return true;
            }

            try {
                String miuiVersionName = getLoupeSystemProperties("ro.miui.ui.version.name");
                if (miuiVersionName != null) {
                    //miui v9以后的版本执行下面代码
                    int version = Integer.valueOf(miuiVersionName.substring(1));
                    if (version >= 9) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        //     window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        return true;
                    }
                }

            } catch (Exception e) {

            }

        }

        Class clazz = window.getClass();
        try {

            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
            }
            Log.i("info", "MIUISetStatusBarLightMode old");
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return false;
    }

    public static boolean isLoupeFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }
}
