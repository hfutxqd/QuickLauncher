package xyz.imxqd.quicklauncher.utils;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

import xyz.imxqd.quicklauncher.App;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.ui.GestureDetectActivity;

public class ShortcutUtil {
    public static void create() {
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClass(App.getApp(), GestureDetectActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Resources res = App.getApp().getResources();

        ShortcutInfoCompat infoCompat = new ShortcutInfoCompat.Builder(App.getApp(), "GestureDetect")
                .setIcon(IconCompat.createWithResource(App.getApp(), R.mipmap.ic_launcher))
                .setIntent(shortcutIntent)
                .setShortLabel(res.getString(R.string.action_gesture))
                .setLongLabel(res.getString(R.string.action_gesture))
                .build();

        ShortcutManagerCompat.requestPinShortcut(App.getApp(), infoCompat, null);
    }
}
