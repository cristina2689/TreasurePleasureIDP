package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.JOIN_GAME;
import static org.project.treasurepleasure.Constants.OPEN_CAMERA;
import static org.project.treasurepleasure.Constants.waitCube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.project.databaseutil.classes.Treasure;
import org.project.databaseutil.conn.GetTreasuresConnectDB;
import org.project.treasurepleasure.camera.CameraActivity;
import org.project.treasurepleasure.camera.LatLong;
import org.project.treasurepleasure.camera.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings("deprecation")
public class JoinGameActivity extends Activity implements LocationListener, OnMapReadyCallback, OnMapClickListener {

	public static TextView tex1; // for test
	public static TextView tex2; // for test
	public static LatLong hint; // get from database
	public static boolean back;
	private LocationManager locationManager;
	private Location testLocation;
	private ProgressDialog progress;
	private Preview prev;
	private double thetaV;
	private double thetaH;
	private int gameId;
	public List<Treasure> treasures = new ArrayList<Treasure>();

	public boolean flag = false;

	// Calculate angle for camera and send to Utils
	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;

		Preview(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.setFixedSize(1, 1);
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			thetaV = -1;
			thetaH = -1;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d("SEARCH ", "surfaceCreated");
			mCamera = Camera.open();
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d("SEARCH ", "surfaceDestroyed");
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			ViewGroup.LayoutParams params = prev.getLayoutParams();
			params.width = 0;
			params.height = 0;
			prev.setLayoutParams(params);
			// Compute camera angle
			Camera.Parameters p = mCamera.getParameters();
			int zoom = p.getZoomRatios().get(p.getZoom()).intValue();
			Camera.Size sz = p.getPreviewSize();
			double aspect = (double) sz.width / (double) sz.height;
			thetaV = Math.toRadians(p.getVerticalViewAngle());
			thetaH = 2d * Math.atan(aspect * Math.tan(thetaV / 2));
			thetaV = 2d * Math.atan(100d * Math.tan(thetaV / 2d) / zoom);
			thetaH = 2d * Math.atan(100d * Math.tan(thetaH / 2d) / zoom);

			Log.d("Theta H: ", " = " + Math.toDegrees(thetaH));
			Log.d("Theta V: ", " = " + Math.toDegrees(thetaV));

			mCamera.setParameters(p);
			mCamera.startPreview();
			// Send camera angle to Utils
			Utils.cameraViewAngle = Math.toDegrees(thetaV);
			Utils.deltaDistance = 50;
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Utils.width = metrics.widthPixels;
			hint = new LatLong(44.4361, 26.0484);
		}
	}

	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);

		gameId = (Integer) getIntent().getExtras().get(JOIN_GAME);

		new GetTreasuresConnectDB(this, JoinGameActivity.this).execute("" + gameId);

		progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.show();

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

		// --------------------------------------------------------------------------------
		final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_treasure);
		map = mapFragment.getMap();

		mapFragment.getMapAsync(this);
		map.setOnMapClickListener(this);

		if (map != null) {
			LatLng zoomPosition = new LatLng(testLocation.getLatitude(), testLocation.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomPosition, 10));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomPosition, 10));
			map.setMyLocationEnabled(true);
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}
		
		// --------------------------------------------------------------------------------
		back = false;
		prev = new Preview(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (prev.mCamera != null) {
			prev.mCamera.stopPreview();
			prev.mCamera.release();
			prev.mCamera = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (back)
			finish();
		if (prev.mCamera != null) {
			prev.mCamera.stopPreview();
			prev.mCamera.release();
			prev.mCamera = null;
			finish();
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		Utils.userLocation = location;

		progress.dismiss();

		if (Utils.userLocation != null && flag && !waitCube) {
			for (Treasure treasure : treasures) {
				hint = new LatLong(treasure.getLatitude(), treasure.getLongitude());
				if (Utils.isNear(hint)) {
					waitCube = true;
					
					Intent intent = new Intent(this, CameraActivity.class);
					System.out.println("Started camera activity\n");
					intent.putExtra(OPEN_CAMERA, treasure.getFileLocation());
					Log.d("ok", "started camera activity");
					startActivity(intent);
					
					treasures.remove(treasure);
					break;
				}
			}
			
			// if found all treasures, game over
			if (treasures.size() == 0) {
				Toast toast = Toast.makeText(getApplicationContext(), "You found all treasures!", Toast.LENGTH_LONG);
				toast.show();
				
				finish();
			}
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("GPS", "disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("GPS", "enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("GPS", "status");
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

	@Override
	public void onMapClick(LatLng arg0) {
	}

	@Override
	public void onMapReady(GoogleMap arg0) {
	}
}
