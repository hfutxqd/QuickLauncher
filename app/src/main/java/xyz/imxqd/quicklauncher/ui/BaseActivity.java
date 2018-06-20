package xyz.imxqd.quicklauncher.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMembers();
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        initViews();
        initEvents();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }


    protected  <T extends View> T f(@IdRes int viewId) {
        return findViewById(viewId);
    }

    @LayoutRes
    protected abstract int getLayoutResId();

    protected void initMembers() {

    }

    protected  void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected  void initEvents() {

    }
}
