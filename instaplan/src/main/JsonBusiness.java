package main;

import java.util.ArrayList;

import com.google.gson.Gson;

public class JsonBusiness {
	public String type = "business";
	public String business_id;
	public String name;
	public ArrayList<String> neighborhoods;
	public String full_address;
	public String city;
	public String state;
	public float latitutde;
	public float longitude;
	public float stars;
	public int review_count;
	public String photo_url;
	public ArrayList<String> categories;
	public boolean open;
	public ArrayList<String> schools;
	public String url;

	public void dump(){
		Gson gson = new Gson();
		System.out.println(gson.toJson(this));
	}
	
}
