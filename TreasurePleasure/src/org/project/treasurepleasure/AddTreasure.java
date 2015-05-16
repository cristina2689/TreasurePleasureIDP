package org.project.treasurepleasure;

import static org.project.treasurepleasure.Utilities.GO_BACK_HINT;
import static org.project.treasurepleasure.Utilities.GO_BACK_LATITUDE;
import static org.project.treasurepleasure.Utilities.GO_BACK_LONGITUDE;
import static org.project.treasurepleasure.Utilities.GO_BACK_TREASURE_URL;
import static org.project.treasurepleasure.Utilities.UPLOAD_FILE_PATH;
import static org.project.treasurepleasure.Utilities.game_id;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.project.databaseutil.conn.AddTreasureConnectDB;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

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

@SuppressWarnings("deprecation")
public class AddTreasure extends ActionBarActivity implements OnMapReadyCallback {

	double latitude = 45.46363, longitude = 54.43636;
	String selectedImagePath;
	private GoogleMap map;

	private static final int SELECT_PHOTO = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_treasure);

		final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

		mapFragment.getMapAsync(this);
	}

	public void addTreasure(View view) {
		String hint = ((EditText) findViewById(R.id.hintEditText)).getText().toString();
		String treasure_url = Utilities.SERVER_URL + Utilities.game_title + "_" + Utilities.game_id + "/" + selectedImagePath.substring(selectedImagePath.lastIndexOf('/') + 1);

		// add in DB
		new AddTreasureConnectDB(this, AddTreasure.this).execute(String.valueOf(latitude), String.valueOf(longitude), treasure_url, String.valueOf(game_id), hint);

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
		String uploadFileName = toUploadFilePath.substring(toUploadFilePath.lastIndexOf('/') + 1);
		String uploadDirectory = Utilities.game_title + "_" + Utilities.game_id;
		File sourceFile = new File(toUploadFilePath);
		String result;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(UPLOAD_FILE_PATH);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			entityBuilder.addTextBody("upload_directory", uploadDirectory);
			entityBuilder.addBinaryBody("uploaded_file", sourceFile, ContentType.create("image/jpeg"), uploadFileName);

			HttpEntity entity = entityBuilder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			result = EntityUtils.toString(httpEntity);

			android.util.Log.i("result", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Override
	public void onMapReady(GoogleMap arg0) {
		
	}

}
