package org.project.treasurepleasure;

import org.project.databaseutil.conn.GetTreasuresConnectDB;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import static org.project.treasurepleasure.Utilities.JOIN_GAME;

public class JoinGameActivity extends ActionBarActivity {

	private int gameId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		
		gameId = (Integer)getIntent().getExtras().get(JOIN_GAME);
		
		new GetTreasuresConnectDB(this, JoinGameActivity.this).execute("" + gameId);
		
		// TODO: download photos
	}

}
