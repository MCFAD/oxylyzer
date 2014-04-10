package com.mcfad.oxylyzer;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuestionnaireForm3 extends Activity {

	private int score;
	private boolean isFilled;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionnaire_form3);

		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		final SharedPreferences.Editor editor = questionnaire.edit();
	
		isFilled = false;
		
		Button button = (Button) findViewById(R.id.saveQuestionnaire3);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				

				
				RadioGroup question1 = (RadioGroup) findViewById(R.id.question3_1);
				int selectedId = question1.getCheckedRadioButtonId();
				RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
				int yesOrNo = 0;
				if(radioSexButton.getText().toString().equals("Yes")){
					yesOrNo = 1;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q1", yesOrNo);

				RadioGroup question2 = (RadioGroup) findViewById(R.id.question3_2);
				selectedId = question2.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("Louder than talking")
						|| radioSexButton.getText().toString().equals("Very loud – can be heard in adjacent rooms") ){
					yesOrNo = 1;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q2", yesOrNo);

				RadioGroup question3 = (RadioGroup) findViewById(R.id.question3_3);
				selectedId = question3.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("Nearly every day")
						|| 
						radioSexButton.getText().toString().equals("3-4 times a week")	){
					yesOrNo = 1;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q3", yesOrNo);

				RadioGroup question4 = (RadioGroup) findViewById(R.id.question3_4);
				selectedId = question4.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("Yes")){
					yesOrNo = 1;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q4", yesOrNo);
				
				RadioGroup question5 = (RadioGroup) findViewById(R.id.question3_5);
				selectedId = question5.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				yesOrNo = 0;
				if(radioSexButton.getText().toString().equals("Nearly every day")
						||
					radioSexButton.getText().toString().equals("3-4 times a week")
						){
					yesOrNo = 2;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q5", yesOrNo);

				RadioGroup question6 = (RadioGroup) findViewById(R.id.question3_6);
				selectedId = question6.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("Nearly every day")
						||
				radioSexButton.getText().toString().equals("3-4 times a week")	){
					yesOrNo = 1;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q6", yesOrNo);

				RadioGroup question7 = (RadioGroup) findViewById(R.id.question3_7);
				selectedId = question7.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("Nearly every day")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("3-4 times a week")){
					yesOrNo = 1;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q7", yesOrNo);

				RadioGroup question8 = (RadioGroup) findViewById(R.id.question3_8);
				selectedId = question8.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("Yes")){
					yesOrNo = 0;
				}
				score+=yesOrNo;
				editor.putInt("Q3Q8", yesOrNo);
				
				editor.putInt("Q3Score",score);
				System.out.println("score: "+score);
				editor.putBoolean("Answers3Saved", true);
				editor.commit();
				
				isFilled = true;
				score = 0;
				finish();
				
				
			}
		});
		
	}

	
	public boolean isFilled() { return isFilled; }
	
	public int getScore() { return score; }
	
}


