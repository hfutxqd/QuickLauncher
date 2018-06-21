package xyz.imxqd.quicklauncher.model;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import xyz.imxqd.quicklauncher.App;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.dao.GestureAction;
import xyz.imxqd.quicklauncher.ui.AppChooseActivity;

public class GestureManager {

    private static volatile GestureManager sInstance;

    private File mStoreFile;

    private ArrayList<GestureAction> mGestureActions = new ArrayList<>();

    private HashMap<Long, Bitmap> mGestureThumbs = new HashMap<>();
    private HashMap<Long, Gesture> mGestures = new HashMap<>();


    private GestureLibrary mStore;

    private int mThumbnailSize;
    private int mThumbnailInset;
    private int mPathColor;

    private GestureManager() {
    }

    private void initInternal(Context context) {
        mStoreFile = new File(context.getFilesDir(), "gestures");
        mStore = GestureLibraries.fromFile(mStoreFile);
        final Resources resources = context.getResources();

        mPathColor = Color.BLACK;
        mThumbnailInset = (int) resources.getDimension(R.dimen.gesture_thumbnail_inset);
        mThumbnailSize = (int) resources.getDimension(R.dimen.gesture_thumbnail_size);
    }

    public static void init(Context context) {
        if (sInstance == null) {
            synchronized (GestureManager.class) {
                sInstance = new GestureManager();
                sInstance.initInternal(context);
            }
        }
    }

    public boolean load() {
        return mStore.load();
    }


    Disposable mLoadDisposable;
    public void load(Consumer<ArrayList<GestureAction>> consumer) {
        if (consumer == null) {
            return;
        }
        if (mLoadDisposable != null) {
            mLoadDisposable.dispose();
        }
        mLoadDisposable = Observable.create((ObservableOnSubscribe<ArrayList<GestureAction>>) emitter -> {
            if (mStore.load()) {
                mGestureActions = (ArrayList<GestureAction>) GestureAction.getAll();
                for (GestureAction action : mGestureActions) {
                    ArrayList<Gesture>  gestures = mStore.getGestures(String.valueOf(action.id));
                    if (gestures != null && gestures.size() > 0) {
                        Gesture gesture = gestures.get(0);
                        Bitmap bitmap = gesture.toBitmap(mThumbnailSize, mThumbnailSize,
                                mThumbnailInset, mPathColor);
                        mGestureThumbs.put(gesture.getID(), bitmap);
                        mGestures.put(gesture.getID(), gesture);
                    } else {
                        action.delete();
                    }
                }

                emitter.onNext(mGestureActions);
                emitter.onComplete();
            } else {
                mGestureActions.clear();
                emitter.onNext(mGestureActions);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);

    }

    public Bitmap getGestureThumbById(long id) {
        return mGestureThumbs.get(id);
    }

    public Gesture getGestureById(long id) {
        return mGestures.get(id);
    }

    public void save(Gesture gesture, AppChooseActivity.AppInfo info, int actionType) {
        if (actionType == GestureAction.ACTION_TYPE_ACTIVITY_INTENT) {
            GestureAction action = new GestureAction();
            action.actionType = actionType;
            action.gestureId = gesture.getID();
            action.name = info.name;
            action.description = App.getApp().getString(R.string.lanucher_app_prefix) + info.name;
            action.actionData = info.getLauncherIntent(App.getApp()).toUri(0);
            action.save();
            action.load();
            mStore.addGesture(String.valueOf(action.id), gesture);
            mStore.save();
        }
    }

    public void saveActivityIntentAction(Gesture gesture, String name, String subTitle, Intent intent) {
        GestureAction action = new GestureAction();
        action.actionType = GestureAction.ACTION_TYPE_ACTIVITY_INTENT;
        action.gestureId = gesture.getID();
        action.name = name;
        action.description = subTitle;
        action.actionData = intent.toUri(0);
        action.save();
        action.load();
        mStore.addGesture(String.valueOf(action.id), gesture);
        mStore.save();
    }

    public void delete(long gestureId) {
        Gesture gesture = getGestureById(gestureId);
        if (gesture == null) {
            return;
        }
        GestureAction action = GestureAction.getByGestureId(gesture.getID());
        if (action == null) {
            return;
        }
        mStore.removeGesture(String.valueOf(action.id), gesture);
        mStore.save();
        action.delete();

    }

    public GestureLibrary getGestureStore() {
        return mStore;
    }


    public static GestureManager get() {
        return sInstance;
    }

}
