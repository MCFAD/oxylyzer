package com.mcfad.oxylyzer.diagnosis;

import com.mcfad.oxylyzer.R;
import com.mcfad.oxylyzer.R.id;
import com.mcfad.oxylyzer.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;


public class Report extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		
		SharedPreferences settings = getSharedPreferences("Profile", 0);
		double height = settings.getInt("Height",0);
		double weight = settings.getInt("Weight",0);
		boolean metric = settings.getBoolean("Metric", false);
		
		if(metric == true)
		{
		double BMI = weight/((height/100)*(height/100));
		TextView BMIval = (TextView) findViewById(R.id.BMIval);
		BMIval.setText(String.valueOf(BMI));
		}
		else
		{
		double BMI = (weight/(height*height))*703;
		TextView BMIval = (TextView) findViewById(R.id.BMIval);
		BMIval.setText(String.valueOf(BMI));
		}
		


}
}
