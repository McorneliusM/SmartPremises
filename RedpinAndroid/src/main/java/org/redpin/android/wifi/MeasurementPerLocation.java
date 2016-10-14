package org.redpin.android.wifi;

/**
 * Created by TQJG84 on 14-Oct-16.
 */
public class MeasurementPerLocation {

    public int numOfWifiPoints;

    public WifiInfoRow[] myWifiInfoRowArray;

    public String name;


    public MeasurementPerLocation(String inputName, int inputNumOfWifiPoints)
    {
        name = inputName;
        numOfWifiPoints = inputNumOfWifiPoints;
        myWifiInfoRowArray = new WifiInfoRow[inputNumOfWifiPoints];
    }

    public void fillUpEachWifiInfoRow(int index,String inputBSSID,String inputSSID,int inputlevel)
    {
        myWifiInfoRowArray[index] = new WifiInfoRow(inputBSSID,inputSSID,inputlevel);
    }


}
