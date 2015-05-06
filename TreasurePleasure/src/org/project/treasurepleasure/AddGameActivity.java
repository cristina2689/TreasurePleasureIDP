package org.project.treasurepleasure;

import static org.project.treasurepleasure.Utilities.AGA_DESCRIPTION;
import static org.project.treasurepleasure.Utilities.AGA_END_DATE;
import static org.project.treasurepleasure.Utilities.AGA_GAME_MASTER;
import static org.project.treasurepleasure.Utilities.AGA_START_DATE;
import static org.project.treasurepleasure.Utilities.AGA_TITLE;

import java.util.Calendar;

import org.project.databaseutil.ValidateNameDB;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

	public void addGameNext(View view) {
		String title = ((EditText) findViewById(R.id.gametitle)).getText().toString();
		String description = ((EditText) findViewById(R.id.treasurestory)).getText().toString();
		String gameMaster = ((EditText) findViewById(R.id.gameMasterEditText)).getText().toString();
		
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

		new ValidateNameDB(this, AddGameActivity.this)
			.setParams(title, description, startDate.toString(), endDate.toString(), gameMaster)
			.execute(gameMaster);
		
	}
	
}
