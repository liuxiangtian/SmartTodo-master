package com.example.incredibly.smarttodo.contract;

import android.content.Context;

import com.example.incredibly.smarttodo.model.Task;

import java.util.List;

public interface AddContract {

    interface View {
        void updateSpinner(List<String> categories, String category);
        void updateBackground();
        void updateTimeHint(Task task);
    }

    interface Presenter {
        void loadSpinner(Context context, String category);
    }

}
