package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.GAME_DIR;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void joinGame(View view) {
		Intent intent = new Intent(this, ViewGamesActivity.class);
		startActivity(intent);
	}

	public void addGame(View view) {
		Intent intent = new Intent(this, AddGameActivity.class);
		startActivity(intent);
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
