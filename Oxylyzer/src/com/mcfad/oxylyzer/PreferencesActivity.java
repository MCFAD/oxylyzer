package com.mcfad.oxylyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.Recording;

public class PreferencesActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		Preference importPref = (Preference) findPreference("import");
		importPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				selectFileToImport();
				return false;
			}
		});
		Preference clearProfilePref = (Preference) findPreference("clear_profile");
		clearProfilePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				clearProfile();
				return false;
			}
		});
		Preference clearRecordingsPref = (Preference) findPreference("clear_recordings");
		clearRecordingsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				clearRecordings();
				return false;
			}
		});
	}
	protected void clearRecordings() {
		getContentResolver().delete(OxContentProvider.RECORDINGS_URI, null, null);
	}
	protected void clearProfile() {
		getSharedPreferences("Profile", 0).edit().clear().commit();
		getSharedPreferences("Questionnaire", 0).edit().clear().commit();
		getSharedPreferences("Answers", 0).edit().clear().commit();
	}
	FilenameFilter csvFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			return lowercaseName.endsWith(".csv");
		}
	};
	File downloadsDir;
	public void selectFileToImport() {
		downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		if(downloadsDir==null){
			downloadsDir = Environment.getExternalStorageDirectory();
			if(downloadsDir==null){
				Toast.makeText(this, "Could not access device storage", Toast.LENGTH_LONG).show();
				return;
			}
		}

		final String[] downloads = downloadsDir.list(csvFilter);
		if(downloads.length==0){
			Toast.makeText(this, "No .csv files found in storage", Toast.LENGTH_LONG).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a recording to import");

		ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, downloads);
		builder.setAdapter(fileAdapter,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				importRecording(downloads[which]);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	public void importRecording(String file){
		file = downloadsDir.getAbsolutePath()+"/"+file;
		try {
			Recording.importFromFile(PreferencesActivity.this, file);
		} catch (FileNotFoundException e) {
			Toast.makeText(PreferencesActivity.this, "Couldn't find file: "+file, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(PreferencesActivity.this, "Error reading: "+file, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
