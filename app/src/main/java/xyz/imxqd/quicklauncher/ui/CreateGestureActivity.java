package xyz.imxqd.quicklauncher.ui;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.model.GestureManager;

public class CreateGestureActivity extends BaseActivity {
    private static final float LENGTH_THRESHOLD = 120.0f;
    public static final int REQUEST_APP_LIST = 1;

    @BindView(R.id.gestures_overlay)
    GestureOverlayView mOverlayView;

    Gesture mGesture;

    MenuItem mNextMenu;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_create_gesture;
    }

    @Override
    protected void initEvents() {
        mOverlayView.addOnGestureListener(new GesturesProcessor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gesture, menu);
        mNextMenu = menu.getItem(0).setEnabled(false);
        mNextMenu.getIcon().setAlpha(80);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_next) {
            Intent intent = new Intent(this, AppChooseActivity.class);
            intent.putExtra(AppChooseActivity.ARG_GESTURE, mGesture);
            startActivityForResult(intent, REQUEST_APP_LIST);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_APP_LIST && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            mOverlayView.post(() -> mOverlayView.setGesture(mGesture));

        }
    }

    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {

        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mGesture = null;
            mNextMenu.setEnabled(false);
            mNextMenu.getIcon().setAlpha(80);
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }

        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(false);
                mGesture = null;
                mNextMenu.setEnabled(false);
                mNextMenu.getIcon().setAlpha(80);
            } else {
                mNextMenu.setEnabled(true);
                mNextMenu.getIcon().setAlpha(255);
            }
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            mGesture = null;
            mNextMenu.setEnabled(false);
            mNextMenu.getIcon().setAlpha(80);
        }
    }
}
