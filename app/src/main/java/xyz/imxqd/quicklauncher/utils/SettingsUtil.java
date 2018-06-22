package xyz.imxqd.quicklauncher.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Method;

import xyz.imxqd.quicklauncher.App;
import xyz.imxqd.quicklauncher.R;

public class SettingsUtil {
    public static int getBackgroundColor() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getInt(res.getString(R.string.pref_key_background_color), Color.parseColor(res.getString(R.string.background_color_default)));
    }

    public static int getBackgroundColorAlpha() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getInt(res.getString(R.string.pref_key_background_alpha), 0);
    }

    public static int getStrikeColor() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getInt(res.getString(R.string.pref_key_strike_color), Color.parseColor(res.getString(R.string.strike_color_default)));
    }

    public static int getStrikeColorAlpha() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getInt(res.getString(R.string.pref_key_strike_color_alpha), 0);
    }

    public static boolean isFloatingBallOn() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getBoolean(res.getString(R.string.pref_key_floating_ball_switch), false);
    }

    public static int getFloatingBallSize() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getInt(res.getString(R.string.pref_key_floating_ball_size), 60);
    }

    public static boolean isNotificationOn() {
        Resources res = App.getApp().getResources();
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getBoolean(res.getString(R.string.pref_key_floating_notification_switch), true);
    }

    public static void save(String key, int value) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        shp.edit().putInt(key, value).apply();
    }

    public static void save(String key, boolean value) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        shp.edit().putBoolean(key, value).apply();
    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getInt(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return shp.getBoolean(key, defaultValue);
    }

    public static boolean canDrawOverlayViews(Context con){
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(con);
        } else {
            return canDrawOverlaysUsingReflection(con);
        }
    }

    public static void requestDrawOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getApp().startActivity(intent);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean canDrawOverlaysUsingReflection(Context context) {
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", new Class[] { int.class, int.class, String.class });
            //AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            int mode = (Integer) dispatchMethod.invoke(manager, new Object[] { 24, Binder.getCallingUid(), context.getApplicationContext().getPackageName() });

            return AppOpsManager.MODE_ALLOWED == mode;

        } catch (Exception e) {  return false;  }

    }

}
