package org.project.databaseutil;

import static org.project.treasurepleasure.Utilities.SERVER_URL;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.project.treasurepleasure.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("deprecation")
public class AddGameConnectDB extends AsyncTask<String, Void, String> {

	private ProgressDialog progressMessage;
	private Activity activity;
	private Context context;

	public AddGameConnectDB(Activity activity, Context context) {
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
			nameValuePairs.add(new BasicNameValuePair("user_name", params[4]));

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
				
				if (!result.startsWith("error")) {
					// take user_id and game_id
					String[] results = result.split("_");
					Utilities.user_id = Integer.parseInt(results[0]);
					
					results = results[1].split("\n");
					Utilities.game_id = Integer.parseInt(results[0]);
				}

				if (!result.startsWith("error")) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							progressMessage.dismiss();

							new AlertDialog.Builder(context).setTitle("New Game").setMessage("Game created successfully!").setCancelable(false)
									.setPositiveButton("OK", new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).create().show();

						}
					});
				} else {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							progressMessage.dismiss();

							new AlertDialog.Builder(context)
								.setTitle("New Game")
								.setMessage("Failed creating new game. Please try once again").setCancelable(false)
								.setPositiveButton("OK", new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).create().show();

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
