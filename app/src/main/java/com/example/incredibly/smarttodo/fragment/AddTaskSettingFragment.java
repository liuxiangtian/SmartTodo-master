package com.example.incredibly.smarttodo.fragment;

import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.example.incredibly.smarttodo.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddTaskSettingFragment extends Fragment implements View.OnClickListener, RadialTimePickerDialogFragment.OnTimeSetListener {

    @Bind(R.id.text_time_settings)
    LinearLayout textTimeSettings;
    @Bind(R.id.calendarView)
    MaterialCalendarView calendarView;
    @Bind(R.id.linear_execute_time) LinearLayout linearExecuteTime;
    @Bind(R.id.linear_execute_notify) LinearLayout linearExecuteNotify;
    @Bind(R.id.text_execute_notify) TextView textExecuteNotify;
    @Bind(R.id.text_execute_repeat) TextView textExecuteRepeat;
    @Bind(R.id.text_execute_delay) TextView textExecuteDelay;
    @Bind(R.id.text_execute_review) TextView textExecuteReview;
    @Bind(R.id.text_execute_time)
    TextView textExecuteTime;
    @Bind(R.id.linear_execute_repeat) LinearLayout linearExecuteRepeat;
    @Bind(R.id.linear_execute_delay) LinearLayout linearExecuteDelay;
    @Bind(R.id.linear_execute_review) LinearLayout linearExecuteReview;

    public static AddTaskSettingFragment newInstance() {
        return new AddTaskSettingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task_setting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearExecuteTime.setOnClickListener(this);
        linearExecuteDelay.setOnClickListener(this);
        linearExecuteNotify.setOnClickListener(this);
        linearExecuteRepeat.setOnClickListener(this);
        linearExecuteReview.setOnClickListener(this);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.WEDNESDAY)
                .setMinimumDate(CalendarDay.from(2016, 4, 3))
                .setMaximumDate(CalendarDay.from(2016, 5, 12))
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.linear_execute_time) {
            RadialTimePickerDialogFragment fragment = new RadialTimePickerDialogFragment()
                    .setStartTime(10, 10)
                    .setDoneText("确定")
                    .setCancelText("取消")
                    .setOnTimeSetListener(this)
                    .setThemeDark();
            fragment.show(getChildFragmentManager(), "date");
        } else if (id == R.id.linear_execute_notify) {
            executNotify();
        } else if (id == R.id.linear_execute_repeat) {
            executRepeat();
        } else if (id == R.id.linear_execute_delay) {
            executDelay();
        } else if (id == R.id.linear_execute_review) {
            executReview();
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.min = minute;
        textExecuteTime.setText("执行时间 "+hour+":"+min);
    }

    private int hour, min;

    private void executNotify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("提前多少时间？");
        final String[] cities = {"5分钟", "15分钟", "30分钟", "1小时", "2小时", "4小时", "6小时", "1天", "1周", "1月"};
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                textExecuteNotify.setText("提前时间为 "+cities[which]);
            }
        });
        builder.show();
    }

    private void executRepeat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("推迟策略？");
        final String[] cities = {"5分钟", "15分钟", "30分钟", "1小时", "2小时", "4小时", "6小时", "1天", "1周", "1月"};
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                textExecuteRepeat.setText("重复策略为 "+cities[which]);
            }
        });
        builder.show();
    }


    private void executDelay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("推迟策略？");
        final String[] cities = {"5分钟后", "15分钟后", "30分钟后", "1小时后", "2小时后", "6小时后", "1天后", "1周后", "1月后"};
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                textExecuteDelay.setText("重复策略为 "+cities[which]);
            }
        });
        builder.show();
    }

    private void executReview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("复习策略？");
        final String[] cities = {"一次明天", "两次后天"};
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                textExecuteReview.setText("复习策略为 "+cities[which]);
            }
        });
        builder.show();
    }

}
