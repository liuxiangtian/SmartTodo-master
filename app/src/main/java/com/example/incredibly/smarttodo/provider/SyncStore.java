package com.example.incredibly.smarttodo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.incredibly.smarttodo.model.Sync;
import java.util.ArrayList;
import java.util.List;

public class SyncStore {

    private static String TABLE_NAME = "TABLE_SYNC";
    private static String TYPE = "TYPE";
    private static String UPDATE_TIME = "UPDATE_TIME";
    private static String IS_DELETE = "IS_DELETE";
    private static String ID = "ID";
    private static String OBJ_ID = "OBJ_ID ";

    private static final String[] PROJECTION = new String[]{TYPE, UPDATE_TIME, IS_DELETE, ID, OBJ_ID};

    private static String CREATE_TABLE;
    private static String DROP_TABLE;

    private static SyncStore sInstance = null;

    static {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(ID);
        builder.append(" INTEGER, ");
        builder.append(OBJ_ID);
        builder.append(" STRING, ");
        builder.append(TYPE);
        builder.append(" INTEGER, ");
        builder.append(IS_DELETE);
        builder.append(" LONG NOT NULL, ");
        builder.append(UPDATE_TIME);
        builder.append(" LONG NOT NULL");
        builder.append(");");
        CREATE_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static synchronized SyncStore getInstance() {
        if(sInstance==null){
            synchronized (SyncStore.class) {
                if(sInstance==null) {
                    sInstance = new SyncStore();
                }
            }
        }
        return sInstance;
    }

    void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TABLE);
    }

    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    private void insert(Context context, final Sync sync) {
        if (sync == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        ContentValues values = getContentValues(sync);
        database.insert(SyncStore.TABLE_NAME, null, values);
    }

    private void update(Context context, final Sync sync) {
        if (sync == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        ContentValues values = getContentValues(sync);
        database.update(SyncStore.TABLE_NAME, values, SyncStore.ID + " = ? and " + SyncStore.TYPE + " =? ", new String[]{sync.getId() + "", "" + sync.getType()});
    }

    public boolean isSyncExist(Context context, final Sync sync) {
        boolean result = false;
        if (sync == null) {
            return result;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(SyncStore.TABLE_NAME, SyncStore.PROJECTION,
                SyncStore.TYPE + " =? and " + SyncStore.ID + " =?", new String[]{"" + sync.getType(), ""+sync.getId()},
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            result = true;
        }
        closeCursor(cursor);
        return result;
    }

    public void insertOrUpdate(Context context, final Sync sync) {
        if (sync == null) {
            return;
        }

        boolean isExist = isSyncExist(context, sync);
        if (isExist) {
            update(context, sync);
        } else {
            insert(context, sync);
        }
    }

    public List<Sync> loadAll(Context context) {
        List<Sync> syncs = new ArrayList<>();
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(SyncStore.TABLE_NAME, SyncStore.PROJECTION, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                boolean isDelete = cursor.getInt(3) == 1;
                Sync sync = new Sync();
                sync.setType(cursor.getInt(0));
                sync.setUpdateTime(cursor.getLong(1));
                sync.setDelete(isDelete);
                sync.setId(cursor.getInt(4));
                sync.setObjId(cursor.getString(5));
                syncs.add(sync);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return syncs;
    }

    public void delete(Context context, final Sync sync) {
        if (sync == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        database.delete(SyncStore.TABLE_NAME, SyncStore.TYPE + " = ? and " + SyncStore.ID + " = ?", new String[]{"" + sync.getType(), " " + sync.getId()});
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    private ContentValues getContentValues(Sync sync) {
        final ContentValues values = new ContentValues();
        values.put(ID, sync.getId());
        values.put(TYPE, sync.getType());
        values.put(IS_DELETE, sync.isDelete()?1:0);
        values.put(UPDATE_TIME, sync.getUpdateTime());
        values.put(OBJ_ID, sync.getObjId());
        return values;
    }
}
