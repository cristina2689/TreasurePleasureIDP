package org.project.treasurepleasure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class JoinAddActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_add);
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
