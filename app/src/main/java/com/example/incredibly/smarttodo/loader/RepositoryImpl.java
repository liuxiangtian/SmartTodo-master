package com.example.incredibly.smarttodo.loader;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.model.Category;
import com.example.incredibly.smarttodo.model.MyUser;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Sync;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.provider.CategoryStore;
import com.example.incredibly.smarttodo.provider.NotifyStore;
import com.example.incredibly.smarttodo.provider.ReviewStore;
import com.example.incredibly.smarttodo.provider.SyncStore;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.provider.TodoDB;
import com.example.incredibly.smarttodo.util.Constant;

import org.joda.time.DateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;

public class RepositoryImpl implements Repository {

    @Override
    public List<Task> loadNativeTasks(String navigation, String title, long start, long end) {
        if (Constant.NAV_SOMEDAY.equals(navigation) || Constant.NAV_TODAY.equals(navigation) || Constant.NAV_WEEKDAY.equals(navigation) || Constant.NAV_MONTH.equals(navigation)) {
            return TaskStore.getInstance().loadTaskByTime(App.getInstance(), start, end);
        } else if (Constant.NAV_CATEGORY.equals(navigation)) {
            return TaskStore.getInstance().loadTaskByCategory(App.getInstance(), title);
        }
        return null;
    }

    @Override
    public Observable<List<Task>> loadNetworkTasks(String navigation, String title, long start, long end) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return null;
        }
        BmobQuery<Task> query = new BmobQuery<>();
        query.addWhereGreaterThanOrEqualTo(TaskStore.EXECUTE_END_TIME, start);
        query.addWhereLessThan(TaskStore.EXECUTE_END_TIME, end);
        query.addWhereEqualTo("user", myUser.getObjectId());
        return query.findObjectsObservable(Task.class);
    }

    @Override
    public void loadTaskByObjId(String objId, QueryListener<Task> queryListener) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        BmobQuery<Task> taskBmobQuery = new BmobQuery<>();
        taskBmobQuery.addWhereEqualTo("user", myUser.getObjectId());
        taskBmobQuery.getObject(objId, queryListener);
    }

    @Override
    public void loadNotifyByObjId(String objId, QueryListener<Notify> queryListener) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        BmobQuery<Notify> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user", myUser.getObjectId());
        bmobQuery.getObject(objId, queryListener);
    }

    @Override
    public void loadReviewByObjId(String objId, QueryListener<Review> queryListener) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        BmobQuery<Review> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user", myUser.getObjectId());
        bmobQuery.getObject(objId, queryListener);
    }

    @Override
    public void loadCategoryByObjId(String objId, QueryListener<Category> queryListener) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        BmobQuery<Category> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user", myUser.getObjectId());
        bmobQuery.getObject(objId, queryListener);
    }

    @Override
    public void deleteTasks(Context context, List<Task> deleteTasks) {
        if(deleteTasks==null || deleteTasks.size()==0){
            return;
        }
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        boolean needSync = (myUser == null);
        for (Task deleteTask : deleteTasks) {
            String taskObjectId = deleteTask.getObjectId();
            deleteTask.setUpdateTime(System.currentTimeMillis());
            if(TextUtils.isEmpty(taskObjectId)){
                TaskStore.getInstance().delete(context, deleteTask, false);
            } else {
                if(needSync){
                    TaskStore.getInstance().delete(context, deleteTask, true);
                } else {
                    TaskStore.getInstance().delete(context, deleteTask, false);
                    deleteTask.delete(taskObjectId, null);
                }
            }
        }
    }

    @Override
    public void pushTaskToRemote(final Context context, final Task task) {
        if (task == null) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            TaskStore.getInstance().insertOrUpdate(context, task, true);
            return;
        }
        task.setMyUser(myUser);

        String taskObjectId = task.getObjectId();
        if (TextUtils.isEmpty(taskObjectId)) {
            task.save(new SaveListener<String>() {
                @Override
                public void done(String taskId, BmobException e) {
                    task.setObjectId(taskId);
                    if (e == null) {
                        TaskStore.getInstance().update(context, task, false);
                    } else {
                        TaskStore.getInstance().update(context, task, true);
                    }
                }
            });
        } else {
            task.update(taskObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        TaskStore.getInstance().update(context, task, true);
                    }
                }
            });
        }
    }

    @Override
    public void deleteRemoteTask(final Context context, final Task task) {
        if (task == null) return;
        String taskObjectId = task.getObjectId();
        if(!TextUtils.isEmpty(taskObjectId)){
            task.delete(taskObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e!=null){
                        TaskStore.getInstance().delete(context, task, true);
                    }
                }
            });
        }
    }

    @Override
    public void synchronize(Context context) {
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        if (bmobUser == null) {
            Toast.makeText(context, "先登录再同步!", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在将数据同步到后台...");

        final List<Sync> syncs = SyncStore.getInstance().loadAll(context);
        if (syncs == null || syncs.size() == 0) {
            return;
        }

        int flag = syncs.size();
        final AtomicInteger integer = new AtomicInteger(flag);

        for (Sync sync : syncs) {
            int type = sync.getType();
            if (type == Sync.TYPE_TASK) {
                if (sync.isDelete()) {
                    synchronizeDeleteTask(context, sync, new SynchronizeCallback(integer, progressDialog));
                } else {
                    synchronizeUpdateTask(context, sync, new SynchronizeCallback(integer, progressDialog));
                }
            } else if (type == Sync.TYPE_NOTIFY) {
                if (sync.isDelete()) {
                    synchronizeDeleteNotify(context, sync, new SynchronizeCallback(integer, progressDialog));
                } else {
                    synchronizeUpdateNotify(context, sync, new SynchronizeCallback(integer, progressDialog));
                }
            } else if (type == Sync.TYPE_REVIEW) {
                if (sync.isDelete()) {
                    synchronizeDeleteReview(context, sync, new SynchronizeCallback(integer, progressDialog));
                } else {
                    synchronizeUpdateReview(context, sync, new SynchronizeCallback(integer, progressDialog));
                }
            } else if (type == Sync.TYPE_CATEGORY) {
                if (sync.isDelete()) {
                    synchronizeDeleteCategory(context, sync, new SynchronizeCallback(integer, progressDialog));
                } else {
                    synchronizeUpdateCategory(context, sync, new SynchronizeCallback(integer, progressDialog));
                }
            }
        }
    }

    @Override
    public void prepareRepeatTask(Context context) {
        long dateTime = new DateTime().withTimeAtStartOfDay().toDate().getTime();
        if (App.getPrefsApi().getTaskCreatedDate(-1L) != dateTime) {
            TaskStore.getInstance().refreshTasks(context);
            App.getPrefsApi().putTaskCreatedDate(dateTime);
        }
    }

    @Override
    public void insertCategorySafely(Context context, String name, boolean isHide, boolean needSync) {
        String newName = name;
        if (TextUtils.isEmpty(newName)) {
            newName = Category.CATEGORY_DEFAULT;
        }
        Category category = CategoryStore.getInstance().getCategoryFromName(context, newName);
        if (category == null) {
            category = new Category(newName, isHide);
            CategoryStore.getInstance().insert(context, category);
        } else {
            category.setHide(isHide);
            CategoryStore.getInstance().update(context, category);
        }
        if(needSync){
            Sync sync = new Sync();
            sync.setType(Sync.TYPE_CATEGORY);
            sync.setUpdateTime(System.currentTimeMillis());
            sync.setDelete(false);
            sync.setObjId(category.getObjectId());
            sync.setId(category.getId());
            SyncStore.getInstance().insertOrUpdate(context, sync);
        }
    }

    @Override
    public void deleteCategory(Context context, String name, boolean needSync) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        Category category = CategoryStore.getInstance().getCategoryFromName(context, name);
        if(needSync && category != null){
            Sync sync = new Sync();
            sync.setObjId(category.getObjectId());
            sync.setDelete(true);
            sync.setType(Sync.TYPE_CATEGORY);
            sync.setUpdateTime(System.currentTimeMillis());
            sync.setId(category.getId());
            SyncStore.getInstance().insertOrUpdate(context, sync);
        }
        final SQLiteDatabase database = TodoDB.getInstance(context).getWritableDatabase();
        database.delete(CategoryStore.TABLE_NAME, CategoryStore.NAME + " = ?", new String[]{name});
    }

    @Override
    public Set<Category> loadAllCategory(Context context) {
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(CategoryStore.TABLE_NAME, CategoryStore.PROJECTION, null, null, null, null, null, null);
        return CategoryStore.getInstance().getCategoriesFromCursor(cursor);
    }

    @Override
    public Set<Category> loadAllCategoryWithoutHide(Context context) {
        final SQLiteDatabase database = TodoDB.newInstance(context).getReadableDatabase();
        Cursor cursor = database.query(CategoryStore.TABLE_NAME, CategoryStore.PROJECTION, CategoryStore.IS_HIDE +" = ?", new String[]{"0"}, null, null, null, null);
        return CategoryStore.getInstance().getCategoriesFromCursor(cursor);
    }

    @Override
    public void pushNotifiesToRemote(final Context context, List<Notify> notifies) {
        if (notifies == null || notifies.size()==0) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        for (final Notify notify : notifies) {
            notify.setMyUser(myUser);
            String notifyObjectId = notify.getObjectId();
            if(TextUtils.isEmpty(notifyObjectId)){
                notify.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        notify.setObjectId(s);
                        if (e == null) {
                            NotifyStore.getInstance().update(context, notify, false);
                        } else {
                            NotifyStore.getInstance().update(context, notify, true);
                        }
                    }
                });
            } else {
                notify.update(notifyObjectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            NotifyStore.getInstance().update(context, notify, true);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void pushNotifyToRemote(final Context context, final Notify notify) {
        if (notify == null) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            NotifyStore.getInstance().insertOrUpdate(context, notify, true);
            return;
        }
        notify.setMyUser(myUser);
        String notifyObjectId = notify.getObjectId();
        if(TextUtils.isEmpty(notifyObjectId)){
            notify.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    notify.setObjectId(s);
                    if (e == null) {
                        NotifyStore.getInstance().insertOrUpdate(context, notify, false);
                    } else {
                        NotifyStore.getInstance().insertOrUpdate(context, notify, true);
                    }
                }
            });
        } else {
            notify.update(notifyObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        NotifyStore.getInstance().insertOrUpdate(context, notify, true);
                    }
                }
            });
        }
    }

    @Override
    public void deleteRemoteNotify(final Context context, final Notify notify) {
        if (notify == null) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        notify.setMyUser(myUser);
        String notifyObjectId = notify.getObjectId();
        if(!TextUtils.isEmpty(notifyObjectId)){
            notify.delete(notifyObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e!=null){
                        NotifyStore.getInstance().delete(context, notify, true);
                    }
                }
            });
        }
    }

    @Override
    public void pushReviewToRemote(final Context context, final Review review) {
        if (review == null) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            ReviewStore.getInstance().update(context, review, true);
            return;
        }
        review.setMyUser(myUser);
        String reviewObjectId = review.getObjectId();
        if(TextUtils.isEmpty(reviewObjectId)){
            review.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    review.setObjectId(s);
                    if (e == null) {
                        ReviewStore.getInstance().update(context, review, false);
                    } else {
                        ReviewStore.getInstance().update(context, review, true);
                    }
                }
            });
        } else {
            review.update(reviewObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        ReviewStore.getInstance().update(context, review, true);
                    }
                }
            });
        }
    }

    @Override
    public void deleteReviewById(final Context context, int id) {
        List<Review> reviews = ReviewStore.getInstance().loadReviewsById(context, id);
        if (reviews==null || reviews.size()==0) return;
        for (final Review review : reviews) {
            String objId = review.getObjectId();
            if(!TextUtils.isEmpty(objId)){
                review.delete(objId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            ReviewStore.getInstance().delete(context, review, false);
                        } else {
                            ReviewStore.getInstance().delete(context, review, true);
                        }
                    }
                });
            } else {
                ReviewStore.getInstance().delete(context, review, true);
            }
        }
    }

    @Override
    public void deleteNotifyById(final Context context, int id) {
        List<Notify> notifies = NotifyStore.getInstance().loadNotifiesById(context, id);
        if (notifies==null || notifies.size()==0) return;
        for (final Notify notify : notifies) {
            String objId = notify.getObjectId();
            if(!TextUtils.isEmpty(objId)){
                notify.delete(objId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            NotifyStore.getInstance().delete(context, notify, false);
                        } else {
                            NotifyStore.getInstance().delete(context, notify, true);
                        }
                    }
                });
            } else {
                NotifyStore.getInstance().delete(context, notify, true);
            }
        }
    }

    @Override
    public void deleteRemoteReview(final Context context, final Review review) {
        if (review == null) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            return;
        }
        review.setMyUser(myUser);
        String reviewObjectId = review.getObjectId();
        if(!TextUtils.isEmpty(reviewObjectId)){
            review.delete(reviewObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e!=null){
                        ReviewStore.getInstance().delete(context, review, true);
                    }
                }
            });
        }
    }

    @Override
    public void pushCategoryToRemote(final Context context, final Category category) {
        if (category == null) return;
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if (myUser == null) {
            insertCategorySafely(context, category.getName(), category.isHide(), true);
            return;
        }
        category.setMyUser(myUser);

        String categoryObjectId = category.getObjectId();
        if (TextUtils.isEmpty(categoryObjectId)) {
            category.save(new SaveListener<String>() {
                @Override
                public void done(String taskId, BmobException e) {
                    category.setObjectId(taskId);
                    if (e == null) {
                        insertCategorySafely(context, category.getName(), category.isHide(), false);
                    } else {
                        insertCategorySafely(context, category.getName(), category.isHide(), true);
                    }
                }
            });
        } else {
            category.update(categoryObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        insertCategorySafely(context, category.getName(), category.isHide(), true);
                    }
                }
            });
        }
    }

    public class SynchronizeCallback {

        private AtomicInteger mAtomicInteger;
        private ProgressDialog mProgressDialog;

        public SynchronizeCallback(AtomicInteger atomicInteger, ProgressDialog progressDialog) {
            mAtomicInteger = atomicInteger;
            mProgressDialog = progressDialog;
        }

        public void onSynchronized() {
            int flag = mAtomicInteger.decrementAndGet();
            if (flag == 0) {
                mProgressDialog.dismiss();
                return;
            }
        }
    }

    private void synchronizeDeleteCategory(final Context context, final Sync sync, final SynchronizeCallback synchronizeCallback) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        final String objectId = sync.getObjId();

        if (myUser == null || TextUtils.isEmpty(objectId)) {
            SyncStore.getInstance().delete(context, sync);
            if (synchronizeCallback != null) {
                synchronizeCallback.onSynchronized();
            }
            return;
        }
        final long updatedAt = sync.getUpdateTime();
        loadCategoryByObjId(objectId, new QueryListener<Category>() {
            @Override
            public void done(Category networkCategory, BmobException e) {
                long updatedAtNetwork = networkCategory.getUpdateTime();
                if(e==null){
                    if (updatedAt >= updatedAtNetwork) {
                        networkCategory.delete(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SyncStore.getInstance().delete(context, sync);
                                }
                            }
                        });
                    } else {
                        SyncStore.getInstance().delete(context, sync);
                    }
                } else {
                    SyncStore.getInstance().delete(context, sync);
                }
                if (synchronizeCallback != null) {
                    synchronizeCallback.onSynchronized();
                }
            }
        });
    }

    private void synchronizeUpdateCategory(final Context context, final Sync sync, final SynchronizeCallback synchronizeCallback) {
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        if (bmobUser == null) {
            return;
        }
        final Category category = CategoryStore.getInstance().loadCategoryBySync(context, sync);
        if (category == null) {
            if (synchronizeCallback != null) {
                SyncStore.getInstance().delete(context, sync);
                synchronizeCallback.onSynchronized();
            }
        } else {
            category.setMyUser(bmobUser);
            final String objectId = category.getObjectId();

            if (TextUtils.isEmpty(objectId)) {
                category.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        category.setObjectId(s);
                        insertCategorySafely(context, category.getName(), category.isHide(), false);
                        if (e == null) {
                            SyncStore.getInstance().delete(context, sync);
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            } else {
                loadCategoryByObjId(objectId, new QueryListener<Category>() {
                    @Override
                    public void done(Category networkCategory, BmobException e) {
                        long newworkTime = networkCategory.getUpdateTime();
                        long updateTime = category.getUpdateTime();
                        if(e==null) {
                            if (updateTime >= newworkTime) {
                                category.update(objectId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            SyncStore.getInstance().delete(context, sync);
                                        }
                                    }
                                });
                            } else {
                                insertCategorySafely(context, networkCategory.getName(), networkCategory.isHide(), false);
                                SyncStore.getInstance().delete(context, sync);
                            }
                        } else {
                            category.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    category.setObjectId(s);
                                    insertCategorySafely(context, category.getName(), category.isHide(), false);
                                    if (e == null) {
                                        SyncStore.getInstance().delete(context, sync);
                                    }
                                }
                            });
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            }
        }
    }

    private void synchronizeDeleteReview(final Context context, final Sync sync, final SynchronizeCallback synchronizeCallback) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        final String objectId = sync.getObjId();

        if (myUser == null || TextUtils.isEmpty(objectId)) {
            SyncStore.getInstance().delete(context, sync);
            if (synchronizeCallback != null) {
                synchronizeCallback.onSynchronized();
            }
            return;
        }
        final long updatedAt = sync.getUpdateTime();
        loadReviewByObjId(objectId, new QueryListener<Review>() {
            @Override
            public void done(Review networkReview, BmobException e) {
                long updatedAtNetwork = networkReview.getUpdateTime();
                if(e==null){
                    if (updatedAt >= updatedAtNetwork) {
                        networkReview.delete(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SyncStore.getInstance().delete(context, sync);
                                }
                            }
                        });
                    } else {
                        SyncStore.getInstance().delete(context, sync);
                    }
                } else {
                    SyncStore.getInstance().delete(context, sync);
                }
                if (synchronizeCallback != null) {
                    synchronizeCallback.onSynchronized();
                }
            }
        });
    }

    private void synchronizeUpdateReview(final Context context, final Sync sync, final SynchronizeCallback synchronizeCallback) {
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        if (bmobUser == null) {
            return;
        }
        final Review review = ReviewStore.getInstance().loadReviewBySync(context, sync);
        if (review == null) {
            if (synchronizeCallback != null) {
                SyncStore.getInstance().delete(context, sync);
                synchronizeCallback.onSynchronized();
            }
        } else {
            review.setMyUser(bmobUser);
            final String objectId = review.getObjectId();

            if (TextUtils.isEmpty(objectId)) {
                review.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        review.setObjectId(s);
                        ReviewStore.getInstance().update(context, review, false);
                        if (e == null) {
                            SyncStore.getInstance().delete(context, sync);
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            } else {
                loadReviewByObjId(objectId, new QueryListener<Review>() {
                    @Override
                    public void done(Review networkReview, BmobException e) {
                        long newworkTime = networkReview.getUpdateTime();
                        long updateTime = review.getUpdateTime();
                        if(e==null) {
                            if (updateTime >= newworkTime) {
                                review.update(objectId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            SyncStore.getInstance().delete(context, sync);
                                        }
                                    }
                                });
                            } else {
                                ReviewStore.getInstance().update(context, networkReview, false);
                                SyncStore.getInstance().delete(context, sync);
                            }
                        } else {
                            review.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    review.setObjectId(s);
                                    ReviewStore.getInstance().update(context, review, false);
                                    if (e == null) {
                                        SyncStore.getInstance().delete(context, sync);
                                    }
                                }
                            });
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            }
        }
    }

    private void synchronizeDeleteNotify(final Context context, final Sync sync, final SynchronizeCallback callback) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        final String objectId = sync.getObjId();

        if (myUser == null || TextUtils.isEmpty(objectId)) {
            SyncStore.getInstance().delete(context, sync);
            if (callback != null) {
                callback.onSynchronized();
            }
            return;
        }
        final long updatedAt = sync.getUpdateTime();
        loadNotifyByObjId(objectId, new QueryListener<Notify>() {
            @Override
            public void done(Notify networkNotify, BmobException e) {
                long updatedAtNetwork = networkNotify.getUpdateTime();
                if(e==null){
                    if (updatedAt >= updatedAtNetwork) {
                        networkNotify.delete(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SyncStore.getInstance().delete(context, sync);
                                }
                            }
                        });
                    } else {
                        SyncStore.getInstance().delete(context, sync);
                    }
                } else {
                    SyncStore.getInstance().delete(context, sync);
                }
                if (callback != null) {
                    callback.onSynchronized();
                }
            }
        });
    }

    private void synchronizeUpdateNotify(final Context context, final Sync sync, final SynchronizeCallback synchronizeCallback) {
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        if (bmobUser == null) {
            return;
        }
        final Notify notify = NotifyStore.getInstance().loadTaskBySync(context, sync);
        if (notify == null) {
            if (synchronizeCallback != null) {
                SyncStore.getInstance().delete(context, sync);
                synchronizeCallback.onSynchronized();
            }
        } else {
            notify.setMyUser(bmobUser);
            final String objectId = notify.getObjectId();

            if (TextUtils.isEmpty(objectId)) {
                notify.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        notify.setObjectId(s);
                        NotifyStore.getInstance().update(context, notify, false);
                        if (e == null) {
                            SyncStore.getInstance().delete(context, sync);
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            } else {
                loadNotifyByObjId(objectId, new QueryListener<Notify>() {
                    @Override
                    public void done(Notify networkNotify, BmobException e) {
                        long newworkTime = networkNotify.getUpdateTime();
                        long updateTime = notify.getUpdateTime();
                        if(e==null) {
                            if (updateTime >= newworkTime) {
                                notify.update(objectId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            SyncStore.getInstance().delete(context, sync);
                                        }
                                    }
                                });
                            } else {
                                NotifyStore.getInstance().insertOrUpdate(context, networkNotify, false);
                                SyncStore.getInstance().delete(context, sync);
                            }
                        } else {
                            notify.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    notify.setObjectId(s);
                                    NotifyStore.getInstance().insertOrUpdate(context, notify, false);
                                    if (e == null) {
                                        SyncStore.getInstance().delete(context, sync);
                                    }
                                }
                            });
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            }
        }
    }

    private void synchronizeDeleteTask(final Context context, final Sync sync, final SynchronizeCallback callback) {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        final String objectId = sync.getObjId();

        if (myUser == null || TextUtils.isEmpty(objectId)) {
            SyncStore.getInstance().delete(context, sync);
            if (callback != null) {
                callback.onSynchronized();
            }
            return;
        }
        final long updatedAt = sync.getUpdateTime();
        loadTaskByObjId(objectId, new QueryListener<Task>() {
            @Override
            public void done(Task networkTask, BmobException e) {
                long updatedAtNetwork = networkTask.getUpdateTime();
                if(e==null){
                    if (updatedAt >= updatedAtNetwork) {
                        networkTask.delete(objectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SyncStore.getInstance().delete(context, sync);
                                }
                            }
                        });
                    } else {
                        SyncStore.getInstance().delete(context, sync);
                    }
                } else {
                    SyncStore.getInstance().delete(context, sync);
                }
                if (callback != null) {
                    callback.onSynchronized();
                }
            }
        });
    }

    private void synchronizeUpdateTask(final Context context, final Sync sync, final SynchronizeCallback synchronizeCallback) {
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);
        if (bmobUser == null) {
            return;
        }
        final Task task = TaskStore.getInstance().loadTaskBySync(context, sync);
        if (task == null) {
            if (synchronizeCallback != null) {
                SyncStore.getInstance().delete(context, sync);
                synchronizeCallback.onSynchronized();
            }
        } else {
            task.setMyUser(bmobUser);
            final String objectId = task.getObjectId();

            if (TextUtils.isEmpty(objectId)) {
                task.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        task.setObjectId(s);
                        TaskStore.getInstance().update(context, task, false);
                        if (e == null) {
                            SyncStore.getInstance().delete(context, sync);
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            } else {
                loadTaskByObjId(objectId, new QueryListener<Task>() {
                    @Override
                    public void done(Task networkTask, BmobException e) {
                        long newworkTime = networkTask.getUpdateTime();
                        long updateTime = task.getUpdateTime();
                        if(e==null) {
                            if (updateTime >= newworkTime) {
                                task.update(objectId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            SyncStore.getInstance().delete(context, sync);
                                        }
                                    }
                                });
                            } else {
                                TaskStore.getInstance().insertOrUpdate(context, networkTask, false);
                                SyncStore.getInstance().delete(context, sync);
                            }
                        } else {
                            task.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    task.setObjectId(s);
                                    TaskStore.getInstance().update(context, task, false);
                                    if (e == null) {
                                        SyncStore.getInstance().delete(context, sync);
                                    }
                                }
                            });
                        }
                        if (synchronizeCallback != null) {
                            synchronizeCallback.onSynchronized();
                        }
                    }
                });
            }
        }
    }

}
