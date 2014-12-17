package com.mcfad.oxylyzer.diagnosis;

import com.mcfad.oxylyzer.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public abstract class QuestionnaireForm extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		setupQuestionnaire();
	}
	public abstract int getLayoutId();
	
	public void setupQuestionnaire(){	
		Button button = (Button) findViewById(R.id.saveQuestionnaire);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onSave();
			}
		});
	}
	public abstract void onSave();

	
	// Helper code for questionnaire forms
	public static class Question {
		String pref;
		int radioGroup;
		boolean trueFalse;
		int scoreMultiplier;
		public Question(String pref,int radioGroup){
			this(pref,radioGroup,1);
		}
		public Question(String pref,int radioGroup, int scoreMultiplier){
			this.pref = pref;
			this.radioGroup = radioGroup;
			this.scoreMultiplier = scoreMultiplier;
		}
	}
	public String getRadioButtonText(int radioGroupId){
		RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupId);
		int selectedId = radioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton) findViewById(selectedId);
		return radioButton.getText().toString();
	}

	public boolean getRadioButtonTrueFalse(int radioGroupId){
		RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupId);
		int selectedId = radioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton) findViewById(selectedId);
		Object tag = radioButton.getTag();
		if(tag!=null){
			return ((String)tag).equals("True");
		}
		return false;
	}

	public int getRadioButtonNumber(int radioGroupId) {
		String text = getRadioButtonText(radioGroupId);
		return Integer.valueOf(text);
	}

	public int getRadioGroupCategory(int radioGroupId) {
		RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupId);
		return Integer.valueOf((String) radioGroup.getTag());
	}
}
