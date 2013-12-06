package com.st40611.bruinmenu.data;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;

public class Parser extends AsyncTask<String, Void, HashMap<String, HashMap<String, HashMap<String, ArrayList<Food>>>> > {
	
	public Parser() {
	}
	
	private String getDigits(String s)
	{
		StringBuffer buf = new StringBuffer();

		for(int i = 0; i < s.length(); i++)
		{
			if (validChar(s.charAt(i)))
			{
				buf.append(s.charAt(i));
			}
			//just want to grab numbers
			if (!buf.toString().isEmpty() && (s.charAt(i) == 'g' || s.charAt(i) == 'm'))
				return buf.toString();
			//just want to grab calories
			if (s.contains("Fat Cal."))
			{
				if (s.charAt(i) == 'F')
					return buf.toString();
			}
		}
		
		return buf.toString();
	}
	
	private boolean validChar(char c){
		char[] compare = {
				  '0','1','2','3','4','5',
				  '6','7','8','9','.'
				};
		for (int i = 0; i < compare.length; i++)
		{
			if (compare[i] == c)
				return true;
		}
		return false;
	}

	private Food passBy(Food f)
	{
		Food temp = f;
		return temp;
	}
	
	private HashMap<String, HashMap<String, ArrayList<Food>>> nameAndFood(String loc){
		Calendar c = Calendar.getInstance(); 
		HashMap<String, HashMap<String, ArrayList<Food>>> foodItems = new HashMap<String, HashMap<String, ArrayList<Food>>>();
		try {
			//Location will have to be inputted depending on deneve, covel, rieber
			//covel location 07
			//deneve location 01
			//feast location 04
			int totalMeals = 3;
			if (loc.equals("04") || loc.equals("02")) {
				totalMeals = 2;
			}
			
			Document doc = Jsoup.connect("http://menu.ha.ucla.edu/foodpro/default.asp?location="+ loc + "&date=" 
										+ "12" + "%2F" 
										+ "6"
										+ "%2F2013").get();
			
			for (int mealType = 0; mealType < totalMeals; mealType++) {
				HashMap<String, ArrayList<Food>> mealItems = new HashMap<String, ArrayList<Food>>();
				Elements parseTable = doc.select("table.menugridtable");
				for (Element table : parseTable) {
					Elements rows = table.select("tr");
					for (int i = 3; i < rows.size(); i++) {
			        	Elements items = rows.get(i).select("td").get(mealType).select("ul li");
			        	ArrayList<Food> itemsList = new ArrayList<Food>();
			        	if (loc.equals("04")) {
			        		switch (i) {	
			        		case 3: itemsList.add(new Food("Soups", true));
			        				break;
			        		case 4: 
			        				itemsList.add(new Food("Prepared Salads", true));
			        				break;
			        		case 5: 
			        				itemsList.add(new Food("Bruin Wok", true));
			        				break;
			        		case 6: 
			        				itemsList.add(new Food("Spice Kitchen", true));
		    						break;
			        		case 7: 
			        				itemsList.add(new Food("Stone Oven", true));
		    						break;
			        		case 8: 
			        				itemsList.add(new Food("Iron Grill", true));
		    						break;
			        		case 9: 
			        				itemsList.add(new Food("Sweets", true));
		    						break;
			        		case 10:
			        				itemsList.add(new Food("Beverage Special", true));
		    						break;
				        	}
			        	} else if (loc.equals("02")) {
			        		switch (i) {	
			        		case 3: itemsList.add(new Food("Soups", true));
			        				break;
			        		case 4: 
			        				itemsList.add(new Food("Greens & More", true));
			        				break;
			        		case 5: 
			        				itemsList.add(new Food("Freshly Bowled", true));
			        				break;
			        		case 6: 
			        				itemsList.add(new Food("Harvest", true));
		    						break;
			        		case 7: 
			        				itemsList.add(new Food("Farm Stand", true));
		    						break;
			        		case 8: 
			        				itemsList.add(new Food("Stone Fired", true));
		    						break;
			        		case 9: 
			        				itemsList.add(new Food("Simply Grilled", true));
		    						break;
			        		case 10:
			        				itemsList.add(new Food("Fruit", true));
		    						break;
			        		case 11:
		        				itemsList.add(new Food("Sweet Bites", true));
	    						break;
			        		case 12:
		        				itemsList.add(new Food("Beverage Special", true));
	    						break;
				        	}
			        	} else {
			        		switch (i) {	
				        		case 3: itemsList.add(new Food("Hot Cereals", true));
				        				break;
				        		case 4: 
				        				itemsList.add(new Food("Soups", true));
				        				break;
				        		case 5: 
				        				itemsList.add(new Food("Prepared Salads", true));
				        				break;
				        		case 6: 
				        				itemsList.add(new Food("Exhibition Kitchen", true));
			    						break;
				        		case 7: 
				        				itemsList.add(new Food("Euro Kitchen", true));
			    						break;
				        		case 8: 
				        				itemsList.add(new Food("Pizza Oven", true));
			    						break;
				        		case 9: 
				        				itemsList.add(new Food("Grill", true));
			    						break;
				        		case 10:
				        				itemsList.add(new Food("Hot Food Bar", true));
			    						break;
			    				case 11:
			    						itemsList.add(new Food("From the Bakery", true));
										break;
				        	}
			        	}
			        	for (int j = 1; j < items.size(); j++) {
			        		/* Construct Food Item */
			        		Food food = new Food(items.get(j).text(), false);
			        		String url = "http://menu.ha.ucla.edu/foodpro/" + items.get(j).select("a").first().attr("href");
			        		Document recipe = Jsoup.connect(url).get();
			        		
			        		if (!recipe.select("p.rderror").isEmpty()) {
			        			food.setError(true);
			        			itemsList.add(food);
			        			continue;
			        		}
			        		
			        		String isV = "";
			        		if (!items.get(j).select("img").isEmpty())
			        			isV = items.get(j).select("img").first().attr("alt");

			        		String vegan = "Vegan Menu Option";
			        		String vegetarian = "Vegetarian Menu Option";
			    			
			        		//determines if food is vegan or vegetarian
			        		if (!isV.isEmpty()){
			        			if (isV.equals(vegan))
			        				food.setVegan(true);
			        			if (isV.equals(vegetarian))
			        				food.setVegetarian(true);
			        		}
			        					    			
			    			Elements elem = recipe.select("p.nfcal");
			    			for (Element e : elem){
			    				food.setCalories(Integer.parseInt(getDigits(e.text())));
			    			}
			    			
			    			elem = recipe.select("p.nfnutrient");
			    			//loop through all paragraph tags
			    			//the paragraph tags contain nutrition facts
			    			for (Element e : elem){
			    				//Set fat, cholesterol, sodium, protein
			    				if(e.text().contains("Total Fat"))
			    					food.setFat(Double.parseDouble(getDigits(e.text())));
			    				if(e.text().contains("Cholesterol"))
			    					food.setChol(Double.parseDouble(getDigits(e.text())));
			    				if(e.text().contains("Sodium"))
			    					food.setSodium(Double.parseDouble(getDigits(e.text())));
			    				if(e.text().contains("Protein"))
			    					food.setProtein(Double.parseDouble(getDigits(e.text())));				
			    			}
			    			
			    			//Get ingredients
			    			elem = recipe.select("p.rdingred_list");
			    			for(Element e : elem)
			    			{
			    				String s = e.text();
			    				String[] ingredients = s.split(",");
			    				ArrayList<String> foodIngredients = new ArrayList<String>();
			    				for(int k = 0; k < ingredients.length; k++) {
			    					foodIngredients.add(ingredients[k].trim().toLowerCase(Locale.US));
			    				}
			    				food.setIngredients(foodIngredients);
			    			}
			    			
			    			food.setImageURL(recipe.select("div.rdimg").first().select("img").first().attr("src"));

			        		itemsList.add(food);
			        	}
			        	
			        	mealItems.put(itemsList.get(0).getName(), itemsList);
					}
				}
				if (loc.equals("04") || loc.equals("02")) {
					switch  (mealType) {
						case 0:
							foodItems.put("Lunch", mealItems);
							break;
						case 1:
							foodItems.put("Dinner",  mealItems);
							break;
					}
				} else {
					switch  (mealType) {
						case 0:
							foodItems.put("Breakfast", mealItems);
							break;
						case 1:
							foodItems.put("Lunch",  mealItems);
							break;
						case 2:
							foodItems.put("Dinner", mealItems);
							break;
					}
				}
			}
			return foodItems;
		} catch (Exception e) {
			return foodItems;
		}
	}
	
	@Override
	protected HashMap<String, HashMap<String, HashMap<String, ArrayList<Food> >>> doInBackground(String... params) {
		//covel location 07
		//deneve location 01
		//feast location 04
		HashMap<String, HashMap<String, HashMap<String, ArrayList<Food>>>> allDining = new HashMap<String, HashMap<String, HashMap<String, ArrayList<Food>>>>();
		allDining.put("Covel", nameAndFood("07"));
		allDining.put("De Neve", nameAndFood("01"));
		allDining.put("Feast", nameAndFood("04"));
		allDining.put("Bruin Plate", nameAndFood("02"));
		return allDining;
	}
	
	@Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onPostExecute(HashMap<String, HashMap<String, HashMap<String, ArrayList<Food> >>> response)
    {
        // after completed finished the progressbar
    }
}
