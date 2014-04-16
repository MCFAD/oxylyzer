package com.mcfad.oxylyzer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcfad.oxylyzer.view.RealtimeOxGraph;
import com.mcfad.oxylyzer.view.VerticalBar;

public class RealtimeFragment extends Fragment {

	public RealtimeFragment(){
	}

	private TextView spo2Text;
	private TextView bpmText;
	private VerticalBar levelBar;
	RealtimeOxGraph graph;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_realtime, container, false);
		spo2Text = (TextView)rootView.findViewById(R.id.spo2);
		bpmText = (TextView)rootView.findViewById(R.id.bpm);
		levelBar = (VerticalBar)rootView.findViewById(R.id.level);
		levelBar.setMaxVal(100); // max value for pleth is 100
		LinearLayout graphLayout = (LinearLayout) rootView.findViewById(R.id.graph1);
		graph = new RealtimeOxGraph(getActivity(),graphLayout);

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
}