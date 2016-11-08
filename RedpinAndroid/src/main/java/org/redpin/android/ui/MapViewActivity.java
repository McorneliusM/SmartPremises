/**
 *  Filename: MapViewActivity.java (in org.repin.android.ui)
 *  This file is part of the Redpin project.
 */

package org.redpin.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.redpin.android.ApplicationContext;
import org.redpin.android.R;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.ui.list.MainListActivity;
import org.redpin.android.ui.list.SearchListActivity;
import org.redpin.android.ui.mapview.MapView;
import org.redpin.android.wifi.ScanRepeater;
import org.redpin.android.wifi.WifiInformation;
import org.redpin.android.wifi.WifiScanReceiver;

import java.util.Random;
import java.util.Timer;

/********************************************************************************
 * Main activity of the client that displays maps and locations
 *
 ********************************************************************************/
public class MapViewActivity extends Activity {
	private static final String TAG = MapViewActivity.class.getSimpleName();
	private static MapView mapView = null;
	TextView mapName;
	ProgressDialog progressDialog;
	Location mLocation;
	ImageButton locateButton;

	private RelativeLayout mapTopBar;

	WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	Timer timer;
	ScanRepeater myScanRepeater;
	Context myContext;
	WifiInformation myWifiInfo;

	public static int currentMarkerX = 0;
	public static int currentMarkerY = 0;

	private static boolean WifiIsOnShown = false;
	public static float lastNumOfSteps = 0, currentNumOfSteps = 0;
	public static int first3Runs = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.init(getApplicationContext());

		//startWifiSniffer();

		mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		//------------------------------------------
		//         Try to turn on wifi
		//------------------------------------------
		boolean WifiOn = mainWifiObj.isWifiEnabled();
		if (WifiOn)
		{
			if (WifiIsOnShown == false)
			{
				Toast.makeText(MapViewActivity.this, "Wifi is On", Toast.LENGTH_SHORT).show();
				WifiIsOnShown = true;
			}
		}
		else
		{
			Toast.makeText(MapViewActivity.this, "Wifi turned On", Toast.LENGTH_SHORT).show();
			mainWifiObj.setWifiEnabled(true);
		}

		myContext=getApplicationContext();
		myWifiInfo=new WifiInformation();

		wifiReciever = new WifiScanReceiver(mainWifiObj,myContext,myWifiInfo);

		mainWifiObj.startScan();

		SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// Step Counter
		sManager.registerListener(new SensorEventListener() {

									  @Override
									  public void onSensorChanged(SensorEvent event) {
										  currentNumOfSteps = event.values[0];
									  }

									  @Override
									  public void onAccuracyChanged(Sensor sensor, int accuracy) {

									  }
								  }, sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
				SensorManager.SENSOR_DELAY_UI);

		//***************important code Start registering and start wifi activities

		//Setting up scan repeater
		if(timer != null)
		{
			timer.cancel();
		}
		timer = new Timer();
		myScanRepeater = new ScanRepeater(mainWifiObj);
		timer.schedule(myScanRepeater, 0, 1200);//Starts after 0 ms , then repeat every 1200ms


		//********************************************************************************

		setContentView(R.layout.map_view);
		mapView = (MapView) findViewById(R.id.map_view_component);
		mapName = (TextView) findViewById(R.id.map_name);
		mapTopBar = (RelativeLayout) findViewById(R.id.map_topbar);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mapTopBar.setVisibility(View.GONE);
		}

		mapView.showMap(null, getApplicationContext());
		mapView.setModifiable(true);

//		final Intent starterIntent = getIntent();

		locateButton = (ImageButton) findViewById(R.id.locate_button);
		locateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				Random rand = new Random();
//				int x = rand.nextInt(1000);
//				int y = rand.nextInt(1000);
				MapViewActivity.setMarkerLocation(currentMarkerX, currentMarkerY);

//				finish();
//				startActivity(starterIntent);
			}
		});

		restoreState();
		show();
	}

	protected void onPause() {
		unregisterReceiver(wifiReciever);
		timer.cancel();

		super.onPause();
	}

	protected void onResume() {
		mapView.invalidate();

		registerReceiver(wifiReciever, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		//Setting up scan repeater
		if(timer != null)
		{
			timer.cancel();
		}
		timer = new Timer();
		myScanRepeater = new ScanRepeater(mainWifiObj);
		timer.schedule(myScanRepeater, 0, 1200);

		super.onResume();
	}

	/*************************************************************************
	 * Starts the setting screen
	 *
	 * @param target {@link View} that called this method
	 **************************************************************************/
	public void button_Settings(View target) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private static final String pref_scrollX = "x";
	private static final String pref_scrollY = "y";

	/************************************************************************
	 * Restores the {@link MapView} to show the last shown map
	 ***************************************************************************/
	private void restoreState() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		int scrollX = preferences.getInt(pref_scrollX, 0);
		int scrollY = preferences.getInt(pref_scrollY, 0);

		if (getIntent().getData() == null) {
			preferences.edit().clear().commit();
			mapView.requestScroll(scrollX, scrollY, true);
		}

		preferences.edit().clear().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Shows the {@link MapView}
	 */
	protected void show() {
		getIntent().resolveType(this);
		mapView.show(getIntent().getData());
		Map m = mapView.getCurrentMap();

		if (m != null) {
			mapName.setText(m.getMapName());
		}
	}

	/**
	 * Refresh map and prevents white screen issue
	 * it is called periodically by updateInformation
	 */
	public static void refreshMap() {
		mapView.showImage("myMap");
	}


	/**
	 * Displays the current location on the map
	 * @param loc The current estimated location
	 */
	protected void showLocation(Location loc) {
		if (loc == null)
			return;

		Map m = (Map) loc.getMap();

		if (m != null) {
			mapName.setText(m.getMapName());
		}

		mapView.showLocation(loc, true);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		show();
	}

	/*****************************************************************************************
	 * Initiates a scan for a new measurement, creates a location and afterwards displays it on the map
	 *****************************************************************************************/
	private void addNewLocation() {

		Map currentMap = mapView.getCurrentMap();

		if (currentMap == null) {
			new AlertDialog.Builder(this).setPositiveButton(
					android.R.string.ok, null)
					.setTitle(R.string.map_view_title).setMessage(
					R.string.map_view_no_map_selected).create().show();
			Log.w(TAG, "addNewLocation: no current map shown");
			return;
		}

		progressDialog.show();

		Location location = new Location();

		location.setMap(currentMap);

		mLocation = location;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, R.id.options_menu_search, 0,
				R.string.options_menu_search_text).setIcon(
				R.drawable.menu_search);
		menu.add(0, R.id.options_menu_listview, 0,
				R.string.options_menu_listview_text).setIcon(
				R.drawable.menu_list_black);
		menu.add(0, R.id.options_menu_add_map, 0,
				R.string.options_menu_add_map_text).setIcon(
				R.drawable.menu_addmap_black);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.options_menu_add_map:
				Intent newmap = new Intent(this, NewMapActivity.class);
				startActivity(newmap);
				return true;
			case R.id.options_menu_listview:
				Intent mainlist = new Intent(this, MainListActivity.class);
				startActivity(mainlist);
				return true;
			case R.id.options_menu_search:
				Intent search = new Intent(this, SearchListActivity.class);
				startActivity(search);
				return true;
		}
		return false;
	}

	public static void setMarkerLocation(int x, int y)
	{
		setMarkerLocation("Me", x, y);
	}

	public static void setMarkerLocation(String id , int x, int y)
	{
		if(first3Runs!=0 || currentNumOfSteps-lastNumOfSteps>=2)
		{
			first3Runs--;
			lastNumOfSteps = currentNumOfSteps;

			if( (currentMarkerX != x) || (currentMarkerY != y) )
			{
				currentMarkerX = x;
				currentMarkerY = y;
				if(mapView != null)
				{
					if(mapView.getCurrentActiveMarker() != null)
					{
						mapView.getCurrentActiveMarker().setMapXcord(currentMarkerX);
						mapView.getCurrentActiveMarker().setMapYcord(currentMarkerY);
						mapView.showLocation(mapView.getCurrentActiveMarker(), true);
					}
				}
			}
		}
	}
}
