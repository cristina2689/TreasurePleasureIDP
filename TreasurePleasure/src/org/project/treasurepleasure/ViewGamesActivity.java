package org.project.treasurepleasure;

import org.project.databaseutil.GetGamesConnectDB;

import android.app.Activity;
import android.os.Bundle;

public class ViewGamesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_games);
		
		// TODO: take info from DB
		new GetGamesConnectDB(this, ViewGamesActivity.this).execute();
	}

	
}
