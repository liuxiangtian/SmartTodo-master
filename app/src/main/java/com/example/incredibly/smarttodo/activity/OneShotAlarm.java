package com.example.incredibly.smarttodo.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.model.Task;

public class OneShotAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Task task = (Task) intent.getSerializableExtra("TASK_NOTIFY");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        Intent newIntent = new Intent(context, TimingActivity.class);
        intent.putExtra("TASK_TIMING", task);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(task.getCategory());
        builder.setContentText(task.getContent());
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setTicker(task.getContent());
        builder.setOngoing(true);
        builder.setNumber(20);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0, notification);
    }

}
