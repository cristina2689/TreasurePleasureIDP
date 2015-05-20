package org.project.treasurepleasure.camera;

import org.project.treasurepleasure.R;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class AnswerActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answer);
		try {
			Utils.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
