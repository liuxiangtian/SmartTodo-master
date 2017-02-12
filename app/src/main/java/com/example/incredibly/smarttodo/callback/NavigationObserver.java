package com.example.incredibly.smarttodo.callback;


public interface NavigationObserver {
    void navigation(String navigation, String title, long start, long end);
    void backPress();
}
