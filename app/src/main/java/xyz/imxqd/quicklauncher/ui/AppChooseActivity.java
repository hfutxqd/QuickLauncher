package xyz.imxqd.quicklauncher.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.dao.GestureAction;
import xyz.imxqd.quicklauncher.model.GestureManager;
import xyz.imxqd.quicklauncher.ui.base.BaseActivity;
import xyz.imxqd.quicklauncher.utils.ClickUtil;


public class AppChooseActivity extends BaseActivity {

    public static final String ARG_GESTURE = "arg_gesture";

    private static final int REQUEST_CLICK_FUNC = 4;

    @BindView(R.id.app_list)
    ListView mAppList;
    @BindView(R.id.app_loading)
    ProgressBar mProgressBar;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            mProgressBar.setVisibility(View.GONE);
            mAppList.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    };
    List<AppInfo> mAppInfoList = new ArrayList<>();
    List<AppInfo> displayedPackages = new ArrayList<>();
    AppListAdapter mAdapter = new AppListAdapter();

    MenuItem mDoneMenu;
    Gesture mGesture;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_app_choose;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void initMembers() {
        mGesture = getIntent().getParcelableExtra(ARG_GESTURE);
        if (mGesture == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void initEvents() {
        loadInfos();
        mAppList.setOnItemClickListener((parent, view, position, id) -> {
            mDoneMenu.setEnabled(true);
            mDoneMenu.getIcon().setAlpha(255);
            mAdapter.select(position);
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
        getMenuInflater().inflate(R.menu.app_list2, menu);
        mDoneMenu = menu.findItem(R.id.action_done);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                displayedPackages.clear();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.name.contains(s)) {
                        displayedPackages.add(appInfo);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            GestureManager.get().save(mGesture, mAdapter.getSelectedItem(), GestureAction.ACTION_TYPE_ACTIVITY_INTENT);
            setResult(RESULT_OK);
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_func) {
            Intent intent = new Intent(this, ClickFuncActivity.class);
            intent.putExtra(ClickFuncActivity.ARG_GESTURE, mGesture);
            startActivityForResult(intent, REQUEST_CLICK_FUNC);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CLICK_FUNC && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void loadInfos() {
        mProgressBar.setVisibility(View.VISIBLE);
        mAppList.setVisibility(View.GONE);
        new Thread(() -> {
            final PackageManager pm = getPackageManager();
            List<AppInfo> infos = new ArrayList<>();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo applicationInfo : packages) {
                AppInfo appInfo = new AppInfo();
                appInfo.name = applicationInfo.loadLabel(pm).toString();
                appInfo.icon = applicationInfo.loadIcon(pm);
                appInfo.packageName = applicationInfo.packageName;
                Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
                if (intent != null) {
                    infos.add(appInfo);
                }
            }
            Comparator<AppInfo> cmp = (o1, o2) -> Collator.getInstance(Locale.getDefault()).compare(o1.name, o2.name);
            Collections.sort(infos, cmp);
            mAppInfoList = infos;
            displayedPackages.clear();
            displayedPackages.addAll(mAppInfoList);
            mHandler.sendEmptyMessage(0);

        }).start();
    }


    @Override
    protected void initViews() {
        super.initViews();
        mAppList.setAdapter(mAdapter);
        mAppList.setFastScrollEnabled(true);
    }

    public class AppListAdapter extends BaseAdapter {

        AppInfo mSelectedItem;

        @Override
        public int getCount() {
            return displayedPackages.size();
        }

        @Override
        public Object getItem(int position) {
            return displayedPackages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_app, parent, false);
                convertView.setTag(new AppViewHolder(convertView));
            }
            AppViewHolder holder = (AppViewHolder) convertView.getTag();
            AppInfo info = displayedPackages.get(position);
            holder.name.setText(info.name);
            holder.packageName.setText(info.packageName);
            holder.icon.setImageDrawable(info.icon);
            convertView.setSelected(info.isSelected);
            convertView.setActivated(info.isSelected);
            return convertView;
        }

        public void select(int pos) {
            for (AppInfo info : displayedPackages) {
                info.isSelected = false;
            }
            mSelectedItem = displayedPackages.get(pos);
            mSelectedItem.isSelected = true;

        }

        public AppInfo getSelectedItem() {
            return mSelectedItem;
        }
    }

    public class AppViewHolder {

        ImageView icon;
        TextView name;
        TextView packageName;

        public AppViewHolder(View itemView) {
            icon = itemView.findViewById(R.id.app_icon);
            name = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.app_package);
        }
    }

    public static class AppInfo {
        public String name;
        public Drawable icon;
        public String packageName;
        public boolean isSelected = false;

        public Intent getLauncherIntent(Context context) {
            return context.getPackageManager().getLaunchIntentForPackage(packageName);
        }
    }
}
