package com.mcfad.oxylyzer.db;

import android.database.Cursor;

public class DataPoint {
	public static String[] projection = new String[]{OxSQLiteHelper.COLUMN_TIME,OxSQLiteHelper.COLUMN_BPM,OxSQLiteHelper.COLUMN_SPO2};
	public long time;
	public int bpm;
	public int spo2;
	public DataPoint(Cursor cursor){
		time = cursor.getLong(0);
		bpm = cursor.getInt(1);
		spo2 = cursor.getInt(2);
	}
}