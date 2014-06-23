package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class Questionnaire1STOPBANG extends QuestionnaireForm {

	Question[] questions = new Question[]{
			new Question("Q3Q1",R.id.question1),
			new Question("Q3Q2",R.id.question2),
			new Question("Q3Q3",R.id.question3),
			new Question("Q3Q4",R.id.question4)
	};

	@Override
	public int getLayoutId() {
		return R.layout.activity_questionnaire_1_stopbang;
	}
	
	@Override
	public void onSave() {
		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		SharedPreferences.Editor editor = questionnaire.edit();

		int score = 0;
		for(Question question:questions){
			int buttonScore = getRadioButtonTrueFalse(question.radioGroup)?1:0;
			editor.putInt(question.pref, buttonScore);
			score += buttonScore;
		}
			
		editor.putString("Q1Result", getResult(score));
		editor.putInt("Q1Score",score);
		System.out.println("score: "+score);
		editor.putBoolean("Answers1Saved", true);
		editor.commit();

		finish();
	}

	public static String getResult(int score) {		
		if(score>2)
			return "You have a high risk of OSA (Obstructive Sleep Apna). Please consider medical attention.";
		else
			return "You have a low risk of OSA (Obstructive Sleep Apna)";
	}
}


