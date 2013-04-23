package main;

import com.google.gson.Gson;

public class JsonUser {
	public String type;
	public Integer review_count;
	public String user_id;
	public String name;
	public float average_stars;
	public String url;
	public Votes votes;

	public JsonUser(String uid, String name){
		user_id = uid;
		this.name = name;
	}
	public void setReviewCount(Integer c){
		review_count = c;
	}
	public void setAvgStars(float avg_stars){
		average_stars = avg_stars;
	}
	public void dump(){
		Gson gson = new Gson();
		System.out.println(gson.toJson(this));
	}

}
