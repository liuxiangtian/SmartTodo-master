package com.example.incredibly.smarttodo.provider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.model.Sync;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.util.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TaskStore {

    public static String TABLE_NAME = "Table_Task";

    public static String ID = "ID";
    public static String OBJECT_ID = "OBJECT_ID";
    public static String IMPORTANT = "IMPORTANT";
    public static String HARD = "HARD";
    public static String CONTENT = "CONTENT";
    public static String CATEGORY = "CATEGORY";
    public static String COMMENT = "COMMENT";
    public static String CREATE_DATE = "CREATE_DATE";
    public static String UPDATE_DATE = "UPDATE_DATE";
    public static String DONE_TIME = "DONE_TIME";
    public static String COUNT_DOWN_TIME = "COUNT_DOWN_TIME";
    public static String EXECUTE_DURATION = "EXECUTE_DURATION";
    public static String EXECUTE_TIME = "EXECUTE_TIME";
    public static String EXECUTE_TIME_TYPE = "EXECUTE_TIME_TYPE";
    public static String EXECUTE_START_TIME = "EXECUTE_START_TIME";
    public static String EXECUTE_END_TIME = "EXECUTE_END_TIME";
    public static String REPEAT = "REPEAT";

    public static final String[] PROJECTION = new String[]{
            ID, OBJECT_ID, IMPORTANT, HARD, CONTENT, CATEGORY, COMMENT,
            CREATE_DATE, UPDATE_DATE, DONE_TIME, COUNT_DOWN_TIME, EXECUTE_DURATION,
            EXECUTE_TIME, EXECUTE_TIME_TYPE, REPEAT,EXECUTE_START_TIME,EXECUTE_END_TIME};

    private static String CREATE_TABLE;
    private static String DROP_TABLE;

    private static TaskStore sInstance = null;

    static {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(TABLE_NAME);
        builder.append(" ( ");
        builder.append(ID);
        builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        builder.append(OBJECT_ID);
        builder.append(" STRING, ");
        builder.append(IMPORTANT);
        builder.append(" INTEGER NOT NULL, ");
        builder.append(HARD);
        builder.append(" INTEGER NOT NULL, ");
        builder.append(REPEAT);
        builder.append(" INTEGER NOT NULL, ");
        builder.append(CONTENT);
        builder.append(" STRING NOT NULL, ");
        builder.append(CATEGORY);
        builder.append(" STRING NOT NULL, ");
        builder.append(COMMENT);
        builder.append(" STRING, ");
        builder.append(CREATE_DATE);
        builder.append(" LONG NOT NULL,");
        builder.append(UPDATE_DATE);
        builder.append(" LONG NOT NULL,");
        builder.append(EXECUTE_START_TIME);
        builder.append(" LONG NOT NULL,");
        builder.append(EXECUTE_END_TIME);
        builder.append(" LONG NOT NULL,");
        builder.append(DONE_TIME);
        builder.append(" LONG,");
        builder.append(COUNT_DOWN_TIME);
        builder.append(" LONG,");
        builder.append(EXECUTE_DURATION);
        builder.append(" LONG,");
        builder.append(EXECUTE_TIME);
        builder.append(" LONG, ");
        builder.append(EXECUTE_TIME_TYPE);
        builder.append(" INTEGER NOT NULL");
        builder.append(");");
        CREATE_TABLE = builder.toString();
        builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ");
        builder.append(TABLE_NAME);
        DROP_TABLE = builder.toString();
    }

    public static final synchronized TaskStore getInstance() {
        if (sInstance == null) {
            synchronized (TaskStore.class) {
                if (sInstance == null) {
                    sInstance = new TaskStore();
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

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public void delete(Context context, final Task task, final boolean needSync) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        database.delete(TABLE_NAME, CREATE_DATE + " =?", new String[]{"" + task.getCreateTime()});
        if (needSync) {
            handlerSync(context, task, true);
        }
    }

    public void delete(Context context, List<Task> deleteTasks, final boolean needSync) {
        for (Task deleteTask : deleteTasks) {
            delete(context, deleteTask, needSync);
        }
    }

    public void insert(Context context, final Task task, boolean needSync) {
        if (task == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(task);
        database.insert(TABLE_NAME, null, values);
        if (needSync) {
            int id = getTaskId(context, task);
            if(id!=-1) {
                task.setId(id);
                handlerSync(context, task, false);
            }
        }
    }

    public void update(Context context, Task task, boolean needSync) {
        if (task == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(task);
        database.update(TABLE_NAME, values, CREATE_DATE + " =?", new String[]{"" + task.getCreateTime()});
        if (needSync) {
            handlerSync(context, task, false);
        }
    }

    public int getTaskId(Context context, Task task) {
        int id = -1;
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{ID}, CREATE_DATE + " =?", new String[]{"" + task.getCreateTime()}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        closeCursor(cursor);
        return id;
    }

    public void deleteByCategory(Context context, String category, boolean sync) {
        if (category == null) {
            return;
        }
        List<Task> tasks = loadTaskByCategory(context, category);
        for (Task task : tasks) {
            delete(context, task, sync);
        }
    }

    public List<Task> loadTaskByCategory(Context context, String category) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION,
                CATEGORY + " = ? ", new String[]{category}, null, null, null, null);
        return getTasksFromCursor(cursor);
    }

    public List<Task> loadAll(Context context) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(true, TABLE_NAME, PROJECTION, null, null, null, null, null, null);
        return getTasksFromCursor(cursor);
    }

    public List<Task> loadTaskByTime(Context context, long start, long end) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(true, TABLE_NAME, PROJECTION,
                "("+EXECUTE_TIME+" < ? and "+EXECUTE_START_TIME+" > ? and "+ EXECUTE_START_TIME+" < ? ) or ("
                        +EXECUTE_TIME+" < ? and "+ EXECUTE_END_TIME+ "> ? and "+EXECUTE_END_TIME+" < ? ) or ("+
                        EXECUTE_TIME + " > ? and " + EXECUTE_TIME + " <= ?)",
                new String[]{"0","" + start, "" + end,"0","" + start, "" + end,"" + start, "" + end}, null, null, null, null);
        return getTasksFromCursor(cursor);
    }

    public Task loadTaskBySync(Context context, Sync sync) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION,
                ID + " =? ", new String[]{"" + sync.getId()}, null, null, CREATE_DATE, "1");
        return getTaskFromCursor(cursor);
    }

    public void updateTasksCategory(Context context, List<Task> tasks, String category, boolean needSync) {
        if (tasks == null || tasks.size() == 0) {
            return;
        }
        for (Task task : tasks) {
            task.setCategory(category);
            task.setUpdateTime(System.currentTimeMillis());
            update(context, task, needSync);
        }
    }

    public void insertOrUpdate(Context context, final Task task, boolean sync) {
        if (task == null) {
            return;
        }
        boolean isExist = isTaskExist(context, task);
        if (isExist) {
            update(context, task, sync);
        } else {
            insert(context, task, sync);
        }
    }

    public void insertOrUpdateItemByTime(Context context, Task task) {
        if (task == null) {
            return;
        }
        boolean isExist = isTaskExist(context, task);
        if (isExist) {
            updateItemByTime(context, task);
        } else {
            insert(context, task, false);
        }
    }

    public void updateItemByTime(Context context, Task task) {
        if (task == null) {
            return;
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        final ContentValues values = getContentValues(task);
        database.update(TABLE_NAME, values, CREATE_DATE + " =? and " + UPDATE_DATE + " > ?",
                new String[]{"" + task.getCreateTime(), task.getUpdateTime() + ""});
    }

    public List<Task> loadTasksByContent(Context context, String content) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, CONTENT + " like ?",
                new String[]{"%" + content + "%"}, null, null, CREATE_DATE, null);
        return getTasksFromCursor(cursor);
    }


    public List<Task> loadRepeatTasks(Context context) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, REPEAT + " != ? ", new String[]{"0"}, null, null, null, null);
        return getTasksFromCursor(cursor);
    }

    public List<Task> getTasksFromCursor(Cursor cursor) {
        List<Task> tasks = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = new Task();
                int id = cursor.getInt(0);
                task.setId(id);
                String objId = cursor.getString(1);
                task.setObjectId(objId);

                boolean important = cursor.getInt(2) != 0;
                boolean hard = cursor.getInt(3) != 0;
                task.setImportant(important);
                task.setHard(hard);

                String content = cursor.getString(4);
                task.setContent(content);
                String category = cursor.getString(5);
                String comment = cursor.getString(6);
                task.setCategory(category);
                task.setComment(comment);

                long createTime = cursor.getLong(7);
                long updateTime = cursor.getLong(8);
                long doneTime = cursor.getLong(9);
                task.setCreateTime(createTime);
                task.setUpdateTime(updateTime);
                task.setDoneTime(doneTime);

                long countDownTime = cursor.getLong(10);
                long executeDuration = cursor.getLong(11);
                long executeTime = cursor.getLong(12);
                task.setCountDownTime(countDownTime);
                int executeTimeType = cursor.getInt(13);
                task.setExecuteDuration(executeDuration);
                task.setExecuteTime(executeTime);
                task.setExecuteTimeType(executeTimeType);
                task.setRepeat(cursor.getInt(14));
                task.setExecuteStartTime(cursor.getLong(15));
                task.setExecuteEndTime(cursor.getLong(16));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return tasks;
    }

    public Task getTaskFromCursor(Cursor cursor) {
        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = new Task();
            int id = cursor.getInt(0);
            task.setId(id);
            String objId = cursor.getString(1);
            task.setObjectId(objId);

            boolean important = cursor.getInt(2) != 0;
            boolean hard = cursor.getInt(3) != 0;
            task.setImportant(important);
            task.setHard(hard);

            String content = cursor.getString(4);
            task.setContent(content);
            String category = cursor.getString(5);
            String comment = cursor.getString(6);
            task.setCategory(category);
            task.setComment(comment);

            long createTime = cursor.getLong(7);
            long updateTime = cursor.getLong(8);
            long doneTime = cursor.getLong(9);
            task.setCreateTime(createTime);
            task.setUpdateTime(updateTime);
            task.setDoneTime(doneTime);

            long countDownTime = cursor.getLong(10);
            long executeDuration = cursor.getLong(11);
            long executeTime = cursor.getLong(12);
            task.setCountDownTime(countDownTime);
            int executeTimeType = cursor.getInt(13);
            task.setExecuteDuration(executeDuration);
            task.setExecuteTime(executeTime);
            task.setExecuteTimeType(executeTimeType);
            task.setRepeat(cursor.getInt(14));
            task.setExecuteStartTime(cursor.getLong(15));
            task.setExecuteEndTime(cursor.getLong(16));
        }
        closeCursor(cursor);
        return task;
    }

    public void refresh(Context context, Task task) {
        if(task==null){return;}
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, PROJECTION, CREATE_DATE + " = ? ", new String[]{task.getCreateTime()+""}, null, null, null, null);
        Task newTask = getTaskFromCursor(cursor);
        if(newTask!=null){
            task = newTask;
        }
    }

    private ContentValues getContentValues(Task task) {
        final ContentValues values = new ContentValues();
        values.put(OBJECT_ID, task.getObjectId());
        values.put(IMPORTANT, task.isImportant());
        values.put(HARD, task.isHard());
        values.put(CONTENT, task.getContent());
        values.put(CATEGORY, task.getCategory());
        values.put(COMMENT, task.getComment());
        values.put(CREATE_DATE, task.getCreateTime());
        values.put(UPDATE_DATE, task.getUpdateTime());
        values.put(DONE_TIME, task.getDoneTime());
        values.put(COUNT_DOWN_TIME, task.getCountDownTime());
        values.put(EXECUTE_DURATION, task.getExecuteDuration());
        values.put(EXECUTE_TIME, task.getExecuteTime());
        values.put(EXECUTE_TIME_TYPE, task.getExecuteTimeType());
        values.put(REPEAT, task.getRepeat());
        values.put(EXECUTE_START_TIME, task.getExecuteStartTime());
        values.put(EXECUTE_END_TIME, task.getExecuteEndTime());
        return values;
    }


    private void handlerSync(Context context, Task task, boolean isDelete) {
        Sync sync = new Sync();
        sync.setObjId(task.getObjectId());
        sync.setId(task.getId());
        sync.setType(Sync.TYPE_TASK);
        sync.setUpdateTime(task.getUpdateTime());
        sync.setDelete(isDelete);
        SyncStore.getInstance().insertOrUpdate(context, sync);
    }


    private boolean isTaskExist(Context context, Task task) {
        final SQLiteDatabase database = TodoDB.getInstance(context).getReadableDatabase();
        boolean isExist = false;
        Cursor cursor = database.query(TABLE_NAME, new String[]{ID}, CREATE_DATE + " =?", new String[]{"" + task.getCreateTime()}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            isExist = true;
        } else {
            isExist = false;
        }
        closeCursor(cursor);
        return isExist;
    }

    public void refreshTasks(Context context) {
        int count = App.getPrefsApi().getTestTime(0);

        long start = Util.getTodayStartTime();
        long end = Util.getTodayEndTime();
        start = new DateTime(start).plusDays(count).toDate().getTime();
        end = new DateTime(end).plusDays(count).toDate().getTime();
        List<Task> tasks = TaskStore.getInstance().loadRepeatTasks(context);
        for (Task task : tasks) {
            int repeatTime = 0;
            DateTime dateTime = new DateTime();
            int day = dateTime.getDayOfWeek();
            int standant = 0;
            if (day == 1) {
                standant = 0x40;
            } else if (day == 2) {
                standant = 0x20;
            } else if (day == 3) {
                standant = 0x10;
            } else if (day == 4) {
                standant = 0x08;
            } else if (day == 5) {
                standant = 0x04;
            } else if (day == 6) {
                standant = 0x02;
            } else if (day == 7) {
                standant = 0x01;
            }
            if ((repeatTime & standant) != 0) {
                Task newTask = task.clone();
                newTask.setCreateTime(System.currentTimeMillis());
                newTask.setUpdateTime(System.currentTimeMillis());
                TaskStore.getInstance().insert(context, newTask, false);
            }
        }
    }

}
