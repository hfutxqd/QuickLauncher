package xyz.imxqd.quicklauncher.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import xyz.imxqd.quicklauncher.App;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.ui.GestureDetectActivity;
import xyz.imxqd.quicklauncher.ui.MainActivity;

public class NotificationUtil {


    private static final String NOTIFICATION_CHANNEL_ID = "foreground_service";


    public static Notification getForeground(Context context, String title) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && nm != null) {
            CharSequence name = App.getApp().getString(R.string.foreground_service_channel_name);
            String description = App.getApp().getString(R.string.foreground_service_description);

            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(false);
            mChannel.setShowBadge(false);
            nm.createNotificationChannel(mChannel);
        }
        Resources res = context.getResources();
        PendingIntent main = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent gesture = PendingIntent.getActivity(context, 0, new Intent(context, GestureDetectActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setTicker(title)
                .addAction(new NotificationCompat.Action(0, res.getString(R.string.action_gesture), gesture))
                .addAction(new NotificationCompat.Action(0, res.getString(R.string.action_main), main))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true);
        return builder.build();
    }
}
