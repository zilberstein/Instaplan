package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {
	
	
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
					+ "PRIMARY KEY (businessId, userId))");
			st.execute("CREATE TABLE yelpUser ("
					+ "id VARCHAR(40), "
					+ "reviewCount SMALLINT, "
					+ "avgStars DECIMAL, "
					+ "useful SMALLINT, "
					+ "funny SMALLINT, "
					+ "cool SMALLINT, "
					+ "PRIMARY KEY (id))");
			st.execute("CREATE TABLE writes ("
					+ "userId VARCHAR(40), "
					+ "businessId VARCHAR(40), "
					+ "PRIMARY KEY (userId, businessID), "
					+ "FOREIGN KEY (userID) REFERENCES yelpUser(id), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id))");
			st.execute("CREATE TABLE reviewOf ("
					+ "userId VARCHAR(40), "
					+ "businessID VARCHAR(40), "
					+ "PRIMARY KEY (userId, businessID), "
					+ "FOREIGN KEY (userID) REFERENCES yelpUser(id), "
					+ "FOREIGN KEY (businessId) REFERENCES business(id))");
			st.execute("CREATE TABLE belongs ("
					+ "businessId VARCHAR(40), "
					+ "name VARCHAR(11), "
					+ "PRIMARY KEY (businessId, name), "
					+ "FOREIGN KEY businessId REFERENCES business(id), "
					+ "FOREIGN KEY name REFERENCES categories(name))");
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	
	public static void DMLs(ArrayList<Business> businesses, 
			ArrayList<YelpUser> users, Statement st) {
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
		for (Business b : businesses) {
			for (Category c : b.categories) {
				try {
					String t0 = "INSERT INTO belongs VALUES (" +c.name + ", '"
							+ b.id +"')";
					st.execute(t0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
}
