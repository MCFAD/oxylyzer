package com.mcfad.oxylyzer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mcfad.oxylyzer.MainActivity.GraphFragment;
import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.OxSQLiteHelper;

public class HistorySectionFragment extends GraphFragment {
	String dateStringStart;
	//ArrayList<String> dateList;
	ArrayAdapter<String> adapter3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_calendar, container, false);

		final Activity activity = getActivity();
		Spinner spinner = (Spinner) rootView.findViewById(R.id.oxi_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
				R.array.oxi_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		Spinner spinner2 = (Spinner) rootView.findViewById(R.id.pulse_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(activity,
				R.array.pulse_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner2.setAdapter(adapter2);

		Button button = (Button) rootView.findViewById(R.id.button_start);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button click
				// Do something in response to button click
				if( dateStringStart==null)
				{
					long date = System.currentTimeMillis(); 
					SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
					dateStringStart = sdf.format(date);
				}

			}
		});

		//dateList = new ArrayList<String>();

		Button button2 = (Button) rootView.findViewById(R.id.button_stop);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v2) {

				long date = System.currentTimeMillis(); 
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
				String dateStringFinish = sdf.format(date);
				if(dateStringStart != null)
				{
					String dateString = dateStringStart + "-" + dateStringFinish;
					//TextView time_period=(TextView)findViewById(R.id.time_period);
					//time_period.setText(dateString);

					adapter3.add(dateString);
					dateStringStart= null;
				}
			}
		});

		Spinner spinner3 = (Spinner) rootView.findViewById(R.id.records);
		// Create an ArrayAdapter using the string array and a default spinner layout
		adapter3 = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item);//.createFromResource(this,

		//        R.array.records_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner3.setAdapter(adapter3);


		Button button4 = (Button) rootView.findViewById(R.id.button_view_report);
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {


				SharedPreferences settings = activity.getSharedPreferences("Profile", 0);
				/*settings.edit().clear();//for checking with empty profile
				settings.edit().commit();
				settings = activity.getSharedPreferences("Profile", 0);*/
				boolean profile = settings.getBoolean("ProfileSaved", false);
				if(!profile)
				{
					Intent intent = new Intent(activity, NewProfileActivity.class);
					startActivityForResult(intent, 0);
				}
				else
				{
					Intent intent2 = new Intent(activity, Report.class);
					HistorySectionFragment.this.startActivity(intent2);
				}
			}
		});
		Cursor recordingsCursor = activity.getContentResolver().query(OxContentProvider.RECORDINGS_URI, null, null, null, null);
		CursorAdapter recordingsAdapter = new SimpleCursorAdapter(activity, android.R.layout.simple_spinner_item, 
				recordingsCursor,new String[]{OxSQLiteHelper.COLUMN_TIME}, new int[]{android.R.id.text1},0);
		spinner3.setAdapter(recordingsAdapter);

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Intent intent2 = new Intent(getActivity(), Report.class);
		HistorySectionFragment.this.startActivity(intent2);
	}

}