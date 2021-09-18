package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.media.AudioAttributes.CONTENT_TYPE_MUSIC;

public class GameView extends SurfaceView implements Runnable{

    private Thread thread;
    private SoundPool soundPool;
    private boolean isPlaying, isGameOver = false;
    private Background background1, background2;//two bckgrnds in order for the bckgrnd to move
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;//for different devices to have scalable view, display compatibility; public static so we can enter them in another class
    private Paint paint;
    private Flight flight;
    private List<Bullet> bullets;
    private Bird[] birds;
    private Random random;
    private SharedPreferences prefs;
    private GameActivity activity;
    private int sound;
    private int music;
    private int loose;


    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE); // MODE_PRIVATE hides the content of shared preferences from other apps in the phone

        random = new Random();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();

            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = soundPool.load(activity, R.raw.whoosh, 1);
        music = soundPool.load(activity, R.raw.birds, 1);
        loose = soundPool.load(activity, R.raw.arcadegame, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920 / screenX;
        screenRatioY = 1080 / screenY;


        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.x = screenX; //the second background will not appear on the screen but at the end of the x-axis

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        flight = new Flight(this, screenY, getResources());

        bullets = new ArrayList<>();

        //four birds on the screen
        birds = new Bird[4];

        for(int i = 0; i < 4; i++){
            Bird bird = new Bird(getResources());
            birds[i] = bird;
        }
    }

    @Override
    public void run() {

        while(isPlaying){

            if(!prefs.getBoolean("isMute", false)){
                soundPool.play(music, 1, 1, 0, 0, 1);
            }

            update();
            draw();
            sleep();

        }

    }

    private void update(){
        //changing the positions of our backgrounds on the x-axis
        background1.x -= 10 * screenRatioX; //for the display compatibility, on every screen for the background to move smoothly. screenRatioX is a constant, for every screen size
        background2.x -= 10 * screenRatioX; //both X and Y are multiplied by X because the background is scrolling on X-axis

        if(background1.x + background1.background.getWidth() < 0){//background off the screen
            background1.x = screenX;//get it back
        }

        if(background2.x + background2.background.getWidth() < 0){//background off the screen
            background2.x = screenX;
        }

        if (flight.isGoingUp){
            flight.y -= 30 * screenRatioY;
        }else
            flight.y += 30 * screenRatioY;

        if(flight.y < 0){
            flight.y = 0;
        }

        if(flight.y > screenY - flight.height){
            flight.y = screenY - flight.height;
        }
        List<Bullet> trash = new ArrayList<>();

        for (Bullet  bullet: bullets){
            if(bullet.x > screenX){
                trash.add(bullet);
            }

            bullet.x += 50 * screenRatioX;

            //did the bullet hit the bird; check for every
            for(Bird bird: birds){
                if(Rect.intersects(bird.getCollisionShape(), bullet.getCollisionShape())){

                    score++;
                    bird.x = -500; //set it out of the screen; refer to other functions, when it's out of the screen it's set on the beginning again so it shows up as a new enemy
                    bullet.x = screenX + 500; //bullet out of the screen, it's put in trash as the previous lines show
                    bird.wasShot = true;


                }
            }
        }

        for(Bullet bullet: trash){
            bullets.remove(bullet);
        }

        for (Bird bird: birds){
            bird.x -= bird.speed;

            if(bird.x + bird.width < 0){

                if(!bird.wasShot){ //not shot but still off the screen
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRatioX);
                bird.speed = random.nextInt(bound);

                if(bird.speed < 10 * screenRatioX){
                    bird.speed = (int) (10 * screenRatioX);
                }

                bird.x = screenX; //placing the bird on the end of the right side of the screen
                bird.y = random.nextInt(screenY - bird.height);

                bird.wasShot = false;

            }

            //collision detection; the getCollisionShape functions are making the squares around objects, and the intersects() checks if the boxes are overlapping, once they hit each other the player looses
            if(Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())){
                isGameOver = true;
                return;

            }
        }
    }

    private void draw(){
        //get the canvas
        if(getHolder().getSurface().isValid()){//successfully initiated
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for(Bird bird: birds){
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
            }

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if(isGameOver){
                isPlaying = false;
                canvas.drawBitmap(flight.getLoose(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();

                return;

            }

            //flight is second because it's on the layer above
            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for(Bullet bullet: bullets){
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
            }

            getHolder().unlockCanvasAndPost(canvas); //showing the canvas
        }
    }

    private void waitBeforeExiting() {
        try {
            if(!prefs.getBoolean("isMute", false)){
                soundPool.play(loose, 1, 1, 0, 0, 1);
            }
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        if(prefs.getInt("highscore", 0) < score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply(); //save for a new highscore
        }
    }

    private void sleep(){
        //60fps
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        isPlaying = true;
        //initializing thread object
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        //stop the thread
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < screenX / 2){ //if touching the left part of the screen you are controlling the flight
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;
                if(event.getX() > screenX / 2) //touching the right side of the screen
                {
                    flight.toShoot++;
                }
                break;

        }

        return true;
    }

    public void newBullet() {

        if(!prefs.getBoolean("isMute", false)){
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);
    }
}
