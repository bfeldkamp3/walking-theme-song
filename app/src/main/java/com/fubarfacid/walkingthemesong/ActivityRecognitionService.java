package com.fubarfacid.walkingthemesong;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by brandon on 8/20/17.
 */
public class ActivityRecognitionService extends IntentService {

    private MediaPlayerService mediaPlayerService;

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
        mediaPlayerService = MediaPlayerService.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private boolean handleDetectedActivities(List<DetectedActivity> probableActivities) {
        boolean moving = false;

        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.ON_FOOT:
                case DetectedActivity.RUNNING:
                case DetectedActivity.WALKING: {
                    Log.v( MainActivity.WALKING_THEME_SONG_TAG,
                            "Moving: " + activity.getConfidence());

                    if(activity.getConfidence() >= 25){
                        moving = true;
                        mediaPlayerService.start();
                    }

                    break;
                }

                default: {
                    Log.v( MainActivity.WALKING_THEME_SONG_TAG, "NOT MOVING!");
                    mediaPlayerService.stop();
                    break;
                }
            }
        }

        return moving;
    }
}
