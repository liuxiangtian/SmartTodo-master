package com.example.incredibly.smarttodo.persenter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.incredibly.smarttodo.contract.MainContract;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.provider.TaskStore;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private Repository repository;

    public MainPresenter(MainContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void clickHomeMenu(boolean isDelete) {
        if (isDelete) {
            view.toggleDelete(false);
        } else {
            view.toggleDrawer(true);
        }
    }

    @Override
    public void loadTasks(final String navigation, final String title, final long start, final long end, final boolean fromNetwork) {
        view.refresh(true);
        if (fromNetwork) {
            Observable.just(1).subscribeOn(Schedulers.io())
                    .map(new Func1<Integer, Observable<List<Task>>>() {
                        @Override
                        public Observable<List<Task>> call(Integer integer) {
                            return repository.loadNetworkTasks(navigation, title, start, end);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Observable<List<Task>>>() {
                        @Override
                        public void call(Observable<List<Task>> listObservable) {
                            if(listObservable==null){
                                loadTasks(navigation, title, start, end, false);
                                return;
                            }
                            listObservable.subscribe(new Action1<List<Task>>() {
                                @Override
                                public void call(List<Task> tasks) {
                                    view.showTasks(tasks);
                                    view.refresh(false);
                                }
                            });
                        }
                    });
        } else {
            Observable.just(1).subscribeOn(Schedulers.io())
                    .map(new Func1<Integer, List<Task>>() {
                        @Override
                        public List<Task> call(Integer integer) {
                            return repository.loadNativeTasks(navigation, title, start, end);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Task>>() {
                        @Override
                        public void call(List<Task> tasks) {
                            view.showTasks(tasks);
                            view.refresh(false);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            view.showTasks(null);
                            view.refresh(false);
                        }
                    });
        }
    }

    @Override
    public void deleteTasks(final View recyclerView, final List<Task> deleteTasks) {
        if (deleteTasks == null || deleteTasks.size() == 0) return;
        Snackbar.make(recyclerView, "真的要删除这些任务?", Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Observable.just(1).subscribeOn(Schedulers.io())
                                .map(new Func1<Integer, Integer>() {
                                    @Override
                                    public Integer call(Integer integer) {
                                        repository.deleteTasks(recyclerView.getContext(), deleteTasks);
                                        return integer;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        view.toggleDelete(false);
                                        view.reload();
                                    }
                                });
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        view.toggleDelete(false);
                        view.reload();
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                })
                .show();
    }

    @Override
    public void unDoneTask(final Context context, final Task task, final int position) {
        Observable.just(task).subscribeOn(Schedulers.io())
                .map(new Func1<Task, Task>() {
                    @Override
                    public Task call(Task task) {
                        task.setDoneTime(-1L);
                        task.setUpdateTime(System.currentTimeMillis());
                        TaskStore.getInstance().update(context, task, false);
                        return task;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Task, Task>() {
                    @Override
                    public Task call(Task task) {
                        view.changePosition(position);
                        return task;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Task>() {
                               @Override
                               public void call(Task task) {
                                   repository.pushTaskToRemote(context, task);
                               }
                           }
                );
    }

    @Override
    public void doneTask(final Context context, Task task, final int position) {
        Observable.just(task).subscribeOn(Schedulers.io())
                .map(new Func1<Task, Task>() {
                    @Override
                    public Task call(Task task) {
                        if (!task.isDone()) {
                            task.setDoneTime(System.currentTimeMillis());
                            task.setUpdateTime(System.currentTimeMillis());
                        }
                        TaskStore.getInstance().update(context, task, false);
                        return task;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Task, Task>() {
                    @Override
                    public Task call(Task task) {
                        view.changePosition(position);
                        return task;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Task>() {
                               @Override
                               public void call(Task task) {
                                   repository.pushTaskToRemote(context, task);
                               }
                           }
                );
    }

}
