package org.redpin.android.accesspoint;

public class AverageFilter 
{
	public static final int numOfSamples=5;
	private float[] sample;
	
	public AverageFilter()
	{
		sample=new float[numOfSamples];
		int i;
		for(i=0;i<numOfSamples;i++)
		{
			sample[i]=-100f;
		}
	}
	
   	public void addReading(float level)
   	{
   		int i;
   		for(i=1;i<numOfSamples;i++)
		{
			sample[(i-1)]=sample[i];
		}
   		sample[(numOfSamples-1)]=level;
   	}
   	
   	public float getReading()
   	{
   		int i;
   		float sum=0;
   		for(i=0;i<numOfSamples;i++)
		{
			sum=sum+sample[i];
		}
   		return (sum/numOfSamples);
   	}
}
