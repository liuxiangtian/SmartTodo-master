package com.example.incredibly.smarttodo;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.incredibly.smarttodo.activity.OneShotAlarm;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.provider.NotifyStore;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.util.PrefsApi;
import com.facebook.stetho.Stetho;

import org.joda.time.DateTime;
import org.lxt.xiang.library.Knife;

import java.util.List;

public class App extends Application {

    private static App INSTANCE;
    private static PrefsApi prefsApi;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        prefsApi = Knife.create(this, PrefsApi.class);
        Stetho.initializeWithDefaults(this);

        long firstCreateTime = App.getPrefsApi().getTaskCreatedDate(System.currentTimeMillis());
        if(firstCreateTime!=new DateTime(firstCreateTime).withTimeAtStartOfDay().getMillis()){

            List<Notify> notifies = NotifyStore.getInstance().loadNotifies(this);
            if (notifies != null && notifies.size() != 0) {
                for (Notify notify : notifies) {
                    Intent intent = new Intent(this, OneShotAlarm.class);
                    intent.putExtra("NOTIFY_TASK", notify);
                    PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, notify.getNotifyTime(), sender);
                }
            }

//            List<Review> reviews = ReviewStore.getInstance().loadReviews(this);
//            if (reviews != null && reviews.size() != 0) {
//                for (Review review : reviews) {
//
//                }
//            }

            int week = new DateTime().getDayOfWeek();
            List<Task> repeatTasks = TaskStore.getInstance().loadRepeatTasks(this);
            for (Task repeatTask : repeatTasks) {
                int repeat =repeatTask.getRepeat();
                Task newTask = null;
                if((week==0) && (repeat&0x40)!=0){
                    newTask = repeatTask.clone();
                } else if((week==1) && (repeat&0x20)!=0){
                    newTask = repeatTask.clone();
                } else if((week==2) && (repeat&0x10)!=0){
                    newTask = repeatTask.clone();
                } else if((week==3) && (repeat&0x08)!=0){
                    newTask = repeatTask.clone();
                } else if((week==4) && (repeat&0x04)!=0){
                    newTask = repeatTask.clone();
                } else if((week==5) && (repeat&0x02)!=0){
                    newTask = repeatTask.clone();
                } else if((week==6) && (repeat&0x01)!=0){
                    newTask = repeatTask.clone();
                }
                TaskStore.getInstance().insert(this, newTask, false);
                new RepositoryImpl().pushTaskToRemote(this, newTask);
            }

            App.getPrefsApi().putTaskCreatedDate(new DateTime(firstCreateTime).withTimeAtStartOfDay().getMillis());
        }

    }

    public static App getInstance() {
        return INSTANCE;
    }

    public static PrefsApi getPrefsApi() {
        return prefsApi;
    }

}
