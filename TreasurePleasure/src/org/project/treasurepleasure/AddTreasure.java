package org.project.treasurepleasure;

import static org.project.treasurepleasure.Utilities.GO_BACK_HINT;
import static org.project.treasurepleasure.Utilities.GO_BACK_LATITUDE;
import static org.project.treasurepleasure.Utilities.GO_BACK_LONGITUDE;
import static org.project.treasurepleasure.Utilities.GO_BACK_TREASURE_URL;
import static org.project.treasurepleasure.Utilities.UPLOAD_FILE_PATH;
import static org.project.treasurepleasure.Utilities.game_id;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.project.databaseutil.AddTreasureConnectDB;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

// TODO: integrate google maps
/*
 * info pentru google maps:
 * - https://github.com/anca1ant/IOC_Project/blob/master/myCarpooling/res/layout/activity_passenger.xml
 * - https://github.com/anca1ant/IOC_Project/blob/master/myCarpooling/src/com/BARcode/mycarpooling/AdvancedOptions.java
 */

/*
 * ma gandesc asa: o sa aiba o latitudine si o longitudine care ii vin de pe harta, 
 * o sa ii apara in casutele alea doua ce apar aici (daca nu apar cand citesti asta, vor aparea)
 * si o sa poata sa isi adauge poza pe care o vrea pentru comoara respectiva.
 * apoi o sa puna chestiile astea cu putExtra sa dea inapoi la AddGameActivity_2 care e parintele.
 * 
 * in momentul in care da click pe butonul de aici (addTreasure), o sa i se bage intai in baza de date comoara,
 * apoi o sa se intoarca la parinte sa adauge si alte comori.
 */

public class AddTreasure extends ActionBarActivity {

	double latitude = 45.46363, longitude = 54.43636;
	String selectedImagePath;

	private static final int SELECT_PHOTO = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_treasure);

	}

	public void addTreasure(View view) {
		String hint = ((EditText) findViewById(R.id.hintEditText)).getText().toString();
		String treasure_url = Utilities.SERVER_URL + Utilities.game_title + "_" + Utilities.game_id 
				+ "/" + selectedImagePath.substring(selectedImagePath.lastIndexOf('/') + 1);
				
		// add in DB
		new AddTreasureConnectDB(this, AddTreasure.this).execute(
				String.valueOf(latitude), 
				String.valueOf(longitude),
				treasure_url, 
				String.valueOf(game_id), 
				hint);

		// send back info
		Intent resultIntent = new Intent();
		resultIntent.putExtra(GO_BACK_LATITUDE, String.valueOf(latitude));
		resultIntent.putExtra(GO_BACK_LONGITUDE, String.valueOf(longitude));
		resultIntent.putExtra(GO_BACK_TREASURE_URL, selectedImagePath);
		resultIntent.putExtra(GO_BACK_HINT, hint);
		setResult(Activity.RESULT_OK, resultIntent);

		finish();
	}

	public void addTreasureImage(View view) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImageUri = imageReturnedIntent.getData();
				selectedImagePath = getPath(selectedImageUri);

				((TextView) findViewById(R.id.addTreasureImageTextView)).setText(selectedImagePath);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						 new UploadPhoto().execute(selectedImagePath);
					}
				});
			}

		}
	}

	class UploadPhoto extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			uploadPhoto(params[0]);
			
			return null;
		}
		
	}
	
	private void uploadPhoto(String toUploadFilePath) {
		int serverResponseCode = 0;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String uploadFileName = toUploadFilePath.substring(toUploadFilePath.lastIndexOf('/') + 1);
		String uploadDirectory = Utilities.game_title + "_" + Utilities.game_id;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(toUploadFilePath);

		if (!sourceFile.isFile()) {
			Log.e("uploadFile", "Source File not exist :" + UPLOAD_FILE_PATH + "" + uploadFileName);
		} else {
			try {
				System.setProperty("http.keepAlive", "false");

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(UPLOAD_FILE_PATH);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", uploadFileName);
				conn.setRequestProperty("upload_directory", uploadDirectory);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("upload_directory", uploadDirectory));								
				dos = new DataOutputStream(conn.getOutputStream());
				
				dos.writeBytes(getQuery(params));
				
				dos.writeBytes(twoHyphens + boundary + lineEnd);
//				dos.writeBytes("Content-Disposition: form-data; name=\"upload_directory\"" + lineEnd + uploadDirectory + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + uploadFileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					// runOnUiThread(new Runnable() {
					// public void run() {
					//
					// String msg =
					// "File Upload Completed.\n\n See uploaded file here : \n\n"
					// + UPLOAD_FILE_PATH + uploadFileName;
					//
					// messageText.setText(msg);
					// Toast.makeText(UploadToServer.this,
					// "File Upload Complete.", Toast.LENGTH_SHORT).show();
					// }
					// });
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (IOException e) {

				Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
			}
			// dialog.dismiss();
			// return serverResponseCode;

		} // End else block
	}

	// get image path
	public String getPath(Uri uri) {
		int column_index;
		String imagePath;
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		imagePath = cursor.getString(column_index);

		return imagePath;
	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
	
}
