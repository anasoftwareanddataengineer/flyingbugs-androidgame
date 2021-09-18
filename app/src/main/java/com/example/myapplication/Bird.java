package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.myapplication.GameView.screenRatioX;
import static com.example.myapplication.GameView.screenRatioY;

public class Bird {
    int x = 0, y, width, height, birdCounter = 1;
    Bitmap bird1, bird2, bird3, bird4;
    public int speed = 20;
    public boolean wasShot = true;

    Bird (Resources res){
        //animation
        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

        width = bird1.getWidth();
        height = bird1.getHeight();

        //the birds are too big for the screen, shrink them down
        width /= 6;
        height /= 6;

        //compatibility with other screens
        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);

        //placed off the screen when the game starts
        y = -height;
    }

    Bitmap getBird(){
        if(birdCounter == 1){
            birdCounter++;
            return bird1;
        }

        if(birdCounter == 2){
            birdCounter++;
            return bird2;
        }

        if(birdCounter == 3){
            birdCounter++;
            return bird3;
        }

        //restart the animation to the first frame
        birdCounter = 1;

        //if none of the previous conditions are satisfied then it's the fourth bird, and in next iteration the first one will appear due to the previous line of code, where we set the birdCounter to 1
        return bird4;
    }

    Rect getCollisionShape(){
        return new Rect(x, y, x+ width, y + height);
    }
}
