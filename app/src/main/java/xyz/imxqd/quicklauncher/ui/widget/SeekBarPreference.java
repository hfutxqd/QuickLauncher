package xyz.imxqd.quicklauncher.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import xyz.imxqd.quicklauncher.R;


public class SeekBarPreference extends Preference {
    private SeekBar mSeekBar;
    private TextView mTitle;
    private TextView mSeekBarText;


    private int mSeekBarValue;
    private int mMin;
    private int mMax;
    private int mSeekBarIncrement;
    private boolean mSmooth;
    private boolean mAdjustable; // whether the seekbar should respond to the left/right keys

    private static final String TAG = "SeekBarPreference";

    /**
     * Listener reacting to the SeekBar changing value by the user
     */
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        private boolean mTrackingTouch;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && (mSmooth || !mTrackingTouch)) {
                syncValueInternal(seekBar);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTrackingTouch = false;
            if (seekBar.getProgress() + mMin != mSeekBarValue) {
                syncValueInternal(seekBar);
            }
        }
    };

    /**
     * Listener reacting to the user pressing DPAD left/right keys if {@code
     * adjustable} attribute is set to true; it transfers the key presses to the SeekBar
     * to be handled accordingly.
     */
    private View.OnKeyListener mSeekBarKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }

            if (!mAdjustable && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                // Right or left keys are pressed when in non-adjustable mode; Skip the keys.
                return false;
            }

            // We don't want to propagate the click keys down to the seekbar view since it will
            // create the ripple effect for the thumb.
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                return false;
            }

            if (mSeekBar == null) {
                Log.e(TAG, "SeekBar view is null and hence cannot be adjusted.");
                return false;
            }
            return mSeekBar.onKeyDown(keyCode, event);
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarPreference(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1, -1);
    }

    public SeekBarPreference(Context context) {
        super(context);
        init(context, null, -1, -1);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyleAttr, defStyleRes);
            mSmooth = a.getBoolean(R.styleable.SeekBarPreference_smooth, true);
            mMin = a.getInt(R.styleable.SeekBarPreference_min, 0);
            setMax(a.getInt(R.styleable.SeekBarPreference_max, 100));
            mAdjustable = a.getBoolean(R.styleable.SeekBarPreference_adjustable, true);
            a.recycle();
        } else {
            mSmooth = true;
            mMax = 0;
            setMax(100);
            mAdjustable = true;
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(getLayoutResource());
        return super.onCreateView(parent);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.pref_seekbar_layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        view.setOnKeyListener(mSeekBarKeyListener);
        mSeekBar = view.findViewById(R.id.seekbar);
        mTitle = view.findViewById(R.id.title);
        mSeekBarText = view.findViewById(R.id.seekbar_text);

        if (mSeekBar == null) {
            Log.e(TAG, "SeekBar view is null in onBindViewHolder.");
            return;
        }
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setMax(mMax - mMin);
        if (mSeekBarIncrement != 0) {
            mSeekBar.setKeyProgressIncrement(mSeekBarIncrement);
        } else {
            mSeekBarIncrement = mSeekBar.getKeyProgressIncrement();
        }

        mSeekBar.setProgress(mSeekBarValue - mMin);
        mSeekBar.setEnabled(isEnabled());

        if (mTitle != null) {
            mTitle.setText(getTitle());
        }

        if (mSeekBarText != null) {
            mSeekBarText.setText(String.valueOf(mSeekBarValue + "%"));
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mSeekBarValue)
                : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    public void setMin(int min) {
        if (min > mMax) {
            min = mMax;
        }
        if (min != mMin) {
            mMin = min;
            notifyChanged();
        }
    }

    public int getMin() {
        return mMin;
    }

    public final void setMax(int max) {
        if (max < mMin) {
            max = mMin;
        }
        if (max != mMax) {
            mMax = max;
            notifyChanged();
        }
    }

    /**
     * Returns the amount of increment change via each arrow key click. This value is derived from
     * user's specified increment value if it's not zero. Otherwise, the default value is picked
     * from the default mKeyProgressIncrement value in {@link android.widget.AbsSeekBar}.
     *
     * @return The amount of increment on the SeekBar performed after each user's arrow key press.
     */
    public final int getSeekBarIncrement() {
        return mSeekBarIncrement;
    }

    /**
     * Sets the increment amount on the SeekBar for each arrow key press.
     *
     * @param seekBarIncrement The amount to increment or decrement when the user presses an
     *                         arrow key.
     */
    public final void setSeekBarIncrement(int seekBarIncrement) {
        if (seekBarIncrement != mSeekBarIncrement) {
            mSeekBarIncrement = Math.min(mMax - mMin, Math.abs(seekBarIncrement));
            notifyChanged();
        }
    }

    public int getMax() {
        return mMax;
    }

    public void setAdjustable(boolean adjustable) {
        mAdjustable = adjustable;
    }

    public boolean isAdjustable() {
        return mAdjustable;
    }

    public void setValue(int seekBarValue) {
        setValueInternal(seekBarValue, true);
    }

    private void setValueInternal(int seekBarValue, boolean notifyChanged) {
        if (seekBarValue < mMin) {
            seekBarValue = mMin;
        }
        if (seekBarValue > mMax) {
            seekBarValue = mMax;
        }

        if (seekBarValue != mSeekBarValue) {
            mSeekBarValue = seekBarValue;
            if (mSeekBarText != null) {
                mSeekBarText.setText(String.valueOf(mSeekBarValue + "%"));
            }
            persistInt(seekBarValue);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    public int getValue() {
        return mSeekBarValue;
    }



    /**
     * Persist the seekBar's seekbar value if callChangeListener
     * returns true, otherwise set the seekBar's value to the stored value
     */
    private void syncValueInternal(SeekBar seekBar) {
        int seekBarValue = mMin + seekBar.getProgress();
        if (seekBarValue != mSeekBarValue) {
            if (callChangeListener(seekBarValue)) {
                setValueInternal(seekBarValue, false);
            } else {
                seekBar.setProgress(mSeekBarValue - mMin);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SeekBarPreference.SavedState myState = new SeekBarPreference.SavedState(superState);
        myState.seekBarValue = mSeekBarValue;
        myState.min = mMin;
        myState.max = mMax;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SeekBarPreference.SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SeekBarPreference.SavedState myState = (SeekBarPreference.SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mSeekBarValue = myState.seekBarValue;
        mMin = myState.min;
        mMax = myState.max;
        notifyChanged();
    }

    /**
     * SavedState, a subclass of {@link Preference.BaseSavedState}, will store the state
     * of MyPreference, a subclass of Preference.
     * <p>
     * It is important to always call through to super methods.
     */
    private static class SavedState extends Preference.BaseSavedState {
        int seekBarValue;
        int min;
        int max;

        public SavedState(Parcel source) {
            super(source);

            // Restore the click counter
            seekBarValue = source.readInt();
            min = source.readInt();
            max = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            // Save the click counter
            dest.writeInt(seekBarValue);
            dest.writeInt(min);
            dest.writeInt(max);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SeekBarPreference.SavedState> CREATOR =
                new Parcelable.Creator<SeekBarPreference.SavedState>() {
                    public SeekBarPreference.SavedState createFromParcel(Parcel in) {
                        return new SeekBarPreference.SavedState(in);
                    }

                    public SeekBarPreference.SavedState[] newArray(int size) {
                        return new SeekBarPreference.SavedState[size];
                    }
                };
    }

}
