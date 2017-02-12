package com.example.incredibly.smarttodo.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.incredibly.smarttodo.util.Util;


public class DBUtil {

    public static final String[] COUNT_PROJECTION = new String[]{TaskStore.CONTENT, TaskStore.DONE_TIME};

    private static Cursor getDoneCountCursorByDate(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        return database.query(TaskStore.TABLE_NAME, COUNT_PROJECTION,
                TaskStore.DONE_TIME + "> ? AND " + TaskStore.DONE_TIME + " < ? AND " + TaskStore.DONE_TIME + "!=?",
                new String[]{start + "", end + "", "-1"}, null, null, null, null);
    }

    public static int loadTodayDoneCount(Context context) {
        long start = Util.getTodayStartTime();
        long end = Util.getTodayEndTime();
        Cursor cursor = getDoneCountCursorByDate(context, start, end);
        return DBUtil.getCountFromCursor(cursor);
    }

    public static int loadWeekDoneCount(Context context) {
        long start = Util.getWeekStartTime();
        long end = Util.getWeekEndTime();
        Cursor cursor = getDoneCountCursorByDate(context, start, end);
        return DBUtil.getCountFromCursor(cursor);
    }

    public static int loadMonthDoneCount(Context context) {
        long start = Util.getMonthStartTime();
        long end = Util.getMonthEndTime();
        Cursor cursor = getDoneCountCursorByDate(context, start, end);
        return DBUtil.getCountFromCursor(cursor);
    }

    public static int getCountFromCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();
            cursor.close();
            return count;
        }
        cursor.close();
        return 0;
    }

    private static Cursor getCountCursorByDate(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        return database.query(TaskStore.TABLE_NAME, COUNT_PROJECTION,
                "("+TaskStore.EXECUTE_START_TIME + "> ? AND " + TaskStore.EXECUTE_START_TIME + " < ? ) or"+
                        "("+TaskStore.EXECUTE_END_TIME + "> ? AND " + TaskStore.EXECUTE_END_TIME + " < ? )",
                new String[]{start + "", end + "",start + "", end + ""},
                null, null, null, null);
    }

    public static int loadTodayCount(Context context) {
        long start = Util.getTodayStartTime();
        long end = Util.getTodayEndTime();

        Cursor cursor = getCountCursorByDate(context, start, end);
        return getCountFromCursor(cursor);
    }

    public static int loadWeekCount(Context context) {
        long start = Util.getWeekStartTime();
        long end = Util.getWeekEndTime();
        Cursor cursor = getCountCursorByDate(context, start, end);
        return getCountFromCursor(cursor);
    }

    public static int loadMonthCount(Context context) {
        long start = Util.getMonthStartTime();
        long end = Util.getMonthEndTime();
        Cursor cursor = getCountCursorByDate(context, start, end);
        return getCountFromCursor(cursor);
    }

}
