package com.example.incredibly.smarttodo.util;

import org.lxt.xiang.library.GET;
import org.lxt.xiang.library.PUT;

import java.util.Set;

public interface PrefsApi {

    String KEY_FIRST_BOOT = "KEY_FIRST_BOOT";
    String KEY_SPLASH_ANIM = "KEY_SPLASH_ANIM";
    String KEY_TASK_COLOR_HINT = "KEY_TASK_COLOR_HINT";
    String KEY_REPEAT_LABEL = "KEY_REPEAT_LABEL";
    String KEY_TASK_CREATED = "KEY_TASK_CREATED";
    String KEY_TEST_TIME = "KEY_TEST_TIME";

    @GET(key = KEY_FIRST_BOOT)
    boolean getFirstBoot(boolean first);

    @PUT(key = KEY_FIRST_BOOT)
    void putFirstBoot(boolean first);

    @GET(key = KEY_SPLASH_ANIM)
    boolean getSplashAnim(boolean anim);

    @PUT(key = KEY_SPLASH_ANIM)
    void putSplashAnim(boolean anim);

    @GET(key = KEY_TASK_COLOR_HINT)
    boolean getTaskColorHint(boolean hint);

    @PUT(key = KEY_TASK_COLOR_HINT)
    void putTaskColorHint(boolean hint);

    @GET(key = KEY_REPEAT_LABEL)
    String getRepeatLabels(String label);

    @PUT(key = KEY_REPEAT_LABEL)
    void putRepeatLabels(String label);

    @GET(key = KEY_TASK_CREATED)
    long getTaskCreatedDate(long createdDate);

    @PUT(key = KEY_TASK_CREATED)
    void putTaskCreatedDate(long createdDate);

    @GET(key = KEY_TEST_TIME)
    int getTestTime(int testTime);
}
