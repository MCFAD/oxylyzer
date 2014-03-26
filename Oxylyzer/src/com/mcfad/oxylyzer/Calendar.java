package com.mcfad.oxylyzer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Calendar extends Activity {
	String dateStringStart;
	//ArrayList<String> dateList;
	ArrayAdapter<String> adapter3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		
		
		Spinner spinner = (Spinner) findViewById(R.id.oxi_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.oxi_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		Spinner spinner2 = (Spinner) findViewById(R.id.pulse_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.pulse_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner2.setAdapter(adapter2);
		
		Button button = (Button) findViewById(R.id.button_start);
		button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        // Do something in response to button click
		    	 // Do something in response to button click
		    	if( dateStringStart==null)
		    	{
		    	long date = System.currentTimeMillis(); 
		    	SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
		    	dateStringStart = sdf.format(date);
		    	}
		    	
		    }
		});
		
		//dateList = new ArrayList<String>();
		
		Button button2 = (Button) findViewById(R.id.button_stop);
		button2.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v2) {
		    	
		    	long date = System.currentTimeMillis(); 
		    	SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a",Locale.US);
		    	String dateStringFinish = sdf.format(date);
		    	if(dateStringStart != null)
		    	{
		    	String dateString = dateStringStart + "-" + dateStringFinish;
		    	//TextView time_period=(TextView)findViewById(R.id.time_period);
		    	//time_period.setText(dateString);
		    	
		    	adapter3.add(dateString);
		    	dateStringStart= null;
		    	}
		    }
		});
		
		Spinner spinner3 = (Spinner) findViewById(R.id.records);
		// Create an ArrayAdapter using the string array and a default spinner layout
		adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);//.createFromResource(this,
		
		//        R.array.records_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner3.setAdapter(adapter3);
		
		
		Button button4 = (Button) findViewById(R.id.button_view_report);
		button4.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v4) {
		    	
		    	Intent intent2 = new Intent(Calendar.this, Report.class);
				Calendar.this.startActivity(intent2);
		    	
		    	}
		});

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar, menu);
		return true;
	}
	

	

}
