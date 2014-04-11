package com.mcfad.oxylyzer;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuestionnaireForm4 extends Activity {

	private int score;
	private boolean isFilled;
	boolean personalLife = false;
	boolean haveApnea = false;
	boolean backSleeper = false;
	boolean physObstruct = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionnaire_form4);

		final SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		final SharedPreferences.Editor editor = questionnaire.edit();
	
		isFilled = false;
		
		Button button = (Button) findViewById(R.id.saveQuestionnaire4);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v3) {
				

				
				RadioGroup question1 = (RadioGroup) findViewById(R.id.question4_1);
				int selectedId = question1.getCheckedRadioButtonId();
				RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
				int yesOrNo = 0;
				int all =0;

				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{ personalLife = true;
				all++;}
				editor.putInt("Q4Q1", yesOrNo);

				RadioGroup question2 = (RadioGroup) findViewById(R.id.question4_2);
				selectedId = question2.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{personalLife = true;
				all++;}
				editor.putInt("Q4Q2", yesOrNo);

				RadioGroup question3 = (RadioGroup) findViewById(R.id.question4_3);
				selectedId = question3.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{personalLife = true;
				all++;}				
				editor.putInt("Q4Q3", yesOrNo);

				RadioGroup question4 = (RadioGroup) findViewById(R.id.question4_4);
				selectedId = question4.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{personalLife = true;
				 haveApnea = true;
				all++;}				
				editor.putInt("Q4Q4", yesOrNo);
				
				RadioGroup question5 = (RadioGroup) findViewById(R.id.question4_5);
				selectedId = question5.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				yesOrNo = 0;
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{backSleeper = true;
				all++;}
				editor.putInt("Q4Q5", yesOrNo);

				RadioGroup question6 = (RadioGroup) findViewById(R.id.question4_6);
				selectedId = question6.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{all++;}
				if(yesOrNo >= 4)
				{physObstruct = true;}
				editor.putInt("Q4Q6", yesOrNo);

				RadioGroup question7 = (RadioGroup) findViewById(R.id.question4_7);
				selectedId = question7.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{haveApnea = true;
				all++;}
				editor.putInt("Q4Q7", yesOrNo);

				RadioGroup question8 = (RadioGroup) findViewById(R.id.question4_8);
				selectedId = question8.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{haveApnea = true;
				all++;}
				editor.putInt("Q4Q8", yesOrNo);
				RadioGroup question9 = (RadioGroup) findViewById(R.id.question4_9);
				selectedId = question9.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{all++;}
				editor.putInt("Q4Q9", yesOrNo);
				RadioGroup question10 = (RadioGroup) findViewById(R.id.question4_10);
				selectedId = question10.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) findViewById(selectedId);
				if(radioSexButton.getText().toString().equals("1")){
					yesOrNo = 1;
				}
				else if(radioSexButton.getText().toString().equals("2")){
					yesOrNo = 2;
				}
				else if(radioSexButton.getText().toString().equals("3")){
					yesOrNo = 3;
				}
				else if(radioSexButton.getText().toString().equals("4")){
					yesOrNo = 4;
				}
				else if(radioSexButton.getText().toString().equals("5")){
					yesOrNo = 5;
				}
				if(yesOrNo >= 3)
				{all++;}
				editor.putInt("Q4Q10", yesOrNo);
				if(personalLife == true && all < 10 )
				{editor.putString("Q4Result","Snoring almost certainly interferes with your personal life.");}
				else if(haveApnea == true && all < 10 )
				{editor.putString("Q4Result","There is a moderate chance you experience apnea during sleep. You should consult with your physician");}
				else if(backSleeper == true && all < 10 )
				{editor.putString("Q4Result"," It is likely that your snoring is probably due to the effect of gravity on the tissues of your upper airway rather than to any basic anatomical obstruction. You are what is called a \"positional snorer.\" Remedies that encourage you to sleep on your side or stomach may be sufficient");}
				else if(physObstruct == true && all < 10 )
				{editor.putString("Q4Result","Your snoring is most likely the result of a physical obstruction");}
				else if(all== 10)
				{editor.putString("Q4Result", "It is very likely you have sleep apnea, you should consult and have a sleep study performed to evaluate your sleep and snoring.");}
				else
				{editor.putString("Q4Result", "You appear to be a healthy sleeper, please do another questionairre to be further assessed");}
				editor.putBoolean("Answers4Saved", true);
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


