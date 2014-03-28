package com.mcfad.oxylyzer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
public class NewProfileActivity extends Activity {

	//ArrayAdapter<String> adaptergender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile_actvity);



		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		final SharedPreferences settings = getSharedPreferences("Profile", 0);
		final SharedPreferences.Editor editor = settings.edit();

		Spinner spinnergender = (Spinner) findViewById(R.id.gender_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adaptergender = ArrayAdapter.createFromResource(NewProfileActivity.this,
				R.array.gender_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adaptergender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnergender.setAdapter(adaptergender);
		
		Spinner spinnermeasure = (Spinner) findViewById(R.id.measure_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adaptermeasure = ArrayAdapter.createFromResource(NewProfileActivity.this,
				R.array.measure_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adaptermeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnermeasure.setAdapter(adaptermeasure);
		
		

		Button button3 = (Button) findViewById(R.id.button_save);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				
				Spinner spinnergender = (Spinner) findViewById(R.id.gender_spinner);
				Spinner spinnermeasure = (Spinner) findViewById(R.id.measure_spinner);
				
				
				
				EditText text1 = (EditText) findViewById(R.id.FirstName);
				EditText text3 = (EditText) findViewById(R.id.age);
				EditText text4 = (EditText) findViewById(R.id.height);
				EditText text5 = (EditText) findViewById(R.id.weight);
				EditText text6 = (EditText) findViewById(R.id.neck);				


				if(text1.getText().length()==0 || text3.getText().length()==0 ||text4.getText().length()==0|| text5.getText().length()==0 || text6.getText().length()==0 )
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
				if( Integer.parseInt(text3.getText().toString())  > 150 )
				{
					text3.setError( "Please enter a realistic age" );
					return;
				}
				if( Integer.parseInt(text3.getText().toString())  > 1000 )
				{
					text4.setError( "Please enter a realistic height" );
					return;
				}
				if( Integer.parseInt(text3.getText().toString())  > 1000 )
				{
					text5.setError( "Please enter a realistic weight" );
					return;
				}
				if( Integer.parseInt(text3.getText().toString())  > 1000 )
				{
					text6.setError( "Please enter a realistic neck circumference" );
					return;
				}


				editor.putInt("Height", Integer.parseInt(text4.getText().toString()));
				editor.putInt("Age", Integer.parseInt(text3.getText().toString()));
				editor.putString("Gender", spinnergender.getSelectedItem().toString());
				editor.putBoolean("Metric", spinnermeasure.getSelectedItemPosition()== 0);
				editor.putString("FirstName", text1.getText().toString());
				editor.putInt("Weight", Integer.parseInt(text5.getText().toString()));
				editor.putInt("Neck", Integer.parseInt(text6.getText().toString()));

				editor.putBoolean("ProfileSaved", true);

				Context context = getApplicationContext();
				CharSequence text = "Profile Successfully Created!";
				int duration = Toast.LENGTH_SHORT;

				Toast profile_created = Toast.makeText(context, text, duration);
				profile_created.show();

				// Commit the edits!
				editor.commit();
				
				finish();
			}
		});




	}
}
