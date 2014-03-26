package com.mcfad.oxylyzer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

public class OximeterService extends Service {
	static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String BROADCAST = "com.mcfad.oxylyzer.oxdata";

	long serviceStart;
	@Override
	public void onCreate() {
		super.onCreate();
		serviceStart = new Date().getTime();
	}
	public class OxBinder extends Binder {
		OximeterService getService() {
			return OximeterService.this;
		}
	}
	private final IBinder mBinder = new OxBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void connectDevice(final BluetoothDevice device) throws IOException {

		new Thread(){
			@Override
			public void run() {
				BluetoothSocket socket;
				InputStream input = null;
				try {
					socket = device.createRfcommSocketToServiceRecord(SSP_UUID);
					socket.connect();
					input = socket.getInputStream();
					Log.d("PO", "Bluetooth oximeter connected");
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}

				byte[] data = new byte[5];
				while(true){
					int read = 0;
					try {
						read = input.read(data);
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					//Log.d("PO",byteArrayToHex(Arrays.copyOfRange(data, 0, read)));
					Intent intent = new Intent(BROADCAST);
					intent.putExtra("time", getTime());
					intent.putExtra("spo2", (int)data[4]);
					intent.putExtra("bpm", (int)data[3]);
					intent.putExtra("level", (int)(data[1]<<8 | data[2]));
					OximeterService.this.sendBroadcast(intent);
				}
			}
		}.start();
	}
	// time in seconds since the service started
	private int getTime() {
		return (int) ((new Date().getTime()-serviceStart)/1000);
	}

	public HashMap<String,Double> getOxDataMap(byte[] data){
		HashMap<String,Double> map = new HashMap<String,Double>();
		map.put("level", (double) (data[1]<<8 | data[2]));
		map.put("bpm", (double) data[3]);
		map.put("spo2", (double) data[4]);
		return map;
	}
	// data format:
	// L = level, B = bpm, S = spo2
	// XX LL LL BB SS
	String byteArrayToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(byte b: bytes){
			//String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')+" ";
			String s = String.format("%02x ", b&0xff);
			sb.append(s);
		}
		return sb.toString();
	}
	static FileOutputStream data_file;
	static int[][] data = new int[2][60];
	public void postData(int time,double spo2,double bpm){
		
		/*if(data_file==null){
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
		data[1][time%60] = (int) bpm;*/
		
	}
}
