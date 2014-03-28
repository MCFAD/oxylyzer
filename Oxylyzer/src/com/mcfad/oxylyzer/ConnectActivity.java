package com.mcfad.oxylyzer;

import java.io.IOException;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcfad.oxylyzer.OximeterService.OxBinder;

public class ConnectActivity extends ListActivity {

	private BluetoothAdapter mBtAdapter;
	DeviceAdapter devicesAdapter;
	Spinner deviceSpinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		deviceSpinner = (Spinner)findViewById(R.id.devices_spinner);
		
		bindService(new Intent(this,OximeterService.class), oxConn, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		super.onStop();
		unbindService(oxConn);
	}

	OximeterService oxSrvc;
	ServiceConnection oxConn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			oxSrvc = ((OxBinder)service).getService();
			init();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			oxSrvc = null;
			finish();
		}
	};

	protected void init() {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		devicesAdapter = new DeviceAdapter(this);
		//getListView().setAdapter(devicesAdapter);
		deviceSpinner.setAdapter(devicesAdapter);
		
		Button connect = (Button)findViewById(R.id.connect_button);
		connect.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				BluetoothDevice device = (BluetoothDevice)v.getTag();
				connect(device);
			}
		});
		
		showPairedDevices();
	}
	public void connect(BluetoothDevice device){
		try {
			oxSrvc.connectDevice(device);
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveMAC(device.getAddress());
	}
	public void saveMAC(String address){
		SharedPreferences prefs = getSharedPreferences("Profile", 0);
		prefs.edit().putString("OximeterMAC", address);
		prefs.edit().commit();
	}
	public String getMAC(){
		SharedPreferences prefs = getSharedPreferences("Profile", 0);
		return prefs.getString("OximeterMAC", "");
	}
	public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
		//private DeviceListActivity deviceActivity;

		public DeviceAdapter(ConnectActivity context) {
			super(context, android.R.layout.simple_spinner_item);
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
}
