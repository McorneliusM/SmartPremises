package org.redpin.android.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.net.wifi.WifiManager;

public class WifiScanReceiver extends BroadcastReceiver 
 {
	public static final int NUM_OF_AP=4;
	WifiManager mainWifiObj;
	Context myContext;
	String[] wifiString;
	String[] locString;
	ArrayAdapter<String> myArrayAdapter;
	WifiInformation myWifiInfo;

	
	//Constructor
	public WifiScanReceiver(WifiManager passingWifiManager,Context passingContext,WifiInformation passingInfo) {
		mainWifiObj = passingWifiManager;
		myContext = passingContext;
		myWifiInfo = passingInfo;
	}

   	public void onReceive(Context c, Intent intent) 
   	{
   		myWifiInfo.updateInformation(mainWifiObj.getScanResults());
   		//wifiString=myWifiInfo.getStringArray();
   		locString=myWifiInfo.getLocationInfo();
   		
   	    //myArrayAdapter=new ArrayAdapter<String>(myContext,android.R.layout.simple_list_item_1,wifiString);
   	    
   	    //ArrayAdapter<String> myArrayAdapter2=new ArrayAdapter<String>(myContext,android.R.layout.simple_list_item_1,locString);
	    
//	    if(myWifiInfo.needToSpeak())
//	    {
////	    	if(!ttobj.isSpeaking())
////	    	{
////	    		ttobj.speak(myWifiInfo.speakString(), TextToSpeech.QUEUE_FLUSH, null);
////	    	}
//	    }
   	}
   	
 }
   	
