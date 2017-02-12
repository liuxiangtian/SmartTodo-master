package com.example.incredibly.smarttodo.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.BetterViewPager;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.fragment.StatisticItemFragment;
import com.example.incredibly.smarttodo.model.MyUser;
import com.example.incredibly.smarttodo.util.BitmapUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatisticActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    BetterViewPager viewPager;
    @Bind(R.id.header_image)
    CircleImageView headerImage;
    @Bind(R.id.header_nickname) TextView headerNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        viewPager.setAdapter(new StatisticAdaptor(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        if(myUser!=null){
            headerNickname.setText(myUser.getNickName());
            BitmapUtil.loadHeaderBitmap(headerImage, myUser.getHeaderImage());
        } else {
            headerNickname.setText("");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toolbar.setTitle(titles[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private String[] titles = {"今天", "本周", "本月"};

    public class StatisticAdaptor extends FragmentPagerAdapter {

        public StatisticAdaptor(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return StatisticItemFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
