package xyz.imxqd.quicklauncher.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.model.GestureManager;
import xyz.imxqd.quicklauncher.ui.adapters.FuncAdapter;
import xyz.imxqd.quicklauncher.ui.base.BaseActivity;
import xyz.imxqd.quicklauncher.utils.ClickUtil;

public class ClickFuncActivity extends BaseActivity {
    public static final String ARG_GESTURE = "arg_gesture";


    @BindView(R.id.func_list)
    ListView mAppList;
    @BindView(R.id.func_loading)
    ProgressBar mProgressBar;
    @BindView(android.R.id.empty)
    View mEmptyView;

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
        if (cursor == null) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.require_click_click)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setNeutralButton(R.string.go_to_intall, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
            return;
        }
        mAdapter = new FuncAdapter(this, cursor);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mAppList.setEmptyView(mEmptyView);
        mAppList.setAdapter(mAdapter);
    }

    private String name, description;
    private Intent intent;

    @Override
    protected void initEvents() {
        mAppList.setOnItemClickListener((parent, view, position, id) -> {
            name = (String) view.getTag(R.id.func_name);
            description = (String) view.getTag(R.id.func_description);
            intent = ClickUtil.getFuncIntent(id);

            mDoneMenu.setEnabled(true);
            mDoneMenu.getIcon().setAlpha(255);
            mAdapter.setSelectedId(id);
            mAdapter.notifyDataSetChanged();

        });
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

            GestureManager.get().saveActivityIntentAction(mGesture, name, description, intent);

            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }

}
