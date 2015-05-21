package org.project.databaseutil.conn;

import static org.project.treasurepleasure.Constants.SERVER_URL;

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

import android.os.AsyncTask;
import android.util.Log;

public class AddTreasureConnectDB extends AsyncTask<String, Void, String> {
	
	@Override
	protected String doInBackground(String... params) {
		String url = SERVER_URL + "addTreasure.php";
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse;
		InputStream is = null;
		
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("latitude", params[0]));
			nameValuePairs.add(new BasicNameValuePair("longitude", params[1]));
			nameValuePairs.add(new BasicNameValuePair("treasure_url", params[2]));
			nameValuePairs.add(new BasicNameValuePair("game_id", params[3]));
			nameValuePairs.add(new BasicNameValuePair("hint", params[4]));

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
			} else {
				Log.e("POST:", "HTTP RESPONSE IS NULL");
			}

		} catch (Exception e) {
			Log.e("ERROR:", e.getMessage());
		}

		return "YOLO";
	}
}
