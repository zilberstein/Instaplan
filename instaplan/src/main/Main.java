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
import java.util.HashSet;
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
	
	//@Susan: DDLtemp, DMLtemp
	
	
	//yelpCategory -> set[OurCategory] map
	static HashMap<String, HashSet<String>> ycatOurCat = new HashMap<String, HashSet<String>>();
	//collections for DB
	static ArrayList<Category> our_categories = new ArrayList<Category>();
	static ArrayList<Business> businesses = new ArrayList<Business>();
	static ArrayList<Review> reviews = new ArrayList<Review>();
	static ArrayList<YelpUser> yusers = new ArrayList<YelpUser>();

	static HashMap<String, Review> bestReviewMap = new HashMap<String, Review>();

	public static void main(String[] args) throws Exception {
		if(bestReviewMap!=null) bestReviewMap.clear();
		else	bestReviewMap = new HashMap<String, Review>();
		
		businesses.clear();
		yusers.clear();
		reviews.clear();
		String r = "Bill who? Oh, Bobby. Hmmm, right. I confess, I am a terrible excuse for a foodie. I don't play chef trivia, I can't remember the names of half the international foods I consume, and when a restaurant opens with a crazy long wait just cuz it's some guy from the Food Network... well, that don't impress me much. Bobby's turkey burger, however, did make me sit back and say wow. I got it in the LA style: avocado-relish, watercress, cheddar, & tomato. It was the perfect size, on a fresh bun, and mouth-wateringly-good. My group shared an order of sweet potato fries with horseradish honey mustard. The fries were ok, but not the most amazing things ever. The dipping sauce was kinda watery so you had to do a serious scoop to taste any on the fries. I considered getting a milkshake (you can have yours boozed up for an extra $2.50!), but luckily waiting in line so long meant I had a chance to see how tiny they were before ordering. Pass. While I certainly can't knock the place for its food, I'll say I was a little disappointed to wait 30+ minutes in line for an $8 burger in what I'd call a posh cafeteria. (Long, communal tables = hovering for seats. Ew.) I love the decor, the burgers are great, but the overall business model really turned me off.";
		System.out.println(r.length());
		r=r.replaceAll("'", "");
		System.out.println(r);
		/*categorySetUp();
		
		importData();
		System.out.println("businesses size = "+ businesses.size() + " yusers =" + yusers.size() + " best reviews = " + bestReviewMap.size());
		
		Statement st = null;
		st = makeConnectionWithDatabase(args);
		DDLtemp(st);
		
		//DDLs(st);
		DMLtemp(businesses,st);
		//DMLs(businesses, st);
		*/ 
	}

	/**
	 * read through the .txt files, insert all the yelp categories in it
	 * into HashMap ycatOurCat which will be used for json parsing
	 * Called once ever
	 * @throws Exception
	 */
	public static void categorySetUp() throws Exception{
		ArrayList<String> ourCategories = new ArrayList<String>();
		String[] categories = {"active", "breakfast", "college", 
				"culture", "dessert", "dinner", "family", "kids", 
				"lunch", "nightlife", "old_people","pamper", 
				"overnight", "restaurant"};
		Arrays.sort(categories);
		ourCategories.addAll(Arrays.asList(categories));
		
		//populate Category ArrayList
		for(String c: categories){
			Category category = new Category(c);
			our_categories.add(category);
		}
		try{
			for(String oc: ourCategories){
				File file = new File(oc + ".txt");
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedReader d= new BufferedReader(new InputStreamReader(bis));		
				String ycat = "";
				
				while((ycat = d.readLine()) !=null){
					if(!ycatOurCat.containsKey(ycat)){
						ycatOurCat.put(ycat, new HashSet<String>());
					}
					HashSet<String> catVals = ycatOurCat.get(ycat);
					catVals.add(oc);
					ycatOurCat.put(ycat, catVals);
				}
				
				fis.close();
				bis.close();
				d.close();
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		//dump ycatOurCat to check
		Gson gson = new Gson();
		System.out.println(gson.toJson(ycatOurCat));
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
		
		//for test sake:
		boolean userstarted= false;
		boolean bizStarted = false;
		boolean rvStarted = false;
		System.out.println("num_entries of ycatOurCat:"+ycatOurCat.entrySet().size());
		
		while((json = d.readLine())!=null){
			
			//read in generic hash obj
			Map<String,Object> map=new HashMap<String,Object>();
			map=(Map<String,Object>) gson.fromJson(json, map.getClass());
			Object type = map.get("type");
			
			if(type.equals("user")){
				
				if(userstarted == false){ //test
					userstarted=true;
					System.out.println("userStart");
				}
				
				JsonUser juser = gson.fromJson(json, JsonUser.class);
				Votes v = juser.votes;
				juser.name.replaceAll("'", "");
				juser.name =URLEncoder.encode(juser.name, "UTF-8");
				//convert to YelpUser and add to yusers array
				YelpUser yuser = new YelpUser(juser.user_id, juser.review_count, juser.average_stars, v.useful, v.funny, v.cool );
				yusers.add(yuser);
			}
			else if(type.equals("business")){//add businessto collection
				
				if(bizStarted == false){ bizStarted=true; System.out.println("bizStart"); }
				
				JsonBusiness jb= gson.fromJson(json, JsonBusiness.class);
				
				jb.name = jb.name.replaceAll("'", "");
				jb.name = URLEncoder.encode(jb.name, "UTF-8");				
				
				jb.full_address = jb.full_address.replaceAll("'", "");
				jb.full_address = URLEncoder.encode(jb.full_address, "UTF-8");
				Business b = new Business(jb.business_id, jb.name, jb.full_address, jb.city, jb.state, jb.latitude, jb.longitude, jb.stars, jb.review_count, 0, jb.photo_url);
				
				for(String yc : jb.categories){
					//lowercase, replace whitespaces for yelp categories
					yc = yc.toLowerCase();
					String yc1 = yc.replaceAll(" ", "_");
					String yc2 = yc.replaceAll(" ", "");
					
					if(ycatOurCat.containsKey(yc1)){
						
						HashSet<String> catSet = ycatOurCat.get(yc1);
						for(String c: catSet){
							b.addCategory(new Category(c));
						}
					}
					else if(ycatOurCat.containsKey(yc2)){
						
						HashSet<String> catSet = ycatOurCat.get(yc2);
						for(String c: catSet){
							b.addCategory(new Category(c));
						}
					}
				}
				businesses.add(b);
			}
			else if(type.equals("review")){ //add review to collection
				JsonReview jr = gson.fromJson(json, JsonReview.class);
				jr.text = jr.text.replaceAll("'", "");
				jr.text =URLEncoder.encode(jr.text, "UTF-8");
				Votes v = jr.votes;
				float useful_score = 3*((float)(v.cool) + (float)(v.useful) + (float)(v.funny));
				Review r = new Review(jr.business_id, jr.user_id, jr.text, useful_score);
				
				if(rvStarted == false){
					rvStarted=true;
					System.out.println("reviewStart");
					System.out.println(gson.toJson(jr));
					System.out.println(gson.toJson(r));
				}
				
				Review bestReview = bestReviewMap.get(jr.business_id);

				if(bestReview==null){
					if(r.text.length() < 1500)
						bestReviewMap.put(jr.business_id, r);
				}
				else{
					
					if(bestReview.useful_score < r.useful_score && r.text.length()<1500){
						bestReviewMap.put(jr.business_id, r);
					}
				}
			}

		}

		d.close();
		fis.close();
		bis.close();
		
		//System.out.println("bestReviewMap size: " + bestReviewMap.size());
	}
	

	public static Statement makeConnectionWithDatabase(String[] args) 
			throws Exception {
		PreparedStatement preparedStatement = null;
		
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			if (args.length <2 ) {
				con = DriverManager.getConnection("jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan?"+
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
	

	
	public static void DDLtemp(Statement st) throws Exception{
		try{
			st.execute("DROP TABLE IF EXISTS review");

			st.execute("CREATE TABLE review ("
					+ "businessId VARCHAR(40), "
					+ "userId VARCHAR(40), "
					+ "text VARCHAR(65000), "
					+ "PRIMARY KEY (businessId, userId), " 
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE)");
			
		}
		catch( Exception e){
			e.printStackTrace();
		}
		
	}
	public static void DMLtemp(ArrayList<Business> businesses, Statement st) throws Exception{

		int i=0;
		System.out.println("LOAD reviews");
		
		for(Map.Entry<String, Review> entry :bestReviewMap.entrySet()){
			i++;
			if(i>=1000 && i%1000==0){ System.out.println(i+"th Review reached");}
			Review r = entry.getValue();
			if(i==1){
				Gson g = new Gson();
				System.out.println(g.toJson(r));
			}
			try {
				String t0 = "INSERT INTO review VALUES ('" + r.b_id + 
						"', '" + r.u_id + "', '" + r.text +  "')";
				st.execute(t0); 
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
				//or do nothing and continue
			}
			
		}
		


		System.out.println("LOAD business_belongsTo_categories");
		int in=0;
		
		for (Business b : businesses) {
			in++;

			if(in==5000){
				System.out.println(in+"th reached");
			}
			if(in == 10000){
				System.out.println(in+"th reached");
			}
			
			for (Category c : b.categories) {
				try {
					if(in == 1){System.out.print("first line");}
					
					String t = "INSERT INTO belongs VALUES ('" + b.id +
							"', '" + c.name + "')";
					st.executeUpdate(t);
				} catch (Exception e) {
					//e.printStackTrace();
					//System.out.println("bid:"+ b.id  +" not found; " + "cname:" + c.name +" not found");
					//do nothing and continue
				}
				
			}
			
		}

	}
		

	
	//commented out yelpUser, review as well as writes. feel free to take it back.
	public static void DDLs(Statement st) throws Exception {
		try {
			//safe drop
			st.execute("DROP TABLE IF EXISTS belongs");
			st.execute("DROP TABLE IF EXISTS review");
			st.execute("DROP TABLE IF EXISTS business");
			//st.execute("DROP TABLE IF EXISTS user");
			st.execute("DROP TABLE IF EXISTS category");
			
			System.out.println("Dropped all the tables; Now create tables.");
			
			st.executeUpdate("CREATE TABLE business ("
					+ "id VARCHAR(40), "
					+ "name VARCHAR(255), "
					+ "address VARCHAR(255), "
					+ "city VARCHAR(50), "
					+ "state VARCHAR(50), "
					+ "latitude DECIMAL(20,10), "
					+ "longitude DECIMAL(20,10), "
					+ "stars TINYINT, "
					+ "metric SMALLINT, "
					+ "photoUrl VARCHAR(255), "
					+ "PRIMARY KEY (id))");
			
			st.execute("CREATE TABLE category ("
					+ "name VARCHAR(11),"
					+ "PRIMARY KEY (name))");
	

			st.execute("CREATE TABLE belongs ("
					+ "businessId VARCHAR(40), "
					+ "name VARCHAR(11), "
					+ "PRIMARY KEY (businessId, name), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (name) REFERENCES category(name) ON DELETE CASCADE)");
			/*st.execute("CREATE TABLE user ("
					+ "firstname VARCHAR(60) not null, "
					+ "lastname VARCHAR(60) not null, "
					+ "username VARCHAR(20), "
					+ "email VARCHAR(60) not null, "
					+ "password CHAR(32) not null, "
					+ "avatar BIT not null, "
					+ "PRIMARY KEY (username))");*/
			st.execute("CREATE TABLE review ("
					+ "businessId VARCHAR(40), "
					+ "userId VARCHAR(40), "
					+ "text VARCHAR(65000), "
					+ "PRIMARY KEY (businessId, userId), " 
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE)");
			
			/*st.execute("CREATE TABLE yelpUser ("
			+ "id VARCHAR(40), "
			+ "reviewCount SMALLINT, "
			+ "avgStars DECIMAL, "
			+ "useful SMALLINT, "
			+ "funny SMALLINT, "
			+ "cool SMALLINT, "
			+ "PRIMARY KEY (id))");*/
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	

	
	public static void DMLs(ArrayList<Business> businesses,  Statement st) {
		
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
			if(i>=1000 && i%1000==0){
				System.out.println(i+"th Business reached");
				
			}
			
			try {
				String t0 = "INSERT INTO business VALUES ( '" + b.id + "', '"
						+ b.name + "', '" + b.address + "', '" + b.city +
						"', '" + b.state + "', '" + b.lat + "', '" + b.lon
						+ "', '" + b.stars + "', '" + b.metric + "', '" + b.photo + "')";
				st.executeUpdate(t0);
				
			} catch (Exception e) {
				System.out.println(i);
//				Gson gson = new Gson();
				System.out.println(e.getMessage());
				//e.printStackTrace();
				
			}

		}
		
		i=0;
		System.out.println("LOAD reviews");
		
		for(Map.Entry<String, Review> entry :bestReviewMap.entrySet()){
			i++;
			if(i>=1000 && i%1000==0){ System.out.println(i+"th Business reached");}
			Review r = entry.getValue();
			if(i==1){
				Gson g = new Gson();
				System.out.println(g.toJson(r));
			}
			try {
				String t0 = "INSERT INTO review VALUES ('" + r.b_id + 
						"', '" + r.u_id + "', '" + r.text +  "')";
				st.execute(t0); 
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
				//or do nothing and continue
			}
			
		}
		


		System.out.println("LOAD business_belongsTo_categories");
		int in=0;
		
		for (Business b : businesses) {
			in++;

			if(in==5000){
				System.out.println(in+"th reached");
			}
			if(in == 10000){
				System.out.println(in+"th reached");
			}
			
			for (Category c : b.categories) {
				try {
					if(in == 1){System.out.print("first line");}
					
					String t = "INSERT INTO belongs VALUES ('" + b.id +
							"', '" + c.name + "')";
					st.executeUpdate(t);
				} catch (Exception e) {
					//e.printStackTrace();
					//System.out.println("bid:"+ b.id  +" not found; " + "cname:" + c.name +" not found");
					//do nothing and continue
				}
				
			}
			
		}
		
		/*System.out.println("LOAD YelpUsers");
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
				//or do nothing and continue
			}
		}
		*/
		/*i=0;
		System.out.println("LOAD Reviews");
		for (Review r : reviews) {
			i++;
			if(i==10000)
				System.out.println("10,000th reached");
			if(i==300000)
				System.out.println("300,000th reached");
			
			try {
				String t0 = "INSERT INTO review VALUES ('" + r.b_id + 
						"', '" + r.u_id + "', '" + r.stars + "', '" + r.useful
						+ "', '" + r.funny + "', '" + r.cool + "')";
				st.execute(t0); 
			} catch (Exception e) {
				e.printStackTrace();
				
				break;
				//or do nothing and continue
			}
		}*/
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
}
