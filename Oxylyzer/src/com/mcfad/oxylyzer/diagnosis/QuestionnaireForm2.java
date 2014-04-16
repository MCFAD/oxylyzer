package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class QuestionnaireForm2 extends QuestionnaireForm {

	Question[] questions = new Question[]{
			new Question("Q3Q1",R.id.question2_1),
			new Question("Q3Q2",R.id.question2_2),
			new Question("Q3Q3",R.id.question2_3),
			new Question("Q3Q4",R.id.question2_4),
			new Question("Q3Q5",R.id.question2_5),
			new Question("Q3Q6",R.id.question2_6),
			new Question("Q3Q7",R.id.question2_7),
			new Question("Q3Q8",R.id.question2_8)
			};
	
	@Override
	public int getLayoutId(){
		return R.layout.activity_questionnaire_form2;
	}
	
	@Override
	public void onSave() {
		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		SharedPreferences.Editor editor = questionnaire.edit();
	
		int score = 0;
		for(Question question:questions){
			int buttonScore = getRadioButtonNumber(question.radioGroup);
			editor.putInt(question.pref, buttonScore);
			score += buttonScore;
		}

		editor.putInt("Q2Score",score);
		System.out.println("score: "+score);
		editor.putBoolean("Answers2Saved", true);
		editor.commit();

		finish();
	}
}


