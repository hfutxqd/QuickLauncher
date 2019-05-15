package xyz.imxqd.quicklauncher.utils;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.imxqd.quicklauncher.R;

public class MarketUtil {

    public static void openMarket(Context context, String pkg) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg));
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
            String[] storePkgs = context.getResources().getStringArray(R.array.supported_appstroe);
            List<String> storePkgList = Arrays.asList(storePkgs);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
            for (ResolveInfo ri : activities) {
                String strorePkg = ri.activityInfo.packageName;
                if (storePkgList.contains(strorePkg)) {
                    Intent myIntent = new Intent(intent);
                    myIntent.setComponent(new ComponentName(strorePkg, ri.activityInfo.name));
                    intentList.add(new LabeledIntent(intent, strorePkg, ri.loadLabel(pm), ri.icon));
                }
            }
            LabeledIntent[] extraIntents = intentList
                    .toArray(new LabeledIntent[intentList.size()]);
            Intent chooser = Intent.createChooser(intent, context.getString(R.string.choose_a_market));
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            context.startActivity(chooser);
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://coolapk.com/apk/" + pkg)));
        }
    }

    public static void openRate(Context context, String pkg) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg));
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
            String[] storePkgs = context.getResources().getStringArray(R.array.supported_appstroe);
            List<String> storePkgList = Arrays.asList(storePkgs);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
            for (ResolveInfo ri : activities) {
                String strorePkg = ri.activityInfo.packageName;
                if (storePkgList.contains(strorePkg)) {
                    Intent myIntent = new Intent(intent);
                    myIntent.setComponent(new ComponentName(strorePkg, ri.activityInfo.name));
                    intentList.add(new LabeledIntent(intent, strorePkg, ri.loadLabel(pm), ri.icon));
                }
            }
            LabeledIntent[] extraIntents = intentList
                    .toArray(new LabeledIntent[intentList.size()]);
            Intent chooser = Intent.createChooser(intent, context.getString(R.string.choose_a_market));
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            context.startActivity(chooser);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(context, R.string.no_market_installed, Toast.LENGTH_LONG).show();
        }
    }
}
