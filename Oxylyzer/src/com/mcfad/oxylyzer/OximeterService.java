package com.mcfad.oxylyzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class OximeterService extends Service {
	static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String BROADCAST = "com.mcfad.oxylyzer.oxdata";

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

	public void connectDevice(BluetoothDevice device) throws IOException {

		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SSP_UUID);
		socket.connect();
		InputStream input = socket.getInputStream();

		byte[] bbuff = new byte[5];
		while(true){
			int read = input.read(bbuff);
			Log.d("PO",byteArrayToHex(Arrays.copyOfRange(bbuff, 0, read)));
			OxData data = new OxData(bbuff);
			Intent intent = new Intent(BROADCAST);
			intent.putExtra("data", data);
			this.sendBroadcast(intent);
		}
	}
	class OxData implements Serializable{
		private static final long serialVersionUID = 1L;
		int spo2;
		int bpm;
		double level;
		public OxData(byte[] data){
			// ? = data[0]
			level = data[1]<<8 | data[2];
			bpm = data[3];
			spo2 = data[4];
		}
	}
	// data format:
	// L = level, B = bpm, S = spo2
	// XX LL LL BB SS
	String byteArrayToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(byte b: bytes){
			String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')+" ";
			//String s = String.format("%02x ", b&0xff);
			sb.append(s);
		}
		return sb.toString();
	}

}
