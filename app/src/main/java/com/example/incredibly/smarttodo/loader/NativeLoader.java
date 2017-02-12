package com.example.incredibly.smarttodo.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;

import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.provider.DBUtil;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.provider.TodoDB;
import com.example.incredibly.smarttodo.util.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class NativeLoader {

    public static final String[] COUNT_PROJECTION = new String[]{TaskStore.CONTENT, TaskStore.DONE_TIME};


    public static int loadTodayDoneCount(Context context) {
        long start = Util.getTodayStartTime();
        long end = Util.getTodayEndTime();
        Cursor cursor = getDoneCountCursorByDate(context, start, end);
        return getCountFromCursor(cursor);
    }

    public static int loadWeekDoneCount(Context context) {
        long start = Util.getWeekStartTime();
        long end = Util.getWeekEndTime();
        Cursor cursor = getDoneCountCursorByDate(context, start, end);
        return getCountFromCursor(cursor);
    }

    public static int loadMonthDoneCount(Context context) {
        long start = Util.getMonthStartTime();
        long end = Util.getMonthEndTime();
        Cursor cursor = getDoneCountCursorByDate(context, start, end);
        return getCountFromCursor(cursor);
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

    public static List<Pair<Integer, Integer>> loadTasksByWeeks(Context context) {
        DateTime now = new DateTime(System.currentTimeMillis());
        long flag = now.dayOfWeek().withMinimumValue().withTimeAtStartOfDay().toDate().getTime();
        int count = now.dayOfWeek().getMaximumValue();
        return getStatisticPairs(context, flag, count);
    }

    public static List<Pair<Integer, Integer>> loadTasksByMonths(Context context) {
        DateTime now = new DateTime(System.currentTimeMillis());
        long flag = now.dayOfMonth().withMinimumValue().withTimeAtStartOfDay().toDate().getTime();
        int count = now.dayOfMonth().getMaximumValue();
        return getStatisticPairs(context, flag, count);
    }

    public static List<Task> loadUnDoneTasksForToday(Context context) {
        long start = Util.getTodayStartTime();
        long end = Util.getTodayEndTime();
        Cursor cursor = getUnDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadUnDoneTasksForWeek(Context context) {
        long start = Util.getWeekStartTime();
        long end = Util.getWeekEndTime();
        Cursor cursor = getUnDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadUnDoneTasksForMonth(Context context) {
        long start = Util.getMonthStartTime();
        long end = Util.getMonthEndTime();
        Cursor cursor = getUnDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadUnDoneTasksForSomeDay(Context context, int year, int month, int day) {
        DateTime now = new DateTime(year, month, day, 0, 0);
        long start = now.toDate().getTime();
        long end = now.plusDays(1).toDate().getTime();
        Cursor cursor = getUnDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    private static Cursor getTaskCursorForCategory(Context context, String category) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TaskStore.TABLE_NAME, TaskStore.PROJECTION,
                TaskStore.CATEGORY + " = ? ", new String[]{category},
                null, null, TaskStore.CREATE_DATE, null);
        return cursor;
    }

    public static List<Task> loadDoneTasksForToday(Context context) {
        long start = Util.getTodayStartTime();
        long end = Util.getTodayEndTime();
        Cursor cursor = getDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadDoneTasksForWeek(Context context) {
        long start = Util.getWeekStartTime();
        long end = Util.getWeekEndTime();
        Cursor cursor = getDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadDoneTasksForMonth(Context context) {
        long start = Util.getMonthStartTime();
        long end = Util.getMonthEndTime();
        Cursor cursor = getDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadUnDoneTasksForCategory(Context context, String category) {
        Cursor cursor = getCursorForCategory(context, category, false);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadDoneTasksForCategory(Context context, String category) {
        Cursor cursor = getCursorForCategory(context, category, true);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadDoneTasksForSomeDay(Context context, int year, int month, int day) {
        DateTime now = new DateTime(year, month, day, 0, 0);
        long start = now.toDate().getTime();
        long end = now.plusDays(1).toDate().getTime();
        Cursor cursor = getDoneCursorByDate(context, start, end);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    public static List<Task> loadTasksByTitle(Context context, String title) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TaskStore.TABLE_NAME, TaskStore.PROJECTION,
                TaskStore.CONTENT + " like ?", new String[]{"%" + title + "%"},
                null, null, TaskStore.CREATE_DATE, null);
        return TaskStore.getInstance().getTasksFromCursor(cursor);
    }

    private static Cursor getCursorForCategory(Context context, String category, boolean done) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TaskStore.TABLE_NAME, TaskStore.PROJECTION,
                TaskStore.CATEGORY + " = ? AND " + TaskStore.DONE_TIME + (done ? "= ?" : "!= ?"),
                new String[]{category, "-1"},
                null, null, TaskStore.CREATE_DATE, null);
        return cursor;
    }

    private static Cursor getDoneCursorByDate(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        return database.query(TaskStore.TABLE_NAME, TaskStore.PROJECTION,
                TaskStore.DONE_TIME + "> ? AND " + TaskStore.DONE_TIME + " < ?", new String[]{start + "", end + ""},
                null, null, null, null);
    }

    private static Cursor getDoneCountCursorByDate(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        return database.query(TaskStore.TABLE_NAME, COUNT_PROJECTION,
                TaskStore.DONE_TIME + "> ? AND " + TaskStore.DONE_TIME + " < ? ", new String[]{start + "", end + ""}, null, null, null, null);
    }

    private static Cursor getCountCursorByDate(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        return database.query(TaskStore.TABLE_NAME, COUNT_PROJECTION,
                TaskStore.UPDATE_DATE + "> ? AND " + TaskStore.UPDATE_DATE + " < ? ",
                new String[]{start + "", end + ""},
                null, null, null, null);
    }

    private static int getCountFromCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getCount();
            cursor.close();
            return count;
        }
        cursor.close();
        return 0;
    }

    private static List<Pair<Integer, Integer>> getStatisticPairs(Context context, long flag, int count) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        List<Pair<Integer, Integer>> pairList = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            long start = new DateTime(flag).plusDays(index).withTimeAtStartOfDay().toDate().getTime();
            long end = new DateTime(flag).plusDays(index + 1).withTimeAtStartOfDay().toDate().getTime();
            Cursor cursor = database.query(TaskStore.TABLE_NAME, new String[]{TaskStore.DONE_TIME},
                    TaskStore.CREATE_DATE + "> ? AND " + TaskStore.CREATE_DATE + " < ?",
                    new String[]{String.valueOf(start), String.valueOf(end)},
                    null, null, TaskStore.CREATE_DATE, null);
            int complete = 0;
            int sum = 0;
            if (cursor != null && cursor.moveToFirst()) {
                sum = cursor.getCount();
                do {
                    long doneTime = cursor.getLong(0);
                    if (doneTime!=-1L) {
                        complete++;
                    }
                } while (cursor.moveToNext());
            }
            pairList.add(Pair.create(complete, sum));
            closeCursor(cursor);
        }
        return pairList;
    }

    private static Cursor getUnDoneCursorByDate(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        return database.query(TaskStore.TABLE_NAME, TaskStore.PROJECTION, TaskStore.DONE_TIME + " = ? ", new String[]{"-1"},
                null, null, TaskStore.CREATE_DATE, null);
    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

}
