package com.example.incredibly.smarttodo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.provider.DBUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class StatisticItemFragment extends Fragment {

    @Bind(R.id.text_sum_label) TextView textSumLabel;
    @Bind(R.id.text_sum_count) TextView textSumCount;
    @Bind(R.id.text_complete_label)
    TextView textCompleteLabel;
    @Bind(R.id.text_complete_ratio) TextView textCompleteRatio;

    private int position;
    private int completeCount;
    private int sumCount;

    public static StatisticItemFragment newInstance(int position) {
        StatisticItemFragment fragment = new StatisticItemFragment();
        Bundle args = new Bundle();
        args.putInt("STATISTIC_TYPE", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        position = args.getInt("STATISTIC_TYPE");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_statiistic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(position==0){
            textSumLabel.setText("今日任务总数");
            textCompleteLabel.setText("今日完成总数");
        } else if(position==1){
            textSumLabel.setText("本周任务总数");
            textCompleteLabel.setText("本周完成总数");
        } else if(position==2){
            textSumLabel.setText("本月任务总数");
            textCompleteLabel.setText("本月完成总数");
        }
        loadTodayData();
    }

    private void loadTodayData() {
                Observable.just(1).subscribeOn(Schedulers.io())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if(position==0){
                            completeCount = DBUtil.loadTodayDoneCount(getContext());
                            sumCount = DBUtil.loadTodayCount(getContext());
                        } else if(position==1) {
                            completeCount = DBUtil.loadWeekDoneCount(getContext());
                            sumCount = DBUtil.loadWeekCount(getContext());
                        } else if(position==2) {
                            completeCount = DBUtil.loadMonthDoneCount(getContext());
                            sumCount = DBUtil.loadMonthCount(getContext());
                        }
                        return integer;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer aVoid) {
                        float todayResult = (sumCount == 0) ? 0 : (100f * completeCount / sumCount);
                        String todayRatio = String.format("%2.0f", todayResult) + "%";
                        textSumCount.setText(completeCount + " / " + sumCount);
                        textCompleteRatio.setText(todayRatio);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        textSumCount.setText("0 / 0");
                        textCompleteRatio.setText("0%");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
