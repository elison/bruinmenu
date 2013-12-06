package com.st40611.bruinmenu.sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * The idea here is that we spawn one BluetoothClass per each device
 * we connect to, and they will all be running in parallel in separate threads
 */
public class BluetoothClass implements Runnable {
	private Handler mHandle;
	private final String TAG;
    public static final int HAS_DATA = 0x11;
    
    // variables required to service a bluetooth device
    private BluetoothDevice mDevice;
    private BluetoothSocket mBs;
    private InputStream mIs;
    private OutputStream mOs;
    // buffered reader has a readline() method which we want to use
    // since the device return data per line
    private BufferedReader mBr;
    private OutputStreamWriter mOsw; 
    private int myAverage;
    
    // this ID is the standard ID for RFCOMM
    private UUID mRfcomID =  
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    public BluetoothClass(BluetoothDevice device) {
        mDevice = device;
        // set the tag name to include device name
        TAG = "BluetoothClass [" + device.getAddress() + "]";
        // connect to it
        try {
            mBs = mDevice.createRfcommSocketToServiceRecord(mRfcomID);
            mBs.connect();
            mIs = mBs.getInputStream();
            mOs = mBs.getOutputStream();
            mBr = new BufferedReader(new InputStreamReader(mIs));
            mOsw = new OutputStreamWriter(mOs);
           
        } catch (IOException eio) {
            Log.e(TAG, "Error", eio);
        }
    }
    
    // All classes implementing the Runnable interface
    // have the method run()
    // When the thread is started, run() is executed, when
    // run() method exits the thread is destroyed
    @Override
    public void run() {
    	
    	byte[] buffer = new byte[250];
    	byte[] buffer2 = new byte[100];
    	byte[] buffer3 = new byte[100];
    	byte b = (byte)0xAA;
    	int s = b & 0xFF;
    	byte c = (byte)0x00;
    	byte d = (byte)0xFE;
    	byte g = (byte)0x55;
    	byte len = 0x00;
    	int datnum = 0;
    	int[] buffer4 = new int[100];
    	int sum = 0;
    	int average = 0;
    	//ByteBuffer testbuffer = ByteBuffer.allocate(77);
    
    	
    	
        // write d to device to start data stream
        try {
            mOsw.write("d\n");
            // call flush to make sure that the characters are written asap
            mOsw.flush();    
        } catch (IOException e) {
            Log.e(TAG, "Cannot start stream", e);
            // stop the thread
            Thread.currentThread().interrupt();
        }
        String dirPath = Environment.getExternalStorageDirectory().toString() + "/CyclePro";
        File dir = new File(dirPath);
    	if(!dir.exists()) //check if the directory exists.
    	{
    		dir.mkdir();  //make directory  		
    	}
    	
    	String dataPath = dirPath + "/data";
    	File data = new File(dataPath);
    	if(!data.exists()) //check if the directory exists.
    	{
    		data.mkdir();  //make directory  		
    	}
    	
        String filePath = dataPath + "/hearttest.csv";
        
        File myFile = new File(filePath);
        while(true) {
        	try{
        		mIs.read(buffer);
        		for(int i = 0;i< 200;i++)
        		{
        			if (buffer[i]== (byte)0xAA)
        			{
        				len = buffer[i+2];
        				datnum = i+4;
    
        				for(int k = 0; k < (int)len; k++)
        				{
        					buffer2[k] = buffer[datnum];
        					datnum++;
        				}
        				
        				for (int z = 0,j=buffer2.length-1; z<j; z++, j--)
        				{
        					byte v = buffer2[z];
        					buffer2[z]=buffer2[j];
        					buffer2[j]=v;
        				}
        				
        				for(int m =0;m<buffer2.length; m++)
        				{
        					buffer4[m] = ((int)buffer2[m] & 0xFF);
        				}
        				
        				sum = 0;
        				average =0;
        				for (int n = 0; n < buffer4.length-1; n++)
        				{
        					sum = sum + buffer4[n]+buffer4[n+1];
        					
        					if(n==buffer4.length-3)
        					{
        						sum= sum+buffer4[n+2];
        					}
        					n++;
        				}
        				average = sum/buffer4.length;
        				myAverage = average;
        			}
        		}
        		System.out.println("WHOLEBUFFER");
        		System.out.println(Arrays.toString(buffer));
        		System.out.println("DATA ONLY");
        		System.out.println(Arrays.toString(buffer2));
        		System.out.println("FINAL BUFFER");
        		System.out.println(Arrays.toString(buffer4));
        		System.out.println("AVERAGE");
        		System.out.println(average);
        	}
        		catch (Exception e){
        			//break;
        		}
        	
        }
        			
//        if(myFile.exists())
//        {
//        	myFile.delete();
//        }

        /*
        try {
 			myFile.createNewFile();
            FileOutputStream fOut;
 			fOut = new FileOutputStream(myFile);
 			OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
        
        // an interrupted thread means that the thread has been told to stop
        while(!Thread.currentThread().isInterrupted()) {
            // read a line of data
            try {
            	
            		
            	
            //	bytes = mIs.read(buffer);
            	//Log.d("TESTINGTESTING", "DATA: " + bytes);
            	
            	
                String line = mBr.readLine();
               
             //   Log.d(TAG, "Data: " + line);
                myOutWriter.append(line + "\n");
                Message x = new Message();
                x.what = HAS_DATA;
                x.obj = (line + "\n");
              //  mHandle.dispatchMessage(x);
            } catch(IOException e) {
                // there is a problem reading, we will just log and close thread
                Log.e(TAG, "Error reading stream", e);
                Thread.currentThread().interrupt();
            }
        }
        }//end of try
        catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
        // out of the loop, the thread is stopping, stop the device
        //disconnect();
    }
    
    private void disconnect() {
        try {
            mIs.close();
            mOs.close();
            mBs.close();
        } catch (IOException e) {
            Log.e(TAG, "Error stopping", e);
        }
    }
    
    public int getAverage() {
    	return myAverage;
    }
}
