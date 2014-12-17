package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class Questionnaire3Berlin extends QuestionnaireForm {

	Question[] questions = new Question[]{
			new Question("Q3Q1",R.id.question3_1),
			new Question("Q3Q2",R.id.question3_2),
			new Question("Q3Q3",R.id.question3_3),
			new Question("Q3Q4",R.id.question3_4),
			new Question("Q3Q5",R.id.question3_5,2),
			new Question("Q3Q6",R.id.question3_6),
			new Question("Q3Q7",R.id.question3_7),
			new Question("Q3Q8",R.id.question3_8)
			};

	@Override
	public int getLayoutId(){
		return R.layout.activity_questionnaire_3_berlin;
	}
	
	@Override
	public void onSave() {
		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		SharedPreferences profile = getSharedPreferences("Profile", 0);
		SharedPreferences.Editor editor = questionnaire.edit();
	
		int score = 0;
		int cat1Score = 0, cat2Score = 0, cat3Score = 0;
		
		for(Question question:questions){
			int buttonScore = getRadioButtonTrueFalse(question.radioGroup)?question.scoreMultiplier:0;
			switch(getRadioGroupCategory(question.radioGroup)){
			case 1:
				cat1Score+=buttonScore;
				break;
			case 2:
				cat2Score+=buttonScore;
				break;
			case 3:
				cat3Score+=buttonScore;
				break;
			}
			editor.putInt(question.pref, buttonScore);
			score += buttonScore;
		}
		float BMI = profile.getFloat("BMI", 0);
		if(cat3Score == 0 && BMI >30 )
			cat3Score = 1;
		
		int risk = 0;
		
		if(cat1Score>=2)
			risk++;
		if(cat2Score>=2)
			risk++;
		if(cat3Score>0)
			risk++;
		
		
		editor.putString("Q3Result", getResult(risk));
		
		editor.putInt("Q3Score",score);
		System.out.println("score: "+score);
		editor.putBoolean("Answers3Saved", true);
		editor.commit();

		finish();
	}

	public static String getResult(int risk) {	
		if(risk>=2)
			return "You have a High Risk of OSA. Please consider seeking medical attention.";
		else 
			return "You have a low risk of OSA. Please consider filling in the other questionnaires to be sure";
	}
}


