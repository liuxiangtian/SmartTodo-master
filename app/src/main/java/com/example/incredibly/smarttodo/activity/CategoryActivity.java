package com.example.incredibly.smarttodo.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.adaptor.CategoryAdaptor;
import com.example.incredibly.smarttodo.dialog.AddCategoryFragment;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.provider.CategoryStore;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity implements CategoryAdaptor.OnItemClickListener {

    @Bind(R.id.activity_category)
    LinearLayout activityCategory;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
           private CategoryAdaptor categoryAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryAdaptor = new CategoryAdaptor(null);
        categoryAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(categoryAdaptor);

       Set<String> categories = CategoryStore.getInstance().loadNames(this);
        categoryAdaptor.replaceData(categories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AddCategoryFragment addCategoryFragment = AddCategoryFragment.newInstance();
            addCategoryFragment.show(getSupportFragmentManager(), "add_category");
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemClick(String item, int position, Pair<View, String> pair) {
        Intent intent = new Intent(this, MainActivity.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(String item, int position) {

    }

    @Override
    public void onItemSelected(String item, int position, boolean isChecked) {

    }

    public void addItem(String title) {
        new RepositoryImpl().insertCategorySafely(this, title, false, false);
        Set<String> categories = CategoryStore.getInstance().loadNames(this);
        categoryAdaptor.replaceData(categories);
    }
}
