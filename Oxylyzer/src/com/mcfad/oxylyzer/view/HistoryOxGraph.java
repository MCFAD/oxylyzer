package com.mcfad.oxylyzer.view;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.mcfad.oxylyzer.db.DataPoint;
import com.mcfad.oxylyzer.db.Recording;

public class HistoryOxGraph extends OxGraph {

	private GraphViewSeries apnea;
	private java.util.ArrayList<DataPoint> array;
	
	public HistoryOxGraph(Context context, LinearLayout parent) {
		super(context,parent);

		graphView.setBackgroundColor(Color.LTGRAY);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setTextSize(15.5f);
		graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		//graphView.setShowLegend(true);

		//This array stores only one event of apnea, it the array is cleared out one user go back to normal state
		array = new java.util.ArrayList<DataPoint>();
	}	

	public void updateGraph(Context context,Recording recording){
		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		bpm = new GraphViewSeries(new GraphViewData[] {});
		graphView.removeAllSeries();
		
		Cursor dataCursor = recording.queryDatapoints(context);
		int apneaStartTime = Integer.MAX_VALUE;
		int baseLine = 98;//Set this to baseLine If baseline is available
		int apneaClassificationTime = 10;  //change this to a lower value if you want to see a premature apnea event
		int previousSPO2 = 0;
		DataPoint previousDataPoint = null;
		while(dataCursor.moveToNext()){
			DataPoint dataPoint = new DataPoint(dataCursor);

			// if not set, this is the first datapoint in the graph, set it at x = 0
			if(secondsOffset==-1)
				secondsOffset = dataPoint.time/1000;

			double seconds = (double) ((dataPoint.time/1000)-secondsOffset);
			
			spo2.appendData(new GraphViewData(seconds, dataPoint.spo2), false);
			bpm.appendData(new GraphViewData(seconds, dataPoint.bpm), false);

			if(dataPoint.spo2 < previousSPO2 && baseLine-previousSPO2 >= 3)
			{
				array.add(previousDataPoint);
			}
			else
			{
				if(seconds - apneaStartTime > apneaClassificationTime && !array.isEmpty())
				{
					array.add(previousDataPoint); //the last datapoint of an apnea event
					apnea = new GraphViewSeries(new GraphViewData[] {});
					apnea.getStyle().color = Color.MAGENTA;
					apnea.getStyle().thickness = 10;
					graphView.addSeries(apnea); //indicates where the apnea period.
					for(int i = 0; i < array.size(); i++)
					{
						double apSeconds = (double) ((dataPoint.time/1000)-secondsOffset);
						int apSpO2 = array.get(i).spo2;
						apnea.appendData(new GraphViewData(apSeconds, apSpO2), false);
					}
				}

				//reset the apnea data point and time after highlighting the apnea event
				array.clear();
				apneaStartTime = (int) seconds; 
			}
			previousDataPoint = dataPoint;
			previousSPO2 = dataPoint.spo2;
		}
		graphView.addSeries(spo2); // oxygen level
		spo2.getStyle().color = Color.BLUE;
		graphView.addSeries(bpm); // beats per minutes
		bpm.getStyle().color = Color.RED;
		graphView.setScrollable(true);
		graphView.setScalable(true);
		//graphView.setFocusable(true);
		graphView.redrawAll();
	}
}