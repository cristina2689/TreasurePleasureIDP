package org.project.treasurepleasure;

import static org.project.treasurepleasure.Utilities.SERVER_URL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

@SuppressWarnings("deprecation")
public class AddGameActivity extends ActionBarActivity {

	private Button mStartDate;
	private Button mEndDate;
	private int mYear;
	private int mMonth;
	private int mDay;
	private StringBuilder startDate;
	private StringBuilder endDate;

	// the callback received when the user "sets" the date in the dialog
	static final int START_DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;

	// Initialize a DatePickerDialog
	private DatePickerDialog.OnDateSetListener mStartDateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay(START_DATE_DIALOG_ID);
		}
	};

	private DatePickerDialog.OnDateSetListener mEndDateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay(END_DATE_DIALOG_ID);
		}
	};

	class GameTitleDescriptionTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			EditText gameET = (EditText) findViewById(R.id.gametitle);
			EditText descriptionET = (EditText) findViewById(R.id.treasurestory);

			if (gameET.getText().toString().length() != 0 && descriptionET.getText().toString().length() != 0) {
				Button nextButton = (Button) findViewById(R.id.addGameNext);
				nextButton.setClickable(true);
				nextButton.setTextColor(Color.parseColor("#000000"));
			} else {
				Button nextButton = (Button) findViewById(R.id.addGameNext);
				nextButton.setClickable(false);
				nextButton.setTextColor(Color.parseColor("#505050"));
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game);

		mStartDate = (Button) findViewById(R.id.selectstartdate);
		mEndDate = (Button) findViewById(R.id.selectenddate);

		// add a click listener to the button
		mStartDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID);
			}
		});

		mEndDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(END_DATE_DIALOG_ID);
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date (this method is below)
		updateDisplay(START_DATE_DIALOG_ID);

		EditText gameTitleET = (EditText) findViewById(R.id.gametitle);
		EditText descriptionET = (EditText) findViewById(R.id.treasurestory);

		GameTitleDescriptionTextWatcher gameTitleDescriptionTextWatcher = new GameTitleDescriptionTextWatcher();
		gameTitleET.addTextChangedListener(gameTitleDescriptionTextWatcher);
		descriptionET.addTextChangedListener(gameTitleDescriptionTextWatcher);

	}

	// updates the date in the TextView
	private void updateDisplay(int id) {
		if (id == START_DATE_DIALOG_ID) {
			startDate = new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" ");
		} else if (id == END_DATE_DIALOG_ID) {
			endDate = new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" ");
		}
	}

	// create callback function for Dialog call
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mStartDateListener, mYear, mMonth, mDay);
		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mEndDateListener, mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addGameNext(View view) {
		String title = ((EditText) findViewById(R.id.gametitle)).getText().toString();
		String description = ((EditText) findViewById(R.id.treasurestory)).getText().toString();

		if (startDate == null || endDate == null) {
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}

		if (startDate == null) {
			startDate = new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" ");
		}

		if (endDate == null) {
			endDate = new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" ");
		}

		new AddGameConnectDB().execute(title, description, startDate.toString(), endDate.toString(), MainActivity.USER_ID);
	}

	// communication with DB
	private ProgressDialog progressMessage;

	class AddGameConnectDB extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressMessage = new ProgressDialog(AddGameActivity.this);
			progressMessage.setMessage("Loading ...");
			progressMessage.setIndeterminate(false);
			progressMessage.setCancelable(false);
			progressMessage.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			String url = SERVER_URL + "addGame.php";
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;
			InputStream is = null;

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
				nameValuePairs.add(new BasicNameValuePair("title", params[0]));
				nameValuePairs.add(new BasicNameValuePair("description", params[1]));
				nameValuePairs.add(new BasicNameValuePair("start_date", params[2]));
				nameValuePairs.add(new BasicNameValuePair("end_date", params[3]));
				nameValuePairs.add(new BasicNameValuePair("user_id", params[4]));

				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				httpResponse = httpClient.execute(httpPost);

				// check response
				if (httpResponse != null) {
					is = httpResponse.getEntity().getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					StringBuilder sb = new StringBuilder();
					String line = "";

					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}

					is.close();
					result = sb.toString();

					if (result.startsWith("success")) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								 progressMessage.dismiss();

								if (!isFinishing()) {
									new AlertDialog.Builder(AddGameActivity.this)
										.setTitle("New Game")
										.setMessage("Game created successfully!")
										.setCancelable(false)
										.setPositiveButton("OK",
													new OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															dialog.dismiss();															
														}
													})
										.create().show();
								}
							}
						});
					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								 progressMessage.dismiss();

								if (!isFinishing()) {
									new AlertDialog.Builder(AddGameActivity.this)
										.setTitle("New Game")
										.setMessage("Failed creating new game. Please try once again")
										.setCancelable(false)
										.setPositiveButton("OK", new OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {	
												dialog.dismiss();												
											}
										})
										.create().show();
								}
							}
						});
					}
				} else {
					Log.e("POST:", "HTTP RESPONSE IS NULL");
				}

			} catch (Exception e) {
				Log.e("ERROR:", e.getMessage());
			}

			return "SOLO";
		}

	}
}
