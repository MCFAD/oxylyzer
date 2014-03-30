package com.mcfad.oxylyzer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OxSQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "recordings.db";
	private static final int DATABASE_VERSION = 4;

	public static final String TABLE_RECORDINGS = "recordings";
	public static final String TABLE_VALUES = "data_points";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_SPO2 = "spo2";
	public static final String COLUMN_BPM = "bpm";

	// Database creation sql statement
	private static final String CREATE_RECORDINGS = "create table if not exists "
			+ TABLE_RECORDINGS + " (" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TIME + " text not null"
					+ ");";
			
	private static final String CREATE_VALUES = "create table if not exists "
			+ TABLE_VALUES + " (" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TIME + " integer, "
			+ COLUMN_SPO2 + " integer, "
			+ COLUMN_BPM + " integer"
					+ ");";

	public OxSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_RECORDINGS);
		database.execSQL(CREATE_VALUES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(OxSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDINGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALUES);
		onCreate(db);
	}

}