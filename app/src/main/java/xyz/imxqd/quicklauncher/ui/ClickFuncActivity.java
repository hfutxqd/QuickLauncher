package xyz.imxqd.quicklauncher.ui;

import android.database.Cursor;
import android.gesture.Gesture;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import butterknife.BindView;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.ui.adapters.FuncAdapter;

public class ClickFuncActivity extends BaseActivity {
    public static final String ARG_GESTURE = "arg_gesture";


    @BindView(R.id.func_list)
    ListView mAppList;
    @BindView(R.id.func_loading)
    ProgressBar mProgressBar;

    FuncAdapter mAdapter;

    MenuItem mDoneMenu;
    Gesture mGesture;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_click_func;
    }

    @Override
    protected void initMembers() {
        mGesture = getIntent().getParcelableExtra(ARG_GESTURE);
        if (mGesture == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        Cursor cursor = getContentResolver().query(Uri.parse("content://click_func/"), null, null, null, null);
        mAdapter = new FuncAdapter(this, cursor);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mAppList.setAdapter(mAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_list, menu);
        mDoneMenu = menu.getItem(0).setEnabled(false);
        mDoneMenu.getIcon().setAlpha(80);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            // TODO: 2018/6/21
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }

}