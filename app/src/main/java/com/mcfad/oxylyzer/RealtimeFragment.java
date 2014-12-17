package com.mcfad.oxylyzer;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.mcfad.oxylyzer.view.graphview.RealtimeOxGraph;
import com.mcfad.oxylyzer.view.VerticalBar;

public class RealtimeFragment extends Fragment {

	View rootView;
	private TextView spo2Text;
	private TextView bpmText;
	private VerticalBar levelBar;
	RealtimeOxGraph graph;

	Button baselineButton;

	Handler baselineTimerHandler;	
	ArrayList<Integer> spO2Values;

	// data to be displayed in the realtime graph, stored independently of any views so the view can be recreated
	protected GraphViewSeries spo2;
	protected GraphViewSeries bpm;

	LocalBroadcastManager bman;

	public RealtimeFragment(){
		spo2 = new GraphViewSeries(new GraphViewData[] {});
		bpm = new GraphViewSeries(new GraphViewData[] {});
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
		spo2Text = (TextView)rootView.findViewById(R.id.spo2);
		bpmText = (TextView)rootView.findViewById(R.id.bpm);
		levelBar = (VerticalBar)rootView.findViewById(R.id.level);
		levelBar.setMaxVal(100); // max value for pleth is 100
		LinearLayout graphLayout = (LinearLayout) rootView.findViewById(R.id.graph1);

		graph = new RealtimeOxGraph(getActivity(),graphLayout,spo2,bpm);
		checkBaseline();

		baselineButton = (Button) rootView.findViewById(R.id.baseline_button);
		baselineButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				recordBaseline();
			}
		});

		bman = LocalBroadcastManager.getInstance(getActivity());
		bman.registerReceiver(oxReceiver, new IntentFilter(OximeterService.BROADCAST_DATA));
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		bman.unregisterReceiver(oxReceiver);
	}

	public void checkBaseline(){
		SharedPreferences prefs = getActivity().getSharedPreferences("Profile", 0);
		int baseline = prefs.getInt("baseline", 0);

		if(baseline==0){
			rootView.findViewById(R.id.baseline_view).setVisibility(View.GONE);
		} else {
			rootView.findViewById(R.id.baseline_view).setVisibility(View.VISIBLE);
			TextView baselineText = (TextView) rootView.findViewById(R.id.baseline_value);
			baselineText.setText(baseline+" %");
		}
		graph.updateBaseline(baseline);
	}

	public void recordBaseline(){

		if(baselineTimerHandler==null){
			baselineTimerHandler = new Handler(Looper.getMainLooper());	
			spO2Values = new ArrayList<Integer>();
			Runnable graphUpdate = new Runnable() {
				int seconds = 0;
				@Override
				public void run() {
					baselineButton.setText((60-seconds)+" seconds left");
					if(seconds==59) {
						int avgSpO2 = 0; 
						for(Integer spO2:spO2Values)
							avgSpO2 += spO2;
						avgSpO2 /= spO2Values.size();

						Editor editor = getActivity().getSharedPreferences("Profile", 0).edit();
						editor.putInt("baseline", avgSpO2);
						editor.commit();
						baselineTimerHandler = null;
						baselineButton.setText("Record Baseline");
						spO2Values = null;
						checkBaseline();
					} else {
						baselineTimerHandler.postDelayed(this, 1000);
						seconds++;
					}
				}
			};
			baselineTimerHandler.post(graphUpdate);
		}
	}

	BroadcastReceiver oxReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.hasExtra("spo2")){
				long time = intent.getExtras().getLong("time");
				int spo2 = intent.getExtras().getInt("spo2");
				int bpm = intent.getExtras().getInt("bpm");
				graph.addToGraph(time,spo2,bpm);

				if(RealtimeFragment.this.isVisible()) {

					spo2Text.setText("" + spo2); 
					bpmText.setText("" + bpm);

					if(spO2Values!=null){
						spO2Values.add(spo2);
					}

					graph.updateGraph(time,spo2,bpm);
				}
			}
			if(intent.hasExtra("level")&&RealtimeFragment.this.isVisible())
			{
				int level = intent.getExtras().getInt("level");
				levelBar.setCurrentVal(level);
				levelBar.invalidate();
			}
		}
	};
}