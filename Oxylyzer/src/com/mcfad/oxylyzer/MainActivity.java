package com.mcfad.oxylyzer;

import java.util.Locale;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.ActionBar;
import android.app.Activity;
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
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		}
		@Override
		public Fragment getItem(int position) {
			if(position==0)
				return new RealtimeSectionFragment();
			return new HistorySectionFragment();
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

	public static class RealtimeSectionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main2_dummy, container, false);
			setupGraph(rootView);
			return rootView;
		}

		public void setupGraph(View rootView){
			// init example series data
			final GraphViewSeries spo2 = new GraphViewSeries(new GraphViewData[] {});
			final GraphViewSeries bpm = new GraphViewSeries(new GraphViewData[] {});
			final int NUM_OF_HORI_LABELS = 5;
			final int WINDOW_SIZE = 15;
			/*final GraphViewSeries exampleSeries1 = new GraphViewSeries(new GraphViewData[] {
				      new GraphViewData(1, 2.0d)
				      , new GraphViewData(2, 1.5d)
				      , new GraphViewData(3, 2.5d)
				      , new GraphViewData(4, 1.0d)
				});*/
			
			final GraphView graphView = new LineGraphView(this.getActivity(), "GraphViewDemo");
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
			
			
			//graphView.setViewPort(0, 10);
			graphView.setManualYAxisBounds(100, 0);
			graphView.setVerticalLabels(new String[] {"100%","75%", "50%", "25%", "0%"});
			
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph1);
			layout.addView(graphView);

			
			final Line line = new Line();
			//line.addPoint(new LinePoint());
			final LineGraph li = (LineGraph) rootView.findViewById(R.id.graph);
			li.addLine(line);
			li.setRangeY(0, 100);
			li.setLineToFill(0);
			

			final Handler handler = new Handler(Looper.getMainLooper());
			Runnable graphUpdate = new Runnable() {
				int x = 0;
				String labels[] = new String[NUM_OF_HORI_LABELS];
				@Override
				public void run() {
					x += 1d;
					//if(x>20) return;
					
					spo2.appendData(new GraphViewData(x, 80+20*Math.random()), false, WINDOW_SIZE);
					bpm.appendData(new GraphViewData(x, 10+20*Math.random()), false, WINDOW_SIZE);
					
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

						graphView.setHorizontalLabels(labels );
						//graphView.setViewPort(x - windowSize, x);
					}
					graphView.redrawAll();
					handler.postDelayed(this, 1000);
					
					//LinePoint p = new LinePoint();
					//p.setX(x);
					//p.setY(Math.random());
					//l.addPoint(p);
					//l.setColor(Color.parseColor("#FFBB33"));
					li.addPointToLine(0, x, 100*Math.random());
				}
			};
			handler.post(graphUpdate);
		}
	}
	public static class HistorySectionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_history, container, false);
			setupGraph(rootView);
			return rootView;
		}

		public void setupGraph(View rootView){
		}
	}

	static int x = 0;
	public static void timeTick(LineGraph li){
		x += 1d;
		//exampleSeries.appendData(new GraphViewData(x, Math.random()), true, 100);

		li.addPointToLine(0, x, Math.random()); 
		li.setRangeX(x-10, x);
	}

}
