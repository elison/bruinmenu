package com.st40611.bruinmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.st40611.bruinmenu.data.Food;
import com.st40611.bruinmenu.fragments.MealFragment;
import com.st40611.bruinmenu.sensor.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BruinMenu extends FragmentActivity implements TabListener, HasHandler  {
	MealFragment breakfastFragment;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private final String[] meal = {"Breakfast", "Lunch", "Dinner"};
    private final String[] halls = {"Covel", "De Neve", "Feast", "Bruin Plate"};
    private final String[] options = {"Covel", "De Neve", "Feast", "Bruin Plate", "", 
    									"Low Fat", "Low Sodium", "Low Cholesterol", "High Protein", "Lower Heart Rate", "",
    									"Scan Bluetooth", "Connect Bluetooth", "Check Heart Rate", "Disconnect Bluetooth"};
    private int averageHeartRate = 0;
    private int mealPos = 0;
    private int hallPos = 0;
    private HashMap<String, HashMap<String, HashMap<String, ArrayList<Food> >>> data;
    private List<Thread> mThreads = new ArrayList<Thread>();
    private List<BluetoothClass> mBTClasses = new ArrayList<BluetoothClass>(); 
    private BluetoothScanner mBs;
    private Handler mHandler = new WeakRefHandler(this);
    private boolean canScan = true;
    private boolean canConnect = false;
    private boolean connected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bruin_menu);
		getActionBar().setTitle("Menu Items");
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5b98")));
		getActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5b98")));
		getActionBar().setTitle(halls[hallPos]);
		Toast toast = Toast.makeText(this, "Loading ...", Toast.LENGTH_SHORT);
        toast.show();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, options));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.hello_world,  /* "open drawer" description */
                R.string.hello_world  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(halls[hallPos]);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Dining Halls");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bruin_menu, menu);
		setupNavigationTabs();
		return true;
	}
	
	private void setupNavigationTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		Tab tabBreakfast = actionBar.newTab().setText("Breakfast")
				.setTag("BreakfastFragment")
				.setTabListener(this);
		
		Tab tabLunch = actionBar.newTab().setText("Lunch")
				.setTag("LunchFragment")
				.setTabListener(this);
		
		Tab tabDinner = actionBar.newTab().setText("Dinner")
				.setTag("DinnerFragment")
				.setTabListener(this);
		
		actionBar.addTab(tabBreakfast);
		actionBar.addTab(tabLunch);
		actionBar.addTab(tabDinner);
		actionBar.selectTab(tabBreakfast);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentManager manager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fts = manager.beginTransaction();
		if (("BreakfastFragment").equals(tab.getTag())) {
			// set the fragment in FrameLayout to homeTimeLine
			mealPos = 0;
		} else if (("LunchFragment").equals(tab.getTag())) {
			// set the fragment in FrameLayout to homeTimeLine
			mealPos = 1;
		} else if (("DinnerFragment").equals(tab.getTag())) {
			// set the fragment in FrameLayout to homeTimeLine
			mealPos = 2;
		}
		breakfastFragment = new MealFragment();
		fts.replace(R.id.frame_container, breakfastFragment);
		fts.commit();
		
	}
	
	public int getMealPos() {
		return mealPos;
	}
	
	public int getHallPos() {
		return hallPos;
	}
	
	public String[] getMeals() {
		return meal;
	}
	
	public String[] getHalls() {
		return halls;
	}

	public void setData(HashMap<String, HashMap<String, HashMap<String, ArrayList<Food> >>> newData) {
		data = newData;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, ArrayList<Food> >>> getData() {
		return data;
	}
	
	public double getSum(String hall, String meal, String nutrient){
		double count = 0;
		if (data.get(hall) != null && data.get(hall).get(meal) != null)
		{
			for (Map.Entry<String, ArrayList<Food>> m: data.get(hall).get(meal).entrySet())
			{
				ArrayList<Food> food = m.getValue();
				for (int i = 0; i < food.size(); i++)
				{
					if (nutrient.equals("Protein"))
						count += food.get(i).getProtein();
					else if (nutrient.equals("Cholesterol"))
						count += food.get(i).getChol();
					else if(nutrient.equals("Sodium"))
						count += food.get(i).getSodium();
					else if(nutrient.equals("Fat"))
						count += food.get(i).getFat();
				}
			}
		}
		return count;
	}
	
	
	public int getMin(String meal, String nutrient){
		int diningHall;
		double dcount = 0;
		double ccount = 0;
		double rcount = 0;
		double bcount = 0;
		ccount = getSum("Covel", meal, nutrient);
		dcount = getSum("De Neve", meal, nutrient);
		rcount = getSum("Feast", meal, nutrient);
		bcount = getSum("Bruin Plate", meal, nutrient);
		if (meal.equals("Breakfast"))
		{
			rcount = 99999999;
			bcount = 99999999;
		}
		double min = ccount;
		if (min > dcount)
			min = dcount;
		if (min > rcount)
			min = rcount;
		if (min > bcount)
			min = bcount;
		
		if (min == ccount)
			diningHall = 0;
		else if (min == dcount)
			diningHall = 1;
		else if (min == rcount)
			diningHall = 2;
		else
			diningHall = 3;
		
		return diningHall;
	}
	
	public int getMax(String meal, String nutrient){
		int diningHall;
		double dcount = 0;
		double ccount = 0;
		double rcount = 0;
		double bcount = 0;
		ccount = getSum("Covel", meal, nutrient);
		dcount = getSum("De Neve", meal, nutrient);
		rcount = getSum("Feast", meal, nutrient);
		bcount = getSum("Bruin Plate", meal, nutrient);
		double max = ccount;
		if (max < dcount)
			max = dcount;
		if (max < rcount)
			max = rcount;
		if (max < bcount)
			max = bcount;
		
		if (max == ccount)
			diningHall = 0;
		else if (max == dcount)
			diningHall = 1;
		else if (max == rcount)
			diningHall = 2;
		else 
			diningHall = 3;
		
		return diningHall;
	}
	
	public int getGoodForHeartCount(String hall, String meal){
		int count = 0;
		if (data.get(hall).get(meal) != null)
		{
			for (Map.Entry<String, ArrayList<Food>> m: data.get(hall).get(meal).entrySet())
			{
				ArrayList<Food> food = m.getValue();
				for (int i = 0; i < food.size(); i++)
				{
					if (food.get(i).isGoodForHeart())
						count++;
				}
			}
		}
		return count;
	}
	
	public int getBestLowerHR(String meal){
		int diningHall;
		
		int ccount = getGoodForHeartCount("Covel", meal);
		int dcount = getGoodForHeartCount("De Neve", meal);
		int rcount = getGoodForHeartCount("Feast", meal);
		int bcount = getGoodForHeartCount("Bruin Plate", meal);
		
		int max = ccount;
		if (max < dcount)
			max = dcount;
		if(max < rcount)
			max = rcount;
		
		if (max == ccount)
			diningHall = 0;
		else if (max == dcount)
			diningHall = 1;
		else if (max == rcount)
			diningHall = 2;
		else
			diningHall = 3;
		
		return diningHall;
	}
	
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	 @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Pass the event to ActionBarDrawerToggle, if it returns
	        // true, then it has handled the app icon touch event
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }
	        // Handle your other action bar items...

	        return super.onOptionsItemSelected(item);
	    }
	    
	    private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView parent, View view, int position, long id) {
	            selectItem(position);
	            mDrawerList.setItemChecked(position, true);
	            mDrawerLayout.closeDrawer(mDrawerList);
	        }
	    }

	    /** Swaps fragments in the main content view */
	    private void selectItem(int position) {
	    	if (position > 2) {
	    		Toast toast;
	    		switch(position) {
	    			case 3:
	    			case 4:
	    			case 10:
	    				break;
	    			// Low Fat
	    			case 5:
	    				position = getMin(meal[mealPos], "Fat");
	    				toast = Toast.makeText(this, halls[position] + " is the best option for Low Fat for " + meal[mealPos], Toast.LENGTH_SHORT);
	    		        toast.show();
	    				break;
	    				// Low Sodium
	    			case 6:
	    				position = getMin(meal[mealPos], "Sodium");
	    				toast = Toast.makeText(this, halls[position] + " is the best option for Low Sodium for " + meal[mealPos], Toast.LENGTH_SHORT);
	    		        toast.show();
	    				break;
	    				//Low Cholesterol
	    			case 7:
	    				position = getMin(meal[mealPos], "Cholesterol");
	    				toast = Toast.makeText(this, halls[position] + " is the best option for Low Cholesterol for " + meal[mealPos], Toast.LENGTH_SHORT);
	    		        toast.show();
	    				break;
	    				//High Protein
	    			case 8:
	    				position = getMax(meal[mealPos], "Protein");
	    				toast = Toast.makeText(this, halls[position] + " is the best option for High Protein for " + meal[mealPos], Toast.LENGTH_SHORT);
	    		        toast.show();
	    				break;
	    				//Low Heart Rate
	    			case 9:
	    				position = getBestLowerHR(meal[mealPos]);
	    				toast = Toast.makeText(this, halls[position] + " is the best option for Lowering Heart Rate for " + meal[mealPos], Toast.LENGTH_SHORT);
	    		        toast.show();
	    				break;
	    			case 11:
	    				if (canScan) {
	    					scanBluetooth();
	    				} else {
	    					Toast.makeText(getApplicationContext(), "Can't scan, wait a bit!",
	    	                        Toast.LENGTH_SHORT).show();
	    				}
	    				return;
	    			case 12:
	    				if (canConnect) {
	    					connectBluetooth();
	    				} else {
	    					if (connected) {
	    						Toast.makeText(getApplicationContext(), "You are already connected",
	    	                        Toast.LENGTH_SHORT).show();
	    					} else {
	    						Toast.makeText(getApplicationContext(), "Can't connect at the moment",
		    	                        Toast.LENGTH_SHORT).show();
	    					}
	    				}
	    				return;
	    			case 13:
	    				averageHeartRate = mBTClasses.get(0).getAverage();
	    				
	    				if (averageHeartRate != 0) {
	    					options[13] = "Heart Rate: " + Integer.toString(averageHeartRate);
	    					if (averageHeartRate > 100) {
	    						Toast.makeText(getApplicationContext(), "Your heart rate is " + averageHeartRate + ". Too high!",
	        	                        Toast.LENGTH_SHORT).show();
	    					} else {
	    						Toast.makeText(getApplicationContext(), "Your heart rate is " + averageHeartRate + ". You are OK!",
	        	                        Toast.LENGTH_SHORT).show();
	    					}
	    				}
	    				return;
	    		}
	    	} 
	    	FragmentManager manager = getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction fts = manager.beginTransaction();
			hallPos = position;
			breakfastFragment = new MealFragment();
			fts.replace(R.id.frame_container, breakfastFragment);
			fts.commit();
	    }

		@Override
		public void processUpdate(Message msg) {
			switch(msg.what) {
            case BluetoothScanner.SCAN_COMPLETE:
                // we want to enable the connect button and show a message
                Toast.makeText(getApplicationContext(), "BT Scan Complete",
                        Toast.LENGTH_SHORT).show();
                //mBtnConnect.setEnabled(true);
			}
		}
			
		public void scanBluetooth() {
            mBs = new BluetoothScanner(getApplicationContext(), mHandler);
            canScan = false;
            // scan for device
            mBs.startScan();
            canConnect = true;
		}
		
		public void connectBluetooth() {
			mThreads.clear();
            mBTClasses.clear();
            
            if(mBs.mDevices.size() < 1) {
                Toast.makeText(getApplicationContext(), "No device found",
                        Toast.LENGTH_SHORT).show();
            }
            
            for(BluetoothDevice device : mBs.mDevices) {
                // instance a new BluetoothClass
                BluetoothClass bc = new BluetoothClass(device);
                // create a thread class with the runnable BluetoothClass
                Thread t = new Thread(bc);
                // add the references to lists for later use
                mThreads.add(t);
                mBTClasses.add(bc);
                // start thread
                t.start();
                averageHeartRate = bc.getAverage();
            }
            connected = true;
            canConnect = false;
            Toast.makeText(getApplicationContext(), "Devices connected",
                    Toast.LENGTH_SHORT).show();
		}
}
