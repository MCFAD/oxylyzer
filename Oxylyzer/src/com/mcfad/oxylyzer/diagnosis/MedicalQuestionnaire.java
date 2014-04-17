package com.mcfad.oxylyzer.diagnosis;

import com.mcfad.oxylyzer.R;
import com.mcfad.oxylyzer.R.id;
import com.mcfad.oxylyzer.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MedicalQuestionnaire extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medical_questionnaire);

		
		Button questionnaire1Button = (Button) findViewById(R.id.button_questionnaire1);
		questionnaire1Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire1();
			}
		});

		Button questionnaire2Button = (Button) findViewById(R.id.button_questionnaire2);
		questionnaire2Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire2();
			}
		});
		Button questionnaire3Button = (Button) findViewById(R.id.button_questionnaire3);
		questionnaire3Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire3();
			}
		});

		Button questionnaire4Button = (Button) findViewById(R.id.button_questionnaire4);
		questionnaire4Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v4) {
				viewQuestionnaire4();
			}
		});
	}

	public void updateScoreText(){
		TextView score1 = (TextView) findViewById(R.id.Score1Display);
		TextView score2 = (TextView) findViewById(R.id.Score2Display);
		TextView score3 = (TextView) findViewById(R.id.Score3Display);
		TextView score4 = (TextView) findViewById(R.id.Score4Display);
		

		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);		
		int Q1Score = questionnaire.getInt("Q1Score", 0);
		int Q2score = questionnaire.getInt("Q2Score", 0);
		int Q3score = questionnaire.getInt("Q3Score", 0);
		String Q4Result = questionnaire.getString("Q4Result", "Questionaire hasn't been taken");
		
		score1.setText(String.valueOf(Q1Score));
		score2.setText(String.valueOf(Q2score));
		score3.setText(String.valueOf(Q3score));
		score4.setText(String.valueOf(Q4Result));
	}
	
	public void viewQuestionnaire1(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);
		boolean question1 = questionnaires.getBoolean("Answers1Saved", false);
		if(!question1)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm1.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire2(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);
		boolean question2 = questionnaires.getBoolean("Answers2Saved", false);
		if(!question2)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm2.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire3(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);
		boolean question3 = questionnaires.getBoolean("Answers3Saved", false);
		if(!question3)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm3.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire4(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);
		boolean question4 = questionnaires.getBoolean("Answers4Saved", false);
		if(!question4)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm4.class);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		updateScoreText();
	}
}
