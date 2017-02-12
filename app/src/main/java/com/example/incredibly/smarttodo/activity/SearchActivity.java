package com.example.incredibly.smarttodo.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.adaptor.TaskAdaptor;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.util.Constant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements View.OnKeyListener, RecyclerView.OnItemTouchListener {


    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.edit_search)
    EditText searchEdit;
    @Bind(R.id.image_place_holder)
    FrameLayout imagePlaceHolder;
    @Bind(R.id.image_toy)
    View imageToy;

    private TaskAdaptor taskAdaptor;
    private static final int NAVIGATION_TIMING = 3;
    private ViewPropertyAnimator vpa;
    private Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        mRepository = new RepositoryImpl();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdaptor = new TaskAdaptor(null);
        recyclerView.setAdapter(taskAdaptor);
        searchEdit.setOnKeyListener(this);
        recyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetector(this, simpleOnGestureListener);
        vpa = getAnimator();
        vpa.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == 1 || requestCode == 4)) {
            refresh();
        } else if (requestCode == NAVIGATION_TIMING && resultCode == RESULT_OK) {
            int position = data.getIntExtra("TIMING_TASK_POSITION", 0);
            taskAdaptor.notifyItemChanged(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            refresh();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }

    public void refresh() {
        final String query = searchEdit.getText().toString();
        if (TextUtils.isEmpty(query)) return;
        List<Task> tasks = TaskStore.getInstance().loadTasksByContent(SearchActivity.this, query);
        if (tasks == null || tasks.size() == 0) {
            taskAdaptor.replaceData(null);
            imagePlaceHolder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            vpa = getAnimator();
            vpa.start();
        } else {
            taskAdaptor.replaceData(tasks);
            recyclerView.setVisibility(View.VISIBLE);
            imagePlaceHolder.setVisibility(View.GONE);
            vpa.cancel();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
    }

    private ViewPropertyAnimator getAnimator() {
        vpa = imageToy.animate().rotation(360).setDuration(10000);
        return vpa;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (mGestureDetector.onTouchEvent(e)) {
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private View findChildViewUnder(float x, float y) {
        int count = recyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = recyclerView.getChildAt(i);
            Rect rect = new Rect();
            child.getHitRect(rect);
            if (rect.contains((int) x, (int) y)) {
                return child;
            }
        }
        return null;
    }

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            View childView = findChildViewUnder(e1.getX(), e1.getY());
            if (childView != null) {
                int position = recyclerView.getChildAdapterPosition(childView);
                Task task = taskAdaptor.getTaskFromPosition(position);
                if (velocityX != 0) {
                    Intent intent = new Intent(SearchActivity.this, TimingActivity.class);
                    intent.putExtra("TIMING_TASK", task);
                    intent.putExtra("TIMING_TASK_POSITION", position);
                    startActivityForResult(intent, NAVIGATION_TIMING);
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {

            super.onLongPress(e);
            View childView = findChildViewUnder(e.getX(), e.getY());
            if (childView == null) {
                return;
            }
            int position = recyclerView.getChildAdapterPosition(childView);
            final Task task = taskAdaptor.getTaskFromPosition(position);
            if (task == null) {
                return;
            }
            Snackbar.make(recyclerView, "真的要删除任务?", Snackbar.LENGTH_LONG)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            task.setUpdateTime(System.currentTimeMillis());
                            TaskStore.getInstance().delete(recyclerView.getContext(), task, false);
                            mRepository.deleteRemoteTask(recyclerView.getContext(), task);
                        }
                    })
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            refresh();
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            super.onShown(snackbar);
                        }
                    })
                    .show();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
                int position = recyclerView.getChildAdapterPosition(childView);
                Task task = taskAdaptor.getTaskFromPosition(position);
                Intent intent = new Intent(SearchActivity.this, AddTaskActivity.class);
                intent.putExtra(Constant.NAVIGATION, Constant.NAV_EDIT);
                intent.putExtra(Constant.NAV_ADD_TASK, task);
                startActivityForResult(intent, 1);
                return true;
            }
            return super.onSingleTapUp(e);
        }
    };
    private GestureDetector mGestureDetector;

}
