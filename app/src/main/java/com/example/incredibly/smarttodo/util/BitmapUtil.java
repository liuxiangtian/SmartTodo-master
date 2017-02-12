package com.example.incredibly.smarttodo.util;


import android.widget.ImageView;

import com.example.incredibly.smarttodo.R;
import com.squareup.picasso.Picasso;

public class BitmapUtil {

    public static void loadHeaderBitmap(final ImageView imageView, final String url) {
        Picasso.with(imageView.getContext()).load(url)
                .placeholder(R.drawable.avator_placeholder)
                .error(R.drawable.avator_placeholder)
                .into(imageView);
    }

    public static void loadHeaderBitmap(final ImageView imageView, final int res) {
        Picasso.with(imageView.getContext()).load(res)
                .placeholder(R.drawable.avator_placeholder)
                .error(R.drawable.avator_placeholder)
                .into(imageView);
    }

}
