package com.example.incredibly.smarttodo.persenter;

import android.content.Context;

import com.example.incredibly.smarttodo.contract.Contract;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.model.Category;
import com.example.incredibly.smarttodo.provider.CategoryStore;

import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Presenter implements Contract.Presenter {

    private Contract.View mView;
    private Repository mRepository;

    public Presenter(Contract.View view, Repository repository) {
        this.mView = view;
        this.mRepository = repository;
    }

    @Override
    public void prepareNavMenus(final Context context) {
        Observable.just(1).subscribeOn(Schedulers.io())
                .map(new Func1<Integer, Set<Category>>() {
                    @Override
                    public Set<Category> call(Integer integer) {
                        return mRepository.loadAllCategoryWithoutHide(context);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Set<Category>>() {
                    @Override
                    public void call(Set<Category> categories) {
                        for (Category category: categories) {
                            mView.addSubMenu(category.getName());
                        }
                    }
                });
    }

}
