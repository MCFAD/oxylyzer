package com.mcfad.oxylyzer;

import com.mcfad.oxylyzer.diagnosis.NewProfileActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;

public class Prefs extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		Preference editActivityPreference = (Preference) findPreference("editProfile");
		editActivityPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Prefs.this, NewProfileActivity.class);
				startActivityForResult(intent, 0);
				return false;
			}
		});
		
	}
	

}
