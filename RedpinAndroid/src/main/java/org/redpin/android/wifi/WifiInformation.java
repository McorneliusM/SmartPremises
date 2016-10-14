package org.redpin.android.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.redpin.android.ui.MapViewActivity;

import java.util.List;

public class WifiInformation {

   List<ScanResult> currentScanList;
   private static long numOfScans = 0;
   String[] wifiString;

   public WifiInformation()
   {
      currentScanList = null;
      wifiString=new String[1];
      wifiString[0]="Starting";
   }

   public void updateInformation(List<ScanResult> wifiScanList) {

      currentScanList = wifiScanList;
      int currentScanListSize = currentScanList.size();
      wifiString=new String[wifiScanList.size()];
      int j = 0;

      // Initialize values for comparator
      int counterOfBSSIDWithStrongestRSSI = 0;
      int compareA = currentScanList.get(counterOfBSSIDWithStrongestRSSI).level;

      numOfScans++;

      FingerprintDatabase testDB = new FingerprintDatabase(0);

      //---------------------------------------------------------------
      // Comparator
      //---------------------------------------------------------------

      // Check if size must be at least 2 for a comparison
      if (currentScanListSize >= 2) {

         // Initialize values to be compared
         // B is only available in this case, thus it is initialized here.
         int compareB = currentScanList.get(1).level;

         // Main loop for comparator
         for (int i = 0; i < (currentScanListSize - 2); i++) {

            // If A is less than or equal to B,
            // copy value of B into A for next comparison.
            // else remain the value of A, since it is bigger.
            // Note: this code works with raw negative values of dB
            if (compareA <= compareB) {
               compareA = compareB;
               counterOfBSSIDWithStrongestRSSI = i + 1;
            }

            // If there are more data,
            // update values to be compared in B.
            if ((i + 2) < currentScanListSize) {
               compareB = currentScanList.get(i + 2).level;
            }
         }
      }


      //---------------------------------------------------------------
      // Get location from database based on strongest RSSI
      //---------------------------------------------------------------
      String locationString = testDB.getLocationByBSSID(currentScanList.get(counterOfBSSIDWithStrongestRSSI).BSSID);
      if (locationString.equals("6-004")) {
         MapViewActivity.setMarkerLocation(306, 536);
      }
      else if (locationString.equals("6-005"))
      {
         MapViewActivity.setMarkerLocation(410, 346);
      }
      else if (locationString.equals("6-010"))
      {
         MapViewActivity.setMarkerLocation(880, 510);
      }
      else
      {
         //to prove that it not work
         //MapViewActivity.setMarkerLocation(555, 222);
      }

      //---------------------------------------------------------------
      // Log scan list
      //---------------------------------------------------------------
      for (int i = 0; i < currentScanListSize; i++) {
         Log.i("wj", "numOfScans: " + numOfScans
               + ", BSSID: " + currentScanList.get(i).BSSID
               + ", SSID: " + currentScanList.get(i).SSID
               + ", level: " + currentScanList.get(i).level);

         wifiString[i] = (currentScanList.get(i).BSSID
               +","+currentScanList.get(i).SSID
               +","+currentScanList.get(i).level);

      }

        //Woonjiet comment
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

   public String[] getStringArray()
   {
      return wifiString;
   }
}


