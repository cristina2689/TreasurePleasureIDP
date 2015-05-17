package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.GO_BACK_HINT;
import static org.project.treasurepleasure.Constants.GO_BACK_LATITUDE;
import static org.project.treasurepleasure.Constants.GO_BACK_LONGITUDE;
import static org.project.treasurepleasure.Constants.GO_BACK_TREASURE_URL;
import static org.project.treasurepleasure.Constants.INTEGER_VALUE;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class AddGameActivity2 extends ActionBarActivity {
	String latitude, longitude, treasure_url, hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game2);

		ll = (LinearLayout) findViewById(R.id.addTreasureLinearLayout);
	}

	public void addTreasure(View view) {
		Intent intent = new Intent(this, AddTreasure.class);
		startActivityForResult(intent, INTEGER_VALUE);
	}

	private LinearLayout ll;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (INTEGER_VALUE): {
			if (resultCode == Activity.RESULT_OK) {
				latitude = data.getStringExtra(GO_BACK_LATITUDE);
				longitude = data.getStringExtra(GO_BACK_LONGITUDE);
				treasure_url = data.getStringExtra(GO_BACK_TREASURE_URL);
				hint = data.getStringExtra(GO_BACK_HINT);

				final float scale = getResources().getDisplayMetrics().density;
				int padding_10dp = (int) (10 * scale + 0.5f);
				int padding_20dp = (int) (20 * scale + 0.5f);

				// add view for added treasures
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(0, 0, 5, 10);

				Space space = new Space(AddGameActivity2.this);
				space.setLayoutParams(layoutParams);
				space.setPaddingRelative(padding_20dp, padding_10dp, padding_10dp, padding_10dp);

				// Create LinearLayout HORIZONTAL
				LinearLayout ll1 = new LinearLayout(AddGameActivity2.this);
				ll1.setOrientation(LinearLayout.HORIZONTAL);
				ll1.setLayoutParams(layoutParams);

				// Latitude
				TextView latitudeTV = new TextView(AddGameActivity2.this);
				latitudeTV.setText("Latitude");
				latitudeTV.setTypeface(null, Typeface.BOLD);

				EditText latitudeET = new EditText(AddGameActivity2.this);
				latitudeET.setText(latitude);
				latitudeET.setEnabled(false);

				ll1.addView(latitudeTV);
				ll1.addView(latitudeET);

				space = new Space(AddGameActivity2.this);
				space.setLayoutParams(layoutParams);
				space.setPaddingRelative(padding_20dp, padding_10dp, padding_10dp, padding_10dp);

				// Create LinearLayout HORIZONTAL
				LinearLayout ll2 = new LinearLayout(AddGameActivity2.this);
				ll2.setOrientation(LinearLayout.HORIZONTAL);
				ll2.setLayoutParams(layoutParams);

				// Longitude
				TextView longitudeTV = new TextView(AddGameActivity2.this);
				longitudeTV.setText("Longitude");
				longitudeTV.setTypeface(null, Typeface.BOLD);

				EditText longitudeET = new EditText(AddGameActivity2.this);
				longitudeET.setText(longitude);
				longitudeET.setEnabled(false);

				ll2.addView(longitudeTV);
				ll2.addView(longitudeET);

				space = new Space(AddGameActivity2.this);
				space.setLayoutParams(layoutParams);
				space.setPaddingRelative(padding_20dp, padding_10dp, padding_10dp, padding_10dp);

				// Hint
				TextView hintTV = new TextView(AddGameActivity2.this);
				hintTV.setText("Hint");
				hintTV.setTypeface(null, Typeface.BOLD);

				EditText hintET = new EditText(AddGameActivity2.this);
				hintET.setText(hint);
				hintET.setEnabled(false);

				ll.addView(ll1);
				ll.addView(ll2);
				ll.addView(hintTV);
				ll.addView(hintET);
			}
			break;
		}
		}
	}
	
	public void addGameFinish(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
