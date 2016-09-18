/**
 *  Filename: MapViewActivity.java (in org.repin.android.ui)
 *  This file is part of the Redpin project.
 */

package org.redpin.android.ui;

import org.redpin.android.ApplicationContext;
import org.redpin.android.R;
import org.redpin.android.core.Location;
import org.redpin.android.core.Map;
import org.redpin.android.ui.list.MainListActivity;
import org.redpin.android.ui.list.SearchListActivity;
import org.redpin.android.ui.mapview.MapView;

import org.redpin.android.wifi.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

/********************************************************************************
 * Main activity of the client that displays maps and locations
 *
 ********************************************************************************/
public class MapViewActivity extends Activity {
	private static final String TAG = MapViewActivity.class.getSimpleName();
	MapView mapView;
	TextView mapName;
	ProgressDialog progressDialog;
	Location mLocation;

	private RelativeLayout mapTopBar;

	WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	Timer timer;
	ScanRepeater myScanRepeater;
	Context myContext;
	WifiInformation myWifiInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.init(getApplicationContext());

		//startWifiSniffer();

		mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		myContext=getApplicationContext();
		myWifiInfo=new WifiInformation();

		wifiReciever = new WifiScanReceiver(mainWifiObj,myContext,myWifiInfo);

		mainWifiObj.startScan();

		//***************important code Start registering and start wifi activities

		//Setting up scan repeater
		if(timer != null)
		{
			timer.cancel();
		}
		timer = new Timer();
		myScanRepeater = new ScanRepeater(mainWifiObj);
		timer.schedule(myScanRepeater, 1, 10);//Starts after 1 ms , then repeat every 10ms


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

		restoreState();
		show();

	}

	protected void onPause() {
		unregisterReceiver(wifiReciever);
		timer.cancel();

		super.onPause();
	}

	protected void onResume() {
		registerReceiver(wifiReciever, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		//Setting up scan repeater
		if(timer != null)
		{
			timer.cancel();
		}
		timer = new Timer();
		myScanRepeater = new ScanRepeater(mainWifiObj);
		timer.schedule(myScanRepeater, 1, 10);

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

}
