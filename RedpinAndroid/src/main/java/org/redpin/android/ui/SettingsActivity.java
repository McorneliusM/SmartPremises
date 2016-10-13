/**
 *  Filename: SettingsActivity.java (in org.repin.android.ui)
 *  This file is part of the Redpin project.
 *
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 *
 *  www.redpin.org
 */
package org.redpin.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.redpin.android.R;
import org.redpin.android.wifi.WifiInformation;
import org.redpin.android.wifi.WifiScanReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Class represents an activity responsible for the changing settings.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class SettingsActivity extends Activity {


   //String[] locDataCollection;
   String locDataCollection;
   //WifiInformation myWifiInfoDataCollection;
   WifiInformation myWifiInfo= new WifiInformation();
   //WifiManager mainWifiObj=new WifiManager();
   WifiManager  mainWifiObj = WifiScanReceiver.mainWifiObj;
   public int i;
   /**
    * Called when the activity is starting inflating the activity's UI. This is
    * where most initialization should go.
    */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.settings_view);
      i = 0;

   }


   /**
    * Starts the setting screen
    *
    * @param target {@link View} that called this method
    */
   public void button_Mapview(View target) {
      Toast.makeText(SettingsActivity.this, "You have selected Mapview", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(this, MapViewActivity.class);
      startActivity(intent);
   }

   /**
    * Start the server preferences activity
    *
    * @param target {@link View} that called this method
    */
   public void button_ServerPreferences(View target) {
      Toast.makeText(SettingsActivity.this, "You have selected Server Preferences", Toast.LENGTH_SHORT).show();
      //Intent intent = new Intent(this, ServerPreferences.class);
      //startActivity(intent);
   }

   public void button_DataCollection(View target) throws IOException{

      //Toast.makeText(SettingsActivity.this, "You have selected Data Collection", Toast.LENGTH_SHORT).show();
      TextView tv = (TextView)findViewById(R.id.textViewDataCollection);
      //tv.setText("Data Collection");

      myWifiInfo.updateInformation(mainWifiObj.getScanResults());

      /* ZhiWei changes for csv file got issue */
      File root = android.os.Environment.getExternalStorageDirectory();
      File dir = new File(root.getAbsolutePath()+"/math");
      dir.mkdirs();
      File file = new File(dir,"data.csv");

      StringBuilder builder = new StringBuilder();

      try {
         FileOutputStream out = new FileOutputStream(file, true);
         OutputStreamWriter writer = new OutputStreamWriter(out);

         if (myWifiInfo.getStringArray() != null) {
            //StringBuilder builder = new StringBuilder();
            for (String s : myWifiInfo.getStringArray()) {

               builder.append(i).append(",").append(s).append("\n");

               tv.setText(builder.toString());
               writer.append(i+"").append(",").append(s).append("\n");


               //openFileOutput(FILENAME, Context.MODE_APPEND);
               //out.write(entry.getBytes());
               //out.close();



            }

            i++;

            writer.close();
            out.close();
         }
         else {
            Toast.makeText(SettingsActivity.this, "WifiString is NULL", Toast.LENGTH_SHORT).show();
         }
   } catch(FileNotFoundException e){
         e.printStackTrace();
      }


      //Intent intent = new Intent(this, ServerPreferences.class);
      //startActivity(intent);
   }
}
