package minecraftwl.MCBT;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class MCBTSqlInterface {
	
	private Connection _conn;
	private PreparedStatement _ps_getID;
	private PreparedStatement _ps_updateBreaks;
	private PreparedStatement _ps_updatePlaces;
	
	//
	// Does the following:
	//   Creates ./plugins/MCBT directory (according to CraftBukkit convention)
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
		
		//TODO: can this path be determined from Bukkit?
		new File("./plugins/MCBT/").mkdir();

		// file didn't exist.  getConnection() will succeed, but we'll need to populate with default info.
		if (!new File("./plugins/MCBT/"+filename).exists()) {
			createNewDB = true;
		}
		
	    // init JDBC
	    Class.forName("org.sqlite.JDBC");
	    
	    _conn = DriverManager.getConnection("jdbc:sqlite:./plugins/MCBT/"+filename);
		
	    if (createNewDB) {
	    	System.out.println("Database doesn't exist, populating new one...");
	    	Statement stat = _conn.createStatement();
	        stat.execute("create table player(ID integer PRIMARY KEY, name text);");
	        stat.execute("create table places(ID integer, count BigInt, FOREIGN KEY (ID) REFERENCES player(ID));");
	        stat.execute("create table breaks(ID integer, count BigInt, FOREIGN KEY (ID) REFERENCES player(ID));");

	        stat.execute("insert into player(ID, name) values(0, 'global');"); // I hope nobody named "global" joins
        	stat.execute("insert into places(ID, count) values(0,0);");
        	stat.execute("insert into breaks(ID, count) values(0,0);");
	    }
	    
	    // Create statements
	    _ps_getID = _conn.prepareStatement("select ID from player where name = ?;");
	    _ps_updateBreaks = _conn.prepareStatement("update breaks set count = count + 1 where ID = ?;");
	    _ps_updatePlaces = _conn.prepareStatement("update places set count = count + 1 where ID = ?;");
	}
	
	protected void close() throws SQLException {
		_ps_getID.close();
		_ps_updateBreaks.close();
		_ps_updatePlaces.close();
		_conn.close();
	}
	
    protected int getPlayerID(String player) throws Exception {
    	int id = -1;
    	
    	// use prepared statement
    	_ps_getID.setString(1, player);
    	ResultSet rs = _ps_getID.executeQuery();
    	
    	// find last ID from the table
        while (rs.next()) {
          id = rs.getInt("ID");
        }
        rs.close();
        
        // not found, insert
        if(id == -1) {
        	id = addPlayer(player);
        }
        
        return id;
    }
    
    protected void updatePlayerBreaks(int id) throws Exception {
    	// update breaks set count = count + 1 where ID = '?';
    	_ps_updateBreaks.setInt(1, id);
    	_ps_updateBreaks.execute();
    }

    protected void updatePlayerPlaces(int id) throws Exception {
    	// update places set count = count + 1 where ID = '?';
    	_ps_updatePlaces.setInt(1, id);
    	_ps_updatePlaces.execute();
    }
    
    protected void updateGlobalBreaks() throws Exception {
    	// update breaks set count = count + 1 where ID = '0';
    	_ps_updateBreaks.setInt(1, 0);
    	_ps_updateBreaks.execute();        
    }
    
    protected void updateGlobalPlaces() throws Exception {
    	// update places set count = count + 1 where ID = '0';
    	_ps_updatePlaces.setInt(1, 0);
    	_ps_updatePlaces.execute();
    }
    
    private int addPlayer(String player) throws Exception {
    	
    	int id=-1;
    	
    	Statement stat = _conn.createStatement();
    	stat.execute("insert into player(name) values('" + player + "');");
    	
    	_ps_getID.setString(1, player);
    	ResultSet rs = _ps_getID.executeQuery();
    	
        while (rs.next()) {
          id = rs.getInt("ID");
        }
        
        if (id != -1) {
        	//success!
        	stat.execute("insert into places(ID, count) values('"+id+"',0);");
        	stat.execute("insert into breaks(ID, count) values('"+id+"',0);");
        }
        else {
        	// :(.  throw something?  a chair, perhaps?
        }
        
        rs.close();
        stat.close();
        return id;

    }

}
