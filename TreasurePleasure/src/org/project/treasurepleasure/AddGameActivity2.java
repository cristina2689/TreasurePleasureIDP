package org.project.treasurepleasure;

import static org.project.treasurepleasure.Utilities.GO_BACK_HINT;
import static org.project.treasurepleasure.Utilities.GO_BACK_LATITUDE;
import static org.project.treasurepleasure.Utilities.GO_BACK_LONGITUDE;
import static org.project.treasurepleasure.Utilities.GO_BACK_TREASURE_URL;
import static org.project.treasurepleasure.Utilities.INTEGER_VALUE;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class AddGameActivity2 extends ActionBarActivity {
	String latitude, longitude, treasure_url, hint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game2);
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
	      latitude = data.getStringExtra(GO_BACK_LATITUDE);
	      longitude = data.getStringExtra(GO_BACK_LONGITUDE);
	      treasure_url = data.getStringExtra(GO_BACK_TREASURE_URL);
	      hint = data.getStringExtra(GO_BACK_HINT);
	      
	      // TODO: add view for added treasures
	      
	      } 
	      break; 
	    } 
	  } 
	}
}
