package com.mcfad.oxylyzer.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.widget.LinearLayout;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class OxGraph {

	protected CustomGraphView graphView;
	protected GraphViewSeries spo2;
	protected GraphViewSeries bpm;

	protected long secondsOffset;
	
	protected Context context;
	
	public OxGraph(Context context,LinearLayout parent){
		this.context = context;
		secondsOffset = -1;
		
		graphView = new CustomGraphView(context, "", this);
		graphView.setCustomLabelFormatter(new LabelFormatter());
		parent.addView(graphView);
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss",Locale.US);
	public class LabelFormatter implements CustomLabelFormatter 
	{
		public String formatLabel(double value, boolean isValueX) {
			if (isValueX) {
				long time = (secondsOffset+((long)value))*1000;
				return sdf.format(new Date(time));
			}
			int spO2 = (int)value;
			return spO2+" % ";
		}
	}
	public static String formatLabelBPM(double value) {
		int bpm = (int)((value));//-64.4186)/0.1395348837);
		return " "+bpm+" bpm";
	}
}
