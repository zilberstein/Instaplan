package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Main {

	static HashMap<String, String> ycatOurCat = new HashMap<String, String>();
	static ArrayList<Category> our_categories = new ArrayList<Category>();
	
	static ArrayList<JsonBusiness> jBusinesses = new ArrayList<JsonBusiness>();
	static ArrayList<JsonReview> jReviews = new ArrayList<JsonReview>();
	static ArrayList<JsonUser> jUsers = new ArrayList<JsonUser>();
	static ArrayList<Business> businesses = new ArrayList<Business>();
	static ArrayList<Review> reviews = new ArrayList<Review>();
	static ArrayList<YelpUser> yusers = new ArrayList<YelpUser>();

	/**
	 * read through the .txt files, insert all the yelp categories in it
	 * into HashMap ycatOurCat which will be used for json parsing
	 * Called once ever
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
		
		importData();
	}

	public static void categorySetUp() throws Exception{
		ArrayList<String> ourCategories = new ArrayList<String>();
		String[] categories = {"active", "breakfast", "cats", "college", 
				"culture", "dessert", "dinner", "family", "footer", "kids", 
				"lunch", "nightlife", "old_people","pamper"};
		Arrays.sort(categories);
		ourCategories.addAll(Arrays.asList(categories));
		
		for(String c: categories){
			Category category = new Category(c);
			our_categories.add(category);
		}
		
		for(String oc: ourCategories){
			File file = new File(oc + ".txt");
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedReader d= new BufferedReader(new InputStreamReader(bis));		
			String ycat = "";
			while((ycat = d.readLine()) !=null){
				if(!ycatOurCat.containsKey(ycat)){
					ycatOurCat.put(ycat, oc);
				}
			}
			fis.close();
			bis.close();
			d.close();
		}
	}
	

	/**
	 * read yelp dataset json file into json objects
	 * @throws Exception
	 */
	public static void importData() throws Exception{
		jUsers.clear();
		jBusinesses.clear();
		jReviews.clear();
		
		Gson gson = new Gson();
		
		//convertJSON to objects
		String json = "";
		File file = new File("yelp_academic_dataset.json");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedReader d= new BufferedReader(new InputStreamReader(bis));		
		while((json = d.readLine())!=null){
			//
			Map<String,Object> map=new HashMap<String,Object>();
			map=(Map<String,Object>) gson.fromJson(json, map.getClass());
			for(String k : map.keySet()){
				System.out.println(k + " : " + map.get(k));
			}
			Object type = map.get("type");
			
			if(type.equals("user")){
				JsonUser juser;
				juser = gson.fromJson(json, JsonUser.class);
				juser.dump();
				jUsers.add(juser);
				
			}
			else if(type.equals("business")){
				JsonBusiness jbiz;
				jbiz = gson.fromJson(json, JsonBusiness.class);
				jBusinesses.add(jbiz);
			}
			else if(type.equals("review")){
				JsonReview jreview;
				jreview = gson.fromJson(json, JsonReview.class);
				jReviews.add(jreview);
			}
			
		}
		
		for(JsonUser juser: jUsers){
			Votes v = juser.votes;
			YelpUser yuser = new YelpUser(juser.user_id, juser.review_count, juser.average_stars, v.useful, v.funny, v.cool );
			
			yusers.add(yuser);
		}
		for(JsonReview jr: jReviews){
			Votes v = jr.votes;
			Review r = new Review(jr.business_id, jr.user_id, jr.stars, v.useful, v.funny, v.cool );
			reviews.add(r);
		}
		for(JsonBusiness jb: jBusinesses){
			Business b = new Business(jb.business_id, jb.name, jb.full_address, jb.city, jb.state, jb.latitutde, jb.longitude, jb.stars, jb.review_count, 0, jb.photo_url);
			//CATEGORIES? REVIEWS?
			businesses.add(b);
		}
		
		d.close();
		fis.close();
		bis.close();
		
	}
	
	
	public static Statement makeConnectionWithDatabase(String[] args) 
			throws Exception {
		try {
			Connection con = null;
			if (args.length <2 ) {
				con = DriverManager.getConnection(
						"jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan"
						,"instaplan","password");  
			} else {
				con = DriverManager.getConnection(
						"jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan"
						,args[0],args[1]);  
			}
			Statement st = con.createStatement();
			return st;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	
	private static String safeDropTable(String table) {
		String query = "Begin Execute Immediate 'Drop Table "
				+ table
				+ "'; Exception when others then if SQLCODE != -942 then RAISE; end if; end;";
		return query;
	}
	
	
	
	public static void DDLs(Statement st) throws Exception {
		try {
			st.execute(safeDropTable("business"));
			st.execute(safeDropTable("category"));
			st.execute(safeDropTable("review"));
			st.execute(safeDropTable("yelpUser"));
//			st.execute(safeDropTable("writes"));
			st.execute(safeDropTable("belongs"));
			st.execute(safeDropTable("user"));
			
			st.execute("CREATE TABLE business ("
					+ "id VARCHAR(40), "
					+ "name VARCHAR(40), "
					+ "address VARCHAR(40), "
					+ "city VARCHAR(40), "
					+ "state VARCHAR(40), "
					+ "latitude DECIMAL, "
					+ "longitude DECIMAL, "
					+ "stars TINYINT, "
					+ "metric SMALLINT, "
					+ "photoUrl VARCHAR(40), "
					+ "PRIMARY KEY (id))");
			st.execute("CREATE TABLE category ("
					+ "name VARCHAR(11),"
					+ "PRIMARY KEY (name))");
			st.execute("CREATE TABLE review ("
					+ "businessId VARCHAR(40), "
					+ "userId VARCHAR(40), "
					+ "stars TINYINT, "
					+ "useful SMALLINT, "
					+ "funny SMALLINT, "
					+ "cool SMALLINT, "
					+ "PRIMARY KEY (businessId, userId))" 
					+ "FOREIGN KEY (userId) REFERENCES yelpUser(id), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id))");
			st.execute("CREATE TABLE yelpUser ("
					+ "id VARCHAR(40), "
					+ "reviewCount SMALLINT, "
					+ "avgStars DECIMAL, "
					+ "useful SMALLINT, "
					+ "funny SMALLINT, "
					+ "cool SMALLINT, "
					+ "PRIMARY KEY (id))");
/*			st.execute("CREATE TABLE writes ("
					+ "userId VARCHAR(40), "
					+ "businessId VARCHAR(40), "
					+ "PRIMARY KEY (userId, businessID), "
					+ "FOREIGN KEY (userID) REFERENCES yelpUser(id), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id))");
*/
			st.execute("CREATE TABLE belongs ("
					+ "businessId VARCHAR(40), "
					+ "name VARCHAR(11), "
					+ "PRIMARY KEY (businessId, name), "
					+ "FOREIGN KEY businessId REFERENCES business(id), "
					+ "FOREIGN KEY name REFERENCES categories(name))");
			st.execute("CREATE TABLE user ("
					+ "username VARCHAR(16), "
					+ "password VARCHAR(17), "
					+ "firstName VARCHAR(15), "
					+ "lastName VARCHAR(25), "
					+ "email VARCHAR(50), "
					+ "PRIMARY KEY (username))");
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	
	public static void DMLs(ArrayList<Business> businesses, 
			ArrayList<YelpUser> users, ArrayList<Review> reviews, Statement st) {
		String[] categories = {"active", "breakfast", "cats", "college", 
				"culture", "dessert", "dinner", "family", "footer", "kids", 
				"lunch", "nightlife", "old_people","pamper"};
		for (String c : categories) {
			String t = "INSERT INTO category VALUES ('" + c + "')";
			try {
				st.execute(t);
			} catch ( Exception e) {
				e.printStackTrace();
			}
		}
		for (Business b : businesses) {
			try {
				String t0 = "INSERT INTO business VALUES (" + b.id + ", '"
						+ b.name + "', '" + b.address + "', '" + b.city +
						"', '" + b.state + "', '" + b.lat + "', '" + b.lon
						+ "', '" + b.stars + "', '" + b.metric + "', '" + b.photo + "')";
				st.execute(t0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (YelpUser y : users) {
			try {
				String t0 = "INSERT INTO yelpUser VALUES (" +  y.id + ", '" 
						+ y.num_review + "', '" + y.avg_stars + "', '" + 
						y.useful + "', '" + y.funny + "', '" + y.cool + "')";
				st.execute(t0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Review r : reviews) {
			try {
				String t0 = "INSERT INTO review VALUES (" + r.b_id + 
						", " + r.u_id + ", '" + r.stars + "', '" + r.useful
						+ "', '" + r.funny + "', '" + r.cool + "')";
				st.execute(t0); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (Business b : businesses) {
			for (Category c : b.categories) {
				try {
					String t = "INSERT INTO belongs VALUES (" + b.id +
							", '" + c.name + "')";
					st.execute(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
}
