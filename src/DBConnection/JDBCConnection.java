package DBConnection;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;

public abstract class JDBCConnection {

	private Connection dbConnection = null;
	private Statement statement = null;
	private String dbTable = null;
	private String[] condition = null;
	
	public JDBCConnection() {
		/* Select XXX From dbtable...
		 * 	where condition[]
		 */
	}
	
	public void createConnection(){
		// load jdbc driver
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		}
		catch(ClassNotFoundException ex) {
		   System.err.println("Error: unable to load driver class!");
		}
		try {
			dbConnection = new DriverManager.getConnection("jdbc:mysql://localhost/dhbw", "root", "");
		} catch (Exception e) {
			// TODO: handle exception
		}


	}
	
	public void createStatement(){
		
	}
	
	public void executeStatement(){
		
	}

}
