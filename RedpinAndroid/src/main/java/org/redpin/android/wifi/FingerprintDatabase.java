package org.redpin.android.wifi;

import android.util.Log;

/**
 * Created by Admin on 9/18/2016.
 * To keep wifi fingerprint database
 */

public class FingerprintDatabase {

    String[][] wifiData3points = {
            {"84:24:8d:3f:a2:e0",
                    "6-004"},
            {"84:24:8d:3f:7c:e0",
                    "6-005"},
            {"84:24:8d:3f:8a:d1",
                    "6-010"}
    };

    public String getLocationByBSSID(String BSSID)
    {
        boolean found = false;
        String retVal = "";
        for (int i=0; i<3; i++)
        {
            if (BSSID.equals(wifiData3points[i][0]))
            {
                found = true;
                retVal = wifiData3points[i][1];
            }
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
