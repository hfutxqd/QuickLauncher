package xyz.imxqd.quicklauncher.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;

import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.service.FloatingService;
import xyz.imxqd.quicklauncher.service.NotificationService;
import xyz.imxqd.quicklauncher.ui.base.BaseActivity;
import xyz.imxqd.quicklauncher.ui.base.BasePreferenceFragment;
import xyz.imxqd.quicklauncher.ui.widget.SeekBarPreference;
import xyz.imxqd.quicklauncher.utils.SettingsUtil;
import xyz.imxqd.quicklauncher.utils.ShortcutUtil;


public class SettingsActivity extends BaseActivity {


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initViews() {
        super.initViews();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    public static class GeneralPreferenceFragment extends BasePreferenceFragment {
        SeekBarPreference mPreFloatingBallSize;
        SwitchPreference mPreFloatingSwitch;
        SwitchPreference mPreNotificationSwitch;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            mPreFloatingBallSize = (SeekBarPreference) findPreference(getString(R.string.pref_key_floating_ball_size));
            mPreFloatingSwitch = (SwitchPreference) findPreference(getString(R.string.pref_key_floating_ball_switch));
            mPreNotificationSwitch = (SwitchPreference) findPreference(getString(R.string.pref_key_floating_notification_switch));

            findPreference(getString(R.string.pref_key_shortcut)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ShortcutUtil.create();
                    return true;
                }
            });

            mPreFloatingBallSize.setOnPreferenceChangeListener((preference, newValue) -> {
                int size = (int) newValue;
                FloatingService.sInstance.setSize(size / 100f);
                return true;
            });

            mPreFloatingSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isOn = (boolean) newValue;
                if (isOn && !SettingsUtil.canDrawOverlayViews(getActivity())) {
                    SettingsUtil.requestDrawOverlay();
                    mPreFloatingSwitch.setChecked(false);
                    return false;
                } else {
                    mPreFloatingSwitch.setChecked(isOn);
                }
                if (isOn) {
                    Intent intent = new Intent(FloatingService.ACTION_SHOW);
                    intent.setClass(getActivity(), FloatingService.class);
                    getActivity().startService(intent);
                } else {
                    Intent intent = new Intent(FloatingService.ACTION_HIDE);
                    intent.setClass(getActivity(), FloatingService.class);
                    getActivity().startService(intent);
                }
                initPrefs();
                return true;
            });

            mPreNotificationSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isOn = (boolean) newValue;
                mPreNotificationSwitch.setChecked(isOn);
                if (isOn) {
                    Intent intent = new Intent(NotificationService.ACTION_SHOW);
                    intent.setClass(getActivity(), NotificationService.class);
                    getActivity().startService(intent);
                } else {
                    Intent intent = new Intent(NotificationService.ACTION_HIDE);
                    intent.setClass(getActivity(), NotificationService.class);
                    getActivity().startService(intent);
                }
                return true;
            });

            initPrefs();

        }

        private void initPrefs() {
            if (!SettingsUtil.canDrawOverlayViews(getActivity())) {
                mPreFloatingSwitch.setChecked(false);
            }
            boolean isFloatingBallOn = mPreFloatingSwitch.isChecked();
            if (isFloatingBallOn) {
                mPreFloatingBallSize.setEnabled(true);
            } else {
                mPreFloatingBallSize.setEnabled(false);
            }
        }
    }
}
