package com.fubarfacid.walkingthemesong;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String WALKING_THEME_SONG_TAG = "WALKING_THEME_SONG";
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent pendingIntent;

    private MediaPlayerService mediaPlayerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)==ConnectionResult.SUCCESS

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayerService = MediaPlayerService.getInstance();

        this.findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Log.v(WALKING_THEME_SONG_TAG, "chosen song is "+mediaPlayerService.getChosenSongPath());
                if(mediaPlayerService.getChosenSongPath() == null ||
                        mediaPlayerService.getChosenSongPath().isEmpty()){

                    setSongPathFromUserChoice(true);
                } else{
                    startActivityDetection();
                }
            }
        });

        this.findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(activityRecognitionClient != null && pendingIntent != null) {
                    activityRecognitionClient.removeActivityUpdates(pendingIntent);
                }

                mediaPlayerService.destroy();
            }
        });

        this.findViewById(R.id.chooseASongButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                setSongPathFromUserChoice(false);
            }
        });
    }

    private void startActivityDetection() {
        if(activityRecognitionClient == null || pendingIntent == null) {
            startActivityRecognitionService();
        } else{
            requestActivityUpdates();
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerService.destroy();
    }

    private void startActivityRecognitionService() {
        Log.d(WALKING_THEME_SONG_TAG, "starting activity monitoring.");
        activityRecognitionClient = ActivityRecognition.getClient(this);

        //register pending intent with ActivityRecognitionService
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //start receiving updates
        requestActivityUpdates();
    }

    private void requestActivityUpdates() {
        Log.v(WALKING_THEME_SONG_TAG, "requestActivityUpdates "+pendingIntent);
        activityRecognitionClient.requestActivityUpdates(0, pendingIntent);
    }

    private void setSongPathFromUserChoice(final boolean startServices){
        final Map<String, String> songs = getListOfAudioFilesOnDevice();
        final CharSequence[] songsArray = songs.keySet().toArray(new CharSequence[songs.size()]);

        Log.d(WALKING_THEME_SONG_TAG, "showing the alert dialog!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a song");
        builder.setItems(songsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(WALKING_THEME_SONG_TAG, "chosen song is "+songsArray[which]);
                mediaPlayerService.setChosenSongPath(songs.get(songsArray[which].toString()));

                if(startServices){
                    startActivityDetection();
                    mediaPlayerService.prepareMediaPlayer();
                }


            }
        });
        builder.show();
    }

    private Map<String, String> getListOfAudioFilesOnDevice() {
        Log.d(WALKING_THEME_SONG_TAG, "getting list of audo files on device");

        String[] projection = {
                //MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
                //MediaStore.Audio.Media.DISPLAY_NAME
        };

        final Map<String, String> songsAndLocations = new HashMap<>();

        final Cursor cursor = getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection, null, null, null);

        while(cursor.moveToNext()){
            songsAndLocations.put(
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        }

        cursor.close();

        Log.d(WALKING_THEME_SONG_TAG, "returning "+songsAndLocations.size()+" options");
        return songsAndLocations;
    }
}
