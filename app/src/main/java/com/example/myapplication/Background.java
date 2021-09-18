package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {
    int x = 0, y = 0;
    Bitmap background;

    Background (int screenX, int screenY, Resources resources){

        background = BitmapFactory.decodeResource(resources, R.drawable.background);
        //resize the bitmap in order to fit the screen
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);


    }
}
