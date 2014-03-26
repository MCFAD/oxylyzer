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
				editor.putString("FirstName", text1.getText().toString());
				EditText text2 = (EditText) findViewById(R.id.gender);
				editor.putString("Gender", text2.getText().toString());
				EditText text3 = (EditText) findViewById(R.id.age);
				editor.putString("Age", text3.getText().toString());
				EditText text4 = (EditText) findViewById(R.id.height);
				editor.putInt("Height", Integer.parseInt(text4.getText().toString()));
				EditText text5 = (EditText) findViewById(R.id.weight);
				editor.putInt("Weight", Integer.parseInt(text5.getText().toString()));
				
				/*if(text1 == null || text2 == null  || text3 == null  || text4 == null  || text5 == null )
				{
					 AlertDialog builder = new AlertDialog.Builder(NewProfileActivity.this).create(); //Read Update
				        builder.setMessage(R.string.reminder);
					
					builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User clicked OK button
				           }
				       });
				       AlertDialog dialog = builder.create();
				}*///Want to have a reminder come on the screen if no input to fields
				
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
