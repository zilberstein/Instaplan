package main;

public class Review {
	
	public String b_id;
	public String u_id;
	public String text;
	public float useful_score;
	
	public Review(String b, String u, String t, float uscore) {
		b_id = b;
		u_id = u;
		text = t;
		useful_score = uscore;
	}

}
