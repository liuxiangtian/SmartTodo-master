package com.example.incredibly.smarttodo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Sync;

import java.util.ArrayList;
import java.util.List;

public class ReviewStore {

    public static String TABLE_NAME = "REVIEW_TABLE";

    public static String TYPE = "TYPE";
    public static String ID = "ID";
    public static String TASK_ID = "TASK_ID";
    public static String IS_DONE = "IS_DONE";
    public static String REVIEW_TIME = "NOTIFY_TIME";
    public static String INDEX = "POSITION";
    public static String UPDATE_DATE = "UPDATE_DATE";
    public static String OBJ_ID = "OBJ_ID ";

    private static final String[] PROJECTION = new String[]{TYPE, ID, IS_DONE, REVIEW_TIME, INDEX, OBJ_ID, UPDATE_DATE, TASK_ID};

    private static String CREATE_TABLE;
    private static String DROP_TABLE;
    private static ReviewStore sInstance;

    static  {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(ID);
        builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(IS_DONE);
        builder.append(" INTEGER NOT NULL,");
        builder.append(TASK_ID);
        builder.append(" INTEGER NOT NULL,");
        builder.append(OBJ_ID);
        builder.append(" STRING, ");
        builder.append(INDEX);
        builder.append(" INTEGER NOT NULL,");
        builder.append(TYPE);
        builder.append(" INTEGER NOT NULL,");
        builder.append(UPDATE_DATE);
        builder.append(" LONG NOT NULL,");
        builder.append(REVIEW_TIME);
        builder.append(" LONG NOT NULL");
        builder.append(");");
        CREATE_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static final synchronized ReviewStore getInstance() {
        if(sInstance==null){
            synchronized (ReviewStore.class) {
                if(sInstance==null) {
                    sInstance = new ReviewStore();
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

    private ContentValues getContentValues(Review review) {
        final ContentValues values = new ContentValues();
        values.put(ID, review.getId());
        values.put(TASK_ID, review.getTaskId());
        values.put(OBJ_ID, review.getObjectId());
        values.put(IS_DONE, review.isDone()?1:0);
        values.put(REVIEW_TIME, review.getReviewTime());
        values.put(TYPE, review.getTimeType());
        values.put(INDEX, review.getIndex());
        values.put(UPDATE_DATE, review.getUpdateTime());
        return values;
    }

    public void insert(Context context, final Review review, boolean needSync) {
        if (review==null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(review);
        database.insert(TABLE_NAME, null, values);
        if (needSync) {
            handlerSync(context, review, false);
        }
    }

    private void handlerSync(Context context, Review review, boolean isDelete) {
        Sync sync = new Sync();
        sync.setType(Sync.TYPE_REVIEW);
        sync.setUpdateTime(review.getUpdateTime());
        sync.setDelete(isDelete);
        sync.setId(review.getId());
        sync.setObjId(review.getObjectId());
        SyncStore.getInstance().insertOrUpdate(context, sync);
    }

    public void update(Context context, final Review review, boolean needSync) {
        if (review==null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(review);
        database.update(TABLE_NAME, values, ID+" = ?", new String[]{review.getId()+""});
        if (needSync) {
            handlerSync(context, review, false);
        }
    }

    public void delete(Context context, final Review review, final boolean needSync) {
        if (review == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, ID + " = ? ", new String[]{review.getId()+""});
        if (needSync) {
            handlerSync(context, review, true);
        }
    }

    public void deleteAll(Context context, int id, boolean needSync) {
        if(needSync){
            List<Review> reviews = loadReviewsById(context, id);
            for (Review review : reviews) {
                delete(context, review, needSync);
            }
        } else {
            final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
            database.delete(TABLE_NAME, ID + " = ? ", new String[]{id+""});
        }
    }

    public List<Review> loadReviews(Context context) {
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, null, null, null, null, null, null);
        return getReviewsFromCursor(cursor);
    }

    public List<Review> loadReviewsById(Context context, int id) {
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, ID+" = ?", new String[]{id+""}, null, null, null, null);
        return getReviewsFromCursor(cursor);
    }

    public Review loadReviewBySync(Context context, Sync sync) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION,
                ID + " =? ", new String[]{sync.getId() + ""}, null, null, null, "1");
        Review review = getReviewFromCursor(cursor);
        return review;
    }

    private List<Review> getReviewsFromCursor(Cursor cursor){
        List<Review> reviews = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Review review = new Review();
                review.setTimeType(cursor.getInt(0));
                review.setId(cursor.getInt(1));
                review.setDone((cursor.getInt(2)==1));
                review.setReviewTime(cursor.getLong(3));
                review.setIndex(cursor.getInt(4));
                review.setObjectId(cursor.getString(5));
                review.setUpdateTime(cursor.getLong(6));
                review.setTaskId(cursor.getInt(7));
                reviews.add(review);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return reviews;
    }

    private Review getReviewFromCursor(Cursor cursor){
        Review review = null;
        if (cursor != null && cursor.moveToFirst()) {
            review = new Review();
            review.setTimeType(cursor.getInt(0));
            review.setId(cursor.getInt(1));
            review.setDone((cursor.getInt(2)==1));
            review.setReviewTime(cursor.getLong(3));
            review.setIndex(cursor.getInt(4));
            review.setObjectId(cursor.getString(5));
            review.setUpdateTime(cursor.getLong(6));
            review.setTaskId(cursor.getInt(7));
        }
        closeCursor(cursor);
        return review;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }
}
