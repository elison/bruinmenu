package com.st40611.bruinmenu;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_details);
		getActionBar().setTitle((String)getIntent().getSerializableExtra("Name"));
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5b98")));
		TextView calories = (TextView) findViewById(R.id.tvCalories);
		TextView fat = (TextView) findViewById(R.id.tvFat);
		TextView cholesterol = (TextView) findViewById(R.id.tvCho);
		TextView sodium = (TextView) findViewById(R.id.tvSodium);
		TextView protein = (TextView) findViewById(R.id.tvProtein);
		TextView ingredients = (TextView) findViewById(R.id.tvIngredients);
		calories.setText((String)getIntent().getSerializableExtra("Calories"));
		fat.setText((String)getIntent().getSerializableExtra("Total Fat"));
		cholesterol.setText((String)getIntent().getSerializableExtra("Cholesterol"));
		sodium.setText((String)getIntent().getSerializableExtra("Sodium"));
		protein.setText((String)getIntent().getSerializableExtra("Protein"));
		ingredients.setText((String)getIntent().getSerializableExtra("Ingredients"));
		
		ImageView image = (ImageView) findViewById(R.id.lvImage);
		Log.i("Image", (String)getIntent().getSerializableExtra("imageURL"));
		ImageLoader.getInstance().displayImage((String)getIntent().getSerializableExtra("imageURL"), image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.food_details, menu);
		return true;
	}

}
