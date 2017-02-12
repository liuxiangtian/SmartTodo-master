package com.example.incredibly.smarttodo.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDB extends SQLiteOpenHelper {

    private static final String DATABASE = "TODO_DATABASE";
    private static final int VERSION = 1;
    private static TodoDB sInstance;

    public TodoDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static final synchronized TodoDB newInstance(Context context) {
        return new TodoDB(context.getApplicationContext(), DATABASE, null, VERSION);
    }

    public static final synchronized TodoDB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TodoDB.class) {
                if (sInstance == null) {
                    sInstance = new TodoDB(context.getApplicationContext(), DATABASE, null, VERSION);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CategoryStore.getInstance().onCreate(db);
        TaskStore.getInstance().onCreate(db);
        SyncStore.getInstance().onCreate(db);
        NotifyStore.getInstance().onCreate(db);
        ReviewStore.getInstance().onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CategoryStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        TaskStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        SyncStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        NotifyStore.getInstance().onUpgrade(db, oldVersion, newVersion);
        ReviewStore.getInstance().onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CategoryStore.getInstance().onDowngrade(db, oldVersion, newVersion);
        TaskStore.getInstance().onDowngrade(db, oldVersion, newVersion);
        SyncStore.getInstance().onDowngrade(db, oldVersion, newVersion);
        NotifyStore.getInstance().onDowngrade(db, oldVersion, newVersion);
        ReviewStore.getInstance().onDowngrade(db, oldVersion, newVersion);
    }

}
