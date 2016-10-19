package org.redpin.android.wifi;

import android.util.Log;

/**
 * Created by TQJG84 on 14-Oct-16.
 */
public class MeasurementPerLocation {

    public int numOfWifiPoints;

    public WifiInfoRow[] myWifiInfoRowArray;

    public String name;

    public int xLocation;
    public int yLocation;


    public MeasurementPerLocation(String inputName, int inputNumOfWifiPoints, int x, int y)
    {
        //Giving name of location, number of wifi access point scanned in this location
        name = inputName;
        numOfWifiPoints = inputNumOfWifiPoints;
        myWifiInfoRowArray = new WifiInfoRow[inputNumOfWifiPoints];
        xLocation = x;
        yLocation = y;
    }

    public void fillUpEachWifiInfoRow(int index,String inputBSSID,String inputSSID,int inputlevel)
    {
        myWifiInfoRowArray[index] = new WifiInfoRow(inputBSSID,inputSSID,inputlevel);
    }

    //-- for debugging
    public void printWifiInfoRow()
    {
        int j;

        for(j=0;j<numOfWifiPoints;j++)
        {
            Log.i("wj","db -- measurement per location -- BSSID : " + myWifiInfoRowArray[j].BSSID);
            Log.i("wj","db -- measurement per location -- BSSID : " + myWifiInfoRowArray[j].SSID);
            Log.i("wj","db -- measurement per location -- BSSID : " + myWifiInfoRowArray[j].level);
        }
    }
}
