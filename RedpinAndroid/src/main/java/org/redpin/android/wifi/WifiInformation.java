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
   private String currentAP = "";

   private FingerprintDatabase fingerprintDB;

   public WifiInformation()
   {
      currentScanList = null;
      wifiString=new String[1];
      wifiString[0]="Starting";

      //----------------------------------------------------------------------------------------------------
      //   TODO: remember to update the total number of location point for database
      //----------------------------------------------------------------------------------------------------
      fingerprintDB = new FingerprintDatabase(10);
      initializeFingerprintDb(fingerprintDB);
   }

   // Function to calculate signal strength accuracy per wifi reading
   private double signalContribution(double baseRssi, double measuredRssi) {
      //Possible RSSI range 0 to 100
      //Possible diff range 0 to 100
      double diff = Math.abs(baseRssi - measuredRssi);
      //Log.i("#####FoongFoong#####", "signalContribution(): base: " + baseRssi + ", diff: " + diff);

      //Normalize to range of -1 to 1
      double normalize = (2*(diff/100))-1;
      //Log.i("#####FoongFoong#####", "signalContribution(): normalize: " + normalize);

      //Inverse
      double finalNormalize = -normalize;
      //Log.i("#####FoongFoong#####", "signalContribution(): finalNormalize: " + finalNormalize);

      return finalNormalize;
   }

   // Function to calculate accuracy level per location
   private int measurementAccuracyLevel(MeasurementPerLocation baseMeasurementPerLocation,
                                        MeasurementPerLocation currentMeasurementPerLocation) {

      double totalCredit = 0;
      double account = 0;
      Boolean matchBestBSSID = false;
      Boolean zeroLevelBaseWifi = false;
      Boolean matchZeroLevelBSSID = false;

      if (currentMeasurementPerLocation.bestBSSID.equals(baseMeasurementPerLocation.bestBSSID)){
         matchBestBSSID = true;
         //Log.i("xxxxxxxxxxxxxFoongFoong", "111111measurementAccuracyLevel(): matchBestBSSID!!!!!!!!!!!!!!!!!!! ");
      }

      if (matchBestBSSID) {
         for (int i = 0; i < baseMeasurementPerLocation.numOfWifiPoints; i++) {
            //start of base's for loop
            WifiInfoRow baseWifiRow = baseMeasurementPerLocation.myWifiInfoRowArray[i];
            if (baseWifiRow != null && baseWifiRow.level == 0) {
               zeroLevelBaseWifi = true;
               //Log.i("xxxxxxxxxxxxxFoongFoong", "22222222measurementAccuracyLevel(): zeroLevelBaseWifi = true!!!!!!!!!!!!!!!!!!! ");
            } else {
               zeroLevelBaseWifi = false;
            }
            matchZeroLevelBSSID = false;
            for (int j = 0; j < currentMeasurementPerLocation.numOfWifiPoints; j++) {
               //start of measured's for loop
               WifiInfoRow currentWifiRow = currentMeasurementPerLocation.myWifiInfoRowArray[j];

               //Log.i("#####FoongFoong#####",
//                       "measurementAccuracyLevel(): baseWifiRow.BSSID: " + baseWifiRow.BSSID
//                               + ", baseWifiRow.SSID: " + baseWifiRow.SSID
//                               + ", baseWifiRow.level: " + baseWifiRow.level);
               if (currentWifiRow != null) {
                  //Log.i("#####FoongFoong#####",
//                          "measurementAccuracyLevel(): currentWifiRow.BSSID: " + currentWifiRow.BSSID
//                                  + ", currentWifiRow.SSID: " + currentWifiRow.SSID
//                                  + ", currentWifiRow.level: " + currentWifiRow.level);
               } else {
                  //Log.i("#####FoongFoong#####",
//                          "measurementAccuracyLevel(): currentWifiRow == NULL ");
               }

               if (baseWifiRow != null && baseWifiRow.BSSID != null && currentWifiRow != null && currentWifiRow.BSSID != null
                       && baseWifiRow.BSSID.equals(currentWifiRow.BSSID)) {
                  //non zero level's bssid match: add ID contribution and signal strength
                  matchZeroLevelBSSID = true;
                  //Log.i("#####FoongFoong#####",
//                          "measurementAccuracyLevel(): MATCH baseWifiRow.BSSID: " + baseWifiRow.BSSID
//                                  + ", baseWifiRow.SSID: " + baseWifiRow.SSID
//                                  + ", baseWifiRow.level: " + baseWifiRow.level);
                  //Log.i("#####FoongFoong#####",
//                          "measurementAccuracyLevel(): MATCH currentWifiRow.BSSID: " + currentWifiRow.BSSID
//                                  + ", currentWifiRow.SSID: " + currentWifiRow.SSID
//                                  + ", currentWifiRow.level: " + currentWifiRow.level);
                  account += ID_POS_CONTRIBUTION;
                  account += signalContribution(baseWifiRow.level, currentWifiRow.level);
                  //exit measured's for loop
                  break;
               }
               //end of measured's for loop
            }
            //back to base's for loop
            if (zeroLevelBaseWifi) {
               if (matchZeroLevelBSSID) {
                  //there is zero level wifi in database, but not found the BSSID in measured data
                  //reset account to 0, exit the entire for loop
                  account = 0;
                  //Log.i("xxxxxxxxxxxxxFoongFoong",
//                          "44444444measurementAccuracyLevel(): account = 0000000000000000000 !!!!!!!!!!!!!!!!!!! ");
                  //exit base's for loop
                  break;
               }
            }
            //end of base's for loop
         }
      }

      //Log.i("!!!!!!!!!!!!!!!!!!!",
//              "measurementAccuracyLevel(): currentMeasurementPerLocation.bestBSSID: " + currentMeasurementPerLocation.bestBSSID
//                      + ", baseMeasurementPerLocation.bestBSSID: " + baseMeasurementPerLocation.bestBSSID
//                      + ", account: " + account);

      //totalCredit += baseMeasurementPerLocation.numOfWifiPoints * ID_POS_CONTRIBUTION;
      //totalCredit += baseMeasurementPerLocation.numOfWifiPoints * SIGNAL_CONTRIBUTION;
      totalCredit = 2;

      int accuracy = 0;
      if (account > 0) {
         //Compute percentage of account from totalCredit -> [0,1];
         //stretch by accuracy span -> [0,FACTOR];
         double a = (account / totalCredit) * FACTOR;
         // same as Math.round
         accuracy = (int) Math.floor(a + 0.5d);
      }

      //Log.i("#####FoongFoong#####",
//              "measurementAccuracyLevel(): Account: " + account
//                      + ", Total Credit possible: " + totalCredit
//                      + ", Accuracy: " + accuracy);

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
      int count = 0;
      int bestMeasuredSignalStrength = 100; //assuming 100 is the worse signal strength that is impossible to get
      String bestMeasuredBSSID = "";
      for (i = 0; i < currentScanList.size(); i++) {
         if (currentScanList.get(i).SSID.equals("M-Guest")) {
            count++;
            if (Math.abs(currentScanList.get(i).level) < bestMeasuredSignalStrength) {
               //lower the level, better the signal strength
               bestMeasuredSignalStrength = Math.abs(currentScanList.get(i).level);
               bestMeasuredBSSID = currentScanList.get(i).BSSID;
            }
         }
      }
      MeasurementPerLocation currentMeasurementPerLocation = new MeasurementPerLocation("current",count, 0, 0, bestMeasuredSignalStrength, bestMeasuredBSSID);
      count = 0;
      for (i = 0; i < currentScanList.size(); i++) {
         if (currentScanList.get(i).SSID.equals("M-Guest")) {
            currentMeasurementPerLocation.fillUpEachWifiInfoRow(count, currentScanList.get(i).BSSID, currentScanList.get(i).SSID, Math.abs(currentScanList.get(i).level));
            count++;
            //Log.i("#####FoongFoong#####",
//                    "updateInformation(): MATCH currentScanList.get(i).BSSID: " + currentScanList.get(i).BSSID
//                            + " currentScanList.get(i).SSID: " + currentScanList.get(i).SSID
//                            + " currentScanList.get(i).level: " + currentScanList.get(i).level);
         }
      }
      //Log.i("#####FoongFoong#####",
//              "updateInformation(): currentMeasurementPerLocation.numOfWifiPoints: " + currentMeasurementPerLocation.numOfWifiPoints
//                      + " count: " + count
//                      + " bestMeasuredSignalStrength: " + bestMeasuredSignalStrength
//                      + " bestMeasuredBSSID: " + bestMeasuredBSSID);

      // for //Log.ing , u can use --> currentMeasurementPerLocation.printWifiInfoRow();

      //-----------------------------------------------------------------------------------------------------

      //---------------------------------------------------------------
      // Log scan list
      //---------------------------------------------------------------
      for (i = 0; i < currentScanListSize; i++) {
         //Log.i("wj", "numOfScans: " + numOfScans
//                 + ", BSSID: " + currentScanList.get(i).BSSID
//                 + ", SSID: " + currentScanList.get(i).SSID
//                 + ", level: " + currentScanList.get(i).level);

         wifiString[i] = (currentScanList.get(i).BSSID
                 +","+currentScanList.get(i).SSID
                 +","+currentScanList.get(i).level);

      }

      //---------------------------------------------------------------
      // Location Calculation
      //---------------------------------------------------------------
      //Compare measured data with each location's data in database
      for (int j = 0; fingerprintDB != null && j < fingerprintDB.numOfLocations; j++) {
         MeasurementPerLocation baseMeasurementPerLocation = fingerprintDB.myMeasurementPerLocationArray[j];
         //Log.i("#####FoongFoong#####",
//                 "updateInformation(): FOR LOOP baseMeasurementPerLocation.numOfWifiPoints: " + baseMeasurementPerLocation.numOfWifiPoints);
         int accuracyLevelPerLocation = 0;
         if (baseMeasurementPerLocation != null && currentMeasurementPerLocation != null) {
            accuracyLevelPerLocation = measurementAccuracyLevel(baseMeasurementPerLocation, currentMeasurementPerLocation);
         }

         //Store the highest accuracy level and its MeasurementPerLocation class pointer
         if (accuracyLevelPerLocation > highestAccuracyLevel) {
            highestAccuracyLevel = accuracyLevelPerLocation;
            highAccuracyMeasurementPerLocation = baseMeasurementPerLocation;
         }
      }

      //Log.i("#####FoongFoong#####",
//              "updateInformation(): Highest Accuracy Level: " + highestAccuracyLevel);

      // To refresh map every time wifi scanned to reduce white screen issue
      MapViewActivity.refreshMap();

      //Only display if obj is not null and accuracy level more than half
      //if (highAccuracyMeasurementPerLocation != null && highestAccuracyLevel > 6000) {
      if (highAccuracyMeasurementPerLocation != null) {
         //Log.i("#####FoongFoong#####",
//                 "updateInformation(): Highest Accuracy Level Location Name: " + highAccuracyMeasurementPerLocation.name);

         MapViewActivity.setMarkerLocation(highAccuracyMeasurementPerLocation.xLocation, highAccuracyMeasurementPerLocation.yLocation);
      }
      else
      {
         MapViewActivity.setMarkerLocation(MapViewActivity.currentMarkerX,MapViewActivity.currentMarkerY);
      }
   }

   public void initializeFingerprintDb(FingerprintDatabase fingerprintDB)
   {
      //---------------------------------------------------------------------------------------------
      // Initialize fingerprint database , store all hard-coded database information during this function
      //-----------------------------------------------------------------------------------------------
      fingerprintDB.fillUpEachMeasurementPerLocation(0, "A3", 2, 280 * 2, 8 * 2, 52, "84:24:8d:3f:e2:51");
      fingerprintDB.fillUpEachMeasurementPerLocation(1, "B3", 2, 478 * 2, 8 * 2, 45, "84:24:8d:3f:e2:51");
      fingerprintDB.fillUpEachMeasurementPerLocation(2, "C1", 2, 599 * 2, 50 * 2, 65, "84:24:8d:3f:e2:51");
      fingerprintDB.fillUpEachMeasurementPerLocation(3, "C3", 3, 599 * 2, 135 * 2, 58, "84:24:8d:40:fe:c1");
      fingerprintDB.fillUpEachMeasurementPerLocation(4, "C5", 2, 599 * 2, 220 * 2, 61, "84:24:8d:40:fe:c1");
      fingerprintDB.fillUpEachMeasurementPerLocation(5, "C7", 2, 599 * 2, 304 * 2, 65, "84:24:8d:3f:dd:91");
      fingerprintDB.fillUpEachMeasurementPerLocation(6,"D1",2, 215*2, 389*2, 54, "84:24:8d:40:d1:c1");
      fingerprintDB.fillUpEachMeasurementPerLocation(7,"D3",2, 311*2, 389*2, 63, "84:24:8d:40:d1:c1");
      fingerprintDB.fillUpEachMeasurementPerLocation(8,"D5",3, 407*2, 389*2, 49, "84:24:8d:3f:dd:91");
      fingerprintDB.fillUpEachMeasurementPerLocation(9,"D7",4, 503*2, 389*2, 55, "84:24:8d:3f:dd:91");
      fingerprintDB.fillUpEachMeasurementPerLocation(10, "E2", 3, 167 * 2, 82 * 2, 48, "84:24:8d:40:d8:f1");
      fingerprintDB.fillUpEachMeasurementPerLocation(11, "E8", 2, 167 * 2, 302 * 2, 48, "84:24:8d:40:d1:c1");
      fingerprintDB.fillUpEachMeasurementPerLocation(12, "YodaWaris", 2, 78 * 2, 360 * 2, 48, "84:24:8d:40:d1:c1");

      // wifi info row for A3
      fingerprintDB.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(0,"84:24:8d:3f:e2:51","M-Guest", 52);
      fingerprintDB.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(1,"84:24:8d:40:d1:c1","M-Guest", 0);

      // wifi info row for B3
      fingerprintDB.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(0,"84:24:8d:3f:e2:51","M-Guest", 45);
      fingerprintDB.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(1,"884:24:8d:40:d1:c1","M-Guest", 0);

      // wifi info row for C1
      fingerprintDB.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(0, "84:24:8d:40:d1:c1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(1, "84:24:8d:3f:e2:51", "M-Guest", 65);

      // wifi info row for C3
      fingerprintDB.myMeasurementPerLocationArray[3].fillUpEachWifiInfoRow(0, "84:24:8d:40:d1:c1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[3].fillUpEachWifiInfoRow(1, "84:24:8d:3f:be:b1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[3].fillUpEachWifiInfoRow(2, "84:24:8d:40:fe:c1", "M-Guest", 58);

      // wifi info row for C5
      fingerprintDB.myMeasurementPerLocationArray[4].fillUpEachWifiInfoRow(0, "84:24:8d:3f:be:b1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[4].fillUpEachWifiInfoRow(1, "84:24:8d:40:fe:c1", "M-Guest", 61);

      // wifi info row for C7
      fingerprintDB.myMeasurementPerLocationArray[5].fillUpEachWifiInfoRow(0, "84:24:8d:3f:be:b1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[5].fillUpEachWifiInfoRow(1, "84:24:8d:3f:dd:91", "M-Guest", 65);

      // wifi info row for D1
      fingerprintDB.myMeasurementPerLocationArray[6].fillUpEachWifiInfoRow(0, "84:24:8d:40:d1:c1", "M-Guest", 54);
      fingerprintDB.myMeasurementPerLocationArray[6].fillUpEachWifiInfoRow(1, "84:24:8d:40:f8:91", "M-Guest", 0);

      // wifi info row for D3
      fingerprintDB.myMeasurementPerLocationArray[7].fillUpEachWifiInfoRow(0, "84:24:8d:40:d1:c1", "M-Guest", 63);
      fingerprintDB.myMeasurementPerLocationArray[7].fillUpEachWifiInfoRow(1, "84:24:8d:40:f8:91", "M-Guest", 0);

      // wifi info row for D5
      fingerprintDB.myMeasurementPerLocationArray[8].fillUpEachWifiInfoRow(0, "84:24:8d:3f:be:b1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[8].fillUpEachWifiInfoRow(1, "84:24:8d:3f:dd:91", "M-Guest", 49);
      fingerprintDB.myMeasurementPerLocationArray[8].fillUpEachWifiInfoRow(2, "84:24:8d:40:f8:91", "M-Guest", 0);

      // wifi info row for D7
      fingerprintDB.myMeasurementPerLocationArray[9].fillUpEachWifiInfoRow(0, "84:24:8d:3f:be:b1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[9].fillUpEachWifiInfoRow(1, "84:24:8d:3f:dd:91", "M-Guest", 55);
      fingerprintDB.myMeasurementPerLocationArray[9].fillUpEachWifiInfoRow(2, "84:24:8d:40:d8:f1", "M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[9].fillUpEachWifiInfoRow(3, "84:24:8d:40:f8:91", "M-Guest", 0);

      // wifi info row for E2
      fingerprintDB.myMeasurementPerLocationArray[10].fillUpEachWifiInfoRow(0,"84:24:8d:40:d8:f1","M-Guest", 48);
      fingerprintDB.myMeasurementPerLocationArray[10].fillUpEachWifiInfoRow(1,"84:24:8d:3f:dd:91","M-Guest", 0);
      fingerprintDB.myMeasurementPerLocationArray[10].fillUpEachWifiInfoRow(2,"84:24:8d:40:f8:91","M-Guest", 0);

      // wifi info row for E8
      fingerprintDB.myMeasurementPerLocationArray[11].fillUpEachWifiInfoRow(0,"84:24:8d:40:d1:c1","M-Guest", 48);
      fingerprintDB.myMeasurementPerLocationArray[11].fillUpEachWifiInfoRow(1,"84:24:8d:40:f8:91","M-Guest", 0);

      // wifi info row for YodaWaris
      fingerprintDB.myMeasurementPerLocationArray[12].fillUpEachWifiInfoRow(0,"84:24:8d:40:d1:c1","M-Guest", 48);
      fingerprintDB.myMeasurementPerLocationArray[12].fillUpEachWifiInfoRow(1,"84:24:8d:40:f8:91","M-Guest", 0);

      //Log.i("wj", "initializeFingerprintDb complete ");
   }

   public String[] getStringArray()
   {
      return wifiString;
   }
}