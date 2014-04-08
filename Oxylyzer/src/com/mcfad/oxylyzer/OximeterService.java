package com.mcfad.oxylyzer;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mcfad.oxylyzer.db.OxContentProvider;

public class OximeterService extends Service {
	static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String BROADCAST_DATA = "com.mcfad.oxylyzer.oxdata";
	public static final String BROADCAST_CONNECTION_STATE = "com.mcfad.oxylyzer.oxconn";

	LocalBroadcastManager lBroadMan;
	@Override
	public void onCreate() {
		super.onCreate();
		lBroadMan = LocalBroadcastManager.getInstance(this);
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
	private BluetoothDevice currentDevice;
	private boolean connected = false;
	public class ConnectThread extends Thread {
		public ConnectThread(){
			super("OximeterConnectThread");
		}
		@Override
		public void run() {
			BluetoothSocket socket;
			InputStream input = null;
			try {
				socket = currentDevice.createRfcommSocketToServiceRecord(SSP_UUID);
				socket.connect();
				input = socket.getInputStream();
				Log.d("PO", "Bluetooth oximeter connected");
			} catch (IOException e) {
				e.printStackTrace();
				broadcastConnectionState(false,e.getMessage());
				return;
			}
			onConnect();

			byte[] data = new byte[5];
			while(connected){
				try {
					input.read(data);
				} catch (IOException e) {
					e.printStackTrace();
					connected = false;
				}
				postData(data);
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			onDisconnect();
		}
	};
	ConnectThread connectThread;
	public void connectDevice(BluetoothDevice device) throws IOException {
		if(connectThread!=null&&connectThread.isAlive())
			return;
		
		currentDevice = device;
		connectThread = new ConnectThread();
		connectThread.start();
	}

	public void disconnect() {
		connected = false;
	}

	public void onConnect(){
		connected = true;
		currentRecording = OxContentProvider.startNewRecording(OximeterService.this);
		broadcastConnectionState(true,null);
	}
	public void onDisconnect() {
		connected = false;
		OxContentProvider.endRecording(OximeterService.this,currentRecording);
		broadcastConnectionState(false,"Disconnected");
		
	}
	public void broadcastConnectionState(boolean state, String message){
		Intent intent = new Intent(BROADCAST_CONNECTION_STATE);
		intent.putExtra("state",state);
		intent.putExtra("message",message);
		OximeterService.this.sendBroadcast(intent);

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

	public void postData(byte[] data){
		postData(data[4],data[3],(data[1]<<8 | data[2]));
	}
	static long lastTime = 0;
	public void postData(int spo2,int bpm,int level){

		long time = new Date().getTime();
		Intent intent = new Intent(BROADCAST_DATA);
		intent.putExtra("level", level);
		if(time-lastTime>=1000){
			OxContentProvider.postDatapoint(this, currentRecording, spo2, bpm);
	
			intent.putExtra("time", time);
			intent.putExtra("spo2", spo2);
			intent.putExtra("bpm", bpm);
			lastTime = time;
		}
		//Log.d("OximeterService", "time: "+time+" spo2: "+spo2+" bpm: "+bpm);
		lBroadMan.sendBroadcast(intent);
	}

	public boolean isConnected() {
		return connected;
	}
} 
