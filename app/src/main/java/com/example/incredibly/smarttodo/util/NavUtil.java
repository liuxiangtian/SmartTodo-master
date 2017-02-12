package com.example.incredibly.smarttodo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.activity.LogInActivity;
import com.example.incredibly.smarttodo.activity.MainActivity;
import com.example.incredibly.smarttodo.activity.SettingsActivity;

public class NavUtil {

    public static void launchActivity(Context context, Class<? extends Activity> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }


    public static void launchMainActivity(Activity activity) {
        boolean isFirstBoot = App.getPrefsApi().getFirstBoot(true);
        Intent intent = new Intent(activity, MainActivity.class);
//        if(isFirstBoot){
//            intent = new Intent(activity, IntroActivity.class);
//        } else {
//                intent = new Intent(activity, MainActivity.class);
//        }
        activity.startActivity(intent);
    }

    public static Intent launchEmail() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"liuxiangtian@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "关于秒记的建议 : by "+Build.SERIAL);
        intent.putExtra(Intent.EXTRA_TEXT, getPhoneInfo());
        intent.setType("message/rfc822");
        return Intent.createChooser(intent, "邮件反馈");
    }

    public static String getPhoneInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n\n\n").append("-------------\n")
                .append("BOARD:").append(Build.BOARD).append("\n")
                .append("DEVICE:").append(Build.DEVICE).append("\n")
                .append("User:").append(Build.USER).append("\n")
                .append("系统版本:").append(Build.VERSION.RELEASE).append("\n");
        return builder.toString();
    }


    public static void launchLoginActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchSettingsActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

}
