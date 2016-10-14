package org.redpin.android.wifi;

/**
 * Created by TQJG84 on 14-Oct-16.
 */
public class WifiInfoRow {
    public String BSSID;
    public String SSID;
    public int level;

    public WifiInfoRow(String inputBSSID, String inputSSID, int inputLevel)
    {
        BSSID = inputBSSID;
        SSID = inputSSID;
        level = inputLevel;
    }
}
