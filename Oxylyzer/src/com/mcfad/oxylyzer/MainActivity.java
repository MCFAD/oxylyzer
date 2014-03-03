package com.mcfad.oxylyzer;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;


public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init example series data
		final GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {});

		GraphView graphView = new LineGraphView(this, "GraphViewDemo");
		graphView.addSeries(exampleSeries); // data
		graphView.setScrollable(true);
		graphView.setViewPort(0, 10);
		graphView.setManualYAxis(true);
		graphView.setManualYAxisBounds(1, 0);

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);


		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable graphUpdate = new Runnable() {
			int x = 0;
			@Override
			public void run() {
				x += 1d;
				exampleSeries.appendData(new GraphViewData(x, Math.random()), true, 100);
				handler.postDelayed(this, 200);
			}
		};
		handler.postDelayed(graphUpdate, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
