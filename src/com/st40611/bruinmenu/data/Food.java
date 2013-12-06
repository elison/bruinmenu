package com.st40611.bruinmenu.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Food implements Serializable  {
	
	private static final long serialVersionUID = 2045285743994454879L;
	
	private String foodName;
	public boolean placeholder = false;
	private int calories;
	private ArrayList<String> ingredients;
	private double fat;
	private double sodium;
	private double chol;
	private double protein;
	public String test;
	private int goodForHeart;
	private boolean isVegan;
	private boolean isVegetarian;
	private String imageURL;
	private boolean hasError;
	
	public Food() {
		placeholder = true;
		calories = -1;
		fat = -1.0;
		sodium = -1.0;
		chol = -1.0;
		protein = -1.0;
		goodForHeart = 0;
		isVegan = false;
		isVegetarian = false;
		imageURL="";
		ingredients = new ArrayList<String>();
		hasError = false;
	}
	
	public Food(String foodName, boolean placeholder) {
		if(foodName.startsWith("w/") || foodName.startsWith("&") ) {
		this.foodName = "\t\t" + foodName;
		} else {
			this.foodName = foodName;
		}
		this.placeholder = placeholder;
		
		calories = -1;
		fat = -1.0;
		sodium = -1.0;
		chol = -1.0;
		protein = -1.0;
		goodForHeart = 0;
		isVegan = false;
		isVegetarian = false;
		imageURL = "";
		ingredients = new ArrayList<String>();
		hasError = false;
	}
	
	public void newFood(String foodName, boolean placeholder)
	{
		if(foodName.startsWith("w/") || foodName.startsWith("&") ) {
			this.foodName = "\t\t" + foodName;
		} else {
			this.foodName = foodName;
		}
		this.placeholder = placeholder;
		
		calories = -1;
		fat = -1.0;
		sodium = -1.0;
		chol = -1.0;
		protein = -1.0;
		goodForHeart = 0;
		isVegan = false;
		isVegetarian = false;
		imageURL = "";
		ingredients = new ArrayList<String>();
	}
	
	public void clear(){
		foodName = "";
		placeholder = true;
		calories = -1;
		fat = -1.0;
		sodium = -1.0;
		chol = -1.0;
		protein = -1.0;
		goodForHeart = 0;
		isVegan = false;
		isVegetarian = false;
		imageURL="";
		ingredients = new ArrayList<String>();
	}
	
	public void setName(String name){
		foodName = name;
	}
	
	public String getName(){
		return foodName;
	}
	
	public void setCalories(int cal) {
		calories = cal;
	}
	
	public int getCalories(){
		return calories;
	}
	
	public void setSodium(double sod){
		sodium = sod;
	}
	
	public double getSodium(){
		return sodium;
	}
	
	public void setFat(double f){
		fat = f;
	}
	
	public double getFat(){
		return fat;
	}
	
	public void setChol(double c){
		chol = c;
	}
	
	public double getChol(){
		return chol;
	}
	
	public void setProtein(double p){
		protein = p;
	}
	
	public double getProtein(){
		return protein;
	}
	
	public void setIngredients(ArrayList<String> ingr){
		ingredients = ingr;
	}
	
	public ArrayList<String> getIngredients(){
		return ingredients;
	}
	
	public boolean doesContain(String s)
	{
		return ingredients.contains(s);
	}
	
	public boolean isGoodForHeart()
	{
		//first time running
		if (goodForHeart == 0)
		{
			String[] goodFoods = {
				"banana", "raisin", "spinach", "brazil nut",
				"almond", "milk", "pumpkin", "fish", "avocado",
				"tomato", "garlic", "tofu", "fish oil"
			};
			
			int count = 0;
			
			for (int i = 0; i < goodFoods.length; i++)
			{
				for (int j = 0; j < ingredients.size(); j++)
				{
					if (ingredients.get(j).equals(goodFoods[i]))
						count++;
				}
			}
			
			//must contain at least 2 items from list
			//or else too many things will be in it
			if (count > 1)
				goodForHeart = 1;
			else 
				goodForHeart = -1;
		}
		if (goodForHeart == 1)
			return true;
		else
			return false;
	}
	
	public boolean isVegan()
	{
		return isVegan;
	}
	
	public void setVegan(boolean b){
		isVegan = b;
	}
	
	public boolean isVegetarian()
	{
		return isVegetarian;
	}
	
	public void setVegetarian(boolean b){
		isVegetarian = b;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public void setImageURL(String URL) {
		imageURL = "http://menu.ha.ucla.edu/foodpro/" + URL;
	}
	
	public boolean isPlaceholder() {
		return placeholder;
	}
	
	public void setError(boolean error) {
		hasError = error;
	}
	
	public boolean getError() {
		return hasError;
	}
	
}
