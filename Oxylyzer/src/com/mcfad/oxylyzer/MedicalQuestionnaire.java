package com.mcfad.oxylyzer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
		
		
		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);		
		int Q1Score = questionnaire.getInt("Q1Score", 0);
		
		TextView score1 = (TextView) findViewById(R.id.Score1Display);
		score1.setText(String.valueOf(Q1Score));
		
		int Q2Score = questionnaire.getInt("Q2Score", 0);
		TextView score2 = (TextView) findViewById(R.id.Score2Display);
		score2.setText(String.valueOf(Q2Score));
		
		int Q3Score = questionnaire.getInt("Q3Score", 0);
		TextView score3 = (TextView) findViewById(R.id.Score3Display);
		score2.setText(String.valueOf(Q3Score));
		
		/*Button questionnaire2Button = (Button) findViewById(R.id.button_questionnaire2);
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
		});*/
		
		}
	public void viewQuestionnaire1(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);

		boolean question1 = questionnaires.getBoolean("Answers1Saved", false);
		//boolean profile = false;
		if(!question1)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm1.class);
			startActivityForResult(intent, 0);
		}
		

	}
	
	public void viewQuestionnaire2(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);

		boolean question2 = questionnaires.getBoolean("Answers2Saved", false);
		//boolean profile = false;
		if(!question2)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm2.class);
			startActivityForResult(intent, 0);
		}
	}

	public void viewQuestionnaire3(){
		SharedPreferences questionnaires = getSharedPreferences("Answers", 0);

		boolean question3 = questionnaires.getBoolean("Answers3Saved", false);
		//boolean profile = false;
		if(!question3)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm3.class);
			startActivityForResult(intent, 0);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);		
		int Q1Score = questionnaire.getInt("Q1Score", 0);
		TextView score1 = (TextView) findViewById(R.id.Score1Display);
		score1.setText(String.valueOf(Q1Score));
		
		int Q2score = questionnaire.getInt("Q2Score", 0);
		TextView score2 = (TextView) findViewById(R.id.Score2Display);
		score2.setText(String.valueOf(Q2score));
		
		int Q3score = questionnaire.getInt("Q3Score", 0);
		TextView score3 = (TextView) findViewById(R.id.Score3Display);
		score3.setText(String.valueOf(Q3score));
		
	}
	
	/*public void viewQuestionnaire2(){
		SharedPreferences questionnaires = getSharedPreferences("Answer1", 0);

		boolean question2 = questionnaires.getBoolean("Answers2Saved", false);
		//boolean profile = false;
		if(!question2)
		{
			Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm2.class);
			startActivityForResult(intent, 0);
		}
	}
		
		public void viewQuestionnaire3(){
			SharedPreferences questionnaires = getSharedPreferences("Answers", 0);

			boolean question3 = questionnaires.getBoolean("Answers3Saved", false);
			//boolean profile = false;
			if(!question3)
			{
				Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm3.class);
				startActivityForResult(intent, 0);
			}
		}
			
			public void viewQuestionnaire4(){
				SharedPreferences questionnaires = getSharedPreferences("Answers", 0);

				boolean question4 = questionnaires.getBoolean("Answers4Saved", false);
				//boolean profile = false;
				if(!question4)
				{
					Intent intent = new Intent(MedicalQuestionnaire.this, QuestionnaireForm4.class);
					startActivityForResult(intent, 0);
				}
			}*/
				
			
	


}
