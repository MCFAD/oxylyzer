package com.mcfad.oxylyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcfad.oxylyzer.MainActivity.GraphFragment;
import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.OxSQLiteHelper;

public class HistoryFragment extends GraphFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	Spinner recordingsSpinner;
	CursorAdapter recordingsAdapter;
	EditText recordingDescription;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);

		recordingsSpinner = (Spinner) rootView.findViewById(R.id.records);
		recordingsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();

				Cursor dataCursor = queryPointsInRecording(recording.startTime,recording.endTime);

				Log.d("REC", 
						"start: "+recording.startTime+" end: "+recording.endTime+", "
						+ (recording.endTime-recording.startTime)/1000+" seconds, "
						+ "desc: "+recording.description+", #points: "+dataCursor.getCount());
				dataCursor.close();

				recordingDescription.setText(recording.description);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {		
			}
		});
		recordingsAdapter = new RecordingsAdapter(getActivity(), null);
		recordingsSpinner.setAdapter(recordingsAdapter);
		recordingsSpinner.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				saveDescription();
				return false;
			}
		});

		Button reportButton = (Button) rootView.findViewById(R.id.button_view_report);
		reportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewReport();
			}
		});
		Button exportButton = (Button) rootView.findViewById(R.id.button_export);
		exportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				export();
			}
		});
		Button deleteButton = (Button) rootView.findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				delete();
			}
		});
		recordingDescription = (EditText) rootView.findViewById(R.id.description);

		//getActivity().getSupportLoaderManager().initLoader(0, null, this);
		getActivity().getSupportLoaderManager().restartLoader(0, null, this);

		return rootView;
	}
	public Cursor queryPointsInRecording(long startTime,long endTime){
		String dataSelection = OxSQLiteHelper.COLUMN_TIME+">"+startTime+
				" AND "+OxSQLiteHelper.COLUMN_TIME+"<"+endTime;
		return getActivity().getContentResolver().query(
				OxContentProvider.DATA_POINTS_URI, dataProjection, dataSelection, null, null);
	}
	@Override
	public void onPause(){
		super.onPause();
		saveDescription();
	}
	protected void saveDescription() {
		if(recordingsSpinner.getSelectedView()!=null){
			String description = recordingDescription.getText().toString();
			Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();
			if(!description.equals(recording.description))
				OxContentProvider.setRecordingDescription(getActivity(), recording.getUri(), description);
		}
	}
	protected void delete() {
		Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();
		int deleted = OxContentProvider.deleteRecording(getActivity(), recording.getUri());
		Log.d("OximeterRecording", "deleted?"+deleted);
	}
	public void viewReport(){
		SharedPreferences settings = getActivity().getSharedPreferences("Profile", 0);

		//boolean profile = settings.getBoolean("ProfileSaved", false);
		boolean profile = false;
		if(!profile)
		{
			Intent intent = new Intent(getActivity(), NewProfileActivity.class);
			startActivityForResult(intent, 0);
		}
		else
		{
			Intent intent2 = new Intent(getActivity(), Report.class);
			HistoryFragment.this.startActivity(intent2);
		}
	}
	String[] dataProjection = new String[]{OxSQLiteHelper.COLUMN_TIME,OxSQLiteHelper.COLUMN_BPM,OxSQLiteHelper.COLUMN_SPO2};
	public void export(){
		Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();

		Cursor dataCursor = queryPointsInRecording(recording.startTime,recording.endTime);
		StringBuffer buffer = new StringBuffer();
		while(dataCursor.moveToNext()){
			long time = dataCursor.getLong(0);
			int bpm = dataCursor.getInt(1);
			int spo2 = dataCursor.getInt(2);

			String row = time+","+spo2+","+bpm+'\n';
			buffer.append(row);
		}
		dataCursor.close();
		try {
			FileOutputStream data_file = getActivity().openFileOutput("data.csv", 0);
			data_file.write(buffer.toString().getBytes());
			data_file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = getActivity().getFileStreamPath("data.csv");
		file.setReadable(true, false);
		Uri fileUri = Uri.fromFile(file);

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, 
				"Oximeter Recording: "+
				sdf.format(new Date(recording.startTime))+"-"+sdf.format(new Date(recording.endTime))+
				" "+recording.description);
		sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
		sendIntent.setType("text/html");
		startActivity(sendIntent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Intent intent2 = new Intent(getActivity(), Report.class);
		HistoryFragment.this.startActivity(intent2);
	}

	String finishedRecordings = OxSQLiteHelper.COLUMN_END+">0";
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),OxContentProvider.RECORDINGS_URI, recordingProjection, finishedRecordings, null, null);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		recordingsAdapter.changeCursor(cursor);
		//recordingsSpinner.
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		recordingsAdapter.changeCursor(null);
	}
	public class Recording {
		int id;
		long startTime;
		long endTime;
		String description;
		public Recording(Cursor cursor){
			id = cursor.getInt(0);
			startTime = cursor.getLong(1);
			endTime = cursor.getLong(2);
			description = cursor.getString(3);
		}
		public Uri getUri() {
			return Uri.withAppendedPath(OxContentProvider.RECORDINGS_URI, ""+id);
		}
	}
	static String[] recordingProjection = new String[]{OxSQLiteHelper.COLUMN_ID,OxSQLiteHelper.COLUMN_START,OxSQLiteHelper.COLUMN_END,OxSQLiteHelper.COLUMN_DESC};
	public class RecordingsAdapter extends SimpleCursorAdapter {

		public RecordingsAdapter(Context context, Cursor cursor) {
			super(context, android.R.layout.simple_spinner_item, cursor, recordingProjection, new int[]{android.R.id.text1}, 0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Recording recording = new Recording(cursor);

			view.setTag(recording);

			TextView text = (TextView) view.findViewById(android.R.id.text1);
			String dateStringStart = sdf.format(recording.startTime);
			text.setText(dateStringStart);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
	}
	SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
}