package com.example.incredibly.smarttodo.contract;

import android.content.Context;

public interface Contract {

    interface View {
        void toggleDrawer(boolean toOpen);
        void updateHeader();
        void clearMenus();
        void fixSubMenu();
        void addSubMenu(String category);
    }

    interface Presenter {
        void prepareNavMenus(Context context);
    }

}
