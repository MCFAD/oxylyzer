package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class QuestionnaireForm1 extends QuestionnaireForm {

	Question[] questions = new Question[]{
			new Question("Q3Q1",R.id.question1),
			new Question("Q3Q2",R.id.question2),
			new Question("Q3Q3",R.id.question3),
			new Question("Q3Q4",R.id.question4)
	};

	@Override
	public int getLayoutId() {
		return R.layout.activity_questionnaire_form1;
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
			
		if(score>2)
			editor.putString("Q1Result", "You have a high risk of OSA");
		else
			editor.putString("Q1Result", "You have a low risk of OSA");
		
		editor.putInt("Q1Score",score);
		System.out.println("score: "+score);
		editor.putBoolean("Answers1Saved", true);
		editor.commit();

		finish();
	}

}


