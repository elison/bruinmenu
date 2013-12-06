package com.st40611.bruinmenu.fragments;

import com.st40611.bruinmenu.BruinMenu;
import com.st40611.bruinmenu.FoodAdapter;
import java.util.concurrent.ExecutionException;

import com.st40611.bruinmenu.R;
import com.st40611.bruinmenu.FoodDetailsActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.ArrayList;

import com.st40611.bruinmenu.data.Parser;
import com.st40611.bruinmenu.data.Food;

public class MealFragment extends Fragment {
	
	private ArrayList<Food> items;
	private FoodAdapter adapter;
	private ListView lvFood;
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
		return inf.inflate(R.layout.fragments_breakfast, parent, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		generateMenu();
	}
	
	public void generateMenu() {
		int mealPos = ((BruinMenu) getActivity()).getMealPos();
		int hallPos = ((BruinMenu) getActivity()).getHallPos();
		String[] meals = ((BruinMenu) getActivity()).getMeals();
		String[] halls = ((BruinMenu) getActivity()).getHalls();
		String[] defaultTypes = {"Hot Cereals", "Soups", "Prepared Salads", "Exhibition Kitchen", "Euro Kitchen", "Pizza Oven", "Grill", 
							"Hot Food Bar", "From the Bakery"};
		String[] feastTypes = {"Soups", "Prepared Salads", "Bruin Wok", "Spice Kitchen", "Stone Oven", "Iron Grill", "Sweets", "Beverage Special"};
		String[] bruinPlateTypes = {"Soups", "Greens & More", "Freshly Bowled", "Harvest", "Farm Stand", "Stone Fired", "Simply Grilled", "Fruit", "Sweet Bites", "Beverage Special"};
		items = new ArrayList<Food>();
		
		try {
			if ( ((BruinMenu) getActivity()).getData() == null) {
				Parser parser = new Parser();
				AsyncTask<String, Void, HashMap<String, HashMap<String, HashMap<String, ArrayList<Food> >>>> task = parser.execute(Integer.toString(mealPos), "hi");
				((BruinMenu) getActivity()).setData(task.get());
			}
			if (hallPos == 2) {
				if (mealPos == 0) {
					items.add(new Food("Sorry, but Feast currently doesn't serve breakfast!", true));
				}
				for (String type : feastTypes) {
					if (((BruinMenu) getActivity()).getData().get(halls[hallPos]) != null &&
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]) != null && 
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type) != null && 
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type).size() > 1) {
						items.addAll(((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type));
					}
				}
			} else if (hallPos == 3){
				if (mealPos == 0) {
					items.add(new Food("Sorry, but Bruin Plate currently doesn't serve breakfast!", true));
				}
				for (String type : bruinPlateTypes) {
					if (((BruinMenu) getActivity()).getData().get(halls[hallPos]) != null &&
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]) != null && 
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type) != null && 
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type).size() > 1) {
						items.addAll(((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type));
					}
				}
			} else {
				for (String type : defaultTypes) {
					if (((BruinMenu) getActivity()).getData().get(halls[hallPos]) != null &&
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]) != null && 
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type) != null && 
							((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type).size() > 1) {
						items.addAll(((BruinMenu) getActivity()).getData().get(halls[hallPos]).get(meals[mealPos]).get(type));
					}
				}
			}

			lvFood = (ListView) getActivity().findViewById(R.id.lvFood);
			adapter = new FoodAdapter(getActivity(), items);
			lvFood.setAdapter(adapter);
			lvFood.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View parent, int position, long id) {
					Food selectedFood = items.get(position);
					if (selectedFood.isPlaceholder()) {
						return ;
					}
					if (selectedFood.getError()) {
						Toast toast = Toast.makeText(getActivity(),
											"Further information about this item is not available at this time.", Toast.LENGTH_SHORT);
						toast.show();
						return ;
					}
					Intent i = new Intent(getActivity().getApplicationContext(), FoodDetailsActivity.class);
					i.putExtra("Name", selectedFood.getName());
					i.putExtra("Calories", "" + selectedFood.getCalories());
					i.putExtra("Total Fat", "" + selectedFood.getFat() + "g");
					i.putExtra("Cholesterol", "" + selectedFood.getChol() + "mg");
					i.putExtra("Sodium", "" + selectedFood.getSodium() + "mg");
					i.putExtra("Protein", "" + selectedFood.getProtein() + "g");
					String ingredientsList = "";
					for (int k = 0; k < selectedFood.getIngredients().size(); k++) {
						if (k != selectedFood.getIngredients().size() - 1) {
							ingredientsList += selectedFood.getIngredients().get(k) + ", ";
						} else {
							ingredientsList += selectedFood.getIngredients().get(k);
						}
					}
					i.putExtra("Ingredients", ingredientsList);
					Log.i("imageURL", selectedFood.getImageURL());
					i.putExtra("imageURL", "" + selectedFood.getImageURL());
					startActivity(i);
				}
			});
			//tvBody.setText(buf.toString());
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
