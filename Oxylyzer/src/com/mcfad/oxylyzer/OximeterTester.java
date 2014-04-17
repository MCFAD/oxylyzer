package com.mcfad.oxylyzer;

import java.util.Date;

import com.mcfad.oxylyzer.db.OxContentProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

public class OximeterTester {

	LocalBroadcastManager lBroadMan;
	Context context = null;
	public OximeterTester(final MainActivity activity){
		context = activity;
		lBroadMan = LocalBroadcastManager.getInstance(activity);
		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			int x = 0;
			long time = new Date().getTime();
			
			@Override
			public void run() {
				if(recordingUri==null){
					recordingUri = OxContentProvider.startNewRecording(context,time);
				}
				postData(time);
				time += 1000;
				
				x += 1;
				
				if(x > 1000 && recordingUri!=null){
					OxContentProvider.endRecording(context, recordingUri,time);
				}
				else
					handler.postDelayed(this, 10);
			}
		};
		handler.post(graphUpdate);
	}
	double spo2 = 97;
	double bpm = 68;
	Uri recordingUri = null;

	public void postData(long time){
		//int spO2 = (int) (80+20*Math.random());
		//int bpm = (int) (80+20*Math.random());
		
		
		Intent intent = new Intent(OximeterService.BROADCAST_DATA);
	
		
		

		double r = Math.random();
		if(r > 0.66666667 && spo2 < 98 )
			spo2 = Math.random() + spo2;
		else if(r < 0.33333333 && spo2 > 70 )
			spo2 = spo2 - Math.random();
		
		double r1 = Math.random();
		if(r1 > 0.66666667 && bpm < 90)
			bpm += Math.random();
		else if(r1 < 0.33333333 && bpm > 60 )
			bpm -= Math.random();
			
		
		
		intent.putExtra("time", time);
		intent.putExtra("spo2", (int)Math.round(spo2));
		intent.putExtra("bpm", (int)Math.round(bpm));

		
		OxContentProvider.postDatapoint(context, recordingUri, time, (int)Math.round(spo2), (int)Math.round(bpm));
		
		lBroadMan.sendBroadcast(intent);
	}
}
