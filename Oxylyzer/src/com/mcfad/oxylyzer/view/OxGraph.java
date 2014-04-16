package com.mcfad.oxylyzer.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class OxGraph {

	protected LineGraphView graphView;
	protected GraphViewSeries spo2;
	protected GraphViewSeries bpm;

	protected long secondsOffset;
	
	public OxGraph(Context context,LinearLayout parent){
		secondsOffset = -1;
		
		graphView = new CustomGraphView(context, "");
		graphView.setCustomLabelFormatter(new LabelFormatter());
		parent.addView(graphView);
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss",Locale.US);
	public class LabelFormatter implements CustomLabelFormatter 
	{
		public String formatLabel(double value, boolean isValueX) {
			int label = (int)value;
			if (isValueX) {
				long time = (secondsOffset+((long)value))*1000;
				return sdf.format(new Date(time));
			}
			return label + "%, " + (int)((value-64.4186)/0.1395348837) + "bpm";
		}
	}
}
