package com.mcfad.oxylyzer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.OxSQLiteHelper;
import com.mcfad.oxylyzer.db.Recording;
import com.mcfad.oxylyzer.view.HistoryOxGraph;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	View rootView;
	// view to hide after there are recordings found
	View noRecordingsLayout;
	View haveRecordingsLayout;

	Spinner recordingsSpinner;
	CursorAdapter recordingsAdapter;
	EditText recordingDescription;

	HistoryOxGraph graph;
	
	public static Recording currentRecording;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_history, container, false);

		noRecordingsLayout = (View) rootView.findViewById(R.id.no_recordings);
		haveRecordingsLayout = (View) rootView.findViewById(R.id.have_recordings);

		recordingsSpinner = (Spinner) rootView.findViewById(R.id.records);
		recordingsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentRecording = (Recording)recordingsSpinner.getSelectedView().getTag();

				Cursor dataCursor = currentRecording.queryDatapoints(getActivity());
				Log.d("REC", 
						"start: "+currentRecording.startTime+" end: "+currentRecording.endTime+", "
								+ (currentRecording.endTime-currentRecording.startTime)/1000+" seconds, "
								+ "desc: "+currentRecording.description+", #points: "+dataCursor.getCount());
				dataCursor.close();

				recordingDescription.setText(currentRecording.description);
				graph.updateGraph(getActivity(),currentRecording);
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

		Button exportButton = (Button) rootView.findViewById(R.id.button_export);
		exportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();
				recording.export(getActivity());
			}
		});
		Button deleteButton = (Button) rootView.findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				delete();
			}
		});
		recordingDescription = (EditText) rootView.findViewById(R.id.description);

		LinearLayout graphLayout = (LinearLayout) rootView.findViewById(R.id.graph1);
		graph = new HistoryOxGraph(getActivity(),graphLayout);

		getActivity().getSupportLoaderManager().restartLoader(0, null, this);

		return rootView;
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
		if(recordingsAdapter.getCount()==1) { // deleting last recording, hide this view
			noRecordingsLayout.setVisibility(View.VISIBLE);
		}
		Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();
		int deleted = OxContentProvider.deleteRecording(getActivity(), recording.getUri());
		Log.d("OximeterRecording", "deleted?"+deleted);
	}
	
	public static final String finishedRecordings = OxSQLiteHelper.COLUMN_END+">0";
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),OxContentProvider.RECORDINGS_URI, Recording.projection, finishedRecordings, null, null);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		recordingsAdapter.changeCursor(cursor);
		if(cursor.getCount()>0) {
			noRecordingsLayout.setVisibility(View.GONE);
			haveRecordingsLayout.setVisibility(View.VISIBLE);
		} else {
			noRecordingsLayout.setVisibility(View.VISIBLE);
			haveRecordingsLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		recordingsAdapter.changeCursor(null);
	}
	public class RecordingsAdapter extends SimpleCursorAdapter {

		public RecordingsAdapter(Context context, Cursor cursor) {
			super(context, R.layout.recording_row, cursor, Recording.projection, new int[]{android.R.id.text1}, 0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Recording recording = new Recording(cursor);
			view.setTag(recording);
			TextView text = (TextView) view.findViewById(R.id.text1);
			text.setText(recording.getListTitle());
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
	}
	static SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
}