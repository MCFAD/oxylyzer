package com.mcfad.oxylyzer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.mcfad.oxylyzer.MainActivity.GraphFragment;
import com.mcfad.oxylyzer.view.VerticalProgressBar;

public class RealtimeSectionFragment extends MainActivity.GraphFragment {

	//private final MainActivity mainActivity;
	/*RealtimeSectionFragment(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}*/
	public RealtimeSectionFragment(){
		
	}

	TextView spo2Text;
	TextView bpmText;
	VerticalProgressBar levelBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main2_dummy, container, false);
		setupGraph(rootView);
		spo2Text = (TextView)rootView.findViewById(R.id.spo2);
		bpmText = (TextView)rootView.findViewById(R.id.bpm);
		levelBar = (VerticalProgressBar)rootView.findViewById(R.id.level);
		levelBar.setMax(2^16);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(oxReceiver, new IntentFilter(OximeterService.BROADCAST));
	}
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(oxReceiver);
	}

	public void setupGraph(View rootView){
		// init example series data
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		bpm = new GraphViewSeries(new GraphViewData[] {});

		graphView = new LineGraphView(this.getActivity(), "");
		graphView.addSeries(spo2); // oxygen level
		graphView.addSeries(bpm); // beats per minutes
		graphView.setScrollable(true);
		graphView.setScalable(false);
		graphView.setBackgroundColor(Color.LTGRAY);

		graphView.getGraphViewStyle().setNumHorizontalLabels(NUM_OF_HORI_LABELS);
		graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.RIGHT);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setTextSize(15.5f);
		graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		graphView.setShowLegend(true);

		graphView.setManualYAxisBounds(100, 0);
		graphView.setVerticalLabels(new String[] {"100%","75%", "50%", "25%", "0%"});

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph1);
		layout.addView(graphView);

		/*li = (LineGraph) rootView.findViewById(R.id.graph);
		Line line = new Line();
		li.addLine(line);
		li.setRangeY(0, 100);
		li.setLineToFill(0);*/

		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			int x = 0;
			@Override
			public void run() {
				postData(x,80+20*Math.random(),10+20*Math.random());
				handler.postDelayed(this, 1000);
				x += 1;
			}
		};
		//handler.post(graphUpdate);
	}
	public void postData(int time,double spo2,double bpm){
		updateGraph(time,spo2,bpm,this);
	}
	static final int WINDOW_SIZE = 15;
	static final int NUM_OF_HORI_LABELS = 5;
	static String labels[] = new String[NUM_OF_HORI_LABELS];
	public static void updateGraph(int x,double spo2, double bpm, GraphFragment graph) {

		graph.spo2.appendData(new GraphViewData(x, spo2), false, WINDOW_SIZE);
		graph.bpm.appendData(new GraphViewData(x, bpm), false, WINDOW_SIZE);

		int diference = WINDOW_SIZE/(NUM_OF_HORI_LABELS-1);
		if(x>= WINDOW_SIZE+1)
		{
			labels[4] = x + "";
			labels[3] = x-WINDOW_SIZE+3*diference+2 + "";
			labels[2] = x-WINDOW_SIZE+2*diference+1 + "";
			labels[1] = x-WINDOW_SIZE+1*diference + "";
			labels[0] = x-WINDOW_SIZE + "";

			for(int i = 0; i < NUM_OF_HORI_LABELS; i++)
			{
				int labelIntValue = Integer.parseInt(labels[i]);
				if(labelIntValue >= 60)
					labels[i] = labelIntValue/60 + ":" + labelIntValue % 60;
			}

			graph.graphView.setHorizontalLabels(labels);
			//graphView.setViewPort(x - windowSize, x);
		}
		graph.graphView.redrawAll();

		//graph.li.addPointToLine(0, x, 100*Math.random());
	}
	BroadcastReceiver oxReceiver = new BroadcastReceiver() {
		int lastTime = 0;
		@Override
		public void onReceive(Context context, Intent intent) {
			int time = intent.getExtras().getInt("time");
			int spo2 = intent.getExtras().getInt("spo2");
			int bpm = intent.getExtras().getInt("bpm");
			int level = intent.getExtras().getInt("level");
			Log.i("PO", "time: "+time+" spo2 "+spo2+" bpm "+bpm+" level "+level);
			if(time-lastTime>0)
				postData(time,spo2,bpm);
			
			spo2Text.setText(spo2+"%"); 
			bpmText.setText(bpm+"bpm");
			levelBar.setProgress(level);
			
			lastTime = time;
		}
	};
}