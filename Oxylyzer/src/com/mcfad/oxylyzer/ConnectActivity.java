package com.mcfad.oxylyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ConnectActivity extends ListActivity {

	private BluetoothAdapter mBtAdapter;
	DeviceAdapter devicesAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		devicesAdapter = new DeviceAdapter(this);
		getListView().setAdapter(devicesAdapter);
		
		showPairedDevices();
	}
	public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
		//private DeviceListActivity deviceActivity;

		public DeviceAdapter(ConnectActivity context) {
			super(context, R.layout.row_device);
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
			v.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					BluetoothDevice device = (BluetoothDevice)v.getTag();
					
					try {
						connectDevice(device);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			return v;
		}
	}
	
	static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public void connectDevice(BluetoothDevice device) throws IOException{
		//device.
		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SSP_UUID);
		socket.connect();
		InputStream input = socket.getInputStream();
		while(true){
			System.out.println(input.read());
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
	}
}
