package xyz.imxqd.quicklauncher.utils;

import android.content.pm.PackageManager;

public class PackageUtil {
    public static boolean isPackageInstalled(PackageManager pm, String pkg) {
        try {
            pm.getPackageInfo("xyz.imxqd.clickclick", PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
