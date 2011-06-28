package com.android.walkingTheme;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Android accelerometer sensor tutorial
 * @author antoine vianey
 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class WalkingThemeSong extends Activity implements AccelerometerListener
{
	private Button start, stop, chooseSong;
	private static Context CONTEXT;
	public static MediaPlayer player = new MediaPlayer();
	public static boolean paused = false;
	public static boolean buttonPressed = true;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		CONTEXT = this;

		this.start = (Button) this.findViewById(R.id.start);
		this.start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				buttonPressed = true;
			}

		});

		this.stop = (Button) this.findViewById(R.id.stop);
		this.stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				System.exit(0);
			}        	
		});

		this.chooseSong = (Button) this.findViewById(R.id.chooseSong);
		this.chooseSong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				
			}

		});
	}

	protected void onResume() {
		super.onResume();
		if (AccelerometerManager.isSupported()) {
			AccelerometerManager.startListening(this);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (AccelerometerManager.isListening()) {
			AccelerometerManager.stopListening();
		}

	}

	public static Context getContext() {
		return CONTEXT;
	}

	/**
	 * onShake callback
	 */
	public void onShake(float force) {
		//ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
		//toneGen.startTone(ToneGenerator.TONE_DTMF_6, 500);
		try {
			player.setDataSource("/sdcard/blackberry/music/" +
				"[Star Wars - Symphony for a Saga] - V - Disc 2 - 09 - The Imperial March.mp3");
			player.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.start();

		Toast.makeText(this, "Phone shaked : " + force, 1000).show();
	}
}