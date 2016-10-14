package org.redpin.android.wifi;

import android.util.Log;

/**
 * Created by Admin on 9/18/2016.
 * To keep wifi fingerprint database
 */

public class FingerprintDatabase {

    public int numOfLocations;
    public MeasurementPerLocation[] myMeasurementPerLocationArray;

    public FingerprintDatabase(int inputNumOfLocations)
    {
        numOfLocations = inputNumOfLocations;
        myMeasurementPerLocationArray = new MeasurementPerLocation[inputNumOfLocations];
    }

    public void fillUpEachMeasurementPerLocation(int index, String locationName, int inputNumOfWifiPoints)
    {
        myMeasurementPerLocationArray[index] = new MeasurementPerLocation(locationName, inputNumOfWifiPoints);
    }


    public String getLocationByBSSID(String BSSID)
    {
        boolean found = false;
        String retVal = "";
        for (int i=0; i<3; i++)
        {
//            if (BSSID.equals(wifiData3points[i][0]))
//            {
//                found = true;
//                retVal = wifiData3points[i][1];
//            }
        }

        if (found)
        {
            Log.i("debug", "#######################" + BSSID + retVal);
            return retVal;
        }
        else
        {
            return "notFound";
        }
    }
}
