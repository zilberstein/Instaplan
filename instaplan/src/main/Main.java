package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.gson.Gson;

public class Main {
	
	public static void categorySetUp() throws Exception{
		ArrayList<String> ourCategories = new ArrayList<String>();
		String[] categories = {"active", "breakfast", "cats", "college", "culture", 
				"dessert", "dinner", "family", "footer", "kids", "lunch", "nightlife", "old_people",
				"pamper"};
		Arrays.sort(categories);
		ourCategories.addAll(Arrays.asList(categories));
		
		for(String c: ourCategories){
			File file = new File(c + ".txt");
		}
		
	
	}
	public static void importData(String filename) throws Exception{
		Gson gson = new Gson();
		
		//convertJSON to objects
		String json = "";
		File file = new File("yelp_academic_dataset.json");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		
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
			st.execute(safeDropTable("writes"));
			st.execute(safeDropTable("reviewOf"));
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
		for (Business b : businesses) {
			try {
				String t0 = "INSERT INTO business VALUES (" + b.id + ", '"
						+ b.name + "', '" + b.address + "', '" + b.city +
						"', '" + b.state + "', '" + b.lat + "', '" + b.lon
						+ "', '" + b.stars + "', '" + b.photo + "')";
				st.execute(t0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (YelpUser y : users) {
			try {
				String t0 = "INSERT INTO yelpUser VALUES (" +  y.id + ", '" 
						+ y.num_review + ", " + y.avg_stars + ", " + y.useful + ", "
						+ y.funny + ", " + y.cool + "')";
				st.execute(t0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Review r : reviews) {
			try {
				String t0 = "INSERT INTO reviews VALUES (" + r.b_id + 
						", " + r.u_id + ", '" + r.stars + ", " + r.useful
						+ ", " + r.funny + ", " + r.cool + "')";
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
