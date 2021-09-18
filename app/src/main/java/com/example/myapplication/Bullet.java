package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.myapplication.GameView.screenRatioX;
import static com.example.myapplication.GameView.screenRatioY;

public class Bullet {
    int x, y, width, height;
    Bitmap bullet;

    Bullet(Resources res){
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 4;
        height /= 4;

        //compatibility with other screens
        width *= (int)screenRatioX; //in order not to crash, first it;s multiplied then it's casted to int e.g. 0.8 * 10
        height *= (int) screenRatioY;

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);
    }
    Rect getCollisionShape(){
        return new Rect(x, y, x+ width, y + height);
    }
}
