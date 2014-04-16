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
	
	// set this to true to simulate an oximeter being connected
	public final static boolean OximeterTest = true;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		bindService(new Intent(this,OximeterService.class), oxConn, Context.BIND_AUTO_CREATE);
		
		if(OximeterTest)
			new OximeterTester(this);
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
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public Fragment connectView;
		public Fragment realtimeView;
		public Fragment historyView;

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
		public void onConnect() {
			if(connectView!=null){
				getSupportFragmentManager().beginTransaction().remove(connectView).commit();
				connectView = null;
			}
			notifyDataSetChanged();
		}
		public void onDisconnect() {
			if(realtimeView!=null){
				getSupportFragmentManager().beginTransaction().remove(realtimeView).commit();
				realtimeView = null;
			}
			mSectionsPagerAdapter.notifyDataSetChanged();
		}
	}
	
	private final BroadcastReceiver oxReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	boolean connected = intent.getBooleanExtra("state", false);
	    	if(connected) {
	    		mSectionsPagerAdapter.onConnect();
	    	} else {
	    		String message = intent.getStringExtra("message");
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				mSectionsPagerAdapter.onDisconnect();
	    	}
	    }
	};
}
