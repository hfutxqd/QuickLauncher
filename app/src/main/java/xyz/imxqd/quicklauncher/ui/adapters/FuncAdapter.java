package xyz.imxqd.quicklauncher.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.utils.ClickUtil;

public class FuncAdapter extends CursorAdapter {

    Drawable icon;

    private long mSelectedId = -1;

    public FuncAdapter(Context context, Cursor c) {
        super(context, c, false);
        icon = ClickUtil.getIcon();
    }


    public void setSelectedId(long id) {
        mSelectedId = id;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_app, parent, false);
        v.setTag(new FuncViewHolder(v));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FuncViewHolder holder = (FuncViewHolder) view.getTag();
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        view.setTag(R.id.func_name, name);
        view.setTag(R.id.func_description, description);
        holder.icon.setImageDrawable(icon);
        holder.name.setText(name);
        holder.subTitle.setText(description);
        if (id == mSelectedId) {
            view.setSelected(true);
            view.setActivated(true);
        } else {
            view.setSelected(false);
            view.setActivated(false);
        }
    }

    public class FuncViewHolder {

        ImageView icon;
        TextView name;
        TextView subTitle;

        public FuncViewHolder(View itemView) {
            icon = itemView.findViewById(R.id.app_icon);
            name = itemView.findViewById(R.id.app_name);
            subTitle = itemView.findViewById(R.id.app_package);
        }
    }
}
