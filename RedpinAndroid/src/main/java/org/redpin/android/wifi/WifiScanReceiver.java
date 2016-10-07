package org.redpin.android.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiScanReceiver extends BroadcastReceiver 
 {
	//WifiManager mainWifiObj;
	 public static WifiManager mainWifiObj;
	Context myContext;
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

   	}
   	
 }
   	
