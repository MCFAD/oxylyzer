package com.mcfad.oxylyzer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OxSQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "recordings.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_RECORDINGS = "recordings";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIME = "time";

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_RECORDINGS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_TIME
			+ " text not null);";

	public OxSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(OxSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		onCreate(db);
	}

}