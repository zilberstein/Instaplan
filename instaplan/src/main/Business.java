package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Business {
	
	public String id;
	public String name;
	public String address;
	public String city;
	public String state;
	public float lat;
	public float lon;
	public float stars;
	public int num_reviews;
	public int metric;
	public String photo;
	public Set<String> categories = new HashSet<String>();
	public ArrayList<Review> reviews = new ArrayList<Review>();
	
	public Business (String i, String n, String a, String c, String s, 
			float la, float lo, float st, int nr, int m, String p) {
		id = i;
		name = n;
		address = a;
		city = c;
		state = s;
		lat = la;
		lon = lo;
		stars = st;
		num_reviews = nr;
		metric = (int) ((int) (Math.pow((double)st, 2.0))*Math.sqrt(num_reviews));
		photo = p;
	}
	
	public void addCategory(String c) {
		if (!categories.contains(c)) {
			categories.add(c);
		}
	}
	
	public void addReview(Review r) {
		if (!reviews.contains(r)) {
			reviews.add((r));
		}
	}

}
