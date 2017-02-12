package com.example.incredibly.smarttodo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.contract.AddTaskSettingsContract;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.persenter.AddTaskSettingsPresenter;
import com.example.incredibly.smarttodo.provider.NotifyStore;
import com.example.incredibly.smarttodo.util.Constant;
import com.example.incredibly.smarttodo.util.Util;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddTaskSettingActivity extends AppCompatActivity implements View.OnClickListener, RadialTimePickerDialogFragment.OnTimeSetListener, OnDateSelectedListener,
        OnMonthChangedListener, AddTaskSettingsContract.View {

    @Bind(R.id.text_time_settings)
    LinearLayout textTimeSettings;
    @Bind(R.id.calendarView)
    MaterialCalendarView calendarView;
    @Bind(R.id.linear_execute_time)
    LinearLayout linearExecuteTime;
    @Bind(R.id.linear_execute_notify)
    LinearLayout linearExecuteNotify;
    @Bind(R.id.text_execute_notify)
    TextView textExecuteNotify;
    @Bind(R.id.text_execute_repeat)
    TextView textExecuteRepeat;
    @Bind(R.id.text_execute_review)
    TextView textExecuteReview;
    @Bind(R.id.text_execute_time)
    TextView textExecuteTime;
    @Bind(R.id.linear_execute_repeat)
    LinearLayout linearExecuteRepeat;
    @Bind(R.id.linear_execute_review)
    LinearLayout linearExecuteReview;
    @Bind(R.id.image_execute_time)
    ImageView imageExecuteTime;
    @Bind(R.id.image_execute_notify)
    ImageView imageExecuteNotify;
    @Bind(R.id.image_execute_review)
    ImageView imageExecuteReview;
    @Bind(R.id.image_execute_repeat)
    ImageView imageExecuteRepeat;

    final String[] executeLabels = {"5分钟后", "15分钟后", "30分钟后", "1小时后", "2小时后", "1天后", "2天后", "1周后", "自定义提醒时间"};

    final String[] repeatLabels = {"全选", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    final boolean[] repeats = {true, false, false, false, false, false, false, false};

    final String[] notifyLabels = {"准时", "提前5分钟", "提前15分钟", "提前30分钟", "提前1小时", "提前1天", "自定义提醒时间"};
    final boolean[] notifyExites = {true, false, false, false, false, false, false};
    final List<Notify> mNotifies = new ArrayList<>();

    final String[] reviewLabels = {"明天复习", "后天复习", "周末总结", "下周总结", " 下月总结", "艾宾浩斯复习法"};

    private int year, month, day;
    private long executeTime = -1;
    private int repeatTime = -1;
    private Task task;
    private Toolbar toolbar;
    private int executeTimeType;
    private List<Notify> notifyTimes;
    private List<Review> reviews;
    private int repeat;
    private AddTaskSettingsContract.Presenter mPresenter;
    int tempRepeat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_setting);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        initViews();
        mPresenter = new AddTaskSettingsPresenter(this, new RepositoryImpl());
    }

    private void initViews() {
        linearExecuteTime.setOnClickListener(this);
        linearExecuteNotify.setOnClickListener(this);
        linearExecuteRepeat.setOnClickListener(this);
        linearExecuteReview.setOnClickListener(this);
        imageExecuteTime.setOnClickListener(this);
        imageExecuteRepeat.setOnClickListener(this);
        imageExecuteReview.setOnClickListener(this);
        imageExecuteNotify.setOnClickListener(this);
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);

        mNotifies.add(new Notify(Notify.TIME_TYPE_AT_TIME, task.getId(), false, 0));
        Notify notify = new Notify(Notify.TIME_TYPE_MINITE, task.getId(), false, 0);
        notify.setCount(5);
        mNotifies.add(notify);
        notify = new Notify(Notify.TIME_TYPE_MINITE, task.getId(), false, 0);
        notify.setCount(15);
        mNotifies.add(notify);
        notify = new Notify(Notify.TIME_TYPE_MINITE, task.getId(), false, 0);
        notify.setCount(30);
        mNotifies.add(notify);
        notify = new Notify(Notify.TIME_TYPE_HOUR, task.getId(), false, 0);
        notify.setCount(1);
        mNotifies.add(notify);
        notify = new Notify(Notify.TIME_TYPE_DAY, task.getId(), false, 0);
        notify.setCount(1);
        mNotifies.add(notify);

        task = (Task) getIntent().getSerializableExtra(Constant.NAV_ADD_TASK);
        DateTime dateTime = null;
        if (executeTimeType == Task.TIME_TYPE_FIX) {
            executeTime = task.getExecuteTime();
            dateTime = new DateTime(executeTime);
        } else {
            dateTime = new DateTime();
        }

        this.year = dateTime.getYear();
        this.month = dateTime.getMonthOfYear();
        this.day = dateTime.getDayOfMonth();
        calendarView.setTopbarVisible(false);
        calendarView.setDateTextAppearance(android.R.style.TextAppearance_StatusBar_Title);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        calendarView.state().edit().setMinimumDate(CalendarDay.from(2016, 11, 1))
                .setMaximumDate(CalendarDay.from(2017, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calendarView.setDateSelected(dateTime.toDate(), true);

        toolbar.setTitle(Util.longToToolbarTitle(dateTime));

        updateBackground();

        this.executeTimeType = task.getExecuteTimeType();
        this.executeTime = task.getExecuteTime();
        if (executeTimeType == Task.TIME_TYPE_NONE) {
            textExecuteTime.setText(getResources().getString(R.string.setting_execute_time));
            textExecuteNotify.setText("设置提醒");
            imageExecuteTime.setVisibility(View.GONE);
        } else if (executeTimeType == Task.TIME_TYPE_FIX) {
            textExecuteTime.setText(Util.longToTimeString(executeTime));
            imageExecuteTime.setVisibility(View.VISIBLE);
        } else if (executeTimeType == Task.TIME_TYPE_DURATION) {
            textExecuteTime.setText(Util.longToDuration(executeTime, "之后"));
            imageExecuteTime.setVisibility(View.VISIBLE);
        }

        this.notifyTimes = NotifyStore.getInstance().loadNotifiesById(this, task.getId());
        if (notifyTimes == null || notifyTimes.size() == 0) {
            textExecuteNotify.setText("设置提醒");
            imageExecuteNotify.setVisibility(View.GONE);
        } else {
            StringBuilder builder = new StringBuilder();
            for (Notify notifyItem : notifyTimes) {
                String label = Util.getNotifyStringByType(notifyItem);
                if (!TextUtils.isEmpty(label)) {
                    builder.append(label).append(";");
                }
            }
            textExecuteNotify.setText(builder.toString());
            imageExecuteNotify.setVisibility(View.VISIBLE);
        }

        showExecuteRepeat(task.getRepeat());
        mPresenter.updateExecuteReview(this, task.getId());
    }

    @Override
    public void showExecuteTime(int executeTimeType, long executeTime) {
        notifyTimes.clear();
        if (executeTimeType == Task.TIME_TYPE_NONE) {
            textExecuteTime.setText(getResources().getString(R.string.setting_execute_time));
            textExecuteNotify.setText("设置提醒");
            imageExecuteNotify.setVisibility(View.GONE);
            imageExecuteTime.setVisibility(View.GONE);
            Arrays.fill(notifyExites, false);
            notifyTimes.clear();
        } else {
            imageExecuteTime.setVisibility(View.VISIBLE);
            if (executeTimeType == Task.TIME_TYPE_FIX) {
                textExecuteTime.setText(Util.longToTimeString(executeTime));
            } else if (executeTimeType == Task.TIME_TYPE_DURATION) {
                textExecuteTime.setText(Util.longToDuration(executeTime, "之后"));
            }
            imageExecuteNotify.setVisibility(View.VISIBLE);
            Arrays.fill(notifyExites, false);
            if(this.executeTimeType== Task.TIME_TYPE_NONE){
                textExecuteNotify.setText(notifyLabels[0]);
                imageExecuteNotify.setVisibility(View.VISIBLE);
                notifyExites[0] = true;
                if(!notifyTimes.contains(mNotifies.get(0))){
                    notifyTimes.add(mNotifies.get(0));
                }
            }
        }
        this.executeTimeType = executeTimeType;
        this.executeTime = executeTime;
    }

    @Override
    public void showExecuteNotify(final List<Notify> notifys) {
        this.notifyTimes = notifys;
        if (notifys == null || notifys.size() == 0) {
            textExecuteNotify.setText("设置提醒");
            imageExecuteNotify.setVisibility(View.GONE);
        } else {
            StringBuilder builder = new StringBuilder();
            for (Notify notify : notifys) {
                String label = Util.getNotifyStringByType(notify);
                if (!TextUtils.isEmpty(label)) {
                    builder.append(label).append(";");
                }
            }
            textExecuteNotify.setText(builder.toString());
            imageExecuteNotify.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showExecuteReview(List<Review> reviews) {
        this.reviews = reviews;
        if (reviews == null || reviews.size() == 0) {
            textExecuteReview.setText("设置回顾");
            imageExecuteReview.setVisibility(View.GONE);
        } else {
            textExecuteReview.setText(Util.getReviewStringByType(reviews.get(0)));
            imageExecuteReview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showExecuteRepeat(int repeat) {
        this.repeat = repeat;
        StringBuilder builder = new StringBuilder();
        if (repeat == 0) {
            imageExecuteRepeat.setVisibility(View.GONE);
            Arrays.fill(repeats, false);
            builder.append("设置重复");
        } else if (repeat == 0x7f) {
            Arrays.fill(repeats, true);
            imageExecuteRepeat.setVisibility(View.VISIBLE);
            builder.append("全选");
        } else if (repeat == 0x7c) {
            Arrays.fill(repeats, true);
            repeats[0] = false;
            repeats[6] = false;
            repeats[7] = false;
            imageExecuteRepeat.setVisibility(View.VISIBLE);
            builder.append("周一~周五");
        } else if (repeat == 0x7e) {
            Arrays.fill(repeats, true);
            repeats[0] = false;
            repeats[7] = false;
            imageExecuteRepeat.setVisibility(View.VISIBLE);
            builder.append("周一~周六");
        } else {
            Arrays.fill(repeats, false);
            int distance = Util.hammingWeight(repeat);
            if ((repeat & 0x40) != 0) {
                repeats[1] = true;
                builder.append("周一,");
            }
            if ((repeat & 0x20) != 0) {
                repeats[2] = true;
                builder.append("周二,");
            }
            if ((repeat & 0x10) != 0) {
                repeats[3] = true;
                builder.append("周三,");
            }
            if ((repeat & 0x08) != 0) {
                repeats[4] = true;
                builder.append("周四,");
            }
            if ((repeat & 0x04) != 0) {
                repeats[5] = true;
                builder.append("周五,");
            }
            if ((repeat & 0x02) != 0) {
                repeats[6] = true;
                builder.append("周六,");
            }
            if ((repeat & 0x01) != 0) {
                repeats[7] = true;
                builder.append("周日,");
            }
            if ((builder.charAt(builder.length() - 1)) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(";");
            imageExecuteRepeat.setVisibility(View.VISIBLE);
        }
        textExecuteRepeat.setText(builder.toString());
    }

    @Override
    public void updateBackground() {
        boolean important = task.isImportant();
        boolean hard = task.isHard();
        boolean needColor = App.getPrefsApi().getTaskColorHint(true);
        int backColor = getResources().getColor(R.color.current_color_theme_primary);
        if (needColor) {
            if (important && hard) {
                backColor = Constant.COLOR_IMPORTANT_HARD_WEAK;
            } else if (important && !hard) {
                backColor = Constant.COLOR_IMPORTANT_EASY_WEAK;
            } else if (!important && hard) {
                backColor = Constant.COLOR_HARD_WEAK;
            } else if (!important && !hard) {
                backColor = Constant.COLOR_EASY_WEAK;
            }
        } else {
            backColor = getResources().getColor(R.color.current_color_theme_primary);
        }
        toolbar.setBackgroundColor(backColor);
        textTimeSettings.setBackgroundColor(backColor);
    }

    @Override
    public void clearSettings() {
        showExecuteTime(Task.TIME_TYPE_NONE, -1L);
        showExecuteNotify(new ArrayList<Notify>());
        showExecuteRepeat(0);
        showExecuteReview(new ArrayList<Review>());
    }

    @Override
    public void updateTask() {
        task.setExecuteTimeType(executeTimeType);
        task.setExecuteTime(executeTime);
        task.setNotifies(notifyTimes);
        task.setRepeat(repeat);
        task.setReviews(reviews);

        Intent intent = getIntent();
        intent.putExtra(Constant.NAV_ADD_TASK, task);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.linear_execute_time) {
            executeTime();
        } else if (id == R.id.linear_execute_notify) {
            executeNotify();
        } else if (id == R.id.linear_execute_repeat) {
            executeRepeat();
        } else if (id == R.id.linear_execute_review) {
            executeReview();
        } else if (id == R.id.image_execute_time) {
            showExecuteTime(Task.TIME_TYPE_NONE, -1L);
        } else if (id == R.id.image_execute_notify) {
            showExecuteNotify(new ArrayList<Notify>());
        } else if (id == R.id.image_execute_repeat) {
            showExecuteRepeat(0);
        } else if (id == R.id.image_execute_review) {
            showExecuteReview(new ArrayList<Review>());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_task_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_clear_settings) {
            clearSettings();
        }
        if (id == R.id.action_create) {
            updateTask();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        updateTask();
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        dialog.dismiss();
        DateTime dateTime = new DateTime(year, month, day, hourOfDay, minute);
        if (dateTime.isBeforeNow()) {
            Toast.makeText(this, "设置错误!", Toast.LENGTH_SHORT).show();
            return;
        }
        showExecuteTime(Task.TIME_TYPE_FIX, dateTime.getMillis());
    }

    private void executeTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        builder.setTitle("执行时间");
        builder.setSingleChoiceItems(executeLabels, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DateTime now = new DateTime();
                if (which == 0) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusMinutes(5).getMillis());
                } else if (which == 1) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusMinutes(15).getMillis());
                } else if (which == 2) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusMinutes(30).getMillis());
                } else if (which == 3) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusMinutes(60).getMillis());
                } else if (which == 4) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusMinutes(120).getMillis());
                } else if (which == 5) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusDays(1).getMillis());
                } else if (which == 6) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusDays(2).getMillis());
                } else if (which == 7) {
                    showExecuteTime(Task.TIME_TYPE_DURATION, now.getMillis() - now.minusWeeks(1).getMillis());
                } else if (which == 8) {
                    RadialTimePickerDialogFragment fragment = new RadialTimePickerDialogFragment()
                            .setStartTime(now.getHourOfDay(), now.getMinuteOfHour())
                            .setDoneText("确定")
                            .setCancelText("取消")
                            .setOnTimeSetListener(AddTaskSettingActivity.this)
                            .setThemeLight();
                    fragment.show(getSupportFragmentManager(), "date");
                }
            }
        });
        builder.show();
    }

    private RadialTimePickerDialogFragment.OnTimeSetListener onNotifyTimeSetListener = new RadialTimePickerDialogFragment.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
            calendarView.setEnabled(false);
            DateTime then = new DateTime(year, month, day, hourOfDay, minute);
            if(then.isBeforeNow()){
                Toast.makeText(AddTaskSettingActivity.this, "设置过于提前", Toast.LENGTH_SHORT).show();
                return;
            }
            Notify notify = new Notify(Notify.TIME_TYPE_FIX, task.getId(), false, then.getMillis());
            notifyTimes.add(notify);
            dialog.dismiss();
        }
    };

    private void executeNotify() {
        if (Task.TIME_TYPE_NONE == executeTimeType) {
            Toast.makeText(this, "先要设置执行时间", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        builder.setTitle("提醒");
        builder.setPositiveButton("创建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Notify notify = null;
                if(notifyExites[0]){
                    if(!notifyTimes.contains(mNotifies.get(0))){
                        notifyTimes.add(mNotifies.get(0));
                    }
                } else {
                    if(notifyTimes.contains(mNotifies.get(0))){
                        notifyTimes.remove(mNotifies.get(0));
                    }
                }

                if(notifyExites[1]){
                    if(!notifyTimes.contains(mNotifies.get(1))){
                        notifyTimes.add(mNotifies.get(1));
                    }
                } else {
                    if(notifyTimes.contains(mNotifies.get(1))){
                        notifyTimes.remove(mNotifies.get(1));
                    }
                }

                if(notifyExites[2]){
                    if(!notifyTimes.contains(mNotifies.get(2))){
                        notifyTimes.add(mNotifies.get(2));
                    }
                } else {
                    if(notifyTimes.contains(mNotifies.get(2))){
                        notifyTimes.remove(mNotifies.get(2));
                    }
                }

                if(notifyExites[3]){
                    if(!notifyTimes.contains(mNotifies.get(3))){
                        notifyTimes.add(mNotifies.get(3));
                    }
                } else {
                    if(notifyTimes.contains(mNotifies.get(3))){
                        notifyTimes.remove(mNotifies.get(3));
                    }
                }

                if(notifyExites[4]){
                    if(!notifyTimes.contains(mNotifies.get(4))){
                        notifyTimes.add(mNotifies.get(4));
                    }
                } else {
                    if(notifyTimes.contains(mNotifies.get(4))){
                        notifyTimes.remove(mNotifies.get(4));
                    }
                }

                if(notifyExites[5]){
                    if(!notifyTimes.contains(mNotifies.get(5))){
                        notifyTimes.add(mNotifies.get(5));
                    }
                } else {
                    if(notifyTimes.contains(mNotifies.get(5))){
                        notifyTimes.remove(mNotifies.get(5));
                    }
                }
                showExecuteNotify(notifyTimes);
            }
        });
        builder.setMultiChoiceItems(notifyLabels, notifyExites, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                Notify notify = null;
                DateTime now = new DateTime();
                if (which == 0) {
                    notifyExites[0] = isChecked;
                }
                if (which == 1) {
                    notifyExites[1] = isChecked;
                }
                if (which == 2) {
                    notifyExites[2] = isChecked;
                }
                if (which == 3) {
                    notifyExites[3] = isChecked;
                }
                if (which == 4) {
                    notifyExites[4] = isChecked;
                }
                if (which == 5) {
                    notifyExites[5] = isChecked;
                }
                if (which == 6) {
                    calendarView.setEnabled(false);
                    RadialTimePickerDialogFragment fragment = new RadialTimePickerDialogFragment()
                            .setStartTime(now.getHourOfDay(), now.getMinuteOfHour())
                            .setDoneText("确定")
                            .setCancelText("取消")
                            .setOnTimeSetListener(onNotifyTimeSetListener)
                            .setThemeLight();
                    fragment.show(getSupportFragmentManager(), "date");
                }
            }
        });
        builder.show();
    }

    private void executeRepeat() {
        tempRepeat = repeat;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        builder.setTitle("重复");
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                repeat = tempRepeat;
                Log.i("main", "onClick: "+repeat);
                showExecuteRepeat(repeat);
            }
        });
        builder.setMultiChoiceItems(repeatLabels, repeats, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (which == 0) {
                    tempRepeat = isChecked ? 0x7f : 0x0;
                } else if (which == 1) {
                    if(isChecked){
                        tempRepeat |= 0x40;
                    } else {
                        tempRepeat &= 0x3f;
                    }
                } else if (which == 2) {
                    if(isChecked){
                        tempRepeat |= 0x20;
                    } else {
                        tempRepeat &= 0x5f;
                    }
                } else if (which == 3) {
                    if(isChecked){
                        tempRepeat |= 0x10;
                    } else {
                        tempRepeat &= 0x6f;
                    }
                } else if (which == 4) {
                    if(isChecked){
                        tempRepeat |= 0x08;
                    } else {
                        tempRepeat &= 0x77;
                    }
                } else if (which == 5) {
                    if(isChecked){
                        tempRepeat |= 0x04;
                    } else {
                        tempRepeat &= 0x7b;
                    }
                } else if (which == 6) {
                    if(isChecked){
                        tempRepeat |= 0x02;
                    } else {
                        tempRepeat &= 0x7d;
                    }
                } else if (which == 7) {
                    if(isChecked){
                        tempRepeat |= 0x01;
                    } else {
                        tempRepeat &= 0x7e;
                    }
                }
            }
        });
        builder.show();
    }

    private void executeReview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        builder.setTitle("回顾策略？");
        final List<Review> reviews = new ArrayList<>();

        builder.setSingleChoiceItems(reviewLabels, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DateTime now = new DateTime();
                Review review = null;
                reviews.clear();
                if (which == 0) {
                    review = new Review();
                    review.setTaskId(task.getId());
                    review.setTimeType(Review.TIME_TYPE_DAY);
                    review.setCount(1);
                    review.setIndex(1);
                    reviews.add(review);
                } else if (which == 1) {
                    review = new Review();
                    review.setTaskId(task.getId());
                    review.setTimeType(Review.TIME_TYPE_DAY);
                    review.setCount(2);
                    review.setIndex(1);
                    reviews.add(review);
                } else if (which == 2) {
                    review = new Review();
                    review.setTaskId(task.getId());
                    review.setTimeType(Review.TIME_TYPE_WEEKEND);
                    review.setIndex(1);
                    reviews.add(review);
                } else if (which == 3) {
                    review = new Review();
                    review.setTaskId(task.getId());
                    review.setTimeType(Review.TIME_TYPE_WEEK);
                    review.setCount(1);
                    review.setIndex(1);
                    reviews.add(review);
                } else if (which == 4) {
                    review = new Review();
                    review.setTaskId(task.getId());
                    review.setTimeType(Review.TIME_TYPE_MONTH);
                    review.setCount(1);
                    review.setIndex(1);
                    reviews.add(review);
                } else if (which == 5) {
                    for (int i = 0; i < 4; i++) {
                        review = new Review();
                        review.setTaskId(task.getId());
                        review.setTimeType(Review.TIME_TYPE_MULTI);
                        review.setCount(i + 1);
                        review.setIndex(i + 1);
                        reviews.add(review);
                    }
                }
                showExecuteReview(reviews);
            }
        });
        builder.show();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        DateTime dateTime = new DateTime(date.getDate().getTime());
        year = dateTime.getYear();
        month = dateTime.getMonthOfYear();
        day = dateTime.getDayOfMonth();
        toolbar.setTitle(Util.longToToolbarTitle(dateTime));
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        toolbar.setTitle(Util.longToTimeShortString(date.getDate().getTime()));
    }

}
