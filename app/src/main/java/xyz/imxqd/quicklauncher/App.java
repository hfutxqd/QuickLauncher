package xyz.imxqd.quicklauncher;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import xyz.imxqd.quicklauncher.model.GestureManager;

public class App extends Application {

    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        GestureManager.init(this);
        FlowManager.init(this);
        sApp = this;
    }

    public static App getApp() {
        return sApp;
    }

}
