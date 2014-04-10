package com.mcfad.oxylyzer;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuestionnaireForm1 extends Activity {

	private int score;
	private boolean isFilled;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionnaire_form1);

		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		final SharedPreferences.Editor editor = questionnaire.edit();
	
		isFilled = false;
		
		Button button = (Button) findViewById(R.id.saveQuestionnaire1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				

				
				RadioGroup question1 = (RadioGroup) findViewById(R.id.question1);
				int selectedId = question1.getCheckedRadioButtonId();
				RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
				int yesOrNo = 0;
				if(radioSexButton.getText().toString().equals("YES")){
					yesOrNo = 1;
					score++;
				}
				else
					yesOrNo = 0;
				editor.putInt("Q1Q1", yesOrNo);

				RadioGroup question2 = (RadioGroup) findViewById(R.id.question2);
				selectedId = question2.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("YES")){
					yesOrNo = 1;
					score++;
				}
				else
					yesOrNo = 0;
				editor.putInt("Q1Q2", yesOrNo);

				RadioGroup question3 = (RadioGroup) findViewById(R.id.question3);
				selectedId = question3.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("YES")){
					yesOrNo = 1;
					score++;
				}
				else
					yesOrNo = 0;
				editor.putInt("Q1Q3", yesOrNo);

				RadioGroup question4 = (RadioGroup) findViewById(R.id.question4);
				selectedId = question4.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("YES")){
					yesOrNo = 1;
					score++;
				}
				else
					yesOrNo = 0;
				editor.putInt("Q1Q4", yesOrNo);
				
				editor.putInt("Q1Score",score);
				System.out.println("score: "+score);
				editor.putBoolean("Answers1Saved", true);
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


