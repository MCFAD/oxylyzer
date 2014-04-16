package com.mcfad.oxylyzer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.mcfad.oxylyzer.MainActivity.GraphFragment;
import com.mcfad.oxylyzer.view.VerticalBar;
import com.mcfad.oxylyzer.view.VerticalProgressBar;

public class RealtimeFragment extends MainActivity.GraphFragment {

	//private final MainActivity mainActivity;
	/*RealtimeSectionFragment(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}*/
	public RealtimeFragment(){
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
		spo2Text = (TextView)rootView.findViewById(R.id.spo2);
		bpmText = (TextView)rootView.findViewById(R.id.bpm);
		levelBar = (VerticalBar)rootView.findViewById(R.id.level);
		levelBar.setMaxVal(0);
		setupGraph(rootView); 
		
		initiatingTime = (new java.util.Date()).getTime();
		Date date = new Date(initiatingTime);
		initiatingTime = date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
		remainRefreshTime = 0;
		
		//array = new ArrayList<thisdata>();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		//seconds = (new java.util.Date()).getTime();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(oxReceiver, new IntentFilter(OximeterService.BROADCAST_DATA));
		
	}
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(oxReceiver);
	}
	

	static SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss",Locale.US);
	public static class LabelFormatter implements CustomLabelFormatter 
	{
		   
		   public String formatLabel(double value, boolean isValueX) {
			   
			  int label = (int)value;
			   
		      if (isValueX) {
		    	  if(value < 0)
		    		  return "";
		    	  if(label >= 3600)
		    		  return label/3600 + ":" + label%3600/60 + ":" + label%3600%60;
		    	  if(label >= 60)
		    		  return label/60 + ":" + label%60;
		    	  return "" + label;
		      }
		      return label + "%, " + (int)((value-64.4186)/0.1395348837) + "bpm";
		   }
		}

	public void setupGraph(View rootView){
		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		spo2.getStyle().color = Color.BLUE;
		bpm = new GraphViewSeries(new GraphViewData[] {});
		bpm.getStyle().color = Color.RED;
		graphView = new LineGraphView(this.getActivity(), "");
		
		graphView.setCustomLabelFormatter(new LabelFormatter());
		
		graphView.getChildAt(1).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				remainRefreshTime = (new java.util.Date()).getTime();
				return false;
			}
		});
		
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

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph1);
		layout.addView(graphView);
	
	}
	public void postData(long time,int spo2,int bpm){
		updateGraph(time,spo2,bpm);
	}
	
	
	public void updateGraph(long time,int spo2Val, int bpmVal) {
		//Log.i("PO", "time: "+time+" spo2 "+spo2Val+" bpm "+bpmVal);

		double bpmInPercentage = bpmVal*0.1395348837 + 64.4186;	
	
		spo2Text.setText("" + spo2Val); 
		bpmText.setText("" + bpmVal);
		Date date = new Date(time);
		time = date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
		spo2.appendData(new GraphViewData(time, spo2Val), false);
		bpm.appendData(new GraphViewData(time, bpmInPercentage), false);
		
		
		
		//Log.i("PO", "time: "+time+" initiatingTime "+initiatingTime);
		
		if((new java.util.Date()).getTime() - remainRefreshTime > 5000) //after 5 second of inactivity, the graph will refresh
		{
			graphView.setViewPort( (time-initiatingTime < 60)? initiatingTime : time-60, 60);
			graphView.redrawAll();
		}
	}
	BroadcastReceiver oxReceiver = new BroadcastReceiver() {
		int level;
		int progressBarMax;
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.hasExtra("level"))
			{
				level = intent.getExtras().getInt("level");
				if(progressBarMax < level) //increase the maximum of the progress bar when there is a higher level
				{
					progressBarMax = level;
					levelBar.setMaxVal(progressBarMax);
				}
				levelBar.setCurrentVal(level);
				levelBar.invalidate();
			}

			if(intent.hasExtra("spo2")){
				long time = intent.getExtras().getLong("time");
				int spo2 = intent.getExtras().getInt("spo2");
				int bpm = intent.getExtras().getInt("bpm");
				
				updateGraph(time,spo2,bpm);
			}
			
		}
	};
	static final int NUM_OF_HORI_LABELS = 5;
	private long remainRefreshTime;
	private TextView spo2Text;
	private TextView bpmText;
	private long initiatingTime;
	private VerticalBar levelBar;
	private long seconds;
}