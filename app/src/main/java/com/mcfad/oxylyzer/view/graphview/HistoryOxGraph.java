package com.mcfad.oxylyzer.view.graphview;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
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
	
	public HistoryOxGraph(Context context, LinearLayout parent) {
		super(context,parent);

		graphView.setBackgroundColor(Color.LTGRAY);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setTextSize(15.5f);
		graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		//graphView.setShowLegend(true);
	}	

	public int getBaseline(){
		SharedPreferences prefs = context.getSharedPreferences("Profile", 0);
		return prefs.getInt("baseline", 0);
	}
	public static int MAX_POINTS = 1000;
	public void updateGraph(Context context,Recording recording){
		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		bpm = new GraphViewSeries(new GraphViewData[] {});
		graphView.removeAllSeries();
		
		Cursor dataCursor = recording.queryDatapoints(context);
		
		int pointInterval = Math.max(dataCursor.getCount()/MAX_POINTS,1);
		int pointNum = 0;
		while(dataCursor.moveToPosition(pointNum)){
			DataPoint dataPoint = new DataPoint(dataCursor);
			if(secondsOffset==-1)
				secondsOffset = dataPoint.time/1000;

			double seconds = (double) ((dataPoint.time/1000)-secondsOffset);

			//Log.d("Graph","pn: "+pointNum+" mod "+pointNum%pointInterval+" int "+pointInterval);
			spo2.appendData(new GraphViewData(seconds, dataPoint.spo2), false);
			bpm.appendData(new GraphViewData(seconds, dataPoint.bpm), false);
			pointNum += pointInterval;
		}

		graphView.addSeries(spo2); // oxygen level
		spo2.getStyle().color = Color.BLUE;
		graphView.addSeries(bpm); // beats per minutes
		bpm.getStyle().color = Color.RED;
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.redrawAll();
	}
	public void analyzeData(Cursor dataCursor){
		ArrayList<DataPoint> currentApneaEvent = new ArrayList<DataPoint>();
		int apneaStartTime = Integer.MAX_VALUE;

		int baseLine = getBaseline();//Set this to baseLine If baseline is available
		int apneaClassificationTime = 5;  //change this to a lower value if you want to see a premature apnea event

		int previousSPO2 = 0;
		DataPoint previousDataPoint = null;
		
		ArrayList<Long> apneaEvents = new ArrayList<Long>();
		
		while(dataCursor.moveToNext()){
			DataPoint dataPoint = new DataPoint(dataCursor);

			// if not set, this is the first datapoint in the graph, set it at x = 0
			if(secondsOffset==-1)
				secondsOffset = dataPoint.time/1000;

			double seconds = (double) ((dataPoint.time/1000)-secondsOffset);

			// if spo2 is decreasing and is 3 or more below the baseline
			if(dataPoint.spo2 < previousSPO2 && baseLine-previousSPO2 >= 3)
			{
				currentApneaEvent.add(previousDataPoint);
			}
			else
			{
				// if its been x seconds since the event started
				if(seconds - apneaStartTime > apneaClassificationTime && !currentApneaEvent.isEmpty())
				{
					currentApneaEvent.add(previousDataPoint); //the last datapoint of an apnea event
					apnea = new GraphViewSeries(new GraphViewData[] {});
					apnea.getStyle().color = Color.MAGENTA;
					apnea.getStyle().thickness = 10;
					graphView.addSeries(apnea); //indicates where the apnea period.
					for(int i = 0; i < currentApneaEvent.size(); i++)
					{
						double apSeconds = (double) ((currentApneaEvent.get(i).time/1000)-secondsOffset);
						int apSpO2 = currentApneaEvent.get(i).spo2;
						apnea.appendData(new GraphViewData(apSeconds, apSpO2), false);
					}
					apneaEvents.add(currentApneaEvent.get(0).time);
				}

				//reset the apnea data point and time after highlighting the apnea event
				currentApneaEvent.clear();
				apneaStartTime = (int) seconds; 
			}
			previousDataPoint = dataPoint;
			previousSPO2 = dataPoint.spo2;
		}
		
		/*int numApneaEvents = 0;
		int apneaEventsPerHour = 0;
		int time = apneaEvents.get(0) - apneaEvents.get(apneaEvents.size()-1);
		for(Long apneaTime:apneaEvents){
			
			
		}*/
	}
}