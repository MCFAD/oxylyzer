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
			String s = String.format("%4s", Integer.toBinaryString((b>>4)&0x0F)).replace(' ', '0')+
					" "+String.format("%4s", Integer.toBinaryString(b&0x0F)).replace(' ', '0')+"  ";
			//String s = String.format("%4s %4s", Integer.toBinaryString((b>>4)&0x0F),Integer.toBinaryString(b&0x0F))+"  ";
			//String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')+"  ";
			//String s = String.format("%02x ", b&0xff);
			sb.append(s);
		}
		return sb.toString();
	}

	static long time = new Date().getTime();
	static long lastTime = 0;
	public void postData(byte[] data){
		//Log.i("OS", byteArrayToHex(data));
		DataMessage msg = new DataMessage(data);
		Log.i("OS", msg.toString());
		
		if(!msg.isValid())
			return;

		long time = new Date().getTime();
		Intent intent = new Intent(BROADCAST_DATA);
		intent.putExtra("level", msg.pleth);
		if(time-lastTime>=1000){
			OxContentProvider.postDatapoint(this, currentRecording, msg.spO2, msg.pulseRate);
	
			intent.putExtra("time", time);
			intent.putExtra("spo2", msg.spO2);
			intent.putExtra("bpm", msg.pulseRate);
			lastTime = time;
		}
		//Log.d("OximeterService", "time: "+time+" spo2: "+spo2+" bpm: "+bpm);
		lBroadMan.sendBroadcast(intent);
	}

	public boolean isConnected() {
		return connected; 
	}
	
	public class DataMessage {
		boolean pulseBeep;
		boolean unplugged;
		boolean noSignal;
		int strength;
		Integer pleth;
		boolean pulseResearch;
		boolean sensorOff;
		Integer pulseRate;
		Integer spO2;
		
		boolean invalidReading = false;
		// Message format:
		// 1BUN KKKK  0PPP PPPP  0HRO GGGG  0HHH HHHH  0SSS SSSS
		public DataMessage(byte data[]) {
			pulseBeep = ((data[0]>>6)&0x01)==1;			// B
			unplugged = ((data[0]>>5)&0x01)==1;			// U
			noSignal = ((data[0]>>4)&0x01)==1;			// N
			strength = data[0]&0x0F;					// K
			
			pleth = data[1]&0x7F; 						// P
			
			pulseResearch = ((data[2]>>5)&0x01)==1; 	// R
			sensorOff = ((data[2]>>4)&0x01)==1; 		// O
			
			pulseRate = (((data[2]>>6)&0x01)<<7) + data[3]&0x7F; 	// H
			
			spO2 = data[4]&0x7F; 						// S
			
			// invalid values
			if(pleth==0) pleth = null;
			if(pulseRate==0xFF) pulseRate = null;
			if(spO2==0x7F) spO2 = null;
			
			invalidReading = pleth==null||spO2==null||pulseRate==null||unplugged||noSignal||pulseResearch||sensorOff;
		}
		public boolean isValid() {
			return !invalidReading;
		}
		public String toString(){
			return "spo02: "+spO2+" bpm:"+pulseRate+" pleth:"+pleth+" strength:"+strength+
					" beep:"+pulseBeep+" unplugged:"+unplugged+" nosignal:"+noSignal+
					" scanning:"+pulseResearch+" sensorOff:"+sensorOff;
		}
	}
	// 
/*
Message Format:
1BUN KKKK  0PPP PPPP  0HRO GGGG  0HHH HHHH  0SSS SSSS

From documentation:
1	0~3	    K: Signal strength (0~8)
	4	    N: 1=no signal, 0=OK
	5	    U: 1=probe unplugged, 0=OK
	6	    B: 1=pulse beep
	7	    Sync bit=1
2	0~6	    P: Pleth(0-100, invalid pleth=0)
	7	    Sync bit=0
3	0~3	    G: Bargraph(0-15, invalid bar=0) 	seems to be the same value as pleth, with low precision
	4	    Sensor off=1, 0=OK
	5	    Pulse research=1, 0=OK
	6	    Bit 6 of Byte 3 is bit 7 of the Pulse Rate
	7	    Sync bit
4	0~6	    Bits 0-6 of Byte 4 are bits 0-6 of the Pulse Rate
	7	    Sync=0
5	0~6	    SpO2(0-100%)
	7	    Sync=0
	
Invalid Pulse Rate=0xff
invalid Spo2=%0x7F
*/
} 
