package com.mcfad.oxylyzer.db;


import java.util.Date;

import com.mcfad.oxylyzer.OximeterService;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class OxContentProvider extends ContentProvider {

	private OxSQLiteHelper database;

	private static final String AUTHORITY = "com.mcfad.oxylyzer.db";

	private static final String RECORDINGS_PATH = "recordings";
	private static final String DATA_POINTS_PATH = "data_points";
	public static final Uri RECORDINGS_URI = Uri.parse("content://" + AUTHORITY + "/" + RECORDINGS_PATH);
	public static final Uri DATA_POINTS_URI = Uri.parse("content://" + AUTHORITY + "/" + DATA_POINTS_PATH);

	private static final int RECORDINGS = 10;
	private static final int RECORDING_ID = 20;
	private static final int DATA_POINTS = 30;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, RECORDINGS_PATH, RECORDINGS);
		sURIMatcher.addURI(AUTHORITY, RECORDINGS_PATH + "/#", RECORDING_ID);
		sURIMatcher.addURI(AUTHORITY, DATA_POINTS_PATH, DATA_POINTS);
	}

	@Override
	public boolean onCreate() {
		database = new OxSQLiteHelper(getContext());
		return false;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteDatabase sqlDB = database.getReadableDatabase();
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case RECORDINGS:
			Cursor cursor = sqlDB.query(OxSQLiteHelper.TABLE_RECORDINGS, projection, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), RECORDINGS_URI);
			return cursor;
		case DATA_POINTS:
			return sqlDB.query(OxSQLiteHelper.TABLE_VALUES, projection, selection, selectionArgs, null, null, sortOrder);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] allValues) {
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int inserted = 0;
		try {
			sqlDB.beginTransaction();
			for(ContentValues values:allValues) {
				sqlDB.insert(OxSQLiteHelper.TABLE_VALUES, null, values);
				inserted++;
			}
			sqlDB.setTransactionSuccessful();
		}
		catch (SQLException e) {}
		finally	{
			sqlDB.endTransaction();
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return inserted;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		Uri newUri = uri;
		switch (uriType) {
		case RECORDINGS:
			id = sqlDB.insert(OxSQLiteHelper.TABLE_RECORDINGS, null, values);
			newUri = Uri.withAppendedPath(uri, ""+id);
			break;
		case DATA_POINTS:
			sqlDB.insert(OxSQLiteHelper.TABLE_VALUES, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return newUri;
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int deleted = 0;
		switch (uriType) {
		case RECORDING_ID:
			int recordingId = Integer.parseInt(uri.getLastPathSegment());
			deleted = sqlDB.delete(OxSQLiteHelper.TABLE_RECORDINGS, OxSQLiteHelper.COLUMN_ID+"="+recordingId, null);
			getContext().getContentResolver().notifyChange(RECORDINGS_URI, null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		//getContext().getContentResolver().notifyChange(uri, null);
		return deleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int updated = 0;
		switch (uriType) {
		case RECORDING_ID:
			int recordingId = Integer.parseInt(uri.getLastPathSegment());
			updated = sqlDB.update(OxSQLiteHelper.TABLE_RECORDINGS, values, OxSQLiteHelper.COLUMN_ID+"="+recordingId, null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return updated;
	}

	public static Uri startNewRecording(Context context,long time) {
		ContentValues values = new ContentValues();
		values.put(OxSQLiteHelper.COLUMN_START, time);
		return context.getContentResolver().insert(RECORDINGS_URI, values);
	}
	public static void endRecording(Context context, Uri recording, long time) {
		ContentValues values = new ContentValues();
		values.put(OxSQLiteHelper.COLUMN_END, time);
		context.getContentResolver().update(recording, values, null, null);
	}
	public static int deleteRecording(Context context, Uri recording) {
		return context.getContentResolver().delete(recording, null, null);
	}
	public static void setRecordingDescription(Context context, Uri recording,String description) {
		ContentValues values = new ContentValues();
		values.put(OxSQLiteHelper.COLUMN_DESC, description);
		context.getContentResolver().update(recording, values, null, null);
	}
	public static Uri postDatapoint(Context context, Uri recording, long time,int spo2,int bpm) {
		ContentValues values = new ContentValues();
		values.put(OxSQLiteHelper.COLUMN_TIME, time);
		values.put(OxSQLiteHelper.COLUMN_SPO2, spo2);
		values.put(OxSQLiteHelper.COLUMN_BPM, bpm);

		return context.getContentResolver().insert(DATA_POINTS_URI, values);
	}
	public static int postDatapoints(Context context, Uri recording, ContentValues[] values){

		return context.getContentResolver().bulkInsert(DATA_POINTS_URI,values);
	}
} 