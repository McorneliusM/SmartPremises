package org.redpin.android.wifi;

/**
 * Created by Admin on 9/18/2016.
 * To keep wifi fingerprint database
 */

public class FingerprintDatabase {

    //Declaration of inner class
    public class Single_Loc_Fingerprint {
        public int numOfDetectedAPs;

        public Single_BSSID_Fingerprint[] single_BSSID_Fingerprint_Array;

        Single_Loc_Fingerprint(int numOfDetectAP)
        {
            numOfDetectedAPs = numOfDetectAP;

            single_BSSID_Fingerprint_Array = new Single_BSSID_Fingerprint[numOfDetectAP];
        }
    }

    //Declaration of inner class
    public class Single_BSSID_Fingerprint {
        public String BSSID;
        public float[] possibleLevels;
        public float[] correspondingProb;

        Single_BSSID_Fingerprint(String id, int numOfPossibleLevels)
        {
            BSSID = id;
            possibleLevels = new float[numOfPossibleLevels];     // e.g. -52dB,Prob=0.1 , -53dB,Prob=0.2 , ....
            correspondingProb = new float[numOfPossibleLevels];
        }
    }



    public static final int NUM_OF_LOCATIONS = 9; //Temporary set to 9
    public Single_Loc_Fingerprint[] singleLocFingerprintArray;


    public FingerprintDatabase()
    {
        singleLocFingerprintArray = new Single_Loc_Fingerprint[NUM_OF_LOCATIONS];
    }

    public void setConfigForSingleLoc(int locIndex, int detectedAP)
    {
        singleLocFingerprintArray[locIndex] = new Single_Loc_Fingerprint(detectedAP);
    }

    public void setConfigForSingleBSSID(int locIndex, int bssidIndex, String uniqueName, int possibleLevels)
    {
        singleLocFingerprintArray[locIndex].single_BSSID_Fingerprint_Array[bssidIndex] = new Single_BSSID_Fingerprint(uniqueName, possibleLevels);
    }

    public void setValueForSinglePossibleLevels(int locIndex, int bssidIndex, int possibleLevelsIndex , float levels, float prob)
    {
        singleLocFingerprintArray[locIndex].single_BSSID_Fingerprint_Array[bssidIndex].possibleLevels[possibleLevelsIndex] = levels;
        singleLocFingerprintArray[locIndex].single_BSSID_Fingerprint_Array[bssidIndex].correspondingProb[possibleLevelsIndex] = prob;
    }



}
