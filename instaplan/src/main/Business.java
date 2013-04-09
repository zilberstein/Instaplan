package main;

public class Business {
	
	public int id;
	public String name;
	public String address;
	public String city;
	public String state;
	public float lat;
	public float lon;
	public int stars;
	public String photo;
	
	public Business (int i, String n, String a, String c, String s, float la, float lo, int st, String p) {
		id = i;
		name = n;
		address = a;
		city = c;
		state = s;
		lat = la;
		lon = lo;
		stars = st;
		photo = p;
	}

}
