package com.example.incredibly.smarttodo.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.contract.TimingContract;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.persenter.TimingPresenter;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.util.Constant;
import com.example.incredibly.smarttodo.view.TimeView;
import com.example.incredibly.smarttodo.view.TimelyView;
import com.example.incredibly.smarttodo.view.model.NumberUtils;
import org.joda.time.Duration;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimingActivity extends AppCompatActivity implements View.OnClickListener, TimingContract.View {

    private static final int FADE_DURATION = 2000;
    private static final int DURATION = 600;

    @Bind(R.id.activity_time)
    PercentRelativeLayout mMainLayout;
    @Bind(R.id.text_task_type)
    TextView mTaskTypeText;
    @Bind(R.id.text_task_info)
    TextView mTaskInfoText;
    @Bind(R.id.image_task_timer)
    TimeView mTimeView;
    @Bind(R.id.timely_first)
    TimelyView mTimelyFirst;
    @Bind(R.id.timely_second)
    TimelyView mTimelySecond;
    @Bind(R.id.timely_three)
    TimelyView mTimelyThree;
    @Bind(R.id.timely_four)
    TimelyView mTimelyFour;
    @Bind(R.id.text_task_indicator)
    TextView mTaskIndicatorText;
    @Bind(R.id.text_task_title)
    TextView mTaskTitleText;
    @Bind(R.id.layout_select)
    LinearLayout mSelectLayout;
    @Bind(R.id.text_five)
    TextView mTextFive;
    @Bind(R.id.text_fiftieth)
    TextView mTextFiftieth;
    @Bind(R.id.text_thirth)
    TextView mTextThirth;
    @Bind(R.id.text_fourth_five)
    TextView mTextFourthFive;
    @Bind(R.id.layout_finish_result)
    PercentFrameLayout mFinishResultLayout;
    @Bind(R.id.text_finish_refresh)
    TextView mTextFinishRefresh;
    @Bind(R.id.text_finish_cancel)
    TextView mTextFinishCancel;
    @Bind(R.id.text_finish_complete)
    TextView mTextFinishComplete;

    private TimingPresenter mTimingPresenter;
    private Task task;
    private long countDownTime;
    private long executeDuration;
    private AlphaAnimation fadeLoopAnim;
    private boolean isTiming = false;
    private int firstDefault;
    private int secondDefault;
    private int thirdDefault;
    private int fourDefault;
    private int first;
    private int second;
    private int third;
    private int four;

    private Timer timer;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        task = (Task) getIntent().getSerializableExtra("TIMING_TASK");
        position = getIntent().getIntExtra("TIMING_TASK_POSITION", -1);

        ButterKnife.bind(this);
        mTimingPresenter = new TimingPresenter(this, new RepositoryImpl());

        countDownTime = task.getCountDownTime();
        executeDuration = task.getExecuteDuration();
        if (executeDuration <= 0) {
            executeDuration = 30 * 60 * 1000;
        }
        if (countDownTime <= 0) {
            countDownTime = 30 * 60 * 1000;
        }

        fadeLoopAnim = new AlphaAnimation(1f, 0.2f);
        fadeLoopAnim.setRepeatCount(-1);
        fadeLoopAnim.setRepeatMode(Animation.REVERSE);
        fadeLoopAnim.setDuration(FADE_DURATION);

        prepareView();
    }

    private void prepareView() {
        boolean important = task.isImportant();
        boolean hard = task.isHard();
        if (important && hard) {
            mMainLayout.setBackgroundColor(Constant.COLOR_IMPORTANT_HARD_WEAK);
        } else if (important && !hard) {
            mMainLayout.setBackgroundColor(Constant.COLOR_IMPORTANT_EASY_WEAK);
        } else if (!important && hard) {
            mMainLayout.setBackgroundColor(Constant.COLOR_HARD_WEAK);
        } else if (!important && !hard) {
            mMainLayout.setBackgroundColor(Constant.COLOR_EASY_WEAK);
        }

        mTaskInfoText.setText(task.getCategory());
        mTaskTitleText.setText(task.getContent());
        mTaskIndicatorText.setText("准备执行任务");
        mTimeView.setOnClickListener(this);
        mTextThirth.setFocusable(true);
        updateDefaultDigit();
    }

    @Override
    public void onClick(View v) {
        mTimingPresenter.toggleTiming(isTiming);
    }

    @Override
    public void onBackPressed() {
        task.setCountDownTime(countDownTime);
        task.setExecuteDuration(executeDuration);
        task.setUpdateTime(System.currentTimeMillis());
        mTimingPresenter.backAndSave(this, task, isTiming);
    }

    @Override
    public void startTiming() {
        isTiming = true;
        mSelectLayout.animate().alpha(0).setDuration(FADE_DURATION).start();
        mTaskTypeText.animate().alpha(0).setDuration(FADE_DURATION).start();
        mTaskInfoText.animate().alpha(0).setDuration(FADE_DURATION).start();
        mTaskTitleText.animate().alpha(0).setDuration(FADE_DURATION).start();
        mTaskIndicatorText.startAnimation(fadeLoopAnim);
        mTaskIndicatorText.setText("执行中...");

        timer = new Timer(countDownTime, 500);
        timer.start();
    }

    @Override
    public void stopTiming() {
        if (timer != null) {
            mTaskIndicatorText.setText("暂停中...");
            mSelectLayout.animate().alpha(1).setDuration(FADE_DURATION).start();
            mTaskTypeText.animate().alpha(1).setDuration(FADE_DURATION).start();
            mTaskInfoText.animate().alpha(1).setDuration(FADE_DURATION).start();
            mTaskTitleText.animate().alpha(1).setDuration(FADE_DURATION).start();
            timer.cancel();
            isTiming = false;
        }
    }

    @OnClick(R.id.text_five)
    public void onFiveClick(View v) {
        if (isTiming) return;
        updateDigit(5);
    }

    @OnClick(R.id.text_fiftieth)
    public void onFiftiethClick(View v) {
        if (isTiming) return;
        updateDigit(15);
    }

    @OnClick(R.id.text_thirth)
    public void onThirtClick(View v) {
        if (isTiming) return;
        updateDigit(30);
    }

    @OnClick(R.id.text_fourth_five)
    public void onFourthFiveClick(View v) {
        if (isTiming) return;
        updateDigit(45);
    }

    @OnClick(R.id.text_finish_refresh)
    public void onFinishRefresh() {
            countDownTime = executeDuration;
            mFinishResultLayout.setVisibility(View.GONE);
            mFinishResultLayout.animate().alpha(0).setDuration(FADE_DURATION).start();
            mTimingPresenter.toggleTiming(isTiming);
    }

    @OnClick(R.id.text_finish_complete)
    public void onFinishComplete() {
        task.setUpdateTime(System.currentTimeMillis());
        mTimingPresenter.finishComplete(this, task);
    }


    @Override
    public void finishComplete() {
        mFinishResultLayout.setVisibility(View.GONE);
        selectLayoutFadeOut();
        backToMainActivity();
    }

    @OnClick(R.id.text_finish_cancel)
    public void onFinishCancel() {
            countDownTime = executeDuration;
            updateDefaultDigit();
            mFinishResultLayout.setVisibility(View.GONE);
            selectLayoutFadeOut();
    }

    private void selectLayoutFadeOut() {
        mSelectLayout.animate().alpha(1).setDuration(FADE_DURATION).start();
        mTaskTypeText.animate().alpha(1).setDuration(FADE_DURATION).start();
        mTaskInfoText.animate().alpha(1).setDuration(FADE_DURATION).start();
        mTaskTitleText.animate().alpha(1).setDuration(FADE_DURATION).start();
        mTaskIndicatorText.animate().alpha(1).setDuration(FADE_DURATION).start();
        mTaskIndicatorText.setText("任务执行完成");
    }

    @Override
    public void updateDefaultDigit() {
        Duration duration = new Duration(countDownTime);
        long minutes = duration.getStandardMinutes();
        long seconds = duration.getStandardSeconds();
        firstDefault = (int) (minutes % 60 / 10);
        secondDefault = (int) (minutes % 60 % 10);
        thirdDefault = (int) (seconds % 60 / 10);
        fourDefault = (int) (seconds % 60 % 10);
        mTimelyFirst.setControlPoints(NumberUtils.getControlPointsFor(firstDefault));
        mTimelySecond.setControlPoints(NumberUtils.getControlPointsFor(secondDefault));
        mTimelyThree.setControlPoints(NumberUtils.getControlPointsFor(thirdDefault));
        mTimelyFour.setControlPoints(NumberUtils.getControlPointsFor(fourDefault));
        mTimeView.setAngle(countDownTime * 1f / executeDuration);
    }

    @Override
    public void updateDigit(int minite) {
        countDownTime = minite * 60 * 1000;
        executeDuration = countDownTime;
        updateDefaultDigit();
    }

    @Override
    public void backToMainActivity() {
        Intent intent = new Intent();
        intent.putExtra("TIMING_TASK", task);
        intent.putExtra("TIMING_TASK_POSITION", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void backToHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public class Timer extends CountDownTimer {

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            countDownTime = millisUntilFinished;
            Duration duration = new Duration(countDownTime);
            long minutes = duration.getStandardMinutes();
            long seconds = duration.getStandardSeconds();
            first = (int) (minutes % 60 / 10);
            second = (int) (minutes % 60 % 10);
            third = (int) (seconds % 60 / 10);
            four = (int) (seconds % 60 % 10);
            mTimeView.setAngle(millisUntilFinished * 1f / executeDuration);
            mTimelyFirst.animate(firstDefault, first).setDuration(DURATION).start();
            mTimelySecond.animate(secondDefault, second).setDuration(DURATION).start();
            mTimelyThree.animate(thirdDefault, third).setDuration(DURATION).start();
            mTimelyFour.animate(fourDefault, four).setDuration(DURATION).start();
            firstDefault = first;
            secondDefault = second;
            thirdDefault = third;
            fourDefault = four;
        }

        @Override
        public void onFinish() {
            countDownTime = 0;
            mTimeView.setAngle(0);
            mTimelyFirst.animate(firstDefault, 0).setDuration(DURATION).start();
            mTimelySecond.animate(secondDefault, 0).setDuration(DURATION).start();
            mTimelyThree.animate(thirdDefault, 0).setDuration(DURATION).start();
            mTimelyFour.animate(fourDefault, 0).setDuration(DURATION).start();
            firstDefault = secondDefault = thirdDefault = fourDefault = 0;
            updateDefaultDigit();

            if (mFinishResultLayout.getVisibility() == View.GONE) {
                mFinishResultLayout.setVisibility(View.VISIBLE);
                mFinishResultLayout.animate().alpha(1).setDuration(FADE_DURATION).start();
            }

            task.setCountDownTime(countDownTime);
            task.setExecuteDuration(executeDuration);
            isTiming = false;
            timer = null;
            vibrator();
        }

    }

    private void vibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{2000, 1000, 2000, 1000, 2000}, -1);
        }
    }

}
