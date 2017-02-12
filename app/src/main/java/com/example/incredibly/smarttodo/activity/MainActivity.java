package com.example.incredibly.smarttodo.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.example.incredibly.smarttodo.callback.NavigationObserver;
import com.example.incredibly.smarttodo.contract.Contract;
import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.fragment.MainFragment;
import com.example.incredibly.smarttodo.loader.Repository;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.MyUser;
import com.example.incredibly.smarttodo.persenter.Presenter;
import com.example.incredibly.smarttodo.util.BitmapUtil;
import com.example.incredibly.smarttodo.util.Constant;
import com.example.incredibly.smarttodo.util.NavUtil;
import com.example.incredibly.smarttodo.util.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, Contract.View, CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final int REQUEST_LOGIN_IN = 1;
    private static final int REQUEST_SETTINGS = 2;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    private List<NavigationObserver> mObservers = new ArrayList<>();
    private CircleImageView mHeaderImg;
    private TextView mNickNameTxt;
    private CircleImageView mSettingsBtn;
    private CircleImageView mStatisticBtn;
    private CircleImageView mSyncBtn;
    private Button mLoginInBtn;
    private Contract.Presenter mPresenter;
    private Repository mRepository;

    private Set<String> mCategorySet = new HashSet<>();
    private Map<String, Integer> mCategoryMap = new HashMap<>();
    private String mNavigation = Constant.NAV_TODAY;
    private String mNavigationTitle = Constant.NAV_TITLE_TODAY;
    private long mNavigationStartTime = Util.getTodayStartTime();
    private long mNavigationEndTime = Util.getTodayEndTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRepository = new RepositoryImpl();
        mPresenter = new Presenter(this, mRepository);

        initViews();
        initNavigation();
        updateHeader();
        prepareFragment();
        mPresenter.prepareNavMenus(this);
    }

    private void initViews() {
        View headerView = mNavigationView.getHeaderView(0);
        mHeaderImg = (CircleImageView) headerView.findViewById(R.id.header_image);
        mSettingsBtn = (CircleImageView) headerView.findViewById(R.id.header_settings);
        mStatisticBtn = (CircleImageView) headerView.findViewById(R.id.header_statistic);
        mSyncBtn = (CircleImageView) headerView.findViewById(R.id.header_synchronize);
        mLoginInBtn = (Button) headerView.findViewById(R.id.header_login_in);
        mNickNameTxt = (TextView) headerView.findViewById(R.id.header_nickname);
        mSettingsBtn.setOnClickListener(this);
        mStatisticBtn.setOnClickListener(this);
        mSyncBtn.setOnClickListener(this);
        mHeaderImg.setOnClickListener(this);
        mLoginInBtn.setOnClickListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initNavigation() {
        Intent intent = getIntent();
        if(intent!=null) {
            mNavigation = intent.getStringExtra(Constant.NAVIGATION);
            if(!TextUtils.isEmpty(mNavigation)){
                mNavigationTitle = intent.getStringExtra(Constant.NAVIGATION_TITLE);
                mNavigationStartTime = intent.getLongExtra(Constant.NAVIGATION_START_DATE, Util.getTodayStartTime());
                mNavigationEndTime = intent.getLongExtra(Constant.NAVIGATION_END_DATE, Util.getTodayEndTime());
            } else {
                mNavigation = Constant.NAV_TODAY;
                mNavigationTitle = Constant.NAV_TITLE_TODAY;
                mNavigationStartTime = Util.getTodayStartTime();
                mNavigationEndTime = Util.getTodayEndTime();
            }
            mNavigationTitle = intent.getStringExtra(Constant.NAVIGATION_TITLE);
            mNavigationStartTime = intent.getLongExtra(Constant.NAVIGATION_START_DATE, Util.getTodayStartTime());
            mNavigationEndTime = intent.getLongExtra(Constant.NAVIGATION_END_DATE, Util.getTodayEndTime());
        }
    }

    @Override
    public void updateHeader() {
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if(myUser !=null){
            String nickName = myUser.getNickName();
            String userImage = myUser.getHeaderImage();
            mLoginInBtn.setVisibility(View.GONE);
            mNickNameTxt.setVisibility(View.VISIBLE);
            mNickNameTxt.setText(nickName);
            BitmapUtil.loadHeaderBitmap(mHeaderImg, userImage);
        } else {
            mNickNameTxt.setVisibility(View.GONE);
            mLoginInBtn.setVisibility(View.VISIBLE);
            mHeaderImg.setImageResource(R.drawable.avator_placeholder);
        }
    }

    public void prepareFragment() {
        MainFragment mainFragment = MainFragment.newInstance(mNavigation, mNavigationTitle, mNavigationStartTime, mNavigationEndTime);
        mObservers.add(mainFragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mainFragment).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_settings:
                NavUtil.launchSettingsActivity(this, REQUEST_SETTINGS);
                break;
            case R.id.header_statistic:
                NavUtil.launchActivity(this, StatisticActivity.class);
                break;
            case R.id.header_synchronize:
                mRepository.synchronize(this);
                break;
            case R.id.header_login_in:
                NavUtil.launchLoginActivity(this, REQUEST_LOGIN_IN);
                break;
            case R.id.header_image:
                NavUtil.launchLoginActivity(this, REQUEST_LOGIN_IN);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        for (NavigationObserver navigationObserver : mObservers) {
            navigationObserver.backPress();
        }
    }

    public void backPress() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter = null;
        mObservers = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        int groupId = item.getGroupId();
        final String category = item.getTitle().toString();

        toggleDrawer(false);
        switch (id) {
            case R.id.action_today:
                navigation(Constant.NAV_TODAY, "今天", Util.getTodayStartTime(), Util.getTodayEndTime());
                return true;
            case R.id.action_week:
                navigation(Constant.NAV_WEEKDAY, "本周", Util.getWeekStartTime(), Util.getWeekEndTime());
                return true;
            case R.id.action_month:
                navigation(Constant.NAV_MONTH, "本月", Util.getMonthStartTime(), Util.getMonthEndTime());
                return true;
            case R.id.action_category:
                NavUtil.launchActivity(this, CategoryActivity.class);
                return true;
            case R.id.action_some_day:
                DateTime today = new DateTime();
                Date start = today.minusMonths(6).toDate();
                Date end = today.plusYears(1).toDate();
                CalendarDatePickerDialogFragment datePickerDialogFragment = new CalendarDatePickerDialogFragment()
                        .setDateRange(new MonthAdapter.CalendarDay(start.getTime()), new MonthAdapter.CalendarDay(end.getTime()))
                        .setPreselectedDate(today.getYear(), today.getMonthOfYear() - 1, today.getDayOfMonth());
                datePickerDialogFragment.show(getSupportFragmentManager(), "TIME_SELECT");
                datePickerDialogFragment.setOnDateSetListener(this);
                return true;
            default:
                break;
        }

        if(groupId==R.id.group_1) {
            if(mCategorySet.contains(category)){
                navigation(Constant.NAV_CATEGORY, category, Util.getTodayStartTime(), Util.getTodayEndTime());
            }
        }
        return true;
    }

    private void navigation(String navigation, String title, long startTime, long endTime) {
        this.mNavigation = navigation;
        this.mNavigationTitle = title;
        this.mNavigationStartTime = startTime;
        this.mNavigationEndTime = endTime;
        for (NavigationObserver navigationObserver : mObservers) {
            navigationObserver.navigation(navigation, title, startTime, endTime);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_IN && resultCode == RESULT_OK) {
            updateHeader();
        } else if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK) {
            updateHeader();
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 1);
        navigation(Constant.NAV_SOMEDAY, Util.longToToolbarTitle(dateTime), dateTime.withTimeAtStartOfDay().getMillis(), dateTime.plusDays(1).withTimeAtStartOfDay().getMillis());
        dialog.dismiss();
    }

    @Override
    public void toggleDrawer(boolean toOpen) {
        if (toOpen) {
            if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        } else {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    }

    @Override
    public void addSubMenu(String category) {
        if(mCategorySet.contains(category)){
            return;
        }
        mCategorySet.add(category);
        mCategoryMap.put(category, category.hashCode());
//        mRepository.insertCategorySafely(this, category, false, false);
        mNavigationView.getMenu().add(R.id.group_1, category.hashCode(), Menu.NONE, category).setIcon(R.drawable.ic_category_black_24dp);
    }

    @Override
    public void clearMenus() {
        for (Map.Entry<String, Integer> entry : mCategoryMap.entrySet()) {
            mNavigationView.getMenu().removeItem(entry.getValue());
            mCategorySet.remove(entry.getKey());
        }
        mCategoryMap.clear();
    }

    @Override
    public void fixSubMenu() {
        clearMenus();
        mPresenter.prepareNavMenus(this);
    }

}
