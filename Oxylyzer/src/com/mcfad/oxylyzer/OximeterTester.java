package com.mcfad.oxylyzer;

import java.util.Date;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

public class OximeterTester {

	LocalBroadcastManager lBroadMan;
	public OximeterTester(final MainActivity activity){
		lBroadMan = LocalBroadcastManager.getInstance(activity);
		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			int x = 0;
			@Override
			public void run() {
				postData();
				handler.postDelayed(this, 1000);
				x += 1;
			}
		};
		handler.post(graphUpdate);
	}
	public void postData(){
		int spO2 = (int) (80+20*Math.random());
		int bpm = (int) (80+20*Math.random());
		
		long time = new Date().getTime();
		Intent intent = new Intent(OximeterService.BROADCAST_DATA);
	
		intent.putExtra("time", time);
		intent.putExtra("spo2", spO2);
		intent.putExtra("bpm", bpm);

		lBroadMan.sendBroadcast(intent);
	}
}
