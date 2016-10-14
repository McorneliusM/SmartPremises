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
        //Giving how many locations stored in this database
        numOfLocations = inputNumOfLocations;
        myMeasurementPerLocationArray = new MeasurementPerLocation[inputNumOfLocations];
    }

    public void fillUpEachMeasurementPerLocation(int index, String locationName, int inputNumOfWifiPoints)
    {
        // Giving name and how many wifi point in one location
        myMeasurementPerLocationArray[index] = new MeasurementPerLocation(locationName, inputNumOfWifiPoints);

    }

    //-- for debugging
    public void printFingerprintDb()
    {
        int i,j;

        for(i=0;i<numOfLocations;i++)
        {
            Log.i("wj","db -- measurement per location : "+myMeasurementPerLocationArray[i].name);
            for(j=0;j<myMeasurementPerLocationArray[i].numOfWifiPoints;j++)
            {
                Log.i("wj","db -- measurement per location -- BSSID : " + myMeasurementPerLocationArray[i].myWifiInfoRowArray[j].BSSID);
                Log.i("wj","db -- measurement per location -- BSSID : " + myMeasurementPerLocationArray[i].myWifiInfoRowArray[j].SSID);
                Log.i("wj","db -- measurement per location -- BSSID : " + myMeasurementPerLocationArray[i].myWifiInfoRowArray[j].level);
            }
        }
    }
}
