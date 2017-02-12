package com.example.incredibly.smarttodo.persenter;


import android.content.Context;

import com.example.incredibly.smarttodo.contract.AddContract;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.provider.CategoryStore;
import com.example.incredibly.smarttodo.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddPresenter implements AddContract.Presenter{

    private AddContract.View mView;
    private Repository mRepository;

    public AddPresenter(AddContract.View view, Repository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void loadSpinner(final Context context, final String category) {
        Observable.just(1).subscribeOn(Schedulers.io())
                .map(new Func1<Integer, Set<String>>() {
                    @Override
                    public Set<String> call(Integer integer) {
                        Set<String> categories = CategoryStore.getInstance().loadNames(context);
                        if(!categories.contains(Constant.CATEGORY_DEFAULT)){
                            categories.add(Constant.CATEGORY_DEFAULT);
                        }
                        if(!categories.contains(category)){
                            categories.add(category);
                        }
                        return categories;
                    }
                })
                .map(new Func1<Set<String>, List<String>>() {
                    @Override
                    public List<String> call(Set<String> strings) {
                        List<String> categories = new ArrayList<>();
                        for (String s : categories) {
                            categories.add(s);
                        }
                        return categories;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> categories) {
                        mView.updateSpinner(categories, category);
                    }
                });
    }

}
