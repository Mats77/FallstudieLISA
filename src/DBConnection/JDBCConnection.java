package DBConnection;

import java.sql.*;
import java.util.Properties;

import javax.sql.*;
import javax.naming.*;

import com.mysql.jdbc.Driver;

public abstract class JDBCConnection {

	private Connection dbConnection = null;
	private Statement statement = null;
	private String dbTable = null;
	private String[] condition = null;
	private String url = "/localhost/planspiel/orders";
	
	public JDBCConnection() {
		/* Select XXX From dbtable...
		 * 	where condition[]
		 */
	}
	
	public void createConnection(){
		// load jdbc driver
		try {
			Driver driver = new com.mysql.jdbc.Driver();
			Properties prop = new Properties();
			prop.setProperty("reader", "1234");
			Connection con = driver.connect(url, prop);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createStatement(){
		
	}
	
	public void executeStatement(){
		
	}

}
