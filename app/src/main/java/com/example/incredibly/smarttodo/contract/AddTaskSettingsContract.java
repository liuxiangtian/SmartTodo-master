package com.example.incredibly.smarttodo.contract;


import android.content.Context;

import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;

import java.util.List;

public interface AddTaskSettingsContract {

    interface View {
        void showExecuteTime(int executeTimeType, long executeTime);
        void showExecuteNotify(List<Notify> notifys);
        void showExecuteReview(List<Review> reviews);
        void showExecuteRepeat(int repeat);
        void updateBackground();
        void clearSettings();
        void updateTask();
    }

    interface Presenter {
        void updateExecuteNotify(final Context context, final int id);
        void updateExecuteReview(final Context context, final int id);
    }

}
