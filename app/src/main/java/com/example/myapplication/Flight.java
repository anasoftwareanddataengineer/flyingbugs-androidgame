package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.myapplication.GameView.screenRatioX;
import static com.example.myapplication.GameView.screenRatioY;

public class Flight {
    public int toShoot = 0;
    boolean isGoingUp = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, loose;
    private GameView gameView;

    Flight(GameView gameView, int screenY, Resources res){
        
        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /=4;

        //compatibility with other screens
        width *= (int) screenRatioX; //in order not to crash, first it;s multiplied then it's casted to int e.g. 0.8 * 10
        height *= (int) screenRatioY;

        //resize bitmap
        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);
        loose = BitmapFactory.decodeResource(res, R.drawable.dead);

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false);
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false);
        loose = Bitmap.createScaledBitmap(loose, width, height, false);

        y = screenY /2; //put it in the middle by height
        x = (int) (64 * screenRatioX); //margin 64, screenRatioX for compatibility
    }


    Bitmap getFlight(){

        if(toShoot != 0){
            if(shootCounter == 1){
                shootCounter++;
                return shoot1;
            }

            if(shootCounter == 2){
                shootCounter++;
                return shoot2;
            }

            if(shootCounter == 3){
                shootCounter++;
                return shoot3;
            }

            if(shootCounter == 4){
                shootCounter++;
                return shoot4;
            }

            shootCounter = 1; //restarting of the animation
            toShoot--;
            gameView.newBullet();

            return shoot5;
        }

        if(wingCounter == 0){
            wingCounter++;
            return  flight1;
        }

        wingCounter--;
        return flight2;
    } //will return Bitmap

    Rect getCollisionShape(){
        return new Rect(x, y, x+ width, y + height);
    }

    Bitmap getLoose(){
        return loose;
    }
}
