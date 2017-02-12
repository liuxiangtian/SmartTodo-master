package com.example.incredibly.smarttodo.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.activity.AddTaskActivity;
import com.example.incredibly.smarttodo.activity.MainActivity;
import com.example.incredibly.smarttodo.activity.SearchActivity;
import com.example.incredibly.smarttodo.activity.TimingActivity;
import com.example.incredibly.smarttodo.adaptor.TaskAdaptor;
import com.example.incredibly.smarttodo.callback.NavigationObserver;
import com.example.incredibly.smarttodo.contract.MainContract;
import com.example.incredibly.smarttodo.dialog.MoveTaskFragment;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Task;
import com.example.incredibly.smarttodo.persenter.MainPresenter;
import com.example.incredibly.smarttodo.provider.TaskStore;
import com.example.incredibly.smarttodo.util.Constant;
import com.example.incredibly.smarttodo.util.NavUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        MainContract.View, NavigationObserver, RecyclerView.OnItemTouchListener {

    public static final String KEY_NAVIGATION = "KEY_NAVIGATION";
    public static final String KEY_NAVIGATION_TITLE = "KEY_NAVIGATION_TITLE";
    public static final String KEY_NAVIGATION_START_TIME = "KEY_NAVIGATION_START_TIME";
    public static final String KEY_NAVIGATION_END_TIME = "KEY_NAVIGATION_END_TIME";

    private static final int NAVIGATION_ADD = 1;
    private static final int NAVIGATION_TIMING = 3;

    private String mNavigation;
    private String mNavigationTitle;
    private long mNavigationStartTime;
    private long mNavigationEndTime;

    private ActionBar mActionBar;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.image_place_holder)
    LinearLayout mPlaceHolderLayout;
    @Bind(R.id.view_add_task)
    View mAddTaskView;

    private boolean mIsDeleteState;
    private MainPresenter mMainPresenter;
    private TaskAdaptor mTaskAdaptor;
    private Repository mRepository;

    public static MainFragment newInstance(String navigation, String title, long start, long end) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(KEY_NAVIGATION, navigation);
        args.putString(KEY_NAVIGATION_TITLE, title);
        args.putLong(KEY_NAVIGATION_START_TIME, start);
        args.putLong(KEY_NAVIGATION_END_TIME, end);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mNavigation = args.getString(KEY_NAVIGATION);
        mNavigationTitle = args.getString(KEY_NAVIGATION_TITLE);
        mNavigationStartTime = args.getLong(KEY_NAVIGATION_START_TIME);
        mNavigationEndTime = args.getLong(KEY_NAVIGATION_END_TIME);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(mToolbar);
        mActionBar = activity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mToolbar.setTitle(mNavigationTitle);

        mRepository = new RepositoryImpl();
        mMainPresenter = new MainPresenter(this, mRepository);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int color = getResources().getColor(R.color.color_theme_primary);
        mSwipeRefreshLayout.setColorSchemeColors(color);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAddTaskView.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mTaskAdaptor = new TaskAdaptor(null);
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.setAdapter(mTaskAdaptor);
        mMainPresenter.loadTasks(mNavigation, mNavigationTitle, mNavigationStartTime, mNavigationEndTime, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mMainPresenter = null;
    }

    @Override
    public void onRefresh() {
        if (mIsDeleteState) return;
        mMainPresenter.loadTasks(mNavigation, mNavigationTitle, mNavigationStartTime, mNavigationEndTime, true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view_add_task) {
            launchAddTaskActivity();
        }
    }

    private void launchAddTaskActivity() {
        Intent intent = new Intent(getContext(), AddTaskActivity.class);
        intent.putExtra(Constant.NAVIGATION, Constant.NAV_ADD);
        intent.putExtra(Constant.NAVIGATION_START_DATE, mNavigationStartTime);
        intent.putExtra(Constant.NAVIGATION_END_DATE, mNavigationEndTime);
        startActivityForResult(intent, NAVIGATION_ADD);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        toggleMenu(menu, mIsDeleteState);
    }

    @Override
    public void toggleMenu(Menu menu, boolean isDelete) {
        menu.findItem(R.id.action_add).setVisible(!isDelete);
        menu.findItem(R.id.action_search).setVisible(!isDelete);
        menu.findItem(R.id.action_archive).setVisible(isDelete);
        menu.findItem(R.id.action_delete).setVisible(isDelete);
    }

    @Override
    public void refresh(boolean show) {
        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void reload() {
        mMainPresenter.loadTasks(mNavigation, mNavigationTitle, mNavigationStartTime, mNavigationEndTime, false);
    }

    @Override
    public void toggleDrawer(boolean open) {
        if (open) {
            ((MainActivity) getActivity()).toggleDrawer(true);
        } else {
            ((MainActivity) getActivity()).toggleDrawer(false);
        }
    }

    @Override
    public void fixMenu() {
        ((MainActivity) getActivity()).fixSubMenu();
    }


    @Override
    public void toggleDelete(boolean isDelete) {
        this.mIsDeleteState = isDelete;
        showTitle();
        mActionBar.invalidateOptionsMenu();
        mTaskAdaptor.setDeleteState(mIsDeleteState);
    }

    @Override
    public void changePosition(int position) {
        mTaskAdaptor.notifyItemChanged(position);
    }

    @Override
    public void showTitle() {
        if (mIsDeleteState) {
            mToolbar.setTitle("选中" + mTaskAdaptor.getDeleteTasksCount() + "个");
            this.mActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        } else {
            if (TextUtils.isEmpty(mNavigationTitle)) {
                mToolbar.setTitle(getResources().getString(R.string.app_name));
            } else {
                mToolbar.setTitle(mNavigationTitle);
            }
            this.mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    @Override
    public void showTasks(List<Task> tasks) {
        if (tasks == null || tasks.size() == 0) {
            mAddTaskView.setVisibility(View.VISIBLE);
        } else {
            mAddTaskView.setVisibility(View.GONE);
        }
        mTaskAdaptor.replaceData(tasks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mMainPresenter.clickHomeMenu(mIsDeleteState);
        } else if (id == R.id.action_search) {
            NavUtil.launchActivity(getContext(), SearchActivity.class);
        } else if (id == R.id.action_archive) {
            MoveTaskFragment moveTaskFragment = MoveTaskFragment.newInstance();
            moveTaskFragment.show(getChildFragmentManager(), "MOVE_CATEGORY");
        } else if (id == R.id.action_delete) {
            mMainPresenter.deleteTasks(mRecyclerView, mTaskAdaptor.getDeleteTasks());
        } else if (id == R.id.action_add) {
            launchAddTaskActivity();
        }
        return true;
    }

    public void changeCategory(String category) {
        List<Task> tasks = mTaskAdaptor.getDeleteTasks();
        for (Task task : tasks) {
            task.setCategory(category);
            task.setUpdateTime(System.currentTimeMillis());
            TaskStore.getInstance().update(getContext(), task, false);
            mRepository.pushTaskToRemote(getContext(), task);
        }
        refresh(false);
        fixMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NAVIGATION_ADD && resultCode == RESULT_OK) {
            toggleDelete(false);
            mMainPresenter.loadTasks(mNavigation, mNavigationTitle, mNavigationStartTime, mNavigationEndTime, false);
            fixMenu();
        } else if (requestCode == NAVIGATION_TIMING && resultCode == RESULT_OK) {
            final int position = data.getIntExtra("TIMING_TASK_POSITION", 0);
            Task task = (Task) data.getSerializableExtra("TIMING_TASK");
            final Task oldTask = mTaskAdaptor.getTaskFromPosition(position);
            oldTask.setExecuteDuration(task.getExecuteDuration());
            oldTask.setCountDownTime(task.getCountDownTime());
            Observable.just(task).subscribeOn(Schedulers.io())
                    .map(new Func1<Task, Task>() {
                        @Override
                        public Task call(Task task) {
                            oldTask.setUpdateTime(System.currentTimeMillis());
                            TaskStore.getInstance().update(getContext(), oldTask, false);
                            mRepository.pushTaskToRemote(getContext(), oldTask);
                            return task;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Task>() {
                        @Override
                        public void call(Task task) {
                            mTaskAdaptor.notifyItemChanged(position);
                        }
                    });
        }
    }

    public void onItemClick(Task item, int position) {
        if (!mIsDeleteState) {
            TaskStore.getInstance().refresh(getContext(), item);
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            intent.putExtra(Constant.NAVIGATION, Constant.NAV_EDIT);
            intent.putExtra(Constant.NAV_ADD_TASK, item);
            startActivityForResult(intent, NAVIGATION_ADD);
        } else {
            mTaskAdaptor.toggleDeleteTask(position);
            showTitle();
        }
    }

    public void onLeftSwipe(final Task task, final int position) {
        if (task.isDone()) {
            mMainPresenter.unDoneTask(getContext(), task, position);
        } else {
            Intent intent = new Intent(getContext(), TimingActivity.class);
            intent.putExtra("TIMING_TASK", task);
            intent.putExtra("TIMING_TASK_POSITION", position);
            startActivityForResult(intent, NAVIGATION_TIMING);
        }
    }

    public void onRightSwipe(final Task task, final int position) {
        if (!task.isDone()) {
            mMainPresenter.doneTask(getContext(), task, position);
        }
    }

    public void onItemLongClick(Task item, int position) {
        mTaskAdaptor.addDeleteTask(position);
        toggleDelete(true);
    }

    @Override
    public void navigation(String navigation, String title, long start, long end) {
        this.mNavigation = navigation;
        this.mNavigationTitle = title;
        this.mNavigationStartTime = start;
        this.mNavigationEndTime = end;
        toggleDelete(false);
        mMainPresenter.loadTasks(navigation, title, start, end, false);
    }

    @Override
    public void backPress() {
        if (mIsDeleteState) {
            toggleDelete(false);
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.backPress();
        }
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

    GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            View childView = findChildViewUnder(e1.getX(), e1.getY());
            if (childView != null) {
                int position = mRecyclerView.getChildAdapterPosition(childView);
                if (velocityX < 0) {
                    onLeftSwipe(mTaskAdaptor.getTaskFromPosition(position), position);
                    return true;
                } else if (velocityX > 0) {
                    onRightSwipe(mTaskAdaptor.getTaskFromPosition(position), position);
                    return true;
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {

            super.onLongPress(e);
            View childView = findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
                int position = mRecyclerView.getChildAdapterPosition(childView);
                onItemLongClick(mTaskAdaptor.getTaskFromPosition(position), position);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
                int position = mRecyclerView.getChildAdapterPosition(childView);
                onItemClick(mTaskAdaptor.getTaskFromPosition(position), position);
                return true;
            }
            return super.onSingleTapUp(e);
        }
    });

    private View findChildViewUnder(float x, float y) {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mRecyclerView.getChildAt(i);
            Rect rect = new Rect();
            child.getHitRect(rect);
            if (rect.contains((int) x, (int) y)) {
                return child;
            }
        }
        return null;
    }

}
