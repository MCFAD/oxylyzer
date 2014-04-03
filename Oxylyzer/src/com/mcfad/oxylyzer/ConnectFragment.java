package com.mcfad.oxylyzer;

import java.io.IOException;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mcfad.oxylyzer.OximeterService.OxBinder;
import com.mcfad.oxylyzer.db.OxContentProvider;

public class ConnectFragment extends Fragment {

	private BluetoothAdapter mBtAdapter;
	TextView statusText;
	DeviceAdapter devicesAdapter;
	Spinner deviceSpinner;
	Button connectButton;
	View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_connect, container, false);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		statusText = (TextView)rootView.findViewById(R.id.bt_status);
		if(mBtAdapter!=null){
			if(!mBtAdapter.isEnabled()){ 
				showBluetoothState();
				mBtAdapter.enable();
			} else {
				bluetoothEnabled();
			}
		}
		
		//OxContentProvider.postDatapoint(getActivity());

		return rootView;
	}
	public void bluetoothEnabled(){
		rootView.findViewById(R.id.bt_unavailable).setVisibility(View.GONE);
		deviceSpinner = (Spinner)rootView.findViewById(R.id.devices_spinner);
		devicesAdapter = new DeviceAdapter(this);
		//getListView().setAdapter(devicesAdapter);
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

		showPairedDevices();
	}
	@Override
	public void onResume(){
		super.onResume();
	    getActivity().registerReceiver(btReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
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
		//private DeviceListActivity deviceActivity;

		public DeviceAdapter(ConnectFragment context) {
			super(getActivity(), android.R.layout.simple_spinner_item);
			//deviceActivity = context;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = LayoutInflater.from(getContext()).inflate(R.layout.row_device, null);
			TextView name = (TextView) v.findViewById(R.id.device_name);
			TextView address = (TextView) v.findViewById(R.id.device_address);

			final BluetoothDevice device = this.getItem(position);
			if (device != null) {
				v.setTag(device);

				String deviceName = device.getName();
				if(deviceName==null||deviceName.equals(""))
					deviceName = "Unknown";

				String deviceAddress = device.getAddress();
				deviceAddress += " "+device.getBondState();
				//deviceAddress += " "+device.getBluetoothClass().;
				//deviceAddress += " "+device.getType();

				name.setText(deviceName);
				address.setText(deviceAddress);
			}
			return v;
		}
	}

	protected void showPairedDevices()
	{
		devicesAdapter.clear();
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		for (BluetoothDevice device : pairedDevices) {
			devicesAdapter.add(device);
		}

		//deviceSpinner.setSelection(selected);
	}


	public void showBluetoothState()
	{	
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
		case BluetoothAdapter.STATE_ON:
			bluetoothEnabled();
			break;
		}
	}
	private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        showBluetoothState();
	    }
	};
}
