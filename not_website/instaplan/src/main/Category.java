package main;

/**
 * this is just for OUR category, not from the yelp data.
 * 
 * @author Sung
 *
 */
public class Category {
	
	public String name;
	
	public Category(String n) {
		name = n;
	}
	public boolean equals(Category c){
		if(c.name.equals(this.name)){
			return true;
		}
		else
			return false;
	}

}
