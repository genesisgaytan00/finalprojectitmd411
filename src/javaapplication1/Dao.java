package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE ggaytan_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200))";
		final String createUsersTable = "CREATE TABLE ggaytan_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";
		final String createggaytanTable = "CREATE TABLE ggaytan_table(id INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin INT)";
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			statement.executeUpdate(createggaytanTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table
		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)
		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			//entering my default
			for (List<String> rowData : array) {

				sql = "INSERT into ggaytan_users(uname,upass,admin) " + "VALUES('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			
			sql = "INSERT into ggaytan_table(uname, upass, admin) values('fp411', '411', 1)";
			statement.executeUpdate(sql);
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("INSERT into ggaytan_tickets" + "(ticket_issuer, ticket_description) VALUES(" + " '"
					+ ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM ggaytan_tickets");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	
	//updates description of a ticketNumber

    public void updateRecords(int ticketNum, String updateDes) {
        try {
            statement = getConnection().createStatement();
            System.out.println("Creating update statement...");
            statement.executeUpdate("UPDATE ggaytan_tickets SET ticket_description = '" + updateDes +
                    "' WHERE ticket_id = " + ticketNum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	// continue coding for deleteRecords implementation
	public void deleteRecords(int ticketNum)  {
		try {
			System.out.println("Creating statement...");
			statement = getConnection().createStatement();
			
			//options 
		     int response = JOptionPane.showConfirmDialog(null, "Delete ticket #" + ticketNum + "?",
              "Confirm",
              JOptionPane.YES_NO_OPTION, 
              JOptionPane.QUESTION_MESSAGE);
		     
		     //if no
		     if (response == JOptionPane.NO_OPTION) {
		         System.out.println("No record deleted");
		         
		       //if yes then deleted
		      } else if (response == JOptionPane.YES_OPTION) {
		        statement.executeUpdate("DELETE FROM ggaytan_tickets WHERE ticket_id = " + ticketNum);
		        System.out.println("Record deleted");
		       
		       //cancel request
		      } else if (response == JOptionPane.CLOSED_OPTION) {
		        System.out.println("Request cancelled");
		      }


		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
