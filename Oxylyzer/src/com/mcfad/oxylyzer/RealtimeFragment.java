package com.mcfad.oxylyzer;

import java.util.ArrayList;

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
		spo2Text.setTextColor(Color.BLUE);
		spo2Text.setTextSize(100);
		bpmText.setTextColor(Color.RED);
		bpmText.setTextSize(100);
		levelBar = (VerticalProgressBar)rootView.findViewById(R.id.level);
		levelBar.setMax(0);
		setupGraph(rootView); 
		seconds = (new java.util.Date()).getTime();
		
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
<<<<<<< HEAD
<<<<<<< HEAD
	

	static SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss",Locale.US);
	public static class LabelFormatter implements CustomLabelFormatter 
	{
		   
		   public String formatLabel(double value, boolean isValueX) {
			   
			  int label = (int)value;
			   
		      if (isValueX) {
		    	  //return sdf.format(new Date((long)value*1000));
		    	  
		    	  if(value < 0)
		    		  return "";
		    	  
		    	  
		    	  if(label >= 3600)
		    		  return label/3600 + ":" + label%3600/60 + ":" + label%3600%60;
		    	  if(label >= 60)
		    		  return label/60 + ":" + label%60;
		    	  return "" + label;
		      
		      }
		      return label + "%"; // let graphview generate Y-axis label for us
		   }
		}
=======
>>>>>>> FETCH_HEAD
=======
>>>>>>> FETCH_HEAD

	public void setupGraph(View rootView){
		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		spo2.getStyle().color = Color.BLUE;
		bpm = new GraphViewSeries(new GraphViewData[] {});
		bpm.getStyle().color = Color.RED;
		graphView = new LineGraphView(this.getActivity(), "");
		
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			   @Override
			   public String formatLabel(double value, boolean isValueX) {
			      if (isValueX) {
			    	  if(value < 0)
			    		  return "";
			    	  int label = (int)value;
			    	  
			    	  if(label >= 3600)
			    		  return label/3600 + ":" + label%3600/60 + ":" + label%3600%60;
			    	  if(label >= 60)
			    		  return label/60 + ":" + label%60;
			    	  return "" + label;
			      }
			      return null; // let graphview generate Y-axis label for us
			   }
			});
		
		graphView.getChildAt(1).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("RT", "onTouch");
				return true;
			}
		});
		
		graphView.addSeries(spo2); // oxygen level
		graphView.addSeries(bpm); // beats per minutes
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setBackgroundColor(Color.LTGRAY);

		graphView.getGraphViewStyle().setNumHorizontalLabels(NUM_OF_HORI_LABELS);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setTextSize(15.5f);
		graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		//graphView.setShowLegend(true);

		graphView.setManualYAxisBounds(100, 70);
		graphView.setVerticalLabels(new String[] {"100%","85%", "70%"});

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph1);
		layout.addView(graphView);
/*		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			int x = 0;
			@Override
			public void run() {
				postData(x,(int)(85+15*Math.random()),(int)(40+215*Math.random()));
				handler.postDelayed(this, 1000);
				x += 1;
			}
		};
		handler.post(graphUpdate);
	*/
	}
	public void postData(long time,int spo2,int bpm){
		updateGraph(time,spo2,bpm);
	}
	
	
	static final int WINDOW_SIZE = 15;
	static final int NUM_OF_HORI_LABELS = 5;
	static String labels[] = new String[NUM_OF_HORI_LABELS];
	/*
	private ArrayList<thisdata> array;
	
	private class thisdata
	{
		public thisdata(long t, int o2, int bpm)
		{
			time = t;
			spo2Val = o2;
			bpmVal = bpm;
		}
		public long time;
		public int spo2Val;
		public int bpmVal;
	}
	*/
	public void updateGraph(long time,int spo2Val, int bpmVal) {
		Log.i("PO", "time: "+time+" spo2 "+spo2Val+" bpm "+bpmVal);
		double bpmInPercentage = bpmVal*0.1395348837 + 64.4186;	
	
		spo2Text.setText("" + spo2Val); 
		bpmText.setText("" + bpmVal);
		time = time/1000;
		//array.add(new thisdata(time, spo2Val, bpmVal));
		spo2.appendData(new GraphViewData(time, spo2Val), false);
		bpm.appendData(new GraphViewData(time, bpmInPercentage), false);
<<<<<<< HEAD
<<<<<<< HEAD
		
		
		
		//Log.i("PO", "time: "+time+" initiatingTime "+initiatingTime);
		
		if((new java.util.Date()).getTime() - remainRefreshTime > 5000)
		{
			graphView.setViewPort( (time-initiatingTime < 60)? initiatingTime : time-60, 60);
			graphView.redrawAll();
		}
=======
		graphView.setViewPort( (time < 60)? 0 : time-60, 60);
		graphView.redrawAll();
>>>>>>> FETCH_HEAD
=======
		graphView.setViewPort( (time < 60)? 0 : time-60, 60);
		graphView.redrawAll();
>>>>>>> FETCH_HEAD
	}
	BroadcastReceiver oxReceiver = new BroadcastReceiver() {
		int level;
		int progressBarMax;
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.hasExtra("level"))
			{
				level = intent.getExtras().getInt("level");
				if(progressBarMax < level)
				{
					progressBarMax = level;
					levelBar.setMax(level);
				}
				levelBar.setProgress(level);
				levelBar.invalidate();
			}

			if(intent.hasExtra("spo2")){
				long time = intent.getExtras().getLong("time");
				int spo2 = intent.getExtras().getInt("spo2");
				int bpm = intent.getExtras().getInt("bpm");
				
				
				
				postData((time-seconds),spo2,bpm);
			}
			
		}
	};
	
	private TextView spo2Text;
	private TextView bpmText;
	private VerticalProgressBar levelBar;
	private long seconds;
}