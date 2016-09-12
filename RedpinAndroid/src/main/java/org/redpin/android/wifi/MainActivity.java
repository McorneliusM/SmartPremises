package org.redpin.android.wifi;

import java.util.Locale;
import java.util.Timer;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {

   WifiManager mainWifiObj;
   WifiScanReceiver wifiReciever;
   ListView list;
   ListView list2;
   //String wifis[];
   ArrayAdapter<String> myArrayAdapter;
   Timer timer;
   ScanRepeater myScanRepeater;
   Context myContext;
   WifiInformation myWifiInfo;
   TextToSpeech ttobj;
   
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //setContentView(R.layout.activity_main);
      
      //list = (ListView)findViewById(R.id.listView1);
      //list2 = (ListView)findViewById(R.id.listView2);
      
      mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
      myContext=getApplicationContext();
      myWifiInfo=new WifiInformation();
      
      //Text to speech object
      ttobj=new TextToSpeech(getApplicationContext(), 
    	      new TextToSpeech.OnInitListener() {
    	      @Override
    	      public void onInit(int status) {
    	         if(status != TextToSpeech.ERROR)
    	         {
    	             ttobj.setLanguage(Locale.UK);
    	             ttobj.setSpeechRate(0.75f);
    	         }				
    	         }
    	      });
      
      wifiReciever = new WifiScanReceiver(mainWifiObj,myContext,myWifiInfo);
      
      mainWifiObj.startScan();
      
      //Setting up scan repeater
      if(timer != null)
      {
    	  timer.cancel();
      }
      timer = new Timer();
      myScanRepeater = new ScanRepeater(mainWifiObj);
      timer.schedule(myScanRepeater, 100, 100);
   }


   protected void onPause() {
      unregisterReceiver(wifiReciever);
      timer.cancel();
 
      if(ttobj !=null)
      {
          ttobj.stop();
          ttobj.shutdown();
       }
      super.onPause();
   }

   protected void onResume() {
      registerReceiver(wifiReciever, new IntentFilter(
      WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
      
      //Setting up scan repeater
      if(timer != null)
      {
    	  timer.cancel();
      }
      timer = new Timer();
      myScanRepeater = new ScanRepeater(mainWifiObj);
      timer.schedule(myScanRepeater, 100, 100);
      
      ttobj=new TextToSpeech(getApplicationContext(), 
    	      new TextToSpeech.OnInitListener() {
    	      @Override
    	      public void onInit(int status) {
    	         if(status != TextToSpeech.ERROR)
    	         {
    	             ttobj.setLanguage(Locale.UK);
    	             ttobj.setSpeechRate(0.75f);
    	         }				
    	         }
    	      });
//      wifiReciever.updateTTS(ttobj);
      
      super.onResume();
   }
   
   public void speakText(String toSpeak)
   {
	      //ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
   }

}