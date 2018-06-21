package xyz.imxqd.quicklauncher.dao;

import android.content.Intent;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.net.URISyntaxException;
import java.util.List;

import xyz.imxqd.quicklauncher.App;

@Table(name = "named_gesture_action", database = GestureActionDB.class)
public class GestureAction extends BaseModel {
    public static final int ACTION_TYPE_ACTIVITY_INTENT = 1;

    @PrimaryKey(autoincrement = true)
    public long id;

    @NotNull
    @Column(name = "name")
    public String name;

    @NotNull
    @Column(name = "description")
    public String description;

    @NotNull
    @Unique
    @Column(name = "gesture_id")
    public long gestureId;

    @NotNull
    @Column(name = "action_type")
    public int actionType = 1;

    @NotNull
    @Column(name = "action_data")
    public String actionData;

    @NotNull
    @Column(name = "update_time")
    public long updateTime = System.currentTimeMillis();


    public static GestureAction getById(long id) {
        return new Select().from(GestureAction.class).where(GestureAction_Table.id.eq(id)).querySingle();
    }

    public static GestureAction getByGestureId(long gestureId) {
        return new Select().from(GestureAction.class).where(GestureAction_Table.gesture_id.eq(gestureId)).querySingle();
    }

    public static List<GestureAction> getAll() {
        return new Select().from(GestureAction.class).orderBy(GestureAction_Table.update_time.desc()).queryList();
    }

    public void exec() throws URISyntaxException {
        if (actionType == ACTION_TYPE_ACTIVITY_INTENT) {
            Intent intent = Intent.parseUri(actionData, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getApp().startActivity(intent);
        }
    }
}
