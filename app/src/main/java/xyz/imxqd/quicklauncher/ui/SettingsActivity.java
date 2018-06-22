package xyz.imxqd.quicklauncher.ui;

import android.os.Bundle;

import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.ui.base.BaseActivity;
import xyz.imxqd.quicklauncher.ui.base.BasePreferenceFragment;


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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
        }
    }
}
