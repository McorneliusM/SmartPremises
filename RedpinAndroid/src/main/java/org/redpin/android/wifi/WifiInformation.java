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
      int numOfM_Wireless_Or_M_Guest = 0;

      Log.i("wj", "start current measurement fillUpEachWifiInfoRow");
      for (i = 0; i < currentScanList.size(); i++)
      {
         if (currentScanList.get(i).SSID.equals("M-Wireless") || currentScanList.get(i).SSID.equals("M-Guest"))
         {
            currentMeasurementPerLocation.fillUpEachWifiInfoRow(numOfM_Wireless_Or_M_Guest, currentScanList.get(i).BSSID, currentScanList.get(i).SSID, Math.abs(currentScanList.get(i).level));
            numOfM_Wireless_Or_M_Guest ++;
         }
      }
      currentMeasurementPerLocation.numOfWifiPoints = numOfM_Wireless_Or_M_Guest;


      //---------------------try with dummy-------------------------------------
      currentMeasurementPerLocation = new MeasurementPerLocation("current",3);

      currentMeasurementPerLocation.fillUpEachWifiInfoRow(0,"asdasdasdas","M-guest", 56);
      currentMeasurementPerLocation.fillUpEachWifiInfoRow(1,"asdasdasdas","M-guest", 56);
      currentMeasurementPerLocation.fillUpEachWifiInfoRow(2,"asdasdasdas","M-guest", 56);
      //---------------------try with dummy-------------------------------------




      Log.i("wj", "end current measurement fillUpEachWifiInfoRow");

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

   public void tryWithDatabaseAndAlgorithm()
   {
      //---------------------------------------------------------------------------------------------
      // Initialize fingerprint database , store all hard-coded database information during this function
      //------------------------------------------------------------------------------------------------

      FingerprintDatabase try_db = new FingerprintDatabase(3);

      try_db.fillUpEachMeasurementPerLocation(0,"A1",3);
      try_db.fillUpEachMeasurementPerLocation(1,"B1",3);
      try_db.fillUpEachMeasurementPerLocation(2,"C1",3);


      Log.i("wj", "initializeFingerprintDb part 1 complete ");

      try_db.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(0,"asdasdasdas","M-guest", 56);
      try_db.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(1,"asdasdasdas","M-guest", 56);
      try_db.myMeasurementPerLocationArray[0].fillUpEachWifiInfoRow(2,"asdasdasdas","M-guest", 56);

      Log.i("wj", "initializeFingerprintDb part 2 complete ");

      try_db.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(0,"asdasdasdas","M-guest", 56);
      try_db.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(1 ,"70:62:b9:ea:e4:aa","tetrasit@unifibiz",64);
      try_db.myMeasurementPerLocationArray[1].fillUpEachWifiInfoRow(2,"asdasdasdas","M-guest", 56);

      Log.i("wj", "initializeFingerprintDb part 3 complete ");

      try_db.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(0 ,"e8:94:f6:a2:1f:f6","Emerald_IT1",82);
      try_db.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(1 ,"28:10:7b:d8:c7:56","m4lW4R3_rOuT3R",59);
      try_db.myMeasurementPerLocationArray[2].fillUpEachWifiInfoRow(2 ,"84:24:8d:3f:93:f0","M-Wireless",73);

      try_db.printFingerprintDb();
      Log.i("wj", "start try_currentMeasurementPerLocation fillUpEachWifiInfoRow");
      //---------------------try with dummy-------------------------------------
      MeasurementPerLocation try_currentMeasurementPerLocation = new MeasurementPerLocation("current",21);

      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(0 ,"84:24:8d:3f:f2:90","M-Wireless",46);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(1 ,"84:24:8d:3f:f2:91","M-Guest",46);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(2 ,"28:10:7b:d8:c7:56","m4lW4R3_rOuT3R",59);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(3 ,"c4:e9:84:aa:d2:26","WIFIOTAP2",68);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(4 ,"84:24:8d:3f:bb:a1","M-Guest",66);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(5 ,"84:24:8d:3f:bb:a0","M-Wireless",65);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(6 ,"84:24:8d:3f:83:51","M-Guest",72);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(7 ,"84:24:8d:3f:93:f0","M-Wireless",73);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(8 ,"84:24:8d:3f:93:f1","M-Guest",73);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(9 ,"84:24:8d:3f:bf:b1","M-Guest",74);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(10 ,"70:62:b9:ea:e4:aa","tetrasit@unifibiz",74);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(11 ,"84:24:8d:3f:a0:d1","M-Guest",76);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(12 ,"a0:f3:c1:a2:7e:5a","WIFIOTAP3",76);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(13 ,"84:24:8d:40:98:f1","M-Guest",76);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(14 ,"e8:94:f6:a2:1f:f6","Emerald_IT1",82);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(15 ,"84:24:8d:3f:83:50","M-Wireless",72);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(16 ,"c4:e9:84:aa:d2:54","TP-LINK_D254",86);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(17 ,"fc:b0:c4:a0:99:6d","",91);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(18 ,"84:24:8d:3f:bf:b0","M-Wireless",73);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(19 ,"84:24:8d:40:ee:81","M-Guest",78);
      try_currentMeasurementPerLocation.fillUpEachWifiInfoRow(20 ,"84:24:8d:40:95:41","M-Guest",85);


      //---------------------try with dummy-------------------------------------

      Log.i("wj", "end try_currentMeasurementPerLocation fillUpEachWifiInfoRow");

      try_currentMeasurementPerLocation.printWifiInfoRow();

      //-----------------------------------------------------------------------------------------------------


      //---------------------------------------------------------------
      // Location Calculation
      //---------------------------------------------------------------
      int try_highestAccuracyLevel = 0;
      MeasurementPerLocation try_highAccuracyMeasurementPerLocation = null;
      //Compare measured data with each location's data in database
      Log.i("wj", "looping try_db.numOfLocations");
      for (int j = 0; j < try_db.numOfLocations; j++) {
         Log.i("wj", "looping try_db.numOfLocations = " + j);
         MeasurementPerLocation baseMeasurementPerLocation = try_db.myMeasurementPerLocationArray[j];
         int accuracyLevelPerLocation = measurementAccuracyLevel(baseMeasurementPerLocation, try_currentMeasurementPerLocation);

         //Store the highest accuracy level and its MeasurementPerLocation class pointer
         if (accuracyLevelPerLocation > try_highestAccuracyLevel) {
            Log.i("wj", "accuracyLevelPerLocation > try_highestAccuracyLevel is true");
            try_highestAccuracyLevel = accuracyLevelPerLocation;
            try_highAccuracyMeasurementPerLocation = baseMeasurementPerLocation;
         }
      }

      Log.i("#####FoongFoong#####",
              "updateInformation(): Highest Accuracy Level: " + try_highestAccuracyLevel);

      if (try_highAccuracyMeasurementPerLocation != null) {
         Log.i("#####FoongFoong#####",
                 "updateInformation(): Highest Accuracy Level Location Name: " + try_highAccuracyMeasurementPerLocation.name);

         //---------------------------------------------------------------
         // Show Location
         //---------------------------------------------------------------
         String locationString = try_highAccuracyMeasurementPerLocation.name;

         if (locationString.equals("A1")) {
            Log.i("wj", "locationString.equals(\"A1\")");
            MapViewActivity.setMarkerLocation(205, 8);
         } else if (locationString.equals("B1")) {
            Log.i("wj", "locationString.equals(\"B1\")");
            MapViewActivity.setMarkerLocation(297, 8);
         } else if (locationString.equals("C1")) {
            Log.i("wj", "locationString.equals(C1)");
            MapViewActivity.setMarkerLocation(599, 50);
         } else {
            //to prove that it not work, point to E1
            Log.i("wj", "locationString.equals  ....  not working");
            MapViewActivity.setMarkerLocation(167, 45);
         }
      }
   }
}