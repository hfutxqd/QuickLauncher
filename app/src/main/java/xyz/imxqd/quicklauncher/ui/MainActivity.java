package xyz.imxqd.quicklauncher.ui;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.dao.GestureAction;
import xyz.imxqd.quicklauncher.model.GestureManager;
import xyz.imxqd.quicklauncher.service.FloatingService;
import xyz.imxqd.quicklauncher.service.NotificationService;
import xyz.imxqd.quicklauncher.ui.adapters.GestureActionAdapter;
import xyz.imxqd.quicklauncher.ui.base.BaseActivity;
import xyz.imxqd.quicklauncher.utils.DialogUtil;
import xyz.imxqd.quicklauncher.utils.SettingsUtil;
import xyz.imxqd.quicklauncher.utils.ShortcutUtil;

public class MainActivity extends BaseActivity {

    private static final String TAG = "QuickLauncher";

    private static final int REQUSET_ADD = 2;

    @BindView(android.R.id.list)
    ListView mGestureList;
    @BindView(android.R.id.progress)
    ProgressBar mProgressBar;
    @BindView(android.R.id.empty)
    View mEmptyView;

    GestureActionAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initMembers() {
        mAdapter = new GestureActionAdapter(this);
    }

    @Override
    protected void initViews() {
        mGestureList.setEmptyView(mEmptyView);
        mGestureList.setAdapter(mAdapter);
    }

    @Override
    protected void initEvents() {
        mGestureList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mGestureList.setOnItemClickListener((parent, view, position, id) -> {
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.action_delete));
            DialogUtil.showList(MainActivity.this, list, (pos, item) -> {
                if (pos == 0) {
                    GestureAction action = (GestureAction) mAdapter.getItem(position);
                    GestureManager.get().delete(action.gestureId);
                    load();
                }
            });
        });
        load();
        if (SettingsUtil.isFloatingBallOn()) {
            Intent intent = new Intent(FloatingService.ACTION_SHOW);
            intent.setClass(this, FloatingService.class);
            startService(intent);
        }
        if (SettingsUtil.isNotificationOn()) {
            Intent intent = new Intent(NotificationService.ACTION_SHOW);
            intent.setClass(this, NotificationService.class);
            startService(intent);
        }
        if (!SettingsUtil.getBoolean("shortcut_auto_create", false)) {
            ShortcutUtil.create();
            SettingsUtil.save("shortcut_auto_create", true);
        }

    }

    private void load() {
        GestureManager.get().load(gestureActions -> {
            mGestureList.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mAdapter.setData(gestureActions);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivityForResult(new Intent(this, CreateGestureActivity.class), REQUSET_ADD);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_about:
                WebView webView = new WebView(this);
                webView.loadUrl("file:///android_asset/copyright.html");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_about)
                        .setView(webView)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSET_ADD && resultCode == RESULT_OK) {
            load();
        }
    }
}
