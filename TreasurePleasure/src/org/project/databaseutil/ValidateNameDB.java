package org.project.databaseutil;

import static org.project.treasurepleasure.Utilities.SERVER_URL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("deprecation")
public class ValidateNameDB extends AsyncTask<String, Void, String> {

	private ProgressDialog progressMessage;
	private Context context;
	private Activity activity;

	public ValidateNameDB(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressMessage = new ProgressDialog(context);
		progressMessage.setMessage("Loading ...");
		progressMessage.setIndeterminate(false);
		progressMessage.setCancelable(false);
		progressMessage.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String username = (String) params[0];
		String url = SERVER_URL + "validateUserName.php?user_name=" + username;

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));

			HttpResponse response = client.execute(request);
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuilder sb = new StringBuilder();
			String line = "";

			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			if (sb.toString().startsWith("existing")) {

				progressMessage.dismiss();
				
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						new AlertDialog.Builder(context).setTitle("New Game").setMessage("An user with this name already exists! Please choose a different user name.")
						.setCancelable(false).setNegativeButton("Close", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create().show();						
					}
				});
			} else {
				progressMessage.dismiss();
				
				// TODO: go to next activity
			}

		} catch (Exception e) {
			Log.e("ERROR:", e.getMessage());
		}

		return null;
	}

}
