package DBConnection;

import java.sql.*;

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
		
	}
	
	public void createStatement(){
		
	}
	
	public void executeStatement(){
		
	}

}
