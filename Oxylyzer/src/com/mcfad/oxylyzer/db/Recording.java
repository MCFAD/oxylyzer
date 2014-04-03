package com.mcfad.oxylyzer.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.mcfad.oxylyzer.HistoryFragment;

public class Recording {
	public static String[] projection = new String[]{OxSQLiteHelper.COLUMN_ID,OxSQLiteHelper.COLUMN_START,OxSQLiteHelper.COLUMN_END,OxSQLiteHelper.COLUMN_DESC};
	int id;
	public long startTime;
	public long endTime;
	public String description;
	public Recording(Cursor cursor){
		id = cursor.getInt(0);
		startTime = cursor.getLong(1);
		endTime = cursor.getLong(2);
		description = cursor.getString(3);
	}
	public Uri getUri() {
		return Uri.withAppendedPath(OxContentProvider.RECORDINGS_URI, ""+id);
	}
	public String getListTitle(){
		String title = sdf.format(startTime)+" - ";
		long duration = (endTime-startTime)/1000;
		if(duration>60*60) {
			duration = duration/(60*60);
			title += duration+" hour";
		} else if(duration>60) {
			duration = duration/60;
			title += duration+" minute";
		} else {
			title += duration+" second";
		}
		if(duration>1) // more than one second/minute/hour, add 's'
			title += "s"; 
		return title;
	}
	public Cursor queryDatapoints(Context context){
		String dataSelection = OxSQLiteHelper.COLUMN_TIME+">"+startTime+
				" AND "+OxSQLiteHelper.COLUMN_TIME+"<"+endTime;
		return context.getContentResolver().query(
				OxContentProvider.DATA_POINTS_URI, DataPoint.projection, dataSelection, null, null);
	}
	public void export(Context context){
		Cursor dataCursor = queryDatapoints(context);
		StringBuffer buffer = new StringBuffer();
		while(dataCursor.moveToNext()){
			DataPoint dataPoint = new DataPoint(dataCursor);
			String row = dataPoint.time+","+dataPoint.spo2+","+dataPoint.bpm+'\n';
			buffer.append(row);
		}
		dataCursor.close();
		try {
			FileOutputStream data_file = context.openFileOutput("data.csv", 0);
			data_file.write(buffer.toString().getBytes());
			data_file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = context.getFileStreamPath("data.csv");
		file.setReadable(true, false);
		Uri fileUri = Uri.fromFile(file);

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, 
				"Oximeter Recording: "+
				sdf.format(new Date(startTime))+"-"+sdf.format(new Date(endTime))+
				" "+description);
		sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
		sendIntent.setType("text/html");
		context.startActivity(sendIntent);
	}
	static SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
}