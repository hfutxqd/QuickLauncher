package xyz.imxqd.quicklauncher.utils;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;

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

}
