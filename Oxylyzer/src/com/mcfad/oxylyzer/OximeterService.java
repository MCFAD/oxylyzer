package com.mcfad.oxylyzer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mcfad.oxylyzer.db.OxContentProvider;

public class OximeterService extends Service {
	static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String BROADCAST = "com.mcfad.oxylyzer.oxdata";

	long serviceStart;
	boolean connected = false;
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
	private Uri currentRecording;
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
				connected = true;
				OxContentProvider.startNewRecording(OximeterService.this);

				byte[] data = new byte[5];
				while(true){
					int read = 0;
					try {
						read = input.read(data);
					} catch (IOException e) {
						e.printStackTrace();
						connected = false;
						return;
					}
					postData(data);
				}
			}
		}.start();
	}

	// time in seconds since the service started
	private int getTime() {
		return (int) ((new Date().getTime()-serviceStart)/1000);
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
	
	public void postData(byte[] data){
		postData(getTime(),data[4],data[3],(data[1]<<8 | data[2]));
	}
	public void postData(int time,int spo2,int bpm,double level){
		
		ContentValues values = new ContentValues();
		values.put("time", getTime());
		values.put("spo2", spo2);
		values.put("bpm", bpm);
		values.put("level", level);
		
		getContentResolver().insert(currentRecording, values);

		Intent intent = new Intent(BROADCAST);
		intent.putExtra("time", getTime());
		intent.putExtra("spo2", spo2);
		intent.putExtra("bpm", bpm);
		intent.putExtra("level", level);
		OximeterService.this.sendBroadcast(intent);
	}

	public boolean isConnected() {
		return connected;
	}
}
