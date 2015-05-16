package org.project.treasurepleasure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity { 

	public static String USER_ID = "123456";
	
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

}
