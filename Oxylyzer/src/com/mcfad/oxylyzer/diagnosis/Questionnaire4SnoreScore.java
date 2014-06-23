package com.mcfad.oxylyzer.diagnosis;

import android.content.SharedPreferences;

import com.mcfad.oxylyzer.R;

public class Questionnaire4SnoreScore extends QuestionnaireForm {

	Question[] questions = new Question[]{
			new Question("Q4Q1",R.id.question4_1),
			new Question("Q4Q2",R.id.question4_2),
			new Question("Q4Q3",R.id.question4_3),
			new Question("Q4Q4",R.id.question4_4),
			new Question("Q4Q5",R.id.question4_5),
			new Question("Q4Q6",R.id.question4_6),
			new Question("Q4Q7",R.id.question4_7),
			new Question("Q4Q8",R.id.question4_8),
			new Question("Q4Q9",R.id.question4_9),
			new Question("Q4Q10",R.id.question4_10)
	};

	@Override
	public int getLayoutId() {
		return R.layout.activity_questionnaire_4_snore_score;
	}

	@Override
	public void onSave() {

		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		SharedPreferences.Editor editor = questionnaire.edit();
		
		boolean personalLife = false;
		boolean haveApnea = false;
		boolean backSleeper = false;
		boolean physObstruct = false;
		int score = 0;
		for(Question question:questions){
			int buttonScore = getRadioButtonNumber(question.radioGroup);
			editor.putInt(question.pref, buttonScore);
			if(buttonScore>=3){
				score ++;

				if(question.radioGroup==R.id.question4_1||
						question.radioGroup==R.id.question4_2||
						question.radioGroup==R.id.question4_3||
						question.radioGroup==R.id.question4_4){
					personalLife = true;
				}
				if(question.radioGroup==R.id.question4_4||
						question.radioGroup==R.id.question4_7||
						question.radioGroup==R.id.question4_8){
					haveApnea = true;
				}

				if(question.radioGroup==R.id.question4_5){
					backSleeper = true;
				}
				if(question.radioGroup==R.id.question4_6&&buttonScore>=4){
					physObstruct = true;
				}
			}
		}
		
		editor.putString("Q4Result", getResult(score,personalLife,haveApnea,backSleeper,physObstruct));
		editor.putBoolean("Answers4Saved", true);
		editor.commit();

		finish();
	}
	
	public String getResult(int score,boolean personalLife,boolean haveApnea,boolean backSleeper,boolean physObstruct) {
		String result = "";
		if(personalLife == true && score < 10 ) {
			result = "Snoring almost certainly interferes with your personal life.";
		}
		
		if(haveApnea == true && score < 10 ) {
			if(result.length()>0) result += "\n\n";
			result += "There is a moderate chance you experience apnea during sleep. You should consult with your physician.";
		}
		if(backSleeper == true && score < 10 ) {
			if(result.length()>0) result += "\n\n";
			result += "It is likely that your snoring is probably due to the effect of gravity on the tissues of your upper airway rather than to any basic anatomical obstruction. You are what is called a \"positional snorer.\" Remedies that encourage you to sleep on your side or stomach may be sufficient.";
		}
		if(physObstruct == true && score < 10 ) {
			if(result.length()>0) result += "\n\n";
			result += "Your snoring is most likely the result of a physical obstruction.";
		}
		if(score== 10) {
			if(result.length()>0) result += "\n\n";
			result += "It is very likely you have sleep apnea, you should consult and have a sleep study performed to evaluate your sleep and snoring.";
		}
		if(!physObstruct && !backSleeper && haveApnea && score<10) {
			if(result.length()>0) result += "\n\n";
			result = "You appear to be a healthy sleeper, please do another questionairre to be further assessed.";
		}
		
		return result;
	}
}


