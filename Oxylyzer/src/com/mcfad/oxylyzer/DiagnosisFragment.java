package com.mcfad.oxylyzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mcfad.oxylyzer.db.OxContentProvider;
import com.mcfad.oxylyzer.db.Recording;
import com.mcfad.oxylyzer.diagnosis.ProfileActivity;
import com.mcfad.oxylyzer.diagnosis.QuestionnaireForm1;
import com.mcfad.oxylyzer.diagnosis.QuestionnaireForm2;
import com.mcfad.oxylyzer.diagnosis.QuestionnaireForm3;
import com.mcfad.oxylyzer.diagnosis.QuestionnaireForm4;
import com.mcfad.oxylyzer.diagnosis.Report;

public class DiagnosisFragment extends Fragment {

	View rootView;

	SharedPreferences profilePrefs;
	SharedPreferences answersPrefs;
	SharedPreferences questionnairePrefs;

	Button profileButton;
	Button reportButton;
	
	CheckBox todoProfile;
	CheckBox todoQuestionnaire1;
	CheckBox todoQuestionnaire2;
	CheckBox todoQuestionnaire3;
	CheckBox todoQuestionnaire4;
	CheckBox todoBaseline;
	CheckBox todoRecording;


	int[] questionnaireIds = {
			R.id.button_questionnaire1,R.id.button_questionnaire2,
			R.id.button_questionnaire3,R.id.button_questionnaire4};
	
	static final Class[] questionnaireActivities = {
			QuestionnaireForm1.class,QuestionnaireForm2.class,
			QuestionnaireForm3.class,QuestionnaireForm4.class};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_diagnosis, container, false);

		profilePrefs = getActivity().getSharedPreferences("Profile", 0);
		answersPrefs = getActivity().getSharedPreferences("Answers", 0);
		questionnairePrefs = getActivity().getSharedPreferences("Questionnaire", 0);

		todoProfile = (CheckBox) rootView.findViewById(R.id.todo_profile);
		todoQuestionnaire1 = (CheckBox) rootView.findViewById(R.id.todo_questionnaire_1);
		todoQuestionnaire2 = (CheckBox) rootView.findViewById(R.id.todo_questionnaire_2);
		todoQuestionnaire3 = (CheckBox) rootView.findViewById(R.id.todo_questionnaire_3);
		todoQuestionnaire4 = (CheckBox) rootView.findViewById(R.id.todo_questionnaire_4);
		todoBaseline = (CheckBox) rootView.findViewById(R.id.todo_baseline);
		todoRecording = (CheckBox) rootView.findViewById(R.id.todo_recording);

		updateTodoList();
		
		profileButton = (Button) rootView.findViewById(R.id.button_profile);
		profileButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				completeProfile();
			}
		});

		for(int i=0;i<4;i++){
			final int q = i;
			Button questionnaireButton = (Button) rootView.findViewById(questionnaireIds[q]);
			questionnaireButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(getActivity(), questionnaireActivities[q]);
					startActivityForResult(intent, 0);
				}
			});
		}

		reportButton = (Button) rootView.findViewById(R.id.button_view_report);
		reportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				viewReport();
			}
		});

		return rootView;
	}

	protected void completeProfile() {
		Intent intent = new Intent(getActivity(), ProfileActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onResume(){
		super.onResume();

		if(!profileSaved()) {
			profileButton.setText("Complete Profile");
		} else {
			profileButton.setText("Edit Profile");
		}
		
		updateTodoList();
	}

	public boolean profileSaved() { return profilePrefs.getBoolean("ProfileSaved", false);}
	private void updateTodoList() {
		todoProfile.setChecked(profileSaved());
		todoQuestionnaire1.setChecked(answersPrefs.getBoolean("Answers1Saved", false));
		todoQuestionnaire2.setChecked(answersPrefs.getBoolean("Answers2Saved", false));
		todoQuestionnaire3.setChecked(answersPrefs.getBoolean("Answers3Saved", false));
		todoQuestionnaire4.setChecked(answersPrefs.getBoolean("Answers4Saved", false));
		todoBaseline.setChecked(profilePrefs.getInt("baseline", 0)!=0);
		
		Cursor recordingsCursor = getActivity().getContentResolver().query(OxContentProvider.RECORDINGS_URI, Recording.projection, HistoryFragment.finishedRecordings, null, null);
		todoRecording.setChecked(recordingsCursor.getCount()>0);
		recordingsCursor.close();
	}

	public void viewReport(){
		Intent intent2 = new Intent(getActivity(), Report.class);
		DiagnosisFragment.this.startActivity(intent2);
	}
}