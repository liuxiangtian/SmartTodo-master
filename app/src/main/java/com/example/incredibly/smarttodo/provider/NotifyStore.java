package com.example.incredibly.smarttodo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Sync;

import java.util.ArrayList;
import java.util.List;

public class NotifyStore {

    public static String TABLE_NAME = "NOTIFY_TABLE";

    public static String TYPE = "TYPE";
    public static String ID = "ID";
    public static String TASK_ID = "TASK_ID";
    public static String COUNT = "COUNT";
    public static String IS_DONE = "IS_DONE";
    public static String NOTIFY_TIME = "NOTIFY_TIME";
    public static String UPDATE_DATE = "UPDATE_DATE";
    public static String OBJ_ID = "OBJ_ID ";

    private static final String[] PROJECTION = new String[]{TYPE, ID, IS_DONE, NOTIFY_TIME, OBJ_ID, UPDATE_DATE, COUNT, TASK_ID};

    private static String CREATE_TABLE;
    private static String DROP_TABLE;
    private static NotifyStore sInstance;

    static  {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(IS_DONE);
        builder.append(" INTEGER NOT NULL,");
        builder.append(ID);
        builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(TASK_ID);
        builder.append(" INTEGER NOT NULL,");
        builder.append(COUNT);
        builder.append(" INTEGER,");
        builder.append(OBJ_ID);
        builder.append(" STRING,");
        builder.append(TYPE);
        builder.append(" INTEGER NOT NULL,");
        builder.append(UPDATE_DATE);
        builder.append(" LONG NOT NULL,");
        builder.append(NOTIFY_TIME);
        builder.append(" LONG NOT NULL");
        builder.append(");");
        CREATE_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static final synchronized NotifyStore getInstance() {
        if(sInstance==null){
            synchronized (NotifyStore.class) {
                if(sInstance==null) {
                    sInstance = new NotifyStore();
                }
            }
        }
        return sInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TABLE);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    private ContentValues getContentValues(Notify notify) {
        final ContentValues values = new ContentValues();
        values.put(ID, notify.getId());
        values.put(IS_DONE, notify.isDone()?1:0);
        values.put(NOTIFY_TIME, notify.getNotifyTime());
        values.put(TYPE, notify.getTimeType());
        values.put(OBJ_ID, notify.getObjectId());
        values.put(COUNT, notify.getCount());
        values.put(UPDATE_DATE, notify.getUpdateTime());
        values.put(TASK_ID, notify.getTaskId());
        return values;
    }

    public void insert(Context context, final Notify notify, boolean needSync) {
        if (notify==null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(notify);
        database.insert(TABLE_NAME, null, values);
        if(needSync){
            Sync sync = new Sync();
            sync.setType(Sync.TYPE_NOTIFY);
            sync.setUpdateTime(notify.getUpdateTime());
            sync.setDelete(false);
            sync.setObjId(notify.getObjectId());
            sync.setId(notify.getId());
            SyncStore.getInstance().insertOrUpdate(context, sync);
        }
    }

    public void update(Context context, final Notify notify, boolean needSync) {
        if (notify==null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(notify);
        database.update(TABLE_NAME, values, ID+" = ? ", new String[]{notify.getId()+""});
        if(needSync){
            Sync sync = new Sync();
            sync.setType(Sync.TYPE_NOTIFY);
            sync.setUpdateTime(notify.getUpdateTime());
            sync.setDelete(false);
            sync.setObjId(notify.getObjectId());
            sync.setId(notify.getId());
            SyncStore.getInstance().insertOrUpdate(context, sync);
        }
    }

    public void insertOrUpdate(Context context, final Notify notify, boolean needSync) {
        if (notify==null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, ID + "= ? ", new String[]{notify.getId()+""}, null, null, null, null);
        if(cursor!=null || cursor.moveToFirst()){
            update(context, notify, needSync);
        } else {
            insert(context, notify, needSync);
        }
        closeCursor(cursor);
    }

    public void delete(Context context, final Notify notify, boolean needSync) {
        if (notify == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, ID + " = ? ", new String[]{notify.getId()+""});
        if(needSync){
            Sync sync = new Sync();
            sync.setType(Sync.TYPE_NOTIFY);
            sync.setUpdateTime(notify.getUpdateTime());
            sync.setDelete(true);
            sync.setObjId(notify.getObjectId());
            sync.setId(notify.getId());
            SyncStore.getInstance().insertOrUpdate(context, sync);
        }
    }

    public void deleteAll(Context context, int id, boolean needSync) {
        if(needSync){
            List<Notify> notifies = loadNotifiesById(context, id);
            for (Notify notify : notifies) {
                delete(context, notify, needSync);
            }
        } else {
            final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
            database.delete(TABLE_NAME, ID + " = ? ", new String[]{id + ""});
        }
    }

    public List<Notify> loadNotifiesById(Context context, int id) {
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, ID + "= ? and "+ IS_DONE, new String[]{id+"", "0"}, null, null, null, null);
        return getNotifiesFromCursor(cursor);
    }

    public List<Notify> loadNotifies(Context context) {
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, null, null, null, null, null, null);
        return getNotifiesFromCursor(cursor);
    }

    public Notify loadTaskBySync(Context context, Sync sync) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, ID + " =? ", new String[]{sync.getId() + ""}, null, null, null, "1");
        return getNotifyFromCursor(cursor);
    }


    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    private List<Notify> getNotifiesFromCursor(Cursor cursor){
        List<Notify> notifies = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Notify notify = new Notify();
                notify.setTimeType(cursor.getInt(0));
                notify.setId(cursor.getInt(1));
                notify.setDone((cursor.getInt(2)==1));
                notify.setNotifyTime(cursor.getLong(3));
                notify.setObjectId(cursor.getString(4));
                notify.setUpdateTime(cursor.getLong(5));
                notify.setCount(cursor.getInt(6));
                notify.setTaskId(cursor.getInt(7));
                notifies.add(notify);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return notifies;
    }


    private Notify getNotifyFromCursor(Cursor cursor){
        Notify notify = null;
        if (cursor != null && cursor.moveToFirst()) {
            notify = new Notify();
            notify.setTimeType(cursor.getInt(0));
            notify.setId(cursor.getInt(1));
            notify.setDone((cursor.getInt(2)==1));
            notify.setNotifyTime(cursor.getLong(3));
            notify.setObjectId(cursor.getString(4));
            notify.setUpdateTime(cursor.getLong(5));
            notify.setCount(cursor.getInt(6));
            notify.setTaskId(cursor.getInt(7));
        }
        closeCursor(cursor);
        return notify;
    }
}
