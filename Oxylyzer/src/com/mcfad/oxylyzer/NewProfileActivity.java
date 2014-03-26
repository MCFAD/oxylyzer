package com.mcfad.oxylyzer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
public class NewProfileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile_actvity);



		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences("Profile", 0);
		final SharedPreferences.Editor editor = settings.edit();


		EditText text1 = (EditText) findViewById(R.id.FirstName);
		editor.putString("FirstName", text1.getText().toString());
		
		EditText text2 = (EditText) findViewById(R.id.gender);
		editor.putString("FirstName", text1.getText().toString());
		
		EditText text3 = (EditText) findViewById(R.id.age);
		editor.putString("FirstName", text1.getText().toString());
		
		EditText text4 = (EditText) findViewById(R.id.height);
		editor.putString("FirstName", text1.getText().toString());
		
		EditText text5 = (EditText) findViewById(R.id.weight);
		editor.putString("FirstName", text1.getText().toString());

		Button button3 = (Button) findViewById(R.id.button_save);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				EditText text1 = (EditText) findViewById(R.id.FirstName);
				editor.putString("FirstName", text1.getText().toString());
				
				editor.putBoolean("ProfileSaved", true);

				// Commit the edits!
				editor.commit();
				finish();
			}
		});




	}
}
