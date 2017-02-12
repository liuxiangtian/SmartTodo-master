package com.example.incredibly.smarttodo.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.incredibly.smarttodo.App;
import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.contract.AddContract;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Category;
import com.example.incredibly.smarttodo.model.Notify;
import com.example.incredibly.smarttodo.model.Review;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.persenter.AddPresenter;
import com.example.incredibly.smarttodo.provider.NotifyStore;
import com.example.incredibly.smarttodo.provider.ReviewStore;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.util.Constant;
import com.example.incredibly.smarttodo.util.Util;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddTaskActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener, View.OnClickListener, AddContract.View {

    @Bind(R.id.activity_add_task)
    PercentRelativeLayout mMainLayout;
    @Bind(R.id.auto_category)
    Spinner mSpinner;
    @Bind(R.id.text_time_hint)
    TextView mTimeHintText;
    @Bind(R.id.linear_task_info)
    PercentFrameLayout mTaskInfoLayout;
    @Bind(R.id.linear_task_content)
    LinearLayout mContentLayout;
    @Bind(R.id.checkbox_important)
    Switch mImportantCheckbox;
    @Bind(R.id.checkbox_hard)
    Switch mHardCheckbox;
    @Bind(R.id.edit_content)
    EditText mContentEdit;
    @Bind(R.id.edit_comment)
    EditText mCommentEdit;
    private Toolbar mToolbar;

    private String mNavigation;
    private String mCategory;
    private Task mTask;
    private long mExecuteStartTime = -1;
    private long mExecuteEndTime = -1;

    private ArrayAdapter mArrayAdapter;
    private AddPresenter mAddPresenter;
    private Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        mNavigation = getIntent().getStringExtra(Constant.NAVIGATION);
        if (Constant.NAV_EDIT.equals(mNavigation)) {
            mTask = (Task) getIntent().getSerializableExtra(Constant.NAV_ADD_TASK);
            mCategory = mTask.getCategory();
            mExecuteStartTime = mTask.getExecuteStartTime();
            mExecuteEndTime = mTask.getExecuteEndTime();
        } else {
            mCategory = Category.CATEGORY_DEFAULT;
            mExecuteStartTime = getIntent().getLongExtra(Constant.NAVIGATION_START_DATE, Util.getTodayStartTime()) + 1;
            mExecuteEndTime = getIntent().getLongExtra(Constant.NAVIGATION_END_DATE, Util.getTodayEndTime()) - 1;
        }

        mImportantCheckbox.setOnCheckedChangeListener(this);
        mHardCheckbox.setOnCheckedChangeListener(this);
        mSpinner.setOnItemSelectedListener(this);
        mTimeHintText.setOnClickListener(this);

        mRepository = new RepositoryImpl();
        mAddPresenter = new AddPresenter(this, mRepository);
        mAddPresenter.loadSpinner(this, mCategory);
        prepareByNavigation(mNavigation);
    }

    private void prepareByNavigation(String navigation) {
        if (Constant.NAV_EDIT.equals(navigation)) {
            prepareTaskForEdit();
        } else {
            prepareTaskForAdd();
        }
        updateBackground();
    }

    private void prepareTaskForEdit() {
        mHardCheckbox.setChecked(mTask.isHard());
        mImportantCheckbox.setChecked(mTask.isImportant());
        if (!TextUtils.isEmpty(mTask.getComment())) {
            mCommentEdit.setVisibility(View.VISIBLE);
            mCommentEdit.setText(mTask.getComment());
        } else {
            mCommentEdit.setVisibility(View.GONE);
        }
        mContentEdit.setText(mTask.getContent());
    }

    private void prepareTaskForAdd() {
        mTask = new Task();
        mTimeHintText.setText("设置时间");
        mHardCheckbox.setChecked(false);
        mImportantCheckbox.setChecked(true);
        mCommentEdit.setVisibility(View.GONE);
    }

    @Override
    public void updateSpinner(List<String> categories, String category) {
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categories);
        mSpinner.setAdapter(mArrayAdapter);
        int position = mArrayAdapter.getPosition(category);
        mSpinner.setSelection(position);
    }

    @Override
    public void updateBackground() {
        boolean important = mImportantCheckbox.isChecked();
        boolean hard = mHardCheckbox.isChecked();
        boolean needColor = App.getPrefsApi().getTaskColorHint(true);
        if (needColor) {
            if (important && hard) {
                mSpinner.getPopupBackground().setColorFilter(Constant.COLOR_IMPORTANT_HARD_WEAK, PorterDuff.Mode.SRC);
                mToolbar.setBackgroundColor(Constant.COLOR_IMPORTANT_HARD_WEAK);
                mTaskInfoLayout.setBackgroundColor(Constant.COLOR_IMPORTANT_HARD);
                mTimeHintText.setBackgroundColor(Constant.COLOR_IMPORTANT_HARD);
                mContentLayout.setBackgroundColor(Constant.COLOR_IMPORTANT_HARD_WEAK);
            } else if (important && !hard) {
                mSpinner.getPopupBackground().setColorFilter(Constant.COLOR_IMPORTANT_EASY_WEAK, PorterDuff.Mode.SRC);
                mToolbar.setBackgroundColor(Constant.COLOR_IMPORTANT_EASY_WEAK);
                mTaskInfoLayout.setBackgroundColor(Constant.COLOR_IMPORTANT_EASY);
                mTimeHintText.setBackgroundColor(Constant.COLOR_IMPORTANT_EASY);
                mContentLayout.setBackgroundColor(Constant.COLOR_IMPORTANT_EASY_WEAK);
            } else if (!important && hard) {
                mSpinner.getPopupBackground().setColorFilter(Constant.COLOR_HARD_WEAK, PorterDuff.Mode.SRC);
                mToolbar.setBackgroundColor(Constant.COLOR_HARD_WEAK);
                mTaskInfoLayout.setBackgroundColor(Constant.COLOR_HARD);
                mTimeHintText.setBackgroundColor(Constant.COLOR_HARD);
                mContentLayout.setBackgroundColor(Constant.COLOR_HARD_WEAK);
            } else if (!important && !hard) {
                mSpinner.getPopupBackground().setColorFilter(Constant.COLOR_EASY_WEAK, PorterDuff.Mode.SRC);
                mToolbar.setBackgroundColor(Constant.COLOR_EASY_WEAK);
                mTaskInfoLayout.setBackgroundColor(Constant.COLOR_EASY);
                mTimeHintText.setBackgroundColor(Constant.COLOR_EASY);
                mContentLayout.setBackgroundColor(Constant.COLOR_EASY_WEAK);
            }
        } else {
            int toolbarColor = getResources().getColor(R.color.current_color_theme_primary);
            mSpinner.getPopupBackground().setColorFilter(toolbarColor, PorterDuff.Mode.SRC);
            mToolbar.setBackgroundColor(toolbarColor);
            mTaskInfoLayout.setBackgroundColor(Constant.COLOR_TASK_INIT);
            mTimeHintText.setBackgroundColor(Constant.COLOR_TASK_INIT);
            mContentLayout.setBackgroundColor(Constant.COLOR_TASK_INIT);
        }
    }

    @Override
    public void updateTimeHint(Task task) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_create) {
            createOrUpdateTask();
        } else if (id == R.id.action_collapse) {
            toggleTaskInfo();
        } else if (id == R.id.action_comment) {
            toggleTaskComment();
        }
        return true;
    }

    private void toggleTaskInfo() {
        Intent intent = new Intent(this, AddTaskSettingActivity.class);
        mTask.setImportant(mImportantCheckbox.isChecked());
        mTask.setHard(mHardCheckbox.isChecked());
        intent.putExtra(Constant.NAV_ADD_TASK, mTask);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            mTask = (Task) data.getSerializableExtra(Constant.NAV_ADD_TASK);
        }
    }

    private void toggleTaskComment() {
        if (mCommentEdit.getVisibility() == View.VISIBLE) {
            mCommentEdit.setVisibility(View.GONE);
        } else {
            mCommentEdit.setVisibility(View.VISIBLE);
        }
    }

    private void createOrUpdateTask() {
        String title = mContentEdit.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(AddTaskActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (Constant.NAV_EDIT.equals(mNavigation)) {
                updateTask();
            } else {
                addTask();
            }
            setResult(RESULT_OK);
            finish();
        }
    }

    private void addTask() {
        mTask.setCategory(mCategory);
        mTask.setHard(mHardCheckbox.isChecked());
        mTask.setImportant(mImportantCheckbox.isChecked());
        mTask.setComment(mCommentEdit.getText().toString());
        mTask.setContent(mContentEdit.getText().toString());
        mTask.setExecuteStartTime(mExecuteStartTime);
        mTask.setExecuteEndTime(mExecuteEndTime);

        int executeType = mTask.getExecuteTimeType();
        if(executeType==Task.TIME_TYPE_NONE){
            mTask.setExecuteTime(-1);
        } else if(executeType==Task.TIME_TYPE_DURATION){
            mTask.setExecuteTimeType(Task.TIME_TYPE_FIX);
            mTask.setExecuteTime(System.currentTimeMillis()+mTask.getExecuteTime());
        }
        TaskStore.getInstance().insert(this, mTask, false);
        int id = TaskStore.getInstance().getTaskId(this, mTask);
        if(id!=-1){
            mTask.setId(id);
            mRepository.pushTaskToRemote(this, mTask);
        }

        mRepository.deleteReviewById(this, mTask.getId());
        List<Notify> notifies = mTask.getNotifies();
        if (notifies != null) {
            for (Notify notify : notifies) {
                notify.setUpdateTime(System.currentTimeMillis());
                NotifyStore.getInstance().insert(this, notify, false);
            }
        }

        notifies = NotifyStore.getInstance().loadNotifiesById(this, mTask.getId());
        if (notifies != null) {
            for (Notify notify : notifies) {
                mRepository.pushNotifyToRemote(this, notify);
            }
        }

        mRepository.deleteReviewById(this, mTask.getId());
        List<Review> reviews = mTask.getReviews();
        if (reviews != null) {
            for (Review review : reviews) {
                review.setUpdateTime(System.currentTimeMillis());
                ReviewStore.getInstance().insert(this, review, false);
            }
        }

        reviews = ReviewStore.getInstance().loadReviewsById(this, mTask.getId());
        if (reviews != null) {
            for (Review review : reviews) {
                mRepository.pushReviewToRemote(this, review);
            }
        }
    }

    private void updateTask() {
        mTask.setCategory(mCategory);
        mTask.setUpdateTime(System.currentTimeMillis());
        mTask.setHard(mHardCheckbox.isChecked());
        mTask.setImportant(mImportantCheckbox.isChecked());
        mTask.setContent(mContentEdit.getText().toString());
        mTask.setComment(mCommentEdit.getText().toString());
        mTask.setExecuteStartTime(mExecuteStartTime);
        mTask.setExecuteEndTime(mExecuteEndTime);

        int executeType = mTask.getExecuteTimeType();
        if(executeType==Task.TIME_TYPE_NONE){
            mTask.setExecuteTime(-1);
        } else if(executeType==Task.TIME_TYPE_DURATION){
            mTask.setExecuteTimeType(Task.TIME_TYPE_FIX);
            mTask.setExecuteTime(System.currentTimeMillis()+mTask.getExecuteTime());
        }
        TaskStore.getInstance().update(this, mTask, false);
        mRepository.pushTaskToRemote(this, mTask);

        int id = mTask.getId();

        mRepository.deleteReviewById(this, mTask.getId());
        List<Notify> notifies = mTask.getNotifies();
        if (notifies != null) {
            for (Notify notify : notifies) {
                notify.setUpdateTime(System.currentTimeMillis());
                NotifyStore.getInstance().insert(this, notify, false);
            }
        }

        notifies = NotifyStore.getInstance().loadNotifiesById(this, mTask.getId());
        if (notifies != null) {
            for (Notify notify : notifies) {
                mRepository.pushNotifyToRemote(this, notify);
            }
        }

        mRepository.deleteReviewById(this, mTask.getId());
        List<Review> reviews = mTask.getReviews();
        if (reviews != null) {
            for (Review review : reviews) {
                review.setUpdateTime(System.currentTimeMillis());
                ReviewStore.getInstance().insert(this, review, false);
            }
        }

        reviews = ReviewStore.getInstance().loadReviewsById(this, mTask.getId());
        if (reviews != null) {
            for (Review review : reviews) {
                mRepository.pushReviewToRemote(this, review);
            }
        }
    }

    @Override
    public void onBackPressed() {
        createOrUpdateTask();
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateBackground();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mCategory = Constant.CATEGORY_DEFAULT;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.text_time_hint) {
            toggleTaskInfo();
        }
    }

}
