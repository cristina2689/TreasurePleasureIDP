package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.GO_BACK_HINT;
import static org.project.treasurepleasure.Constants.GO_BACK_LATITUDE;
import static org.project.treasurepleasure.Constants.GO_BACK_LONGITUDE;
import static org.project.treasurepleasure.Constants.GO_BACK_TREASURE_URL;
import static org.project.treasurepleasure.Constants.UPLOAD_FILE_PATH;
import static org.project.treasurepleasure.Constants.game_id;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class AddTreasure extends ActionBarActivity implements OnMapReadyCallback, OnMapClickListener, LocationListener {

	double latitude, longitude;
	String selectedImagePath;
	private GoogleMap map;
	private LocationManager locationManager;
	private Location testLocation;

	private static final int SELECT_PHOTO = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_treasure);

		// Compute GPS Coordinates - Open GPS and get location

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			String data = "GPS is closed!";
			Toast.makeText(getApplicationContext(), (String) data, Toast.LENGTH_LONG).show();
			this.buildAlertMessageNoGps();
		}
		List<String> providers = locationManager.getProviders(true);

		// The real coord handling happen in onLocationChanged(Location
		// location)

		for (int i = providers.size() - 1; i >= 0; i--) {
			testLocation = locationManager.getLastKnownLocation(providers.get(i));
			if (testLocation != null)
				break;
		}

		// -----------------------------------------------------------
		final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();

		mapFragment.getMapAsync(this);
		map.setOnMapClickListener(this);

		if (map != null) {
			LatLng zoomPosition = new LatLng(testLocation.getLatitude(), testLocation.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomPosition, 17));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomPosition, 17));
			map.setMyLocationEnabled(true);
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}

	}

	public void addTreasure(View view) {
		String hint = ((EditText) findViewById(R.id.hintEditText)).getText().toString();
		String treasure_url = Constants.SERVER_URL + Constants.game_title + "_" + Constants.game_id + "/" + selectedImagePath.substring(selectedImagePath.lastIndexOf('/') + 1);

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
		String uploadDirectory = Constants.game_title + "_" + Constants.game_id;
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

	Marker marker;

	@Override
	public void onMapClick(LatLng coord) {
		if (marker != null) {
			marker.remove();
		}
		marker = map.addMarker(new MarkerOptions().position(coord).draggable(true));
		
		latitude = coord.latitude;
		longitude = coord.longitude;
		
		EditText latitudeText = (EditText) findViewById(R.id.latitudeEditText);
		EditText longitudeText = (EditText) findViewById(R.id.longitudeEditText);
		
		latitudeText.setText("" + new DecimalFormat("##.#####").format(latitude));
		longitudeText.setText("" + new DecimalFormat("##.#####").format(longitude));
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS is disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
