package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class Questionnaire2Epworth extends QuestionnaireForm {

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
		return R.layout.activity_questionnaire_2_epworth;
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
		
		editor.putString("Q2Result", getResult(score));
		
		editor.putInt("Q2Score",score);
		System.out.println("score: "+score);
		editor.putBoolean("Answers2Saved", true);
		editor.commit();

		finish();
	}

	public static String getResult(int score) {	
		if(score<6){
			return "Congratulations, you are getting enough sleep!";
		}
		else if(score == 7 || score == 8){
			return "Your score is average";
		}
		else if(score>=9){
			return "Very sleepy and should seek medical advice";
		}
		return null;
	}
}


