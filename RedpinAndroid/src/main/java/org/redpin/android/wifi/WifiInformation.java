package org.redpin.android.wifi;

import java.util.List;
import android.net.wifi.ScanResult;
import android.util.Log;

import org.redpin.android.ui.MapViewActivity;

public class WifiInformation {

	List<ScanResult> currentScanList;
	private static long numOfScans = 0;
	
	public WifiInformation()
	{
		currentScanList = null;
	}
	
	public void updateInformation(List<ScanResult> wifiScanList) {

		currentScanList = wifiScanList;
		int j = 0;

		numOfScans++;

		for (int i = 0; i < wifiScanList.size(); i++) {
			Log.i("wj", "numOfScans: " + numOfScans
					+ ", BSSID: " + currentScanList.get(i).BSSID
					+ ", SSID: " + currentScanList.get(i).SSID
					+ ", level: " + currentScanList.get(i).level);

		}
		FingerprintDatabase testDB = new FingerprintDatabase();
		String locationString = testDB.getLocationByBSSID("84:24:8d:3f:a2:e0");
		locationString = testDB.getLocationByBSSID("84:24:8d:3f:7c:e0");
		locationString = testDB.getLocationByBSSID("84:24:8d:3f:8a:d1");


		//----------------------------------------------------------

		// Do some processing here

		//----------------------------------------------------------

		//If decided point
		//{
            //Set Location Marker to the point

		// Here is the point of 6-004 = (x = 306,y = 536) in pixels
		//MapViewActivity.setMarkerLocation(306, 536);

		// Here is the point of 6-005 = (x = 410,y = 346) in pixels
		//MapViewActivity.setMarkerLocation(410, 346);

		// Here is the point of 6-010 = (x = 880,y = 510) in pixels
		//MapViewActivity.setMarkerLocation(880, 510);

	    //}

	}
}
