package com.example.incredibly.smarttodo.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.activity.CategoryActivity;
import com.example.incredibly.smarttodo.activity.MainActivity;

import java.lang.ref.WeakReference;

public class AddCategoryFragment extends DialogFragment {


    public static AddCategoryFragment newInstance() {
        return new AddCategoryFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout mainLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.add_category, null);
        String createText = getResources().getString(R.string.create_category);
        final EditText editText = (EditText) mainLayout.findViewById(R.id.edit_add_category);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.dialog_add_category))
                .setView(mainLayout)
                .setPositiveButton(createText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = editText.getText().toString();
                        if (TextUtils.isEmpty(title)) {
                            Toast.makeText(getContext(), "输入为空", Toast.LENGTH_SHORT).show();
                        } else {
                            CategoryActivity categoryActivity = (CategoryActivity) getActivity();
                            if (categoryActivity != null) {
                                categoryActivity.addItem(title);
                            }
                        }
                    }
                });
        return builder.create();
    }

}
