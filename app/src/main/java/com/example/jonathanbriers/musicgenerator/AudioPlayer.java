package com.example.jonathanbriers.musicgenerator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Jonny on 12/04/2016.
 */
public class AudioPlayer extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mp;
    private MediaController mc;
    private String audioFile;
    private Handler handler = new Handler();
    private static final int NOTIFY_ID=1;
    private final IBinder musicBind = new MusicBinder();



    public AudioPlayer(MediaPlayer mp, String audioFile) {
        this.mp = mp;
        this.audioFile = audioFile;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    //binder
    public class MusicBinder extends Binder {
        AudioPlayer getService() {
            return AudioPlayer.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init() {
        mp.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setOngoing(true)
                .setContentTitle("Playing");
        Notification not = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            not = builder.build();
        }
        startForeground(NOTIFY_ID, not);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if(mp.getCurrentPosition()>0){
            mp.reset();
        }
    }

    public int getPosn(){
        return mp.getCurrentPosition();
    }

    public int getDur(){
        return mp.getDuration();
    }

    public boolean isPng(){
        return mp.isPlaying();
    }

    public void pausePlayer(){
        mp.pause();
    }

    public void seek(int posn){mp.seekTo(posn);}

    public void go(){
        mp.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
}
