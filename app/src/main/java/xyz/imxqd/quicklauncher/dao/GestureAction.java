package xyz.imxqd.quicklauncher.dao;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(name = "named_gesture_action", database = GestureActionDB.class)
public class GestureAction extends BaseModel {
    public static final int ACTION_TYPE_ACTIVIY_INTENT = 1;

    @PrimaryKey(autoincrement = true)
    public long id;

    @NotNull
    @Column(name = "name")
    public String name;

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


    public static GestureAction getByGestureId(long gestureId) {
        return new Select().from(GestureAction.class).where(GestureAction_Table.gesture_id.eq(gestureId)).querySingle();
    }
}
