package com.mcfad.oxylyzer;

import java.io.IOException;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ConnectFragment extends Fragment {

	private BluetoothAdapter mBtAdapter;
	TextView statusText;
	DeviceAdapter devicesAdapter;
	Spinner deviceSpinner;
	Button connectButton;
	View rootView;
	
	View btAvailable;
	View btUnavailable;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_connect, container, false);

		btAvailable = rootView.findViewById(R.id.bt_available);
		btUnavailable = rootView.findViewById(R.id.bt_unavailable);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// bluetooth unavailable
		statusText = (TextView)rootView.findViewById(R.id.bt_status);	
		
		// bluetooth available
		deviceSpinner = (Spinner)rootView.findViewById(R.id.devices_spinner);
		devicesAdapter = new DeviceAdapter(this);
		deviceSpinner.setAdapter(devicesAdapter);

		connectButton = (Button)rootView.findViewById(R.id.connect_button);
		connectButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				BluetoothDevice device = (BluetoothDevice)deviceSpinner.getSelectedItem();
				if(!getSrvc().isConnected()){
					connect(device);
					connectButton.setText("Connecting");
				} else {
					getSrvc().disconnect();
				}
			}
		});
		Button bluetoothSettings = (Button)rootView.findViewById(R.id.bluetooth_settings);
		bluetoothSettings.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent);
			}
		});

		return rootView;
	}

	@Override
	public void onResume(){
		super.onResume();
	    getActivity().registerReceiver(btReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
	    
	    if(mBtAdapter!=null){
			if(!mBtAdapter.isEnabled()){ 
				mBtAdapter.enable();
			} 
			updateBluetoothState();
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		getActivity().unregisterReceiver(btReceiver);
	}
	public OximeterService getSrvc(){
		return ((MainActivity)getActivity()).oxSrvc;
	}
	public void connect(BluetoothDevice device){
		try {
			getSrvc().connectDevice(device);
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveMAC(device.getAddress());
	}
	public void saveMAC(String address){
		SharedPreferences prefs = getActivity().getSharedPreferences("Profile", 0);
		prefs.edit().putString("OximeterMAC", address);
		prefs.edit().commit();
	}
	public String getMAC(){
		SharedPreferences prefs = getActivity().getSharedPreferences("Profile", 0);
		return prefs.getString("OximeterMAC", "");
	}
	public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
		public DeviceAdapter(ConnectFragment context) {
			super(getActivity(), android.R.layout.simple_spinner_item);
		}
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{
			return getView(position,convertView,parent);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = convertView;
			if(v==null)
				v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_row, null);
			
			TextView name = (TextView) v.findViewById(R.id.text1);

			BluetoothDevice device = this.getItem(position);
			if (device != null) {
				v.setTag(device);

				String deviceName = device.getName();
				if(deviceName==null||deviceName.equals(""))
					deviceName = device.getAddress();

				name.setText(deviceName);
			}
			return v;
		}
	}


	public void updateBluetoothState()
	{	
		if(mBtAdapter.getState()!=BluetoothAdapter.STATE_ON){
			btUnavailable.setVisibility(View.VISIBLE);
			btAvailable.setVisibility(View.GONE);
			switch(BluetoothAdapter.getDefaultAdapter().getState()){
			case BluetoothAdapter.STATE_TURNING_OFF:
				statusText.setText("Bluetooth is Turning Off..");
				break;
			case BluetoothAdapter.STATE_OFF:
				statusText.setText("Bluetooth is Off");
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				statusText.setText("Bluetooth is Turning On..");
				break;
			}
		} else {
			btUnavailable.setVisibility(View.GONE);
			btAvailable.setVisibility(View.VISIBLE);
			devicesAdapter.clear();
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

			for (BluetoothDevice device : pairedDevices) {
				devicesAdapter.add(device);
			}
		}
	}

	private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	updateBluetoothState();
	    }
	};
}
