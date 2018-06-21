package xyz.imxqd.quicklauncher.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import xyz.imxqd.quicklauncher.App;
import xyz.imxqd.quicklauncher.R;

public class ClickUtil {

    public static final String APP_ID_CLICK_CLICK = "xyz.imxqd.clickclick";
    public static final String APP_ID_CLICK_CLICK_X = "xyz.imxqd.clickclick.xposed";

    public static final String ACTION_RUN = ".run";
    public static final String ARG_FUNC_ID = "func_id";

    private static String sInstalledClickClick = null;

    public static Intent getFuncIntent(long funcId) {
        if (!isClickClickInstalled()) {
            return new Intent();
        }
        Intent shortcutIntent = new Intent(sInstalledClickClick + ACTION_RUN);
        shortcutIntent.setComponent(new ComponentName(sInstalledClickClick, ".ui.NoDisplayActivity"));
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.putExtra(ARG_FUNC_ID, funcId);
        return shortcutIntent;
    }

    public static boolean isClickClickInstalled() {
        if (App.getApp().getPackageManager().getLaunchIntentForPackage(APP_ID_CLICK_CLICK) != null) {
            sInstalledClickClick = APP_ID_CLICK_CLICK;
            return true;
        } else if (App.getApp().getPackageManager().getLaunchIntentForPackage(APP_ID_CLICK_CLICK_X) != null) {
            sInstalledClickClick = APP_ID_CLICK_CLICK_X;
            return true;
        }
        return false;
    }

    public static Drawable getIcon() {
        if (!isClickClickInstalled()) {
            return App.getApp().getResources().getDrawable(R.mipmap.ic_launcher);
        }
        PackageManager pm = App.getApp().getPackageManager();
        try {
            return pm.getApplicationInfo(sInstalledClickClick, PackageManager.GET_META_DATA).loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            return App.getApp().getResources().getDrawable(R.mipmap.ic_launcher);
        }
    }
}
