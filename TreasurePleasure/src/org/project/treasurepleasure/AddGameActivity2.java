package org.project.treasurepleasure;

import static org.project.treasurepleasure.Utilities.GO_BACK;
import static org.project.treasurepleasure.Utilities.INTEGER_VALUE;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class AddGameActivity2 extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game2);
		
		
		
		// TODO: move somewhere else
//		new AddGameConnectDB(this, AddGameActivity2.this).execute(
//				(String) getIntent().getExtras().get(AGA_TITLE), 
//				(String) getIntent().getExtras().get(AGA_DESCRIPTION), 
//				(String) getIntent().getExtras().get(AGA_START_DATE), 
//				(String) getIntent().getExtras().get(AGA_END_DATE), 
//				(String) getIntent().getExtras().get(AGA_GAME_MASTER));
	}

	public void addTreasure(View view) {
		Intent intent = new Intent(this, AddTreasure.class);
		startActivityForResult(intent, INTEGER_VALUE);
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (INTEGER_VALUE) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	      String newText = data.getStringExtra(GO_BACK);
	      
	      // TODO: add view for added treasures
	      
	      } 
	      break; 
	    } 
	  } 
	}
	
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.add_game_activity2, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }
}
