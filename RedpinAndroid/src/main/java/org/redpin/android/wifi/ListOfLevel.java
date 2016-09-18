package org.redpin.android.wifi;

import java.util.List;

import android.net.wifi.ScanResult;

public class ListOfLevel
{
	private int NUM_OF_AP;
	private float[] level;
	private String[] BSSID;
	
	private List<ScanResult> wifiScanList;
	private AverageFilter[] signalLevelFilter;
	
	public ListOfLevel(List<ScanResult> passingScanList,AverageFilter[] passingFilter)
	{
		NUM_OF_AP=4;
		level=new float[NUM_OF_AP];
		BSSID=new String[NUM_OF_AP];
		wifiScanList=passingScanList;
		signalLevelFilter=passingFilter;

   		BSSID[0]="00:12:a9:53:63:80";
   		BSSID[1]="00:1f:41:6e:b7:f8";
   		BSSID[2]="00:1f:41:2e:b7:f8";
   		BSSID[3]="00:12:a9:53:63:82";
   		
   		level[0]=-100f;
   		level[1]=-100f;
   		level[2]=-100f;
   		level[3]=-100f;
		
		
		for(int i = 0; i < wifiScanList.size(); i++)
   	    {
   			if(BSSIDcode(wifiScanList.get(i).BSSID)!=(NUM_OF_AP+1))
   			{
   				int currentBSSIDcode=BSSIDcode(wifiScanList.get(i).BSSID);
   				level[currentBSSIDcode]=signalLevelFilter[currentBSSIDcode].getReading();
   			}
   	    }
	}
	
	public int BSSIDcode(String s)
   	{
   		String theBSSID[]=new String[NUM_OF_AP];
   		theBSSID[0]="00:12:a9:53:63:80";
   		theBSSID[1]="00:1f:41:6e:b7:f8";
   		theBSSID[2]="00:1f:41:2e:b7:f8";
   		theBSSID[3]="00:12:a9:53:63:82";

   		int i;

   		for(i=0;i<NUM_OF_AP;i++)
   		{
   			if(s.compareTo(theBSSID[i])==0)
   			{
   				return i;
   			}
   		}

   		return (NUM_OF_AP+1);

   	}
	
	public boolean isLocation1()
	{
		boolean s1Matched;
		boolean s2Matched;
		boolean s3Matched;
		boolean s4Matched;
		
		if(levelIsInRange(level[0],-75f))
		{	s1Matched=true;   }
		else
		{   s1Matched=false;  }
		
		if(levelIsInRange(level[1],-52f))
		{   s2Matched=true;   }
		else
		{   s2Matched=false;   }
		
		if(levelIsInRange(level[2],-52f))
		{   s3Matched=true;   }
		else
		{   s3Matched=false;   }
		
		if(levelIsInRange(level[3],-75f))
		{   s4Matched=true;   }
		else
		{   s4Matched=false;   }
		
		if(s1Matched&&s2Matched&&s3Matched&&s4Matched)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isLocation2()
	{
		boolean s1Matched;
		boolean s2Matched;
		boolean s3Matched;
		boolean s4Matched;
		
		if(levelIsInRange(level[0],-68f))
		{	s1Matched=true;   }
		else
		{   s1Matched=false;  }
		
		if(levelIsInRange(level[1],-66f))
		{   s2Matched=true;   }
		else
		{   s2Matched=false;   }
		
		if(levelIsInRange(level[2],-64f))
		{   s3Matched=true;   }
		else
		{   s3Matched=false;   }
		
		if(levelIsInRange(level[3],-67f))
		{   s4Matched=true;   }
		else
		{   s4Matched=false;   }
		
		if(s1Matched&&s2Matched&&s3Matched&&s4Matched)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isLocation3()
	{
		boolean s1Matched;
		boolean s2Matched;
		boolean s3Matched;
		boolean s4Matched;
		
		if(levelIsInRange(level[0],-76f))
		{	s1Matched=true;   }
		else
		{   s1Matched=false;  }
		
		if(levelIsInRange(level[1],-78f))
		{   s2Matched=true;   }
		else
		{   s2Matched=false;   }
		
		if(levelIsInRange(level[2],-78f))
		{   s3Matched=true;   }
		else
		{   s3Matched=false;   }
		
		if(levelIsInRange(level[3],-76f))
		{   s4Matched=true;   }
		else
		{   s4Matched=false;   }
		
		if(s1Matched&&s2Matched&&s3Matched&&s4Matched)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isLocation4()
	{
		boolean s1Matched;
		boolean s2Matched;
		boolean s3Matched;
		boolean s4Matched;
		
		if(levelIsInRange(level[0],-88f))
		{	s1Matched=true;   }
		else
		{   s1Matched=false;  }
		
		if(levelIsInRange(level[1],-67f))
		{   s2Matched=true;   }
		else
		{   s2Matched=false;   }
		
		if(levelIsInRange(level[2],-67f))
		{   s3Matched=true;   }
		else
		{   s3Matched=false;   }
		
		if(levelIsInRange(level[3],-83f))
		{   s4Matched=true;   }
		else
		{   s4Matched=false;   }
		
		if(s1Matched&&s2Matched&&s3Matched&&s4Matched)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean levelIsInRange(float test,float target)
	{
		if(Math.abs(test-target)<5.00f)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
