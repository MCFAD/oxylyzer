package com.mcfad.oxylyzer.view;

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.mcfad.oxylyzer.view.OxGraph.LabelFormatter;

public class RealtimeOxGraph extends OxGraph {
	private long lastTouchTime;
	private int viewportWidth = 30; // width of graph in seconds
	
	public RealtimeOxGraph(Context context, LinearLayout parent) {
		super(context, parent);

		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		spo2.getStyle().color = Color.BLUE;
		bpm = new GraphViewSeries(new GraphViewData[] {});
		bpm.getStyle().color = Color.RED;

		graphView.addSeries(spo2); // oxygen level
		graphView.addSeries(bpm); // beats per minutes
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setBackgroundColor(Color.LTGRAY);

		//graphView.getGraphViewStyle().setNumHorizontalLabels(NUM_OF_HORI_LABELS);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
		//graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setTextSize(15.5f);
		graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		//graphView.setShowLegend(true);

		//graphView.setManualYAxisBounds(100, 70);
		//graphView.setVerticalLabels(new String[] {"100%","85%", "70%"});
		
		graphView.getChildAt(1).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				lastTouchTime = new Date().getTime();
				return false;
			}
		});
	}

	public void updateGraph(long time,int spo2Val, int bpmVal) {
		double bpmInPercentage = bpmVal*0.1395348837 + 64.4186;	

		// if not set, this is the first datapoint in the graph, set it at x = 0
		if(secondsOffset==-1)
			secondsOffset = time/1000;
		
		double seconds = (double) ((time/1000)-secondsOffset);

		spo2.appendData(new GraphViewData(seconds, spo2Val), false);
		bpm.appendData(new GraphViewData(seconds, bpmInPercentage), false);

		if(new Date().getTime() - lastTouchTime > 5000) //after 5 second of inactivity, the graph will refresh
		{
			graphView.setViewPort( (seconds < viewportWidth)? 0 : seconds-viewportWidth, viewportWidth);
			graphView.redrawAll();
		}
	}
}