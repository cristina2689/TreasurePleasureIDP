package org.project.garbage;

import static org.project.treasurepleasure.Utilities.UPLOAD_FILE_PATH;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.project.treasurepleasure.R;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class UploadFileActivity extends ActionBarActivity {

	private static final int SELECT_PHOTO = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_file);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_file, menu);
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

	public void chooseImage(View view) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);

	}

	String selectedImagePath;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImageUri = imageReturnedIntent.getData();
				selectedImagePath = getPath(selectedImageUri);

				new Thread(new Runnable() {
					public void run() {
						uploadPhoto(selectedImagePath);

					}
				}).start();
			}

			// TextView textView = (TextView)
			// findViewById(R.id.uploadFileTextView);
			// textView.setText(getPath(selectedImageUri));
		}
	}

	private void uploadPhoto(String toUploadFilePath) {
		int serverResponseCode = 0;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String uploadFileName = toUploadFilePath.substring(toUploadFilePath.lastIndexOf('/') + 1);
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

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
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

				// dialog.dismiss();
				// ex.printStackTrace();
				//
				// runOnUiThread(new Runnable() {
				// public void run() {
				// messageText.setText("MalformedURLException Exception : check script url.");
				// Toast.makeText(UploadToServer.this, "MalformedURLException",
				// Toast.LENGTH_SHORT).show();
				// }
				// });

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (IOException e) {

				// dialog.dismiss();
				// e.printStackTrace();
				//
				// runOnUiThread(new Runnable() {
				// public void run() {
				// messageText.setText("Got Exception : see logcat ");
				// Toast.makeText(UploadToServer.this,
				// "Got Exception : see logcat ",
				// Toast.LENGTH_SHORT).show();
				// }
				// });
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
}
