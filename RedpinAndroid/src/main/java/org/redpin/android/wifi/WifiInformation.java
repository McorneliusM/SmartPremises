package org.redpin.android.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.redpin.android.ui.MapViewActivity;

import java.util.List;

public class WifiInformation {

   List<ScanResult> currentScanList;
   private static long numOfScans = 0;
   String[] wifiString;

   private static double ID_POS_CONTRIBUTION = 1;
   private static double SIGNAL_CONTRIBUTION = 1;
   private static double FACTOR = 10000;

   private FingerprintDatabase fingerprintDB;

   public WifiInformation()
   {
      currentScanList = null;
      wifiString=new String[1];
      wifiString[0]="Starting";

      initializeFingerprintDb(fingerprintDB);
   }

   // Function to calculate signal strength accuracy per wifi reading
   private double signalContribution(double baseRssi, double measuredRssi) {
      //Possible RSSI range 0 to 100
      double base = baseRssi;
      //Possible diff range 0 to 100
      double diff = Math.abs(baseRssi - measuredRssi);
      Log.i("#####FoongFoong#####",
              "signalContribution(): base: " + base
                      + ", diff: " + diff);

      //Normalize to range of -1 to 1
      double normalize = (2*(diff/100))-1;
      Log.i("#####FoongFoong#####",
              "signalContribution(): normalize: " + normalize);

      //Inverse
      double finalNormalize = -normalize;
      Log.i("#####FoongFoong#####",
              "signalContribution(): finalNormalize: " + finalNormalize);

      return finalNormalize;
   }

   // Function to calculate accuracy level per location
   public int measurementAccuracyLevel(MeasurementPerLocation baseMeasurementPerLocation,
                                         MeasurementPerLocation currentMeasurementPerLocation) {

      double totalCredit = 0;
      double account = 0;

      for (int i = 0; i < baseMeasurementPerLocation.numOfWifiPoints; i++) {
         WifiInfoRow baseWifiRow = baseMeasurementPerLocation.myWifiInfoRowArray[i];
         for (int j = 0; j < currentMeasurementPerLocation.numOfWifiPoints; j++) {
            WifiInfoRow currentWifiRow = currentMeasurementPerLocation.myWifiInfoRowArray[j];

            //bssid match: add ID contribution and signal strength
            if (baseWifiRow != null && baseWifiRow.BSSID != null && currentWifiRow != null && currentWifiRow.BSSID != null
                    && baseWifiRow.BSSID.equals(currentWifiRow.BSSID)) {
               account += ID_POS_CONTRIBUTION;
               account += signalContribution(baseWifiRow.level, currentWifiRow.level);
            }
         }
      }

      totalCredit += baseMeasurementPerLocation.numOfWifiPoints * ID_POS_CONTRIBUTION;
      totalCredit += baseMeasurementPerLocation.numOfWifiPoints * SIGNAL_CONTRIBUTION;

      int accuracy = 0;
      if (account > 0) {
         //Compute percentage of account from totalCredit -> [0,1];
         //stretch by accuracy span -> [0,FACTOR];
         double a = (account / totalCredit) * FACTOR;
         // same as Math.round
         accuracy = (int) Math.floor(a + 0.5d);
      }

      Log.i("#####FoongFoong#####",
              "measurementSimilarityLevel(): Account: " + account
                      + ", Total Credit possible: " + totalCredit
                      + ", Accuracy: " + accuracy);

      return accuracy;
   }

   public void updateInformation(List<ScanResult> wifiScanList) {

      currentScanList = wifiScanList;
      int currentScanListSize = currentScanList.size();
      wifiString=new String[wifiScanList.size()];
      int i;
      int highestAccuracyLevel = 0;
      MeasurementPerLocation highAccuracyMeasurementPerLocation = null;


      numOfScans++;


      //----------------------------------------------------------------------------------------------------
      //   Getting current wifi point and data into MeasurementPerLocation
      //----------------------------------------------------------------------------------------------------
      MeasurementPerLocation currentMeasurementPerLocation = new MeasurementPerLocation("current",currentScanList.size());

      for (i = 0; i < currentScanList.size(); i++) {
         if (currentScanList.get(i).SSID == "M-Wireless" || currentScanList.get(i).SSID == "M-Guest") {
            currentMeasurementPerLocation.fillUpEachWifiInfoRow(i, currentScanList.get(i).BSSID, currentScanList.get(i).SSID, Math.abs(currentScanList.get(i).level));
         }
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

      //---------------------------------------------------------------
      // Location Calculation
      //---------------------------------------------------------------
      //Compare measured data with each location's data in database
      for (int j = 0; j < fingerprintDB.numOfLocations; j++) {
         MeasurementPerLocation baseMeasurementPerLocation = fingerprintDB.myMeasurementPerLocationArray[j];
         int accuracyLevelPerLocation = measurementAccuracyLevel(baseMeasurementPerLocation, currentMeasurementPerLocation);

         //Store the highest accuracy level and its MeasurementPerLocation class pointer
         if (accuracyLevelPerLocation > highestAccuracyLevel) {
            highestAccuracyLevel = accuracyLevelPerLocation;
            highAccuracyMeasurementPerLocation = baseMeasurementPerLocation;
         }
      }

      Log.i("#####FoongFoong#####",
              "updateInformation(): Highest Accuracy Level: " + highestAccuracyLevel);

      if (highAccuracyMeasurementPerLocation != null) {
         Log.i("#####FoongFoong#####",
                 "updateInformation(): Highest Accuracy Level Location Name: " + highAccuracyMeasurementPerLocation.name);

         //---------------------------------------------------------------
         // Show Location
         //---------------------------------------------------------------
         String locationString = highAccuracyMeasurementPerLocation.name;
         if (locationString.equals("A1")) {
            MapViewActivity.setMarkerLocation(205, 8);
         } else if (locationString.equals("B1")) {
            MapViewActivity.setMarkerLocation(297, 8);
         } else if (locationString.equals("C1")) {
            MapViewActivity.setMarkerLocation(599, 50);
         } else {
            //to prove that it not work, point to E1
            MapViewActivity.setMarkerLocation(167, 45);
         }
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