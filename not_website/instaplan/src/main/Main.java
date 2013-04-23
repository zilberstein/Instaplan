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

	static String[] categories = { "active", "breakfast", "college", "culture",
		"dessert", "dinner", "family", "kids", "lunch", "nightlife",
		"old_people", "pamper", "overnight", "restaurant" };

	// yelpCategory -> set[OurCategory] map
	static HashMap<String, HashSet<String>> ycatOurCat = new HashMap<String, HashSet<String>>();
	// collections for DB
	static ArrayList<Category> our_categories = new ArrayList<Category>();
	static ArrayList<Business> businesses = new ArrayList<Business>();
	static ArrayList<Review> reviews = new ArrayList<Review>();
	static ArrayList<YelpUser> yusers = new ArrayList<YelpUser>();
	static HashMap<String, Review> bestReviewMap = new HashMap<String, Review>();

	public static void main(String[] args) throws Exception {
		if (bestReviewMap != null)
			bestReviewMap.clear();
		else
			bestReviewMap = new HashMap<String, Review>();
		our_categories.clear();
		businesses.clear();
		yusers.clear();
		reviews.clear();

		categorySetUp();
		importData();
		
		System.out.println("businesses size = " + businesses.size()
				+ " yusers =" + yusers.size() + " best reviews = "
				+ bestReviewMap.size());

		Connection conn = makeConnectionWithDatabase(args);
		//DDLtemp(conn);
		DMLtemp(conn);
		/*THIS WILL DELETE TABLE AND RECREATE AND POPULATE ALL THE TABLES!*/
		//DDLs(conn);
		//DMLs(businesses, conn);
	}

	/**
	 * read through the .txt files, insert all the yelp categories in it into
	 * HashMap ycatOurCat which will be used for json parsing Called once ever
	 * 
	 * @throws Exception
	 */
	public static void categorySetUp() throws Exception {
		ArrayList<String> ourCategoriesArray = new ArrayList<String>();
		Arrays.sort(categories);
		ourCategoriesArray.addAll(Arrays.asList(categories));

		// populate Static our_categories ArrayList for DML use
		for (String c : categories) {
			Category category = new Category(c);
			our_categories.add(category);
		}

		// fill up ycatOurCat
		try {
			for (String oc : ourCategoriesArray) {
				File file = new File(oc + ".txt");
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedReader d = new BufferedReader(
						new InputStreamReader(bis));
				String ycat = "";

				while ((ycat = d.readLine()) != null) {
					if (!ycatOurCat.containsKey(ycat)) {
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// dump ycatOurCat to check
		Gson gson = new Gson();
		System.out.println(gson.toJson(ycatOurCat));
	}

	/**
	 * read yelp dataset json file into json objects
	 * 
	 * @throws Exception
	 */
	public static void importData() throws Exception {
		Gson gson = new Gson();
		// convertJSON to objects
		String json = "";
		File file = new File("yelp_academic_dataset.json");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedReader d = new BufferedReader(new InputStreamReader(bis));

		// for testing:
		boolean userstarted = false;
		boolean bizStarted = false;
		boolean rvStarted = false;
		System.out.println("num_entries of ycatOurCat:"
				+ ycatOurCat.entrySet().size());

		while ((json = d.readLine()) != null) {

			// read in generic hash obj
			Map<String, Object> map = new HashMap<String, Object>();
			map = (Map<String, Object>) gson.fromJson(json, map.getClass());
			Object type = map.get("type");

			if (type.equals("user")) {

				if (userstarted == false) { // test
					userstarted = true;
					System.out.println("userStart");
				}

				JsonUser juser = gson.fromJson(json, JsonUser.class);
				Votes v = juser.votes;
				juser.name.replaceAll("'", "");
				juser.name = URLEncoder.encode(juser.name, "UTF-8");
				// convert to YelpUser and add to yusers array
				YelpUser yuser = new YelpUser(juser.user_id,
						juser.review_count, juser.average_stars, v.useful,
						v.funny, v.cool);
				yusers.add(yuser);
			} else if (type.equals("business")) {// add businessto collection

				if (bizStarted == false) {
					bizStarted = true;
					System.out.println("bizStart");
				}

				JsonBusiness jb = gson.fromJson(json, JsonBusiness.class);

				jb.name = jb.name.replaceAll("'", "");
				jb.name = URLEncoder.encode(jb.name, "UTF-8");

				jb.full_address = jb.full_address.replaceAll("'", "");
				jb.full_address = URLEncoder.encode(jb.full_address, "UTF-8");

				Business b = new Business(jb.business_id, jb.name,
						jb.full_address, jb.city, jb.state, jb.latitude,
						jb.longitude, jb.stars, jb.review_count, 0,
						jb.photo_url);

				for (String yc : jb.categories) {
					// lowercase, replace whitespaces for yelp categories
					yc = yc.toLowerCase();
					String yc1 = yc.replaceAll(" ", "_");
					String yc2 = yc.replaceAll(" ", "");

					if (ycatOurCat.containsKey(yc1)) {

						HashSet<String> catSet = ycatOurCat.get(yc1);
						for (String c : catSet) {
							b.addCategory(c);
						}
					} else if (ycatOurCat.containsKey(yc2)) {

						HashSet<String> catSet = ycatOurCat.get(yc2);
						for (String c : catSet) {
							b.addCategory(c);
						}
					}
				}
				businesses.add(b);
			} else if (type.equals("review")) { // add review to collection
				JsonReview jr = gson.fromJson(json, JsonReview.class);
				jr.text = jr.text.replaceAll("'", "");
				jr.text = URLEncoder.encode(jr.text, "UTF-8");
				Votes v = jr.votes;
				float useful_score = 3 * ((float) (v.cool) + (float) (v.useful) + (float) (v.funny));
				Review r = new Review(jr.business_id, jr.user_id, jr.text,
						useful_score);

				if (rvStarted == false) {
					rvStarted = true;
					System.out.println("reviewStart");
					System.out.println(gson.toJson(jr));
					System.out.println(gson.toJson(r));
				}

				Review bestReview = bestReviewMap.get(jr.business_id);

				if (bestReview == null) {
					if (r.text.length() < 1500)
						bestReviewMap.put(jr.business_id, r);
				} else {

					if (bestReview.useful_score < r.useful_score
							&& r.text.length() < 1500) {
						bestReviewMap.put(jr.business_id, r);
					}
				}
			}

		}

		d.close();
		fis.close();
		bis.close();

		// System.out.println("bestReviewMap size: " + bestReviewMap.size());
	}

	/**
	 * Connect to database and return Connection obj
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static Connection makeConnectionWithDatabase(String[] args)
			throws Exception {

		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			if (args.length < 2) {
				con = DriverManager.getConnection(
						"jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan",
						"instaplan", "cis330");
			} else {
				con = DriverManager.getConnection(
						"jdbc:mysql://SQL09.FREEMYSQL.NET/instaplan",
						args[0], args[1]);
			}

			return con;

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static void DDLtemp(Connection conn) throws Exception {
		try {

			Statement st = conn.createStatement();
			st.execute("DROP TABLE IF EXISTS belongs");
			
			
			String ddlBelongs = "CREATE TABLE belongs ("
					+ "businessId VARCHAR(40), "
					+ "name VARCHAR(11), "
					+ "PRIMARY KEY (businessId, name), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (name) REFERENCES category(name) ON DELETE CASCADE)";
			PreparedStatement pstmt = conn.prepareStatement(ddlBelongs);
			pstmt.executeUpdate();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void DMLtemp( Connection conn)
			throws Exception {
		Statement st = conn.createStatement();
		
		st.execute("DROP TABLE IF EXISTS review");
		PreparedStatement pstmt;
		System.out.println("Dropped belongs; Now create review table.");
		String ddlReview = "CREATE TABLE review ("
				+ "businessId VARCHAR(40), "
				+ "userId VARCHAR(40), "
				+ "text VARCHAR(3000), "
				+ "PRIMARY KEY (businessId, userId), "
				+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE)";
		pstmt = conn.prepareStatement(ddlReview);
		pstmt.executeUpdate();

		//DDL DONE
		StringBuffer sql;
		
		System.out.println("LOAD reviews");
		sql = new StringBuffer("INSERT INTO review VALUES (?,?,?)");
		int ir = 1;
		for(ir=1; ir< bestReviewMap.size(); ir++) {
	        sql.append(", (?,?,?)");
	    }

	    try {
	        pstmt= conn.prepareStatement(sql.toString());
	        int j = 1;
	        for(Map.Entry<String, Review> entry : bestReviewMap.entrySet()) {
	        	Review r = entry.getValue();
	        	pstmt.setString(j++, r.b_id);
				pstmt.setString(j++, r.u_id);
				pstmt.setString(j++, r.text);
	        	
	        }

				pstmt.executeUpdate();
				pstmt.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    

	}


	// commented out yelpUser, review as well as writes. feel free to take it
	// back.
	public static void DDLs(Connection conn) throws Exception {
		try {
			// safe drop
			Statement st = conn.createStatement();
			st.execute("DROP TABLE IF EXISTS belongs");
			st.execute("DROP TABLE IF EXISTS review");
			st.execute("DROP TABLE IF EXISTS business");
			st.execute("DROP TABLE IF EXISTS category");
			// st.execute("DROP TABLE IF EXISTS user");
			PreparedStatement pstmt;
			System.out.println("Dropped all the tables; Now create tables.");

			String ddlBusiness = "CREATE TABLE business(" + "id VARCHAR(40), "
					+ "name VARCHAR(255), " + "address VARCHAR(255), "
					+ "city VARCHAR(50), " + "state VARCHAR(50), "
					+ "latitude DECIMAL(20,10), "
					+ "longitude DECIMAL(20,10), " + "stars TINYINT, "
					+ "metric SMALLINT, " + "photoUrl VARCHAR(255), "
					+ "PRIMARY KEY (id))";
			pstmt = conn.prepareStatement(ddlBusiness);
			pstmt.executeUpdate();

			String ddlCategory = "CREATE TABLE category ("
					+ "name VARCHAR(11)," + "PRIMARY KEY (name))";
			pstmt = conn.prepareStatement(ddlCategory);
			pstmt.executeUpdate();

			String ddlBelongs = "CREATE TABLE belongs ("
					+ "businessId VARCHAR(40), "
					+ "name VARCHAR(11), "
					+ "PRIMARY KEY (businessId, name), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (name) REFERENCES category(name) ON DELETE CASCADE)";
			pstmt = conn.prepareStatement(ddlBelongs);
			pstmt.executeUpdate();

			String ddlReview = "CREATE TABLE review ("
					+ "businessId VARCHAR(40), "
					+ "userId VARCHAR(40), "
					+ "text VARCHAR(3000), "
					+ "PRIMARY KEY (businessId, userId), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id) ON DELETE CASCADE)";
			pstmt = conn.prepareStatement(ddlReview);
			pstmt.executeUpdate();

			/*
			 * st.execute("CREATE TABLE user (" +
			 * "firstname VARCHAR(60) not null, " +
			 * "lastname VARCHAR(60) not null, " + "username VARCHAR(20), " +
			 * "email VARCHAR(60) not null, " + "password CHAR(32) not null, " +
			 * "avatar BIT not null, " + "PRIMARY KEY (username))");
			 */
			/*
			 * st.execute("CREATE TABLE yelpUser (" + "id VARCHAR(40), " +
			 * "reviewCount SMALLINT, " + "avgStars DECIMAL, " +
			 * "useful SMALLINT, " + "funny SMALLINT, " + "cool SMALLINT, " +
			 * "PRIMARY KEY (id))");
			 */

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void DMLs(ArrayList<Business> businesses, Connection conn) {

		PreparedStatement pstmt;
		try {

			System.out.println("LOAD Categories");
			for (String c : categories) {
				pstmt = conn
						.prepareStatement("INSERT INTO category VALUES (?)");
				pstmt.setString(1, c);
				pstmt.executeUpdate();
			}

			System.out.println("INSERT into Businesses");

			StringBuffer sql = new StringBuffer("INSERT INTO business VALUES (?,?,?,?,?,?,?,?,?,?)");
			int ib = 1;
			for(ib=1; ib< businesses.size() ; ib++) {
		        sql.append(", (?,?,?,?,?,?,?,?,?,?)");
		    }
		    System.out.println(ib);
		    try {
		        pstmt= conn.prepareStatement(sql.toString());
		        int j = 1;
		        for(Business b: businesses) {
		        	
					pstmt.setString(j++, b.id); 
					pstmt.setString(j++, b.name);
					pstmt.setString(j++, b.address);
					pstmt.setString(j++, b.city);
					pstmt.setString(j++, b.state);
					pstmt.setFloat(j++, b.lat);
					pstmt.setFloat(j++, b.lon);
					pstmt.setFloat(j++, b.stars);
					pstmt.setFloat(j++, b.metric);
					pstmt.setString(j++, b.photo);
					
					
		        }

					pstmt.executeUpdate();
					pstmt.close();

		    } catch (SQLException e) {
		        e.printStackTrace();
		        
		    }
		    
		    System.out.println("LOAD reviews");
			sql = new StringBuffer("INSERT INTO review VALUES (?,?,?)");
			int ir = 1;
			for(ir=1; ir< bestReviewMap.size(); ir++) {
		        sql.append(", (?,?,?)");
		    }

		    try {
		        pstmt= conn.prepareStatement(sql.toString());
		        int j = 1;
		        for(Map.Entry<String, Review> entry : bestReviewMap.entrySet()) {
		        	Review r = entry.getValue();
		        	pstmt.setString(j++, r.b_id);
					pstmt.setString(j++, r.u_id);
					pstmt.setString(j++, r.text);
		        	
		        }

					pstmt.executeUpdate();
					pstmt.close();

		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    
			System.out.println("LOAD business_belongsTo_categories");
			int in = 0;
			int count_cb_pair = 0;
			for(Business b: businesses){
				for(String cat: b.categories){
					count_cb_pair++;
				}
			}
			sql = new StringBuffer("INSERT INTO belongs VALUES (?,?)");
			for(in = 1; in < count_cb_pair; in++){
				sql.append(", (?,?)");
			}
			try{
				pstmt= conn.prepareStatement(sql.toString());
				in = 1;
				for (Business b : businesses) {
					for (String c : b.categories) {
						try {
							pstmt.setString(in++, b.id);
							pstmt.setString(in++, c);

						} catch (Exception e) {
							e.printStackTrace();
							break;
						}

					}
					
				}
				pstmt.executeUpdate();
			}
			catch(SQLException e){
				e.printStackTrace();
			}

			/*
			 * System.out.println("LOAD YelpUsers"); for (YelpUser y : users) {
			 * i++; if(i==10000) System.out.println("10,000th reached");
			 * if(i==100000) System.out.println("100,000th reached");
			 * 
			 * try { String t0 = "INSERT INTO yelpUser VALUES ('" + y.id +
			 * "', '" + y.num_review + "', '" + y.avg_stars + "', '" + y.useful
			 * + "', '" + y.funny + "', '" + y.cool + "')"; st.execute(t0); }
			 * catch (Exception e) { System.out.println(i); Gson gson = new
			 * Gson(); System.out.println(gson.toJson(y)); e.printStackTrace();
			 * 
			 * break; //or do nothing and continue } }
			 */
			/*
			 * i=0; System.out.println("LOAD Reviews"); for (Review r : reviews)
			 * { i++; if(i==10000) System.out.println("10,000th reached");
			 * if(i==300000) System.out.println("300,000th reached");
			 * 
			 * try { String t0 = "INSERT INTO review VALUES ('" + r.b_id +
			 * "', '" + r.u_id + "', '" + r.stars + "', '" + r.useful + "', '" +
			 * r.funny + "', '" + r.cool + "')"; st.execute(t0); } catch
			 * (Exception e) { e.printStackTrace();
			 * 
			 * break; //or do nothing and continue } }
			 */
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		}

	}

}
