package com.mcfad.oxylyzer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class Report extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		
		SharedPreferences settings = getSharedPreferences("Profile", 0);
		double height = settings.getInt("Height",0);
		double weight = settings.getInt("Weight",0);
		

		double BMI = weight/(height*height);
		
		TextView BMIval = (TextView) findViewById(R.id.BMIval);
		BMIval.setText(String.valueOf(BMI));

}
}
