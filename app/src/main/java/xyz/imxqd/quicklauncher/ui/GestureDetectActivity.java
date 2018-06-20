package xyz.imxqd.quicklauncher.ui;

import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
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

    @Override
    protected void initEvents() {
        mOverlayView.addOnGesturePerformedListener((overlay, gesture) -> {
            ArrayList<Prediction> predictions = GestureManager.get().getGestureStore().recognize(gesture);
            for (Prediction prediction : predictions) {
                Log.d(TAG, "id = " + prediction.name);
                Log.d(TAG, "score = " + prediction.score);
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
