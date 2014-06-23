package com.mcfad.oxylyzer;

import java.util.Date;

import com.mcfad.oxylyzer.db.DataPoint;
import com.mcfad.oxylyzer.db.OxContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class OximeterTester {

	LocalBroadcastManager lBroadMan;
	Context context = null;
	public OximeterTester(final MainActivity activity){
		context = activity;
		lBroadMan = LocalBroadcastManager.getInstance(activity);
		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			@Override
			public void run() {
				DataPoint data = generateData(new Date().getTime());
				Intent intent = new Intent(OximeterService.BROADCAST_DATA);
						
				intent.putExtra("time", data.time);
				intent.putExtra("spo2", data.spo2);
				intent.putExtra("bpm", data.bpm);
				lBroadMan.sendBroadcast(intent);
				
				//OxContentProvider.postDatapoint(context, recordingUri, data.time, data.spo2, data.bpm);
				
				handler.postDelayed(this, 1000);
			}
		};
		handler.post(graphUpdate);
	}

	int baseSpo2 = 97;
	int baseBpm = 68;
	
	float spo2 = baseSpo2;
	float bpm = baseBpm;
	
	boolean inEvent = false;

	public DataPoint generateData(long time){

		if(inEvent){
			boolean endEvent = Math.random()<0.1;
			if(endEvent){
				inEvent = false;
			}
		} else {
			inEvent = Math.random()<0.001;
		}
		
		if(!inEvent) {
			spo2 = (float) ((baseSpo2 + spo2+5*Math.random())/2);
		} else {
			spo2 = (float) (spo2 - 0.5*Math.random());
		}
		bpm = (float) ((baseBpm + bpm+5*Math.random())/2);
		
		return new DataPoint(time,(int)bpm,(int)spo2);
	}
	
	public void generateRecording(int numHours) {
		long startTime = new Date().getTime();
		int numSamples = 3600*numHours; // 6 hours
		
		ContentValues[] values = new ContentValues[numSamples];
		for(int i=0;i<numSamples;i++){
			DataPoint data = generateData(startTime+i*1000);
			ContentValues value = new ContentValues();
			value.put("time", data.time);
			value.put("bpm", data.bpm);
			value.put("spo2", data.spo2);
			values[i] = value;
		}

		Uri recordingUri = OxContentProvider.startNewRecording(context,startTime);
		OxContentProvider.postDatapoints(context, recordingUri, values);
		OxContentProvider.endRecording(context, recordingUri,startTime+numSamples*1000);
		
		Log.d("OximeterTester","Finished generating recording");
	}
}
