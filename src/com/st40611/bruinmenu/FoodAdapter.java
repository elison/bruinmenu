package com.st40611.bruinmenu;

import java.util.List;

import com.st40611.bruinmenu.data.Food;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodAdapter extends ArrayAdapter<Food> {
	
	public FoodAdapter(Context context, List<Food> items) {
		super(context, 0, items);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.food_item, null);
		}
		
		Food item = getItem(position);
		
		ImageView statusImage = (ImageView) view.findViewById(R.id.lvStatus);
		statusImage.setVisibility(View.INVISIBLE);
		if (item.isVegetarian() || item.isVegan()) {
			statusImage.setVisibility(View.VISIBLE);
		}
		
		TextView body = (TextView) view.findViewById(R.id.tvName);
		TextView calories = (TextView) view.findViewById(R.id.tvCalories);
		if (item.placeholder) {
			body.setText(Html.fromHtml("<b>" + item.getName() + "</b>"));
			calories.setText("");
		} else if(item.getError()) {
			body.setText(Html.fromHtml("<font color='#777777'>" + item.getName() + "</font>"));
			calories.setText("");
		} else {
			body.setText(Html.fromHtml("<font color='#777777'>" + item.getName() + "</font>"));
			calories.setText(Html.fromHtml("<font align='right' color='#777777'><small>Calories: " + item.getCalories() + "</font></small>"));
		}
		
		return view;
	}
	
}