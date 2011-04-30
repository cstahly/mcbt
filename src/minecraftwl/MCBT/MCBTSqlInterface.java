package minecraftwl.MCBT;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MCBTSqlInterface {
	
	private Connection _conn;
	
	//
	// Does the following:
	//   Creates ./MCBT directory (according to CraftBukkit convention)
	//   Opens/creates database
	//   Populates preprocessed SQL
	//
	// Will throw for permission/other filesystem problems.
	//  Stack trace is enough info, I'm sure...
	//
	// Will keep the connection open (and the database file r/w locked) for the entire server session.
	//  Should be the only consumer of the database, so should be okay?
	//
	
	protected void init(String filename) throws Exception {

		boolean createNewDB = false;
		
		new File("./MCBT/").mkdir();

		// file didn't exist.  getConnection() will succeed, but we'll need to populate with default info.
		if (!new File("./MCBT/"+filename).exists()) {
			createNewDB = true;
		}
		
	    // init JDBC
	    Class.forName("org.sqlite.JDBC");
	    
	    _conn = DriverManager.getConnection("jdbc:sqlite:./MCBT/"+filename);
		
	    if (createNewDB) {
	    	System.out.println("Database doesn't exist, populating new one...");
	    	Statement stat = _conn.createStatement();
	        stat.execute("create table player(ID integer PRIMARY KEY, name text);");
	        stat.execute("create table places(ID integer, count BigInt);");
	        stat.execute("create table breaks(ID integer, count BigInt);");
	        // TODO: add default rows
	    }
	    
	    // TODO: init PreparedStatements
	}
	
    protected int getPlayerID(String player) throws Exception {
    	int id = -1;
    	
        Statement stat = _conn.createStatement();
        
        ResultSet rs = stat.executeQuery("select ID from people where name = '" + player + "';");
        
        while (rs.next()) {
          id = rs.getInt("ID");
        }
        
        rs.close();
        
        if(id == -1) {
        	stat.executeQuery("insert into people(name) values('" + player + "');");
        
        	rs = stat.executeQuery("select ID from people where name = '" + player + "';");
            
            while (rs.next()) {
              id = rs.getInt("ID");
            }
            
            rs.close();
        }
        
        return id;
    }
    
    protected void updatePlayerBreaks(int id) throws Exception {
    	
        Statement stat = _conn.createStatement();
        
        stat.executeQuery("update playerBreaks set count = count + 1 where ID = '" + id + "';");
        
    }

    protected void updatePlayerPlaces(int id) throws Exception {
    	
        Statement stat = _conn.createStatement();
        
        stat.executeQuery("update places set count = count + 1 where ID = '" + id + "';");
        
    }
    
    protected void updateGlobalBreaks() throws Exception {
    	
        Statement stat = _conn.createStatement();
        
        stat.executeQuery("update breaks set count = count + 1 where ID = 0';");
        
    }
    
    protected void updateGlobalPlaces() throws Exception {
    	
        Statement stat = _conn.createStatement();
        
        stat.executeQuery("update places set count = count + 1 where ID = 0';");
        
    }

}
