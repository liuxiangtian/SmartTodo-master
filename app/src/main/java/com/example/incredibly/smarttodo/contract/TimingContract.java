package com.example.incredibly.smarttodo.contract;

import android.content.Context;

import com.example.incredibly.smarttodo.model.Task;

public interface TimingContract {

    interface View {
        void updateDefaultDigit();
        void updateDigit(int minite);
        void backToMainActivity();
        void backToHome();
        void startTiming();
        void stopTiming();
        void finishComplete();
    }

    interface Presenter {
        void toggleTiming(boolean isTiming);
        void backAndSave(Context context, Task task, boolean isTiming);
        void finishComplete(Context context, Task task);
    }

}
