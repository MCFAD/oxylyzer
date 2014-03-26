package com.mcfad.oxylyzer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class NewProfileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile_actvity);



		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences("Profile", 0);
		final SharedPreferences.Editor editor = settings.edit();




		Button button3 = (Button) findViewById(R.id.button_save);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				EditText text1 = (EditText) findViewById(R.id.FirstName);
				EditText text2 = (EditText) findViewById(R.id.gender);
				EditText text3 = (EditText) findViewById(R.id.age);
				EditText text4 = (EditText) findViewById(R.id.height);
				EditText text5 = (EditText) findViewById(R.id.weight);


				if(text1.getText().length()==0 || text2.getText().length()==0  || text3.getText().length()==0 ||text4.getText().length()==0|| text5.getText().length()==0 )
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


				editor.putInt("Height", Integer.parseInt(text4.getText().toString()));
				editor.putString("Age", text3.getText().toString());
				editor.putString("Gender", text2.getText().toString());
				editor.putString("FirstName", text1.getText().toString());
				editor.putInt("Weight", Integer.parseInt(text5.getText().toString()));

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
