package main;

import com.google.gson.Gson;

public class JsonReview {
	public String type;
	public String business_id;
	public String user_id;
	public Integer stars;
	public String text;
	public String date;
	public Votes votes;

	public JsonReview(String bid, String uid, Integer stars){
		business_id = bid;
		user_id = uid;
		this.stars = stars;
		
	}
	
	public void dump(){
		Gson gson = new Gson();
		System.out.println(gson.toJson(this));
	}
}
