package org.project.databaseutil.conn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class GetTreasuresConnectDB extends AsyncTask<String, Void, String> {

	private ProgressDialog progressMessage;
	private Context context;
	private Activity activity;
	
	
	public GetTreasuresConnectDB(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		progressMessage = new ProgressDialog(context);
//		progressMessage.setMessage("Loading ...");
//		progressMessage.setIndeterminate(false);
//		progressMessage.setCancelable(false);
//		progressMessage.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO: complete connection	
		
		return null;
	}
	
}
