package com.fubarfacid.walkingthemesong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 8/20/17.
 */
public class MediaPlayerService {

    public static MediaPlayer player;
    private String chosenSongPath;

    private static MediaPlayerService mediaPlayerService;

    private MediaPlayerService(){

    }

    public void start(){
        if(player == null) {
            prepareMediaPlayer();
        } else {
            player.start();
        }
    }

    public void stop(){
        if(player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public void destroy(){
        if(player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public static MediaPlayerService getInstance(){
        if(mediaPlayerService != null){
            return mediaPlayerService;
        }

        return new MediaPlayerService();
    }

    public void prepareMediaPlayer() {
        try {
            player = new MediaPlayer();
            player.setDataSource(chosenSongPath);
            player.prepare();

        } catch (IOException e) {
            Log.e(MainActivity.WALKING_THEME_SONG_TAG, "error setting up media player :(", e);
        }
    }

    public void setChosenSongPath(String chosenSongPath){
        this.chosenSongPath = chosenSongPath;
    }

    public String getChosenSongPath(){
        return chosenSongPath;
    }
}
