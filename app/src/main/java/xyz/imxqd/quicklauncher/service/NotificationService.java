package xyz.imxqd.quicklauncher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.utils.NotificationUtil;

public class NotificationService extends Service {

    public static final String ACTION_SHOW = "xyz.imxqd.quicklauncher.notification.show";
    public static final String ACTION_HIDE = "xyz.imxqd.quicklauncher.notification.hide";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (TextUtils.equals(ACTION_HIDE, intent.getAction())) {
            stopForeground(true);
        } else if (TextUtils.equals(ACTION_SHOW, intent.getAction())) {
            startForeground(1, NotificationUtil.getForeground(getApplicationContext(), getString(R.string.notification_title_quick_open)));
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
