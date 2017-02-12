package com.example.incredibly.smarttodo.util;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.R;

public class Constant {

    public static final String CATEGORY_DEFAULT = "默认";


    public static final String NAVIGATION = "NAVIGATION";
    public static final String NAVIGATION_TITLE = "NAVIGATION_TITLE";
    public static final String NAVIGATION_START_DATE = "NAVIGATION_START_DATE";
    public static final String NAVIGATION_END_DATE = "NAVIGATION_END_DATE";

    public static final String NAV_CATEGORY = "NAV_CATEGORY";
    public static final String NAV_TODAY = "NAV_TODAY";
    public static final String NAV_WEEKDAY = "NAV_WEEKDAY";
    public static final String NAV_MONTH = "NAV_MONTH";
    public static final String NAV_SOMEDAY = "NAV_SOMEDAY";

    public static final String TYPE_DONE_LABEL = "已完成";
    public static final String TYPE_IMPORTANT_EASY_LABEL = "重要 & 容易";
    public static final String TYPE_IMPORTANT_HARD_LABEL = "重要 & 困难";
    public static final String TYPE_EASY_LABEL = "非重要 & 容易";
    public static final String TYPE_HARD_LABEL = "非重要 & 困难";
    public static final String NAV_TITLE_TODAY = "今天";
    public static final String NAV_EDIT = "NAV_EDIT";
    public static final String NAV_ADD = "NAV_ADD";
    public static final String NAV_ADD_TASK = "NAV_ADD_TASK";
    public static int COLOR_TASK_DONE;
    public static int COLOR_TASK_INIT;
    public static int COLOR_IMPORTANT_HARD;
    public static int COLOR_HARD;
    public static int COLOR_IMPORTANT_EASY;
    public static int COLOR_EASY;
    public static int COLOR_IMPORTANT_HARD_WEAK;
    public static int COLOR_HARD_WEAK;
    public static int COLOR_IMPORTANT_EASY_WEAK;
    public static int COLOR_EASY_WEAK;

    static {
        COLOR_IMPORTANT_HARD = App.getInstance().getResources().getColor(R.color.color_important_hard);
        COLOR_IMPORTANT_EASY = App.getInstance().getResources().getColor(R.color.color_important_easy);
        COLOR_HARD = App.getInstance().getResources().getColor(R.color.color_hard);
        COLOR_EASY = App.getInstance().getResources().getColor(R.color.color_easy);
        COLOR_IMPORTANT_HARD_WEAK = App.getInstance().getResources().getColor(R.color.color_important_hard_week);
        COLOR_IMPORTANT_EASY_WEAK = App.getInstance().getResources().getColor(R.color.color_important_easy_week);
        COLOR_HARD_WEAK = App.getInstance().getResources().getColor(R.color.color_hard_week);
        COLOR_EASY_WEAK = App.getInstance().getResources().getColor(R.color.color_easy_week);
        COLOR_TASK_INIT = App.getInstance().getResources().getColor(R.color.color_task_init);
        COLOR_TASK_DONE = App.getInstance().getResources().getColor(R.color.color_task_done);
    }
}
