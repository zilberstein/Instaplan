package main;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	static ArrayList<Business> businesses = new ArrayList<Business>();
	static ArrayList<Review> reviews = new ArrayList<Review>();
	static ArrayList<YelpUser> yusers = new ArrayList<YelpUser>();


	public static void main(String[] args) throws Exception {
		businesses.clear();
		yusers.clear();
		reviews.clear();
		importData();
		Statement st = null;
		st = makeConnectionWithDatabase(args);
		DDLs(st);
		System.out.println("businesses size = "+ businesses.size() + " yusers =" + yusers.size() + " reviews = " + reviews.size());
		DMLs(businesses, yusers, reviews, st);
	}

	/**
	 * read through the .txt files, insert all the yelp categories in it
	 * into HashMap ycatOurCat which will be used for json parsing
	 * Called once ever
	 * @throws Exception
	 */
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
		
		
		Gson gson = new Gson();
		
		//convertJSON to objects
		String json = "";
		File file = new File("yelp_academic_dataset.json");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedReader d= new BufferedReader(new InputStreamReader(bis));		
		
		boolean userstarted= false;
		boolean bizStarted = false;
		boolean rvStarted = false;
		while((json = d.readLine())!=null){
			//
			Map<String,Object> map=new HashMap<String,Object>();
			map=(Map<String,Object>) gson.fromJson(json, map.getClass());
			Object type = map.get("type");
			
			if(type.equals("user")){
				if(userstarted == false){
					userstarted=true;
					System.out.println("userStart");
				}
				JsonUser juser;
				juser = gson.fromJson(json, JsonUser.class);
				
				Votes v = juser.votes;
				
				//jUsers.add(juser);
				juser.name =URLEncoder.encode(juser.name, "UTF-8");
				//juser.user_id = URLEncoder.encode(juser.user_id, "UTF-8");
				YelpUser yuser = new YelpUser(juser.user_id, juser.review_count, juser.average_stars, v.useful, v.funny, v.cool );
				yusers.add(yuser);
			}
			else if(type.equals("business")){
				if(bizStarted == false){
					bizStarted=true;
					System.out.println("bizStart");
					System.out.println(json);
					
				}
				JsonBusiness jb;
				jb = gson.fromJson(json, JsonBusiness.class);
				
				jb.name = URLEncoder.encode(jb.name, "UTF-8");
				jb.full_address = URLEncoder.encode(jb.full_address, "UTF-8");
				//System.out.println(jb.name);
				Business b = new Business(jb.business_id, jb.name, jb.full_address, jb.city, jb.state, jb.latitutde, jb.longitude, jb.stars, jb.review_count, 0, jb.photo_url);
				
				for(String yc : jb.categories){
					if(ycatOurCat.containsKey(yc)){
						String ourc = ycatOurCat.get(yc);
						b.addCategory(new Category(ourc));
					}
				}
				//jBusinesses.add(jb);
				businesses.add(b);
			}
			else if(type.equals("review")){
				
				if(rvStarted == false){
					rvStarted=true;
					System.out.println("reviewStart");
				}
				JsonReview jr;
				jr = gson.fromJson(json, JsonReview.class);
				jr.text = "";
				Votes v = jr.votes;
				Review r = new Review(jr.business_id, jr.user_id, jr.stars, v.useful, v.funny, v.cool );
				reviews.add(r);
				
				//jReviews.add(jreview);
			}
			
		}

		d.close();
		fis.close();
		bis.close();

	}
	

	public static Statement makeConnectionWithDatabase(String[] args) 
			throws Exception {
		PreparedStatement preparedStatement = null;
		try {
			// This will load the MySQL driver, each DB has its own driver
		      
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			if (args.length <2 ) {
				con = DriverManager.getConnection(
					
						"jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan?"+
								"user=instaplan&password=cis330");
			} else {
				con = DriverManager.getConnection(
						"jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan"
						,args[0],args[1]);  
			}
			
			// Statements allow to issue SQL queries to the database
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
			st.execute("DROP TABLE IF EXISTS belongs");
			st.execute("DROP TABLE IF EXISTS review");
			st.execute("DROP TABLE IF EXISTS business");
			st.execute("DROP TABLE IF EXISTS category");
			st.execute("DROP TABLE IF EXISTS yelpUser");
			st.execute("DROP TABLE IF EXISTS user");
			System.out.println("Dropped all the tables; Now create tables.");
			st.executeUpdate("CREATE TABLE business ("
					+ "id VARCHAR(40), "
					+ "name VARCHAR(255), "
					+ "address VARCHAR(255), "
					+ "city VARCHAR(50), "
					+ "state VARCHAR(50), "
					+ "latitude DECIMAL, "
					+ "longitude DECIMAL, "
					+ "stars TINYINT, "
					+ "metric SMALLINT, "
					+ "photoUrl VARCHAR(255), "
					+ "PRIMARY KEY (id))");
			st.execute("CREATE TABLE category ("
					+ "name VARCHAR(11),"
					+ "PRIMARY KEY (name))");
			st.execute("CREATE TABLE yelpUser ("
					+ "id VARCHAR(40), "
					+ "reviewCount SMALLINT, "
					+ "avgStars DECIMAL, "
					+ "useful SMALLINT, "
					+ "funny SMALLINT, "
					+ "cool SMALLINT, "
					+ "PRIMARY KEY (id))");
			st.execute("CREATE TABLE review ("
					+ "businessId VARCHAR(40), "
					+ "userId VARCHAR(40), "
					+ "stars TINYINT, "
					+ "useful SMALLINT, "
					+ "funny SMALLINT, "
					+ "cool SMALLINT, "
					+ "PRIMARY KEY (businessId, userId), " 
					+ "FOREIGN KEY (userId) REFERENCES yelpUser(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE)");

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
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (name) REFERENCES category(name) ON DELETE CASCADE)");
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
				st.executeUpdate(t);
			} catch ( Exception e) {
				e.printStackTrace();
				break;
			}
		}
		int i = 0;
		System.out.println("LOAD Businesses");
		for (Business b : businesses) {
			i++;
			if(i==10000)
				System.out.println("10,000th Business reached");
			
			try {
				String t0 = "INSERT INTO business VALUES ( '" + b.id + "', '"
						+ b.name + "', '" + b.address + "', '" + b.city +
						"', '" + b.state + "', '" + b.lat + "', '" + b.lon
						+ "', '" + b.stars + "', '" + b.metric + "', '" + b.photo + "')";
				st.executeUpdate(t0);
				
			} catch (Exception e) {
				System.out.println(i);
				Gson gson = new Gson();
				System.out.println(gson.toJson(b));
				e.printStackTrace();
				
				break;
			}
		}
		i=0;
		System.out.println("LOAD YelpUsers");
		for (YelpUser y : users) {
			i++;
			if(i==10000)
				System.out.println("10,000th reached");
			if(i==100000)
				System.out.println("100,000th reached");
			
			try {
				String t0 = "INSERT INTO yelpUser VALUES ('" +  y.id + "', '" 
						+ y.num_review + "', '" + y.avg_stars + "', '" + 
						y.useful + "', '" + y.funny + "', '" + y.cool + "')";
				st.execute(t0);
			} catch (Exception e) {
				System.out.println(i);
				Gson gson = new Gson();
				System.out.println(gson.toJson(y));
				e.printStackTrace();
				break;
			}
		}
		
		i=0;
		System.out.println("LOAD Reviews");
		for (Review r : reviews) {
			i++;
			if(i==10000)
				System.out.println("10,000th reached");
			if(i==100000)
				System.out.println("100,000th reached");
			if(i==300000)
				System.out.println("300,000th reached");
			
			try {
				String t0 = "INSERT INTO review VALUES ('" + r.b_id + 
						"', '" + r.u_id + "', '" + r.stars + "', '" + r.useful
						+ "', '" + r.funny + "', '" + r.cool + "')";
				st.execute(t0); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		i=0;
		System.out.println("LOAD business_belongsTo_categories");
		
		for (Business b : businesses) {
			i++;
			if(i==1000)
				System.out.println("1,000th reached");
			if(i==10000)
				System.out.println("10,000th reached");
			if(i==100000)
				System.out.println("100,000th reached");
			if(i==300000)
				System.out.println("300,000th reached");
			for (Category c : b.categories) {
				try {
					String t = "INSERT INTO belongs VALUES ('" + b.id +
							"', '" + c.name + "')";
					st.execute(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
}
