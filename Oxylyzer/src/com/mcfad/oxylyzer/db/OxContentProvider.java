package com.mcfad.oxylyzer.db;


import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class OxContentProvider extends ContentProvider {

	// database
	private OxSQLiteHelper database;

	// used for the UriMacher
	private static final int RECORDINGS = 10;
	private static final int RECORDING_ID = 20;

	private static final String AUTHORITY = "com.mcfad.oxylyzer.db";

	private static final String RECORDINGS_PATH = "recordings";
	public static final Uri RECORDINGS_URI = Uri.parse("content://" + AUTHORITY + "/" + RECORDINGS_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, RECORDINGS_PATH, RECORDINGS);
		sURIMatcher.addURI(AUTHORITY, RECORDINGS_PATH + "/#", RECORDING_ID);
	}

	@Override
	public boolean onCreate() {
		database = new OxSQLiteHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteDatabase sqlDB = database.getReadableDatabase();
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case RECORDINGS:
			return sqlDB.query(OxSQLiteHelper.TABLE_RECORDINGS, projection, selection, selectionArgs, null, null, sortOrder);
		case RECORDING_ID:
			return sqlDB.query(OxSQLiteHelper.TABLE_VALUES, projection, selection, selectionArgs, null, null, sortOrder);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
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
			newUri = Uri.parse(RECORDINGS_PATH + "/" + id);
			break;
		case RECORDING_ID:
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
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	public static Uri startNewRecording(Context context) {
		ContentValues values = new ContentValues();
		values.put("time", new Date().getTime());
		return context.getContentResolver().insert(OxContentProvider.RECORDINGS_URI, values);
	}
	public static Uri postDatapoint(Context context) {
		ContentValues values = new ContentValues();
		values.put("time", new Date().getTime());
		return context.getContentResolver().insert(OxContentProvider.RECORDINGS_URI, values);
	}
} 