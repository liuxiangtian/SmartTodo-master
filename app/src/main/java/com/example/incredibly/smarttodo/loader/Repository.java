package com.example.incredibly.smarttodo.loader;

import android.content.Context;

import com.example.incredibly.smarttodo.model.Category;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Task;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.listener.QueryListener;
import rx.Observable;

public interface Repository {

    List<Task> loadNativeTasks(String navigation, String title, long start, long end);
    Observable<List<Task>> loadNetworkTasks(String navigation, String title, long start, long end);
    void loadTaskByObjId(String objId, QueryListener<Task> queryListener);
    void loadNotifyByObjId(String objId, QueryListener<Notify> queryListener);
    void loadReviewByObjId(String objId, QueryListener<Review> queryListener);
    void loadCategoryByObjId(String objId, QueryListener<Category> queryListener);

    void deleteTasks(Context context, List<Task> deleteTasks);
    void pushTaskToRemote(final Context context, final Task task);
    void deleteRemoteTask(final Context context, final Task task);
    void pushNotifiesToRemote(final Context context, List<Notify> notifies);
    void pushNotifyToRemote(final Context context, Notify notify);
    void deleteRemoteNotify(final Context context, Notify notify);
    void pushReviewToRemote(final Context context, final Review review);
    void deleteReviewById(final Context context, final int id);
    void deleteNotifyById(final Context context, final int id);
    void deleteRemoteReview(final Context context, final Review review);
    void pushCategoryToRemote(Context context, Category category);
    void synchronize(Context context);
    void prepareRepeatTask(Context context);
    void insertCategorySafely(Context context, final String name, boolean isHide, boolean needSync);
    void deleteCategory(Context context, final String name, boolean needSync);
    Set<Category> loadAllCategory(Context context);
    Set<Category> loadAllCategoryWithoutHide(Context context);
}
