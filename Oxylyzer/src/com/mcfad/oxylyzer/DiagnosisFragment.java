package com.mcfad.oxylyzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mcfad.oxylyzer.diagnosis.NewProfileActivity;
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

	Button reportButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_diagnosis, container, false);

		profilePrefs = getActivity().getSharedPreferences("Profile", 0);
		answersPrefs = getActivity().getSharedPreferences("Answers", 0);
		questionnairePrefs = getActivity().getSharedPreferences("Questionnaire", 0);


		Button completeProfile = (Button) rootView.findViewById(R.id.button_profile);
		completeProfile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				completeProfile();
			}
		});

		Button questionnaire1Button = (Button) rootView.findViewById(R.id.button_questionnaire1);
		questionnaire1Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire1();
			}
		});

		Button questionnaire2Button = (Button) rootView.findViewById(R.id.button_questionnaire2);
		questionnaire2Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire2();
			}
		});
		Button questionnaire3Button = (Button) rootView.findViewById(R.id.button_questionnaire3);
		questionnaire3Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire3();
			}
		});

		Button questionnaire4Button = (Button) rootView.findViewById(R.id.button_questionnaire4);
		questionnaire4Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire4();
			}
		});

		reportButton = (Button) rootView.findViewById(R.id.button_view_report);
		reportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				viewReport();
			}
		});


		return rootView;
	}

	protected void completeProfile() {
		Intent intent = new Intent(getActivity(), NewProfileActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onResume(){
		super.onResume();
		updateScoreText();
		
		boolean profile = profilePrefs.getBoolean("ProfileSaved", false);
		reportButton.setEnabled(profile);
	}

	public void viewReport(){
		Intent intent2 = new Intent(getActivity(), Report.class);
		DiagnosisFragment.this.startActivity(intent2);
	}

	public void updateScoreText() {
		TextView score1 = (TextView) rootView.findViewById(R.id.Score1Display);
		TextView score2 = (TextView) rootView.findViewById(R.id.Score2Display);
		TextView score3 = (TextView) rootView.findViewById(R.id.Score3Display);
		TextView score4 = (TextView) rootView.findViewById(R.id.Score4Display);


		String Q1Result= questionnairePrefs.getString("Q1Result", "Questionaire hasn't been taken");
		String Q2Result= questionnairePrefs.getString("Q2Result", "Questionaire hasn't been taken");
		String Q3Result = questionnairePrefs.getString("Q3Result", "Questionaire hasn't been taken");
		String Q4Result = questionnairePrefs.getString("Q4Result", "Questionaire hasn't been taken");

		if(!Q1Result.equals("Questionaire hasn't been taken"))
			Q1Result = "This Questionaire has been taken";

		if(!Q2Result.equals("Questionaire hasn't been taken"))
			Q2Result = "This Questionaire has been taken";

		if(!Q3Result.equals("Questionaire hasn't been taken"))
			Q3Result = "This Questionaire has been taken";

		if(!Q4Result.equals("Questionaire hasn't been taken"))
			Q4Result = "This Questionaire has been taken";

		score1.setText(String.valueOf(Q1Result));
		score2.setText(String.valueOf(Q2Result));
		score3.setText(String.valueOf(Q3Result));
		score4.setText(String.valueOf(Q4Result));
	}

	public void viewQuestionnaire1(){
		boolean question1 = answersPrefs.getBoolean("Answers1Saved", false);
		if(!question1) {
			Intent intent = new Intent(getActivity(), QuestionnaireForm1.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire2(){
		boolean question2 = answersPrefs.getBoolean("Answers2Saved", false);
		if(!question2) {
			Intent intent = new Intent(getActivity(), QuestionnaireForm2.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire3(){
		boolean question3 = answersPrefs.getBoolean("Answers3Saved", false);
		if(!question3) {
			Intent intent = new Intent(getActivity(), QuestionnaireForm3.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire4(){
		boolean question4 = answersPrefs.getBoolean("Answers4Saved", false);
		if(!question4) {
			Intent intent = new Intent(getActivity(), QuestionnaireForm4.class);
			startActivityForResult(intent, 0);
		}
	}
}