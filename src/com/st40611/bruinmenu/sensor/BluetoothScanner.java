package com.st40611.bruinmenu.sensor;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * Android require you to scan for BT devices before you can
 * connect to them
 * 
 * This class implements a broadcast listener that listens to
 * the bluetooth manager events
 */
public class BluetoothScanner extends BroadcastReceiver  {
    // just a string for printing logs (so we know where the message came from)
    private static final String TAG = "BluetoothScanner";
    
    private BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
    private Context mContext;
    private List<String> mAcceptedDevices = new ArrayList<String>();
    // for call back to the UI thread in Android
    private Handler mHandler;
    
    // Once a scan is complete this public member will hold the list of devices
    // that other threads can use
    public List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
    // ID for signaling that a scan has been completed. Used by handlers
    public static final int SCAN_COMPLETE = 0x01;
    
    public BluetoothScanner(Context context, Handler handler) {
        // need the context of the activity to register listeners in its name
        mContext = context;
        // A broadcast receiver cannot pass messages directly to an Activity
        // so we need to make use of the Handler mechanism
        mHandler = handler;
        // we will hard code in the list of devices that will be accepted 
       // mAcceptedDevices.add("00:06:66:43:43:1A");
        mAcceptedDevices.add("00:14:C5:A1:01:D5");
        // clear the device list
        mDevices.clear();
    }
    
    // method for starting a scan
    public void startScan() {
        /*
         *  Register the listeners that would listen to bluetooth scan messages.
         *  The ones we are interested in are ACTION_FOUND for when a device
         *  is discovered, and ACTION_DISCOVERY_FINISHED for when scan has been
         *  completed 
         */
        
        // register the listener
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(BluetoothDevice.ACTION_FOUND);
        iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(this, iFilter);
        
        // ask the BluetoothAdapter to start a scan
        Log.d(TAG, "Starting a scan");
        ba.startDiscovery();
    }
    
    /*
     * Extending the broadcast receiver require you to implement
     * the onReceive method to handle any messages that are delivered
     */
    @Override
    public void onReceive(Context context, Intent msg) {
        
        // see what message this is
        String action = msg.getAction();
        Log.d(TAG, action);
        if(action == BluetoothDevice.ACTION_FOUND) {
            //Get the device
            BluetoothDevice device = 
                    msg.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // A device is discovered, here we will simply check if this is a 
            // device we want. For a more complete application you would
            // ask the user which devices are the correct ones and go from there
            String mac = device.getAddress();
            String name = device.getName();
            Log.d(TAG, "Found " + name + " ["+mac+"]");
            if(mAcceptedDevices.contains(mac) && (mDevices.size() < 1)) {
                mDevices.add(device);
                Log.d(TAG, "Is an accepted device");
                
            }
        } else if(action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            context.unregisterReceiver(this);
            // tell the UI that scan has completed
            Message m = new Message();
            m.what = SCAN_COMPLETE;
            mHandler.dispatchMessage(m);
        }
    }
}
