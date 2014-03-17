package com.mcfad.oxylyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			realtimeGraph = new RealtimeSectionFragment();
			historyGraph = new HistorySectionFragment();
		}
		@Override
		public Fragment getItem(int position) {
			if(position==0)
				return realtimeGraph;
			return historyGraph;
		}
		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Realtime";
			case 1:
				return "History";
			}
			return null;
		}
	}

	public static GraphFragment realtimeGraph;
	public GraphFragment historyGraph;
	public abstract static class GraphFragment extends Fragment {
		GraphViewSeries spo2;
		GraphViewSeries bpm;
		GraphView graphView;
		LineGraph li;
	}
	public class RealtimeSectionFragment extends GraphFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main2_dummy, container, false);
			setupGraph(rootView);
			return rootView;
		}

		public void setupGraph(View rootView){
			// init example series data
			spo2 = new GraphViewSeries(new GraphViewData[] {});
			bpm = new GraphViewSeries(new GraphViewData[] {});
			
			graphView = new LineGraphView(this.getActivity(), "GraphViewDemo");
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

			li = (LineGraph) rootView.findViewById(R.id.graph);
			Line line = new Line();
			li.addLine(line);
			li.setRangeY(0, 100);
			li.setLineToFill(0);

			final Handler handler = new Handler(Looper.getMainLooper());
			Runnable graphUpdate = new Runnable() {
				int x = 0;
				@Override
				public void run() {
					MainActivity.this.postData(x,80+20*Math.random(),10+20*Math.random());
					handler.postDelayed(this, 1000);
					x += 1;
				}
			};
			handler.post(graphUpdate);
		}
	}
	public static class HistorySectionFragment extends GraphFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_history, container, false);
			setupGraph(rootView);
			return rootView;
		}

		public void setupGraph(View rootView){
		}
	}
	
	static FileOutputStream data_file;
	static int[][] data = new int[2][60];
	public void postData(int time,double spo2,double bpm){
		
		if(data_file==null){
			try {
				data_file = openFileOutput("data.csv", 0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(time%60==0){
			String buffer = "";
			for(int x=0;x<60;x++){
				String row = data[0][x]+","+data[1][x]+'\n';
				buffer+=row;
			}
			System.out.print(buffer);
			try {
				data_file.write(buffer.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		data[0][time%60] = (int) spo2;
		data[1][time%60] = (int) bpm;
		updateGraph(time,spo2,bpm,realtimeGraph);
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
		
		
		graph.li.addPointToLine(0, x, 100*Math.random());
	}
}
