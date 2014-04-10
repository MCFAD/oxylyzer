package com.mcfad.oxylyzer;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuestionnaireForm2 extends Activity {

	private int score;
	private boolean isFilled;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionnaire_form2);

		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		final SharedPreferences.Editor editor = questionnaire.edit();
	
		isFilled = false;
		
		Button button = (Button) findViewById(R.id.saveQuestionnaire2);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				

				
				RadioGroup question1 = (RadioGroup) findViewById(R.id.question2_1);
				int selectedId = question1.getCheckedRadioButtonId();
				RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
				int yesOrNo = 0;
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q1", yesOrNo);

				RadioGroup question2 = (RadioGroup) findViewById(R.id.question2_2);
				selectedId = question2.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q2", yesOrNo);

				RadioGroup question3 = (RadioGroup) findViewById(R.id.question2_3);
				selectedId = question3.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q3", yesOrNo);

				RadioGroup question4 = (RadioGroup) findViewById(R.id.question2_4);
				selectedId = question4.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q4", yesOrNo);
				
				RadioGroup question5 = (RadioGroup) findViewById(R.id.question2_5);
				selectedId = question5.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				yesOrNo = 0;
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q5", yesOrNo);

				RadioGroup question6 = (RadioGroup) findViewById(R.id.question2_6);
				selectedId = question6.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q6", yesOrNo);

				RadioGroup question7 = (RadioGroup) findViewById(R.id.question2_7);
				selectedId = question7.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q7", yesOrNo);

				RadioGroup question8 = (RadioGroup) findViewById(R.id.question2_8);
				selectedId = question8.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("0")){
					yesOrNo = 0;
				}
				else if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				score+=yesOrNo;
				editor.putInt("Q2Q8", yesOrNo);
				
				editor.putInt("Q2Score",score);
				System.out.println("score: "+score);
				editor.putBoolean("Answers2Saved", true);
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


