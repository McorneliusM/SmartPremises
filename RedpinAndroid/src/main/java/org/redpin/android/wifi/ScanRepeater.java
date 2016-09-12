package org.redpin.android.wifi;

import java.util.TimerTask;
import android.net.wifi.WifiManager;


public class ScanRepeater extends TimerTask 
{
	WifiManager mainWifiObj;
	
	
	public ScanRepeater(WifiManager passingWifiManager)
	{
		mainWifiObj=passingWifiManager;
	}
	
	@Override
 	 public void run() 
 	 {
		mainWifiObj.startScan();
 	 }
}
