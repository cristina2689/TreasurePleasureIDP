package org.project.databaseutil;

import static org.project.treasurepleasure.Utilities.SERVER_URL;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

// TODO: delete this class

@SuppressWarnings("deprecation")
public class GetGamesConnectDB extends AsyncTask<String, Void, String> {

	private ProgressDialog progressMessage;
	private Activity activity;
	private Context context;
	
	public GetGamesConnectDB(Activity activity, Context context) {
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
		String url = SERVER_URL + "getAllGames.php";
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse;
		
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			
			// TODO: make connection
			
		} catch (Exception e) {
			Log.e("ERROR:", e.getMessage());
		}
		
		return null;
	}
	
}
