package xyz.imxqd.quicklauncher.service;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.ui.GestureDetectActivity;
import xyz.imxqd.quicklauncher.ui.MainActivity;
import xyz.imxqd.quicklauncher.utils.SettingsUtil;
import xyz.imxqd.quicklauncher.utils.ShockUtil;

public class FloatingService extends Service {

    public static final String ACTION_SHOW = "xyz.imxqd.quicklauncher.floating.show";
    public static final String ACTION_HIDE = "xyz.imxqd.quicklauncher.floating.hide";

    private static final String TAG = "FloatingService";

    private static final String PREF_X = "floating_ball_x";
    private static final String PREF_Y = "floating_ball_y";

    public static FloatingService sInstance;

    ImageView mFloatView;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    GestureDetector mDetector;
    ViewState mViewState = ViewState.None;
    Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        if (!SettingsUtil.canDrawOverlayViews(getApplication()) || !SettingsUtil.isFloatingBallOn()) {
            stopSelf();
            return;
        }
        createFloatView();
        show(false);
        sInstance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (TextUtils.equals(ACTION_HIDE, intent.getAction())) {
            hide();
        } else if (TextUtils.equals(ACTION_SHOW, intent.getAction())) {
            show(true);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        hide();
        super.onDestroy();
    }
    boolean isPressed = false;

    float mBallSize = 1f;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            show(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hide();
        }
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {
        Log.d(TAG, "createFloatView");
        mFloatView = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.floating_view, null);
        mDetector = new GestureDetector(mFloatView.getContext(), new GestureDetector.OnGestureListener() {
            int downX, downY;
            int offsetX, offsetY;
            @Override
            public boolean onDown(MotionEvent e) {
                downX = (int) e.getRawX();
                downY = (int) e.getRawY();
                offsetX = wmParams.x - downX;
                offsetY = wmParams.y - downY;
                mFloatView.setPressed(true);
                show(true);
                return false;
            }


            @Override
            public void onShowPress(MotionEvent e) {
                Log.d(TAG, "onShowPress");
                isPressed = true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp");
                mFloatView.setPressed(false);
                ShockUtil.shock(new long[] {0, 40});
                Intent intent = new Intent(getApplicationContext(), GestureDetectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isPressed) {
                    Log.d(TAG, "onScroll");
                    wmParams.x = (int) e2.getRawX() + offsetX;
                    wmParams.y = (int) e2.getRawY() + offsetY;
                    mWindowManager.updateViewLayout(mFloatView, wmParams);
                    SettingsUtil.save(PREF_X, wmParams.x);
                    SettingsUtil.save(PREF_Y, wmParams.y);
                    return false;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress");
                ShockUtil.shock(new long[] {0, 100});
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling");
                isPressed = false;
                return false;
            }
        });
        mFloatView.setOnTouchListener((v, event) -> {
            mHandler.removeCallbacksAndMessages(null);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isPressed = false;
                mHandler.postDelayed(this::minimize, 5000);
            }
            mDetector.onTouchEvent(event);
            return true;
        });
        mBallSize = SettingsUtil.getFloatingBallSize() / 100f;
        mFloatView.setImageResource(R.mipmap.ic_launcher);
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.x = SettingsUtil.getInt(PREF_X, 0);
        wmParams.y =SettingsUtil.getInt(PREF_Y, 200);
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(mFloatView, wmParams);
    }

    public void minimize() {
        if (mViewState == ViewState.None || mViewState == ViewState.Hide) {
            return;
        }
        if (mFloatView == null) {
            return;
        }
        mViewState = ViewState.Minimize;
        mFloatView.setVisibility(View.VISIBLE);
        mFloatView.animate().scaleX(mBallSize * 0.6f).scaleY(mBallSize * 0.6f).alpha(0.3f).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void show(boolean anim) {
        if (mFloatView == null) {
            return;
        }
        mViewState = ViewState.Show;
        mFloatView.setVisibility(View.VISIBLE);
        if (anim) {
            mFloatView.animate().scaleX(mBallSize).scaleY(mBallSize).alpha(1f).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        } else {
            mFloatView.setScaleX(mBallSize);
            mFloatView.setScaleY(mBallSize);
            mFloatView.setAlpha(1f);
        }

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(this::minimize, 3000);
    }

    public void setSize(float size) {
        mBallSize = size;
        show(false);
    }

    public void hide() {
        if (mFloatView == null) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        mViewState = ViewState.Hide;
        mFloatView.setVisibility(View.VISIBLE);
        mFloatView.animate().scaleX(0f).scaleY(0f).setDuration(200).alpha(0f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFloatView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public enum ViewState {
        Show, Hide, Minimize, None
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
