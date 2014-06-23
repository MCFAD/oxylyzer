package com.mcfad.oxylyzer.diagnosis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mcfad.oxylyzer.R;

public class NewProfileActivity extends Activity {

	protected int spinnerGenderIndex;
	protected int spinnerMetricIndex;

	Spinner genderSpinner;
	Spinner unitsSpinner;

	EditText nameText;
	EditText ageText;
	EditText heightText;
	EditText weightText;
	EditText neckText;

	TextView heightTitle;
	TextView weightTitle;
	TextView neckTitle;

	SharedPreferences settings;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile_actvity);

		settings = getSharedPreferences("Profile", 0);
		editor = settings.edit();

		genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
		unitsSpinner = (Spinner) findViewById(R.id.measure_spinner);

		nameText = (EditText) findViewById(R.id.FirstName);
		ageText = (EditText) findViewById(R.id.age);
		heightText = (EditText) findViewById(R.id.height);
		weightText = (EditText) findViewById(R.id.weight);
		neckText = (EditText) findViewById(R.id.neck);

		heightTitle = (TextView) findViewById(R.id.height_title);
		weightTitle = (TextView) findViewById(R.id.weight_title);
		neckTitle = (TextView) findViewById(R.id.neck_title);

		ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(NewProfileActivity.this,
				R.array.gender_array, android.R.layout.simple_spinner_item);
		genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(genderAdapter);

		ArrayAdapter<CharSequence> unitsAdapter = ArrayAdapter.createFromResource(NewProfileActivity.this,
				R.array.measure_array, android.R.layout.simple_spinner_item);
		unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitsSpinner.setAdapter(unitsAdapter);
		unitsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateUnits();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		Button saveButton = (Button) findViewById(R.id.button_save);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {


			}
		});

		nameText.setText(settings.getString("FirstName", null));
		ageText.setText(""+settings.getInt("Age", 0));
		genderSpinner.setSelection(settings.getInt("spinnerGenderIndex",0));
		unitsSpinner.setSelection(settings.getBoolean("isMetric",true)?0:1);

		updateUnits();
		heightText.setText(""+settings.getInt("Height",0));
		weightText.setText(""+settings.getInt("Weight",0));
		neckText.setText(""+settings.getInt("Neck",0));
	}
	public void save() {

		if(nameText.getText().length()==0 || ageText.getText().length()==0 ||
				heightText.getText().length()==0|| weightText.getText().length()==0 || 
				neckText.getText().length()==0 )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(NewProfileActivity.this); //Read Update

			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button
				}
			});
			builder.setMessage("Please fill out all the fields");
			builder.show();
			return;
		}//Want to have a reminder come on the screen if no input to fields
		if( Integer.parseInt(ageText.getText().toString())  > 150 )
		{
			ageText.setError( "Please enter a realistic age" );
			return;
		}
		if( Integer.parseInt(heightText.getText().toString())  > 1000 )
		{
			heightText.setError( "Please enter a realistic height" );
			return;
		}
		if( Integer.parseInt(weightText.getText().toString())  > 1000 )
		{
			weightText.setError( "Please enter a realistic weight" );
			return;
		}
		if( Integer.parseInt(neckText.getText().toString())  > 1000 )
		{
			neckText.setError( "Please enter a realistic neck circumference" );
			return;
		}

		boolean metric = unitsSpinner.getSelectedItemPosition()==0;
		int height = Integer.parseInt(heightText.getText().toString());
		float weight = Integer.parseInt(weightText.getText().toString());
		int neck = Integer.parseInt(neckText.getText().toString());

		editor.putString("FirstName", nameText.getText().toString());
		editor.putInt("Age", Integer.parseInt(ageText.getText().toString()));
		editor.putString("Gender", genderSpinner.getSelectedItem().toString());
		editor.putBoolean("Metric", metric);
		editor.putInt("Height", height);
		editor.putFloat("Weight", weight);
		editor.putInt("Neck", neck);

		editor.putBoolean("ProfileSaved", true);

		Context context = getApplicationContext();
		CharSequence text = "Profile Successfully Created!";
		int duration = Toast.LENGTH_SHORT;

		Toast profile_created = Toast.makeText(context, text, duration);
		profile_created.show();

		editor.putInt("spinnerGenderIndex",genderSpinner.getSelectedItemPosition());

		// Commit the edits!
		editor.commit();

		finish();
	}

	public void updateUnits() {
		boolean metric = settings.getBoolean("Metric", true);
		if(metric) {
			heightTitle.setText("Height (cm)");
			weightTitle.setText("Weight (kg)");
			neckTitle.setText("Neck Circumference (cm)");
		} else {
			heightTitle.setText("Height (inches)");
			weightTitle.setText("Weight (lbs)");
			neckTitle.setText("Neck Circumference (inches)");
		}
	}
}
