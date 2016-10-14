package org.redpin.android.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.redpin.android.ui.MapViewActivity;

import java.util.List;

public class WifiInformation {

   List<ScanResult> currentScanList;
   private static long numOfScans = 0;
   String[] wifiString;

   private FingerprintDatabase fingerprintDB;

   public WifiInformation()
   {
      currentScanList = null;
      wifiString=new String[1];
      wifiString[0]="Starting";

      initializeFingerprintDb(fingerprintDB);
   }

   public void updateInformation(List<ScanResult> wifiScanList) {

      currentScanList = wifiScanList;
      int currentScanListSize = currentScanList.size();
      wifiString=new String[wifiScanList.size()];
      int i;


      numOfScans++;


      //----------------------------------------------------------------------------------------------------
      //   Getting current wifi point and data into MeasurementPerLocation
      //----------------------------------------------------------------------------------------------------
      MeasurementPerLocation currentMeasurementPerLocation = new MeasurementPerLocation("current",currentScanList.size());

      for (i = 0; i < currentScanList.size(); i++) {
         currentMeasurementPerLocation.fillUpEachWifiInfoRow(i, currentScanList.get(i).BSSID, currentScanList.get(i).SSID, Math.abs(currentScanList.get(i).level));
      }

      // for logging , u can use --> currentMeasurementPerLocation.printWifiInfoRow();

      //-----------------------------------------------------------------------------------------------------




      //---------------------------------------------------------------
      // Log scan list
      //---------------------------------------------------------------
      for (i = 0; i < currentScanListSize; i++) {
         Log.i("wj", "numOfScans: " + numOfScans
                 + ", BSSID: " + currentScanList.get(i).BSSID
                 + ", SSID: " + currentScanList.get(i).SSID
                 + ", level: " + currentScanList.get(i).level);

         wifiString[i] = (currentScanList.get(i).BSSID
                 +","+currentScanList.get(i).SSID
                 +","+currentScanList.get(i).level);

      }
   }

   public void initializeFingerprintDb(FingerprintDatabase db)
   {
      //---------------------------------------------------------------------------------------------
      // Initialize fingerprint database , store all hard-coded database information during this function
      //------------------------------------------------------------------------------------------------

      db = new FingerprintDatabase(3);

      db.fillUpEachMeasurementPerLocation(0,"A1",3);
      db.fillUpEachMeasurementPerLocation(1,"B1",3);
      db.fillUpEachMeasurementPerLocation(2,"C1",3);


      Log.i("wj", "initializeFingerprintDb part 1 complete ");

      db.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(0,"asdasdasdas","M-guest", 56);
      db.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(1,"asdasdasdas","M-guest", 56);
      db.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(2,"asdasdasdas","M-guest", 56);

      Log.i("wj", "initializeFingerprintDb part 2 complete ");

      db.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(0,"asdasdasdas","M-guest", 56);
      db.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(1,"asdasdasdas","M-guest", 56);
      db.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(2,"asdasdasdas","M-guest", 56);

      Log.i("wj", "initializeFingerprintDb part 3 complete ");

      db.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(0,"asdasdasdas","M-guest", 56);
      db.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(1,"asdasdasdas","M-guest", 56);
      db.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(2,"asdasdasdas","M-guest", 56);


      // for debugging , u can use --> db.printFingerprintDb();
   }

   public String[] getStringArray()
   {
      return wifiString;
   }
}