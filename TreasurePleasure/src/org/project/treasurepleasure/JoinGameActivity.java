package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.JOIN_GAME;

import java.util.ArrayList;
import java.util.List;

import org.project.databaseutil.classes.Treasure;
import org.project.databaseutil.conn.GetTreasuresConnectDB;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class JoinGameActivity extends ActionBarActivity {

	private int gameId;
	public List<Treasure> treasures = new ArrayList<Treasure>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		
		gameId = (Integer)getIntent().getExtras().get(JOIN_GAME);
		
		new GetTreasuresConnectDB(this, JoinGameActivity.this).execute("" + gameId);

	}

}
