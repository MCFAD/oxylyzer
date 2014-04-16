package com.mcfad.oxylyzer;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.mcfad.oxylyzer.view.OxGraph;
import com.mcfad.oxylyzer.view.VerticalBar;

public class RealtimeFragment extends Fragment {

	public RealtimeFragment(){
	}

	private TextView spo2Text;
	private TextView bpmText;
	private VerticalBar levelBar;
	OxGraph graph;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
		spo2Text = (TextView)rootView.findViewById(R.id.spo2);
		bpmText = (TextView)rootView.findViewById(R.id.bpm);
		levelBar = (VerticalBar)rootView.findViewById(R.id.level);
		levelBar.setMaxVal(100); // max value for pleth is 100
		LinearLayout graphLayout = (LinearLayout) rootView.findViewById(R.id.graph1);
		graph = new OxGraph(getActivity(),graphLayout);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(oxReceiver, new IntentFilter(OximeterService.BROADCAST_DATA));

	}
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(oxReceiver);
	}

	BroadcastReceiver oxReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.hasExtra("level"))
			{
				int level = intent.getExtras().getInt("level");
				levelBar.setCurrentVal(level);
				levelBar.invalidate();
			}

			if(intent.hasExtra("spo2")){
				long time = intent.getExtras().getLong("time");
				int spo2 = intent.getExtras().getInt("spo2");
				int bpm = intent.getExtras().getInt("bpm");

				graph.updateGraph(time,spo2,bpm);
				spo2Text.setText("" + spo2); 
				bpmText.setText("" + bpm);
			}
		}
	};
	
	class RealtimeOxGraph extends OxGraph {
		private long lastTouchTime;
		private int viewportWidth = 30; // width of graph in seconds
		
		public RealtimeOxGraph(Context context, LinearLayout parent) {
			super(context, parent);

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
}