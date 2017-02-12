package com.example.incredibly.smarttodo.contract;


import android.content.Context;
import android.view.Menu;

import com.example.incredibly.smarttodo.model.Task;

import java.util.List;

public interface MainContract {

    interface View {
        void toggleMenu(Menu menu, boolean isDelete);
        void showTitle();
        void showTasks(List<Task> tasks);
        void refresh(boolean show);
        void reload();
        void toggleDrawer(boolean close);
        void fixMenu();
        void toggleDelete(boolean isDelete);
        void changePosition(int position);
    }

    interface Presenter {
        void clickHomeMenu(boolean isDelete);
        void loadTasks(String navigation, String title, long start, long end, boolean fromNetwork);
        void deleteTasks(android.view.View view, List<Task> deleteTasks);
        void unDoneTask(final Context context, final Task task, final int position);
        void doneTask(final Context context, final Task task, final int position);
    }

}
