package com.mcfad.oxylyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import com.mcfad.oxylyzer.db.Recording;
import com.mcfad.oxylyzer.diagnosis.NewProfileActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.Toast;

public class Prefs extends PreferenceActivity{

	Preference importPreference;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);


		importPreference = (Preference) findPreference("import");
		importPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				selectFileToImport();
				return false;
			}
		});

		Preference editActivityPreference = (Preference) findPreference("editProfile");
		editActivityPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Prefs.this, NewProfileActivity.class);
				startActivityForResult(intent, 0);
				return false;
			}
		});
	}
	FilenameFilter csvFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			return lowercaseName.endsWith(".csv");
		}
	};
	File downloadsDir;
	public void selectFileToImport() {
		//downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
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
			Recording.importFromFile(Prefs.this, file);
		} catch (FileNotFoundException e) {
			Toast.makeText(Prefs.this, "Couldn't find file: "+file, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(Prefs.this, "Error reading: "+file, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
