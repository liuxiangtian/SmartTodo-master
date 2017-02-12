package com.example.incredibly.smarttodo.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.adaptor.CategoryAdaptor;
import com.example.incredibly.smarttodo.fragment.MainFragment;
import com.example.incredibly.smarttodo.loader.RepositoryImpl;
import com.example.incredibly.smarttodo.model.Category;
import com.example.incredibly.smarttodo.provider.CategoryStore;

import java.util.Set;

public class MoveTaskFragment extends DialogFragment implements View.OnClickListener, CategoryAdaptor.OnItemClickListener {

    private ImageView moveTaskAdd;
    private RecyclerView recyclerView;
    private CategoryAdaptor adaptor;
    private View main;

    public static MoveTaskFragment newInstance() {
        return new MoveTaskFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        main = LayoutInflater.from(getContext()).inflate(R.layout.move_category, null);
        moveTaskAdd = (ImageView) main.findViewById(R.id.move_task_add);
        moveTaskAdd.setOnClickListener(this);
        recyclerView = (RecyclerView) main.findViewById(R.id.move_task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Set<String> categories = CategoryStore.getInstance().loadNames(getContext());
        adaptor = new CategoryAdaptor(categories);
        recyclerView.setAdapter(adaptor);
        adaptor.setOnItemClickListener(this);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(main).create();
        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        final EditText editText = new EditText(getContext());
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("添加新集合")
                .setView(editText)
                .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = editText.getText().toString();
                        if(TextUtils.isEmpty(title)){
                            Toast.makeText(getContext(), "输入为空", Toast.LENGTH_SHORT).show();
                        }
                        new RepositoryImpl().insertCategorySafely(getContext(), title, false, false);
                        Category category = CategoryStore.getInstance().loadCategory(getContext(), title);
                        new RepositoryImpl().pushCategoryToRemote(getContext(), category);
                        refresh();
                    }
                })
                .create();
        float destiny = getResources().getDisplayMetrics().density;
        editText.setPadding((int)(6*destiny), (int)(6*destiny),(int)(6*destiny),(int)(6*destiny));
        alertDialog.setView(editText,(int)(24*destiny),(int)(16*destiny),(int)(24*destiny),0);
        alertDialog.show();
    }

    public void refresh(){
        Set<String> categories = CategoryStore.getInstance().loadNames(getContext());
        adaptor.replaceData(categories);
    }


    @Override
    public void onItemClick(String item, int position, Pair<View, String> pair) {

    }

    @Override
    public void onItemLongClick(String item, int position) {

    }

    @Override
    public void onItemSelected(String item, int position, boolean isChecked) {
        MainFragment mainFragment = (MainFragment) getParentFragment();
        if (mainFragment != null) {
            mainFragment.changeCategory(item);
        }
        dismiss();
    }
}
