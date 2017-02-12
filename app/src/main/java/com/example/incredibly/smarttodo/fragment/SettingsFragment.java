package com.example.incredibly.smarttodo.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.incredibly.smarttodo.R;
import com.example.incredibly.smarttodo.model.MyUser;
import com.example.incredibly.smarttodo.util.NavUtil;

import java.util.List;

public class SettingsFragment  extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private Preference feedbackPrefs;
    private Preference logoutPrefs;
    private Preference themeColorPrefs;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_settings);

        feedbackPrefs = findPreference("prefs_feed_back");
        feedbackPrefs.setIntent(NavUtil.launchEmail());

        logoutPrefs = findPreference("prefs_logout");
        logoutPrefs.setOnPreferenceClickListener(this);

        themeColorPrefs = findPreference("prefs_theme_color");
        themeColorPrefs.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("prefs_logout")) {
            MyUser.logOut();
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else if (preference.getKey().equals("prefs_theme_color")) {

        }
        return true;
    }

    public class ThemeColorPreference extends DialogPreference {

        private ArrayAdapter<Integer> arrayAdapter;
        private List<Integer> colors;

        public ThemeColorPreference(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected View onCreateDialogView() {
            GridView gridView = new GridView(getContext());
            arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.prefs_theme_color, colors);
            gridView.setAdapter(arrayAdapter);
            return gridView;
        }
    }

}
