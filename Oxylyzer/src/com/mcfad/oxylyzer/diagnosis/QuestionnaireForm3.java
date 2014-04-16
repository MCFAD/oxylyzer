package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class QuestionnaireForm3 extends QuestionnaireForm {

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
		return R.layout.activity_questionnaire_form3;
	}
	
	@Override
	public void onSave() {
		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		SharedPreferences.Editor editor = questionnaire.edit();
	
		int score = 0;
		for(Question question:questions){
			int buttonScore = getRadioButtonTrueFalse(question.radioGroup)?question.scoreMultiplier:0;
			editor.putInt(question.pref, buttonScore);
			score += buttonScore;
		}

		editor.putInt("Q3Score",score);
		System.out.println("score: "+score);
		editor.putBoolean("Answers3Saved", true);
		editor.commit();

		finish();
	}
}


