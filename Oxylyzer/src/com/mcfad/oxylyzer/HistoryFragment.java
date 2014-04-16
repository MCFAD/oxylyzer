package com.mcfad.oxylyzer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
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

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.mcfad.oxylyzer.MainActivity.GraphFragment;
import com.mcfad.oxylyzer.db.DataPoint;
import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.OxSQLiteHelper;
import com.mcfad.oxylyzer.db.Recording;
import com.mcfad.oxylyzer.diagnosis.MedicalQuestionnaire;
import com.mcfad.oxylyzer.diagnosis.NewProfileActivity;
import com.mcfad.oxylyzer.diagnosis.Report;

public class HistoryFragment extends GraphFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	View rootView;
	// view to hide after there are recordings found
	View noRecordingsLayout;
	
	Spinner recordingsSpinner;
	CursorAdapter recordingsAdapter;
	EditText recordingDescription;
	private GraphViewSeries apnea;
	private java.util.ArrayList<DataPoint> array;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_history, container, false);

		noRecordingsLayout = (View) rootView.findViewById(R.id.no_recordings);
		
		recordingsSpinner = (Spinner) rootView.findViewById(R.id.records);
		recordingsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();

				Cursor dataCursor = recording.queryDatapoints(getActivity());
				Log.d("REC", 
						"start: "+recording.startTime+" end: "+recording.endTime+", "
						+ (recording.endTime-recording.startTime)/1000+" seconds, "
						+ "desc: "+recording.description+", #points: "+dataCursor.getCount());
				dataCursor.close();

				recordingDescription.setText(recording.description);
				updateGraph();
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
		Button questionnaireButton = (Button) rootView.findViewById(R.id.button_questionnaire);
		questionnaireButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				Intent intent = new Intent(getActivity(), MedicalQuestionnaire.class);
				startActivityForResult(intent, 0);
			}
		});
		recordingDescription = (EditText) rootView.findViewById(R.id.description);

		setupGraph();

		getActivity().getSupportLoaderManager().restartLoader(0, null, this);

		array = new java.util.ArrayList<DataPoint>();
		//This array stores only one event of apnea, it the array is cleared out one user go back to normal state
		
		
		return rootView;
	}
	public void setupGraph(){
		graphView = new LineGraphView(this.getActivity(), "");
		
		graphView.setBackgroundColor(Color.LTGRAY);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
		//graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setTextSize(15.5f);
		graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		graphView.setCustomLabelFormatter(new RealtimeFragment.LabelFormatter());
		//graphView.setShowLegend(true);

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph1);
		layout.addView(graphView);
	}
	public void updateGraph(){
		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		bpm = new GraphViewSeries(new GraphViewData[] {});
		graphView.removeAllSeries();
		
		Recording recording = (Recording)recordingsSpinner.getSelectedView().getTag();
		Cursor dataCursor = recording.queryDatapoints(getActivity());
		int apneaStartTime = Integer.MAX_VALUE;
		int baseLine = 98;//Set this to baseLine If baseline is available
		int apneaClassificationTime = 10;  //change this to a lower value if you want to see a premature apnea event
		int previousSPO2 = 0;
		DataPoint previousDataPoint = null;
		while(dataCursor.moveToNext()){
			DataPoint dataPoint = new DataPoint(dataCursor);
			
			if(dataPoint.spo2 >= 127)//skip all the invalid data point
				continue;
			
			Date date = new Date(dataPoint.time);
			int currentTime = date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
			spo2.appendData(new GraphViewData(currentTime, dataPoint.spo2), false);
			bpm.appendData(new GraphViewData(currentTime, dataPoint.bpm), false);
			
			if(dataPoint.spo2 < previousSPO2 && baseLine-previousSPO2 >= 3)
			{
				array.add(previousDataPoint);
			}
			else
			{
				
				if(currentTime - apneaStartTime > apneaClassificationTime && !array.isEmpty())
				{
					array.add(previousDataPoint); //the last datapoint of an apnea event
					apnea = new GraphViewSeries(new GraphViewData[] {});
					apnea.getStyle().color = Color.MAGENTA;
					apnea.getStyle().thickness = 10;
					graphView.addSeries(apnea); //indicates where the apnea period.
					for(int i = 0; i < array.size(); i++)
					{
						Date d = new Date(array.get(i).time);
						int thisTime = d.getHours()*3600+d.getMinutes()*60+d.getSeconds();
						int s = array.get(i).spo2;
						apnea.appendData(new GraphViewData(thisTime, s), false);
					}
					
				}
				
				//reset the apnea data point and time after highlighting the apnea event
				array.clear();
				apneaStartTime = currentTime; 
			
			}
			previousDataPoint = dataPoint;
			previousSPO2 = dataPoint.spo2;
			
		}
		graphView.addSeries(spo2); // oxygen level
		spo2.getStyle().color = Color.BLUE;
		graphView.addSeries(bpm); // beats per minutes
		bpm.getStyle().color = Color.RED;
		graphView.setScrollable(true);
		graphView.setScalable(true);
		//graphView.setFocusable(true);
		graphView.redrawAll();
	}
	
	private class thisdata
	{
		public thisdata(long t, int o2, int bpm)
		{
			time = t;
			spo2 = o2;
			this.bpm = bpm;
		}
		public long time;
		public int spo2;
		public int bpm;
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
	public void viewReport(){
		SharedPreferences settings = getActivity().getSharedPreferences("Profile", 0);

		boolean profile = settings.getBoolean("ProfileSaved", false);
		//boolean profile = false;
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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Intent intent2 = new Intent(getActivity(), Report.class);
		HistoryFragment.this.startActivity(intent2);
	}

	String finishedRecordings = OxSQLiteHelper.COLUMN_END+">0";
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),OxContentProvider.RECORDINGS_URI, Recording.projection, finishedRecordings, null, null);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		recordingsAdapter.changeCursor(cursor);
		//if(cursor.getCount()>0) {
			noRecordingsLayout.setVisibility(View.GONE);
		//} else {
		//	noRecordingsLayout.setVisibility(View.VISIBLE);
		//}
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