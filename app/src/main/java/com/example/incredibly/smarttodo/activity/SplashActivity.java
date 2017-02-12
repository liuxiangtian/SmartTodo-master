package com.example.incredibly.smarttodo.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.util.NavUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.sharesdk.framework.ShareSDK;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static final long DURATION = 2100;

    @Bind(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        ShareSDK.initSDK(this);
        Bmob.initialize(this, "8d5ffee902f38d9ba70fd46791f2ba21");
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        AnimationSet animationSet = getSplashAnimationSet();
        textView.startAnimation(animationSet);
    }

    @NonNull
    private AnimationSet getSplashAnimationSet() {
        Animation alphaAnia = new AlphaAnimation(0.1f, 1f);
        alphaAnia.setDuration(DURATION);
        Animation transAnim = new TranslateAnimation(0, 0, 250f, 0);
        transAnim.setDuration(DURATION);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnia);
        animationSet.addAnimation(transAnim);
        animationSet.setAnimationListener(this);
        return animationSet;
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        NavUtil.launchMainActivity(this);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
