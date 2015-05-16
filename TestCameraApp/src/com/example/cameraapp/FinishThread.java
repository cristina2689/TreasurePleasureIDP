package com.example.cameraapp;

import android.content.Context;

public class FinishThread extends Thread {
	Context context;

	public FinishThread(Context context) {
		this.context = context;
	}

	public void run() {
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Compass.sensorManager.unregisterListener(Compass.compass,
		//// Compass.sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
		//Intent i = new Intent(this.context, AnswerActivity.class);
		//context.startActivity(i);
	}
}