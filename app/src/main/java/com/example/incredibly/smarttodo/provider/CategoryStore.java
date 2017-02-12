package com.example.incredibly.smarttodo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.incredibly.smarttodo.model.Category;
import com.example.incredibly.smarttodo.model.Sync;

import java.util.HashSet;
import java.util.Set;

public class CategoryStore {

    public static String TABLE_NAME = "CATEGORY_TABLE";
    public static String ID = "ID";
    public static String NAME = "NAME";
    public static String IS_HIDE = "IS_HIDE";
    public static String UPDATE_DATE = "UPDATE_DATE";
    public static String OBJECT_ID = "OBJECT_ID";

    public static final String[] PROJECTION = new String[]{ID, NAME, IS_HIDE, OBJECT_ID, UPDATE_DATE};

    private static String CREATE_TABLE;
    private static String DROP_TABLE;
    private static CategoryStore sInstance;

    static  {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(ID);
        builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(IS_HIDE);
        builder.append(" INTEGER NOT NULL,");
        builder.append(OBJECT_ID);
        builder.append(" STRING,");
        builder.append(UPDATE_DATE);
        builder.append(" LONG NOT NULL,");
        builder.append(NAME);
        builder.append(" STRING NOT NULL");
        builder.append(");");
        CREATE_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static final synchronized CategoryStore getInstance() {
        if(sInstance==null){
            synchronized (CategoryStore.class) {
                if(sInstance==null) {
                    sInstance = new CategoryStore();
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

    public void insert(Context context, final Category category) {
        if (category == null || TextUtils.isEmpty(category.getName())) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(NAME, category.getName());
        values.put(IS_HIDE, category.isHide()?1:0);
        values.put(UPDATE_DATE, System.currentTimeMillis());
        values.put(OBJECT_ID, category.getObjectId());
        database.insert(TABLE_NAME, null, values);
    }

    public void update(Context context, Category category) {
        if (category == null || TextUtils.isEmpty(category.getName())) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(NAME, category.getName());
        values.put(IS_HIDE, category.isHide()?1:0);
        values.put(UPDATE_DATE, System.currentTimeMillis());
        values.put(OBJECT_ID, category.getObjectId());
        database.update(TABLE_NAME, values, NAME+" =?", new String[]{category.getName()});
    }

    public Category getCategoryFromName(Context context, final String name){
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, NAME+" = ? ", new String[]{name}, null, null, null, "1");
        Category category = null;
        if (cursor != null && cursor.moveToFirst()) {
            category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(name);
            category.setHide((cursor.getInt(2)==1));
            category.setObjectId(cursor.getString(3));
            category.setUpdateTime(cursor.getLong(4));
        }
        closeCursor(cursor);
        return category;
    }

    public Set<Category> getCategoriesFromCursor(Cursor cursor){
        Set<Category> categories = new HashSet<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setHide((cursor.getInt(2)==1));
                category.setObjectId(cursor.getString(3));
                category.setUpdateTime(cursor.getLong(4));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return categories;
    }

    public Category getCategoryFromCursor(Cursor cursor){
        Category category = new Category();
        if (cursor != null && cursor.moveToFirst()) {
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setHide((cursor.getInt(2)==1));
            category.setObjectId(cursor.getString(3));
            category.setUpdateTime(cursor.getLong(4));
        }
        closeCursor(cursor);
        return category;
    }

    public Category loadCategoryBySync(Context context, Sync sync) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, NAME + " =?", new String[]{sync.getId()+""}, null, null, null, "1");
        return getCategoryFromCursor(cursor);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public Set<String> loadNames(Context context) {
        Set<String> names = new HashSet<>();
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{NAME}, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            names.add(name);
        }
        closeCursor(cursor);
        return names;
    }

    public Category loadCategory(Context context, String title) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, NAME + " =?", new String[]{title}, null, null, null, "1");
        return getCategoryFromCursor(cursor);
    }
}
