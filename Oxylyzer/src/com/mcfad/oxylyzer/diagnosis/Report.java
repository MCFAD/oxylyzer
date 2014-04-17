package com.mcfad.oxylyzer.diagnosis;

import com.mcfad.oxylyzer.HistoryFragment;
import com.mcfad.oxylyzer.R;
import com.mcfad.oxylyzer.R.id;
import com.mcfad.oxylyzer.R.layout;
import com.mcfad.oxylyzer.view.HistoryOxGraph;
import com.mcfad.oxylyzer.view.ReportOxGraph;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Report extends Activity {

	ReportOxGraph graph;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		
		LinearLayout graphLayout = (LinearLayout) findViewById(R.id.graph1);
		graph = new ReportOxGraph(this, graphLayout);
		graph.updateGraph(this, HistoryFragment.currentRecording);
		
		
		SharedPreferences PSettings = getSharedPreferences("Profile", 0);
		SharedPreferences.Editor editor = PSettings.edit();
		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);
		double height = PSettings.getInt("Height",0);
		double weight = PSettings.getInt("Weight",0);
		boolean metric = PSettings.getBoolean("Metric", false);
		
		double BMI = 0;
		
		if(metric == true)
		{
		BMI = weight/((height/100)*(height/100));
		TextView BMIval = (TextView) findViewById(R.id.BMIval);
		BMIval.setText(String.valueOf(BMI));
		}
		else
		{
		BMI = (weight/(height*height))*703;
		TextView BMIval = (TextView) findViewById(R.id.BMIval);
		BMIval.setText(String.valueOf(BMI));
		}
		
		editor.putFloat("BMI", (float) BMI);
		
		String Q1Result = questionnaire.getString("Q1Result", "You haven't completed this Questionnaire yet");
		String Q2Result = questionnaire.getString("Q2Result", "You haven't completed this Questionnaire yet");
		String Q3Result = questionnaire.getString("Q3Result", "You haven't completed this Questionnaire yet");
		String Q4Result = questionnaire.getString("Q4Result", "You haven't completed this Questionnaire yet");
		
		TextView Q1ResultT = (TextView) findViewById(R.id.Q1Result);
		TextView Q2ResultT = (TextView) findViewById(R.id.Q2Result);
		TextView Q3ResultT = (TextView) findViewById(R.id.Q3Result);
		TextView Q4ResultT = (TextView) findViewById(R.id.Q4Result);
		
		Q1ResultT.setText(Q1Result);
		Q2ResultT.setText(Q2Result);
		Q3ResultT.setText(Q3Result);
		Q4ResultT.setText(Q4Result);

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences questionnaire = getSharedPreferences("Questionnaire", 0);

		String Q1Result = questionnaire.getString("Q1Result", "You haven't completed this Questionnaire yet");
		String Q2Result = questionnaire.getString("Q2Result", "You haven't completed this Questionnaire yet");
		String Q3Result = questionnaire.getString("Q3Result", "You haven't completed this Questionnaire yet");
		String Q4Result = questionnaire.getString("Q4Result", "You haven't completed this Questionnaire yet");
		
		TextView Q1ResultT = (TextView) findViewById(R.id.Q1Result);
		TextView Q2ResultT = (TextView) findViewById(R.id.Q2Result);
		TextView Q3ResultT = (TextView) findViewById(R.id.Q3Result);
		TextView Q4ResultT = (TextView) findViewById(R.id.Q4Result);
		
		Q1ResultT.setText(Q1Result);
		Q2ResultT.setText(Q2Result);
		Q3ResultT.setText(Q3Result);
		Q4ResultT.setText(Q4Result);
		
	}
	
	
	
}
