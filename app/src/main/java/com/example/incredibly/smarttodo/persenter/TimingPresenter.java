package com.example.incredibly.smarttodo.persenter;

import android.content.Context;
import android.util.Log;

import com.example.incredibly.smarttodo.activity.TimingActivity;
import com.example.incredibly.smarttodo.contract.TimingContract;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.provider.TaskStore;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TimingPresenter implements TimingContract.Presenter {

    private TimingContract.View mView;
    private Repository mRepository;

    public TimingPresenter(TimingContract.View view, Repository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void toggleTiming(boolean isTiming) {
        if (!isTiming) {
            mView.startTiming();
        } else {
            mView.stopTiming();
        }
    }

    @Override
    public void backAndSave(final Context context, Task task, boolean isTiming) {
        if(!isTiming){
            mView.backToMainActivity();
        } else {
            mView.backToHome();
        }
    }

    @Override
    public void finishComplete(final Context context, Task task) {
        Observable.just(task).subscribeOn(Schedulers.io())
                .map(new Func1<Task, Task>() {
                    @Override
                    public Task call(Task task) {
                        TaskStore.getInstance().update(context, task, false);
                        mRepository.pushTaskToRemote(context, task);
                        return task;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Task>() {
                    @Override
                    public void call(Task task) {
                        mView.finishComplete();
                    }
                });
    }
}
