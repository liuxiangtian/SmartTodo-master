package com.example.incredibly.smarttodo.util;

import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Task;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.DateFormatSymbols;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {

    public static void sortTasksByCreateTime(List<Task> tasks) {
        if (tasks == null && tasks.size()==0) {
            return;
        }
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                if (task1.getCreateTime() == task2.getCreateTime()) {
                    return 0;
                } else if (task1.getCreateTime() < task2.getCreateTime()) {
                    return -1;
                } else if (task1.getCreateTime() > task2.getCreateTime()) {
                    return 1;
                }
                return 0;
            }
        });
    }


    public static void sortTasksByDoneTime(List<Task> tasks) {
        if (tasks == null && tasks.size()==0) {
            return;
        }
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                if (task1.getDoneTime() == task2.getDoneTime()) {
                    return 0;
                } else if (task1.getDoneTime() < task2.getDoneTime()) {
                    return -1;
                } else if (task1.getDoneTime() > task2.getDoneTime()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public static String longToTimeString(long created) {
        if (created == -1) {
            return "未知";
        }
        return new DateTime(created).toString("MM月dd日 HH:mm");
    }

    public static String longToTimeShortString(long created) {
        DateTime dateTime = new DateTime(created);
        int month = dateTime.getMonthOfYear();
        if(month<10){
            return dateTime.toString("YY年M月");
        } else {
            return dateTime.toString("YY年MM月");
        }
    }

    public static String longToToolbarTitle(DateTime dateTime) {
        DateTime today = new DateTime();
        if((today.getYear()==dateTime.getYear())&& (today.getMonthOfYear()==dateTime.getMonthOfYear())&& (today.getDayOfMonth()==dateTime.getDayOfMonth())){
            return  "今天";
        }
        if((today.getYear()==dateTime.getYear())&& (today.getMonthOfYear()==dateTime.getMonthOfYear())&& (today.getDayOfMonth()==(dateTime.getDayOfMonth())-1)){
            return  "明天";
        }
        if((today.getYear()==dateTime.getYear())&& (today.getMonthOfYear()==dateTime.getMonthOfYear())&& (today.getDayOfMonth()==(dateTime.getDayOfMonth())+1)){
            return  "昨天";
        }
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();
        StringBuilder builder = new StringBuilder();
        if(month<10){
            builder.append(month+"月");
        } else {
            builder.append("MM月");
        }
        if(day<10){
            builder.append("d日");
        } else {
            builder.append("d日");
        }
        return dateTime.toString(builder.toString());
    }

    public static String longToTimeMinite(long created) {
        return new DateTime(created).toString("HH:mm;");
    }

    public static String [] LABELS = new String[]{"周一","周二","周三","周四","周五","周六","周日"};

    static {
        String[] temp =  DateFormatSymbols.getInstance().getShortWeekdays();
        for (int i = 2; i < temp.length; i++) {
            LABELS[i-2] = temp[i];
        }
        LABELS[6] = temp[1];
    }

    public static String getLabelByIndex(int index) {
        if(index<0 || index>=7){
            return "星期";
        }
        return LABELS[index];
    }

    public static long getTodayStartTime() {
        return new DateTime().withTimeAtStartOfDay().toDate().getTime();
    }

    public static long getTodayEndTime() {
        return new DateTime().plusDays(1).withTimeAtStartOfDay().toDate().getTime();
    }

    public static long getWeekStartTime() {
        return new DateTime().dayOfWeek().withMinimumValue().withTimeAtStartOfDay().toDate().getTime();
    }

    public static long getWeekEndTime() {
        return new DateTime().dayOfWeek().withMinimumValue().plusDays(7).withTimeAtStartOfDay().toDate().getTime();
    }

    public static long getMonthStartTime() {
        return new DateTime().dayOfMonth().withMinimumValue().withTimeAtStartOfDay().toDate().getTime();
    }

    public static long getMonthEndTime() {
        return new DateTime().dayOfMonth().withMaximumValue().plusDays(1).withTimeAtStartOfDay().toDate().getTime();
    }

    public static String longToDuration(long duration, String suffix) {
        StringBuilder builder = new StringBuilder();
        Duration d = new Duration(duration);
        long days = d.getStandardDays();
        long weeks = days/7;
        long hours = d.getStandardHours();
        long minutes = d.getStandardMinutes();
        if(weeks>0){
            builder.append(weeks).append("周").append(suffix);
        } else if(days>0){
            builder.append(days).append("天").append(suffix);
        } else if(hours>0){
            builder.append(hours).append("小时").append(suffix);
        } else if(minutes>0){
            builder.append(minutes).append("分钟").append(suffix);
        }
        return builder.toString();
    }


    public static String longToNotifyDuration(long duration) {
        StringBuilder builder = new StringBuilder();
        Duration d = new Duration(duration);
        long days = d.getStandardDays();
        long weeks = days/7;
        long hours = d.getStandardHours();
        long minutes = d.getStandardMinutes();
        if(weeks>0){
            builder.append("提前").append(weeks).append("周");
        } else if(days>0){
            builder.append("提前").append(days).append("天");
        } else if(hours>0){
            builder.append("提前").append(hours).append("小时");
        } else if(minutes>0){
            builder.append("提前").append(minutes).append("分钟");
        }
        return builder.toString();
    }

    public static String getNotifyStringByType(Notify time) {
        if(time==null) return null;
        int type = time.getTimeType();
        long notifyTime = time.getNotifyTime();
        int count = time.getCount();
        if(type==Notify.TIME_TYPE_AT_TIME){
            return "准时;";
        } else if(type== Notify.TIME_TYPE_DAY) {
            return "提前"+count+"天;";
        } else if(type== Notify.TIME_TYPE_WEEK) {
            return "提前"+count+"周;";
        } else if(type== Notify.TIME_TYPE_MONTH) {
            return "提前"+count+"月;";
        } else if(type== Notify.TIME_TYPE_HOUR) {
            return "提前"+count+"小时;";
        } else if(type== Notify.TIME_TYPE_MINITE) {
            return "提前"+count+"分钟";
        } else if(type== Notify.TIME_TYPE_FIX){
            return longToTimeMinite(notifyTime);
        }
        return null;
    }

//    final String[] reviewLabels = {"明天复习", "后天复习", "周末总结", "下周总结", " 下月总结", "艾宾浩斯复习法"};

    public static String getReviewStringByType(Review review) {
        if(review==null) return null;
        int type = review.getTimeType();
        int count = review.getCount();
        if(type==Review.TIME_TYPE_DAY){
            return count+"天之后";
        } else if(type== Review.TIME_TYPE_MONTH) {
            if(count==1){
                return "下月总结";
            } else {
                return count+"月之后";
            }
        } else if(type== Review.TIME_TYPE_MULTI) {
            return "艾宾浩斯复习法";
        } else if(type== Review.TIME_TYPE_WEEKEND) {
            return "周末总结";
        } else if(type== Review.TIME_TYPE_WEEK){
            if(count==1){
                return "下周总结";
            } else {
                return count+"周之后";
            }
        }
        return null;
    }

    public static int hammingWeight(int n) {
        int counter = 0;
        while (n != 0)
        {
            if (n % 2 != 0) {
                counter++;
            }
            n = n >> 1;
        }
        return counter;
    }

}
