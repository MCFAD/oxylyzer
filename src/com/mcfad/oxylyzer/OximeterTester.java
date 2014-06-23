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
	long startTime = new Date().getTime();
	int interval = 100;
	public OximeterTester(final MainActivity activity){
		context = activity;
		lBroadMan = LocalBroadcastManager.getInstance(activity);
		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			@Override
			public void run() {
				long timeOffset = (int) ((new Date().getTime()-startTime)/interval*1000);
				DataPoint data = generateData(startTime+timeOffset);
				Intent intent = new Intent(OximeterService.BROADCAST_DATA);
						
				
				intent.putExtra("time", data.time);
				intent.putExtra("spo2", data.spo2);
				intent.putExtra("bpm", data.bpm);
				lBroadMan.sendBroadcast(intent);
				
				//OxContentProvider.postDatapoint(context, recordingUri, data.time, data.spo2, data.bpm);
				
				handler.postDelayed(this, interval);
			}
		};
		handler.post(graphUpdate);
	}

	int baseSpo2 = 97;
	int baseBpm = 68;
	
	float spo2Offset = 0;
	float bpmOffset = 0;
	
	int spo2Range = 6;
	int bpmRange = 20;
	
	boolean inEvent = false;

	public DataPoint generateData(long time){

		if(inEvent){
			boolean endEvent = Math.random()<0.1;
			if(endEvent){
				inEvent = false;
			}
		} else {
			inEvent = Math.random()<0.01;
		}
		
		if(!inEvent) {		
			spo2Offset = (float) (spo2Offset+0.7*(Math.random()-0.5));
			spo2Offset = (float) Math.min(spo2Range/2,Math.max(spo2Offset,-spo2Range/2)); //clamp to (-range/2,+range/2)
		} else {
			spo2Offset = (float) (spo2Offset-Math.random());
		}
		bpmOffset = (float) (bpmOffset+32*(Math.random()-0.5));
		bpmOffset = (float) Math.min(bpmRange/2,Math.max(bpmOffset,-bpmRange/2)); //clamp to (-range/2,+range/2)
		
		return new DataPoint(time,(int)(baseBpm+bpmOffset),(int)(baseSpo2+spo2Offset));
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
