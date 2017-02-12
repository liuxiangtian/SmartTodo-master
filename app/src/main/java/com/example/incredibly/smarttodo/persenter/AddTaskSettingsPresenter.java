package com.example.incredibly.smarttodo.persenter;

import android.content.Context;

import com.example.incredibly.smarttodo.contract.AddTaskSettingsContract;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.provider.NotifyStore;
import com.example.incredibly.smarttodo.provider.ReviewStore;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddTaskSettingsPresenter implements AddTaskSettingsContract.Presenter{

    private AddTaskSettingsContract.View mView;
    private Repository mRepository;

    public AddTaskSettingsPresenter(AddTaskSettingsContract.View view, Repository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void updateExecuteNotify(final Context context, final int id) {
        Observable.from(NotifyStore.getInstance().loadNotifiesById(context, id))
                .subscribeOn(Schedulers.io())
                .filter(new Func1<Notify, Boolean>() {
                    @Override
                    public Boolean call(Notify notify) {
                        return !notify.isDone();
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Notify>>() {
                    @Override
                    public void call(List<Notify> notifies) {
                        mView.showExecuteNotify(notifies);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.showExecuteNotify(null);
                    }
                });
    }

    @Override
    public void updateExecuteReview(final Context context, final int id) {
        Observable.from(ReviewStore.getInstance().loadReviewsById(context, id))
                .subscribeOn(Schedulers.io())
                .filter(new Func1<Review, Boolean>() {
                    @Override
                    public Boolean call(Review reviewTime) {
                        return !reviewTime.isDone();
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Review>>() {
                    @Override
                    public void call(List<Review> undoneReviews) {
                        mView.showExecuteReview(undoneReviews);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.showExecuteReview(null);
                    }
                });
    }

}
