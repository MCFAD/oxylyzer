package com.mcfad.oxylyzer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.mcfad.oxylyzer.OximeterService.OxBinder;
import com.mcfad.oxylyzer.view.NonSwipeableViewPager;

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

		bindService(new Intent(this,OximeterService.class), oxConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume(){
		super.onResume();
	    registerReceiver(oxReceiver, new IntentFilter(OximeterService.BROADCAST_CONNECTION_STATE));
	}
	@Override
	protected void onDestroy() {
		super.onStop();
		unbindService(oxConn);
		unregisterReceiver(oxReceiver);
	}

	OximeterService oxSrvc;
	ServiceConnection oxConn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			oxSrvc = ((OxBinder)service).getService();

			mViewPager.setAdapter(mSectionsPagerAdapter); // so that it doesn't initialize the realtime view until after the service is bound
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			oxSrvc = null;
			finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this,Prefs.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
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
			if(position==0) {
				if(oxSrvc.isConnected()){
					if(realtimeView==null)
						realtimeView = new RealtimeFragment();
					return realtimeView;
				} else {
					if(connectView==null)
						connectView = new ConnectFragment();
					return connectView;
				}
			}
			return historyView = new HistoryFragment();
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
	    @Override
	    public int getItemPosition(Object object)
	    {
	        return POSITION_NONE;
	    }
	}

	public Fragment connectView;
	public static RealtimeFragment realtimeView;
	public GraphFragment historyView;
	public abstract static class GraphFragment extends Fragment {
		GraphViewSeries spo2;
		GraphViewSeries bpm;
		GraphView graphView;
		//LineGraph li;
	}

	private final BroadcastReceiver oxReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	boolean connected = intent.getBooleanExtra("state", false);
	    	if(connected) {
	    		onConnect();
	    	} else {
	    		String message = intent.getStringExtra("message");
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				onDisconnect();
	    	}
	    }
	};
	public void onConnect() {
		if(connectView!=null){
			getSupportFragmentManager().beginTransaction().remove(connectView).commit();
			connectView = null;
		}
		mSectionsPagerAdapter.notifyDataSetChanged();
	}
	public void onDisconnect() {
		if(realtimeView!=null){
			getSupportFragmentManager().beginTransaction().remove(realtimeView).commit();
			realtimeView = null;
		}
		mSectionsPagerAdapter.notifyDataSetChanged();
	}
}
