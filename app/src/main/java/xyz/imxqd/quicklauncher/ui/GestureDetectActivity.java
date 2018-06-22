package xyz.imxqd.quicklauncher.ui;

import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.dao.GestureAction;
import xyz.imxqd.quicklauncher.model.GestureManager;
import xyz.imxqd.quicklauncher.ui.base.BaseActivity;
import xyz.imxqd.quicklauncher.utils.SettingsUtil;

public class GestureDetectActivity extends BaseActivity {
    private static final String TAG = "GestureDetect";

    @BindView(R.id.gestures_overlay)
    GestureOverlayView mOverlayView;

    private boolean isLoaded = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_gesture_detect;
    }

    @Override
    protected void initMembers() {
        isLoaded = GestureManager.get().load();
    }

    public int toArgb(int alphaPercent, int color) {
        int alpha = (int) ((100 - alphaPercent) * (255f / 100f));
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    protected void initViews() {
        int backgroundColor = SettingsUtil.getBackgroundColor();
        int backgroundAlpha = SettingsUtil.getBackgroundColorAlpha();
        mOverlayView.setBackgroundColor(toArgb(backgroundAlpha, backgroundColor));
        int strikeColor = SettingsUtil.getStrikeColor();
        int strikeAlpha = SettingsUtil.getStrikeColorAlpha();
        mOverlayView.setGestureColor(toArgb(strikeAlpha, strikeColor));
        mOverlayView.setUncertainGestureColor(toArgb(strikeAlpha, strikeColor));
    }

    @Override
    protected void initEvents() {
        mOverlayView.addOnGesturePerformedListener((overlay, gesture) -> {
            ArrayList<Prediction> predictions = GestureManager.get().getGestureStore().recognize(gesture);
            for (Prediction prediction : predictions) {
                Log.d(TAG, "id = " + prediction.name);
                Log.d(TAG, "score = " + prediction.score);
                if (!TextUtils.isDigitsOnly(prediction.name)) {
                    Log.e(TAG, "prediction.name is not a number.");
                    return;
                }
                if (prediction.score > 1.0) {
                    long id = Long.valueOf(prediction.name);
                    GestureAction action = GestureAction.getById(id);
                    if (action != null) {
                        try {
                            action.exec();
                            finish();
                        } catch (URISyntaxException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
                }
            }

        });
    }

    @Override
    public void onAttachedToWindow() {
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        getWindowManager().updateViewLayout(view, lp);
    }
}
