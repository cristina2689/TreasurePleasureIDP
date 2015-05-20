package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.GAME_DIR;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

		    @Override
		    public void run() {
		    	Intent intent = new Intent(MainActivity.this, JoinAddActivity.class);
				startActivity(intent);
		    }

		}, 3000L);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		String path = Environment.getExternalStorageDirectory().toString() + GAME_DIR;
		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
