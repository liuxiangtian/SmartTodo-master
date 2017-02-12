package com.example.incredibly.smarttodo.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    public static String TEXT_DONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TEXT_DONE = getResources().getString(R.string.intro_done_text);

        int backgroundColor = getResources().getColor(R.color.color_intro_background);
        int selectedIndicatorColor = getResources().getColor(R.color.color_selected_indicator_background);
        int unSelectedIndicatorColor = getResources().getColor(R.color.color_unselected_indicator_background);
        int introTextColor = getResources().getColor(R.color.color_intro_text);
        int introDesTextColor = getResources().getColor(R.color.color_intro_des_text);

        String titleTypeFaceIndex = "fonts/方正正粗黑简体.ttf";
        String desTypeFaceIndex = "fonts/方正硬笔行书简体.ttf";

        Typeface typeface = Typeface.create(titleTypeFaceIndex, Typeface.BOLD);
        Log.i("main", ": "+typeface.getStyle());

        addSlide(AppIntroFragment.newInstance("轻重缓急", titleTypeFaceIndex, "生命有限，分清轻重缓急乃第一要务", desTypeFaceIndex, R.drawable.slide_one, backgroundColor, introTextColor, introDesTextColor));
        addSlide(AppIntroFragment.newInstance("记录过去，规划未来", titleTypeFaceIndex, "过去与未来", desTypeFaceIndex, R.drawable.slide_one, backgroundColor, introTextColor, introDesTextColor));
        addSlide(AppIntroFragment.newInstance("时间执行", titleTypeFaceIndex, "番茄时间", desTypeFaceIndex, R.drawable.slide_one, backgroundColor, introTextColor, introDesTextColor));

        setIndicatorColor(selectedIndicatorColor, unSelectedIndicatorColor);
        setNavBarColor(R.color.color_nav_bar);
        setBarColor(getResources().getColor(R.color.color_intro_bar));
        setSeparatorColor(getResources().getColor(R.color.color_intro_background));
        setColorDoneText(getResources().getColor(R.color.color_text_bar));

        setDoneText(TEXT_DONE);

        showSkipButton(true);
        setProgressButtonEnabled(true);

        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getPrefsApi().putFirstBoot(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
