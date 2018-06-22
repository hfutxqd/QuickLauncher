package xyz.imxqd.quicklauncher.utils;

import android.app.Service;
import android.os.Vibrator;

import xyz.imxqd.quicklauncher.App;

public class ShockUtil {
    public static void shock(long[] ms) {
        Vibrator vibrator = (Vibrator) App.getApp().getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        vibrator.vibrate(ms, -1);
    }

    public static void shock() {
        Vibrator vibrator = (Vibrator) App.getApp().getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        vibrator.vibrate(60000);
    }

    public static void cancal() {
        Vibrator vibrator = (Vibrator) App.getApp().getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        vibrator.cancel();
    }
}
