package com.mcfad.oxylyzer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcfad.oxylyzer.MainActivity.GraphFragment;
import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.OxSQLiteHelper;

public class HistoryFragment extends GraphFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	Spinner recordsSpinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);

		final MainActivity activity = (MainActivity) getActivity();

		Button button = (Button) rootView.findViewById(R.id.button_start);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				OxContentProvider.startNewRecording(activity);
			}
		});

		recordsSpinner = (Spinner) rootView.findViewById(R.id.records);
		
		Button reportButton = (Button) rootView.findViewById(R.id.button_view_report);
		reportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				SharedPreferences settings = activity.getSharedPreferences("Profile", 0);
				
				//boolean profile = settings.getBoolean("ProfileSaved", false);
				boolean profile = false;
				if(!profile)
				{
					Intent intent = new Intent(activity, NewProfileActivity.class);
					startActivityForResult(intent, 0);
				}
				else
				{
					Intent intent2 = new Intent(activity, Report.class);
					HistoryFragment.this.startActivity(intent2);
				}
			}
		});

		activity.getSupportLoaderManager().initLoader(0, null, this);

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Intent intent2 = new Intent(getActivity(), Report.class);
		HistoryFragment.this.startActivity(intent2);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),OxContentProvider.RECORDINGS_URI, null, null, null, null);
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		CursorAdapter recordingsAdapter = new RecordingsAdapter(getActivity(), android.R.layout.simple_spinner_item, 
				data,new String[]{OxSQLiteHelper.COLUMN_TIME}, new int[]{android.R.id.text1});
		recordsSpinner.setAdapter(recordingsAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	public class RecordingsAdapter extends SimpleCursorAdapter {

		public RecordingsAdapter(Context context, int layout, Cursor cursor,String[] from, int[] to) {
			super(context, layout, cursor, from, to, 0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView text = (TextView) view.findViewById(android.R.id.text1);
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
			String dateStringStart = sdf.format(cursor.getLong(1));
			text.setText(dateStringStart);
		}
	}
}