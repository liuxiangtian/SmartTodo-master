package com.example.incredibly.smarttodo.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.incredibly.smarttodo.activity.MainActivity;

public class RemoveCategoryFragment extends DialogFragment {

    private static final String KEY_CATEGORY = "KEY_CATEGORY";
    private static final String KEY_ITEM_ID = "KEY_ITEM_ID";
    private String category;
    private int itemId;

    public static RemoveCategoryFragment newInstance(String category, int itemId) {
        Bundle args = new Bundle();
        args.putString(KEY_CATEGORY, category);
        args.putInt(KEY_ITEM_ID, itemId);
        RemoveCategoryFragment fragment = new RemoveCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getString(KEY_CATEGORY);
        itemId = getArguments().getInt(KEY_ITEM_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("清空集合?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity mainActivity = (MainActivity) getActivity();

                    }
                });
        return builder.create();
    }

}
