package org.project.databaseutil.conn;

import static org.project.treasurepleasure.Utilities.SERVER_URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.project.databaseutil.classes.Treasure;
import org.project.treasurepleasure.JoinGameActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class GetTreasuresConnectDB extends AsyncTask<String, Void, Void> {

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
		progressMessage = new ProgressDialog(context);
		progressMessage.setMessage("Loading ...");
		progressMessage.setIndeterminate(false);
		progressMessage.setCancelable(false);
		progressMessage.show();
	}

	String treasuresFromDB = "";
	String directoryName = null;

	@Override
	protected Void doInBackground(String... params) {
		String gameId = (String) params[0];
		String url = SERVER_URL + "getTreasures.php?game_id=" + gameId;

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

			String[] lines = sb.toString().split("\n");
			for (int i = 0; i < lines.length - 3; i++) {
				treasuresFromDB += lines[i] + "\n";
			}

			if (!treasuresFromDB.equals("null")) {
				JSONArray ja = null;

				try {
					ja = new JSONArray(treasuresFromDB);
					for (int i = 0; i < ja.length(); i++) {
						Treasure treasure = new Treasure(ja.getJSONObject(i));
						((JoinGameActivity) activity).treasures.add(treasure);

						File directory = null;
						if (directoryName == null) {
							directoryName = treasure.getDirectory();

							// create directory
							directory = new File(Environment.getExternalStorageDirectory().toString() + "/treasure_pleasure/" + directoryName);
							if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
								directory.mkdirs();
							}
						}

						// download treasure photo
						String[] downloadParams = new String[2];
						downloadParams[0] = directory.getAbsolutePath();
						downloadParams[1] = treasure.getTreasureUrl();

						new DownloadPhoto().execute(downloadParams);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			progressMessage.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// after all treasures have been taken from db, download photos
		List<Treasure> treasures = ((JoinGameActivity) activity).treasures;

		// create folder for files
		// String directory = treasures.get(0).getDirectory();

	}

	class DownloadPhoto extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String directory = params[0];
			String url = params[1];

			return null;
		}

	}
}
