package xyz.imxqd.quicklauncher.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.imxqd.quicklauncher.R;
import xyz.imxqd.quicklauncher.dao.GestureAction;
import xyz.imxqd.quicklauncher.model.GestureManager;

public class GestureActionAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<GestureAction> mActions;

    public GestureActionAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mActions = new ArrayList<>();
    }

    public void setData(List<GestureAction> data) {
        mActions.clear();
        mActions.addAll(data);
    }

    @Override
    public int getCount() {
        return mActions.size();
    }

    @Override
    public Object getItem(int position) {
        return mActions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mActions.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_gesture_action, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        GestureAction action = mActions.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.icon.setImageBitmap(GestureManager.get().getGestureThumbById(action.gestureId));
        holder.title.setText(action.name);

        return convertView;
    }

    class ViewHolder {
        View itemView;
        @BindView(R.id.gesture_icon)
        ImageView icon;
        @BindView(R.id.gesture_title)
        TextView title;
        public ViewHolder(View itemView) {
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
