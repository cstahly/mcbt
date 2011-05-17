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
	private PreparedStatement _ps_getCounts;
	private PreparedStatement _ps_storeCurrentCounts;
	private PreparedStatement _ps_getBroken;
	private PreparedStatement _ps_getPlaced;
	
	//
	// Does the following:
	//   Creates ./plugins/MCBT directory (according to CraftBukkit convention)
	//   Opens/creates database
	//   Populates preprocessed SQL
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
	    	createDatabase();
	    }
	    
	    // Create statements
	    _ps_getID              = _conn.prepareStatement("select ID from player where name = ? and reserved = 0;");
	    _ps_updateBreaks       = _conn.prepareStatement("update breaks set count = count + 1 where ID = ?;");
	    _ps_updatePlaces       = _conn.prepareStatement("update places set count = count + 1 where ID = ?;");
	    _ps_getCounts          = _conn.prepareStatement("select (blocksBroken, blocksBurned, blocksExploded, blockPlaceed) FROM players where playerName = ?;");
	    _ps_storeCurrentCounts = _conn.prepareStatement("update players set blocksBroken = ?, set blocksBurned = ?, set blocksExploded = ?, set blockPlaced = ? WHERE playerName = ? and reserved = 0;");
	    _ps_getBroken		   = _conn.prepareStatement("SELECT count FROM breaks WHERE ID = ?;");
	    _ps_getPlaced		   = _conn.prepareStatement("SELECT count FROM placed WHERE ID = ?;");
	}
	
	protected void close() throws SQLException {
		_ps_getID.close();
		_ps_updateBreaks.close();
		_ps_updatePlaces.close();
		_ps_getCounts.close();
		_ps_storeCurrentCounts.close();
		_ps_getBroken.close();
		_ps_getPlaced.close();
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
    
    protected void updateBreaks(int id) throws Exception {
    	// update breaks set count = count + 1 where ID = '?';
    	_ps_updateBreaks.setInt(1, id);
    	_ps_updateBreaks.execute();
    }

    protected void updatePlaces(int id) throws Exception {
    	// update places set count = count + 1 where ID = '?';
    	_ps_updatePlaces.setInt(1, id);
    	_ps_updatePlaces.execute();
    }
    
    private int addPlayer(String player) throws Exception {
    	
    	int id=-1;
    	
    	Statement stat = _conn.createStatement();
    	stat.execute("insert into player(name,reserved) values('" + player + "', 0);");
    	
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
    
    private void createDatabase() throws SQLException {
    	Statement stat = _conn.createStatement();
    	System.out.println("a");
        stat.execute("create table player(ID integer PRIMARY KEY, name text, reserved integer, blocksBroken BigInt DEFAULT 0, blocksBurned BigInt DEFAULT 0, blocksExploded BigInt DEFAULT 0, blocksPlaced BigInt DEFAULT 0);");
        System.out.println("b");
        stat.execute("create table places(ID integer, count BigInt, FOREIGN KEY (ID) REFERENCES player(ID));");
        System.out.println("c");
        stat.execute("create table breaks(ID integer, count BigInt, FOREIGN KEY (ID) REFERENCES player(ID));");

        System.out.println("d");
        stat.execute("insert into player(ID, name, reserved) values(0, 'all_players', 1);");	        
        System.out.println("e");
        stat.execute("insert into places(ID, count) values(0,0);");
        System.out.println("f");
        stat.execute("insert into breaks(ID, count) values(0,0);");

        System.out.println("g");
        stat.execute("insert into player(ID, name, reserved) values(1, 'explosion', 1);");
        System.out.println("h");
        stat.execute("insert into breaks(ID, count) values(1,0);");

        System.out.println("i");
        stat.execute("insert into player(ID, name, reserved) values(2, 'fire', 1);");
        System.out.println("j");
        stat.execute("insert into breaks(ID, count) values(2,0);");
    }

    public MCBTCount getCount(String playerName) throws SQLException {
    	
    	MCBTCount counts = new MCBTCount(0L,0L,0L,0L);
    	
    	_ps_getCounts.setString(1, playerName);
    	_ps_getCounts.execute();
    	
    	ResultSet rs = _ps_getID.executeQuery();
    	
        counts.blocksBroken   = rs.getLong("blocksBroken") - getBroken(0);
        counts.blocksBurned   = rs.getLong("blocksBurned") - getPlaced(0);
        counts.blocksExploded = rs.getLong("blocksExploded") - getBroken(1);
        counts.blocksPlaced   = rs.getLong("blocksPlaced") - getBroken(2);
        
    	return counts;
    
    }
    
    public Long getBroken(int ID) throws SQLException{
    	_ps_getBroken.setInt(1, ID);
    	ResultSet rs = _ps_getBroken.executeQuery();
    	
    	return rs.getLong("count");
    }
    
    public Long getPlaced(int ID) throws SQLException{
    	_ps_getPlaced.setInt(1, ID);
    	ResultSet rs = _ps_getPlaced.executeQuery();
    	
    	return rs.getLong("count");
    }
    
    public void storeCurrentCount(String playerName) throws SQLException {
    	
		_ps_storeCurrentCounts.setLong(1, getBroken(0));  //Broken
    	_ps_storeCurrentCounts.setLong(2, getPlaced(0));  //Burned     
    	_ps_storeCurrentCounts.setLong(3, getBroken(1));  //Exploded
    	_ps_storeCurrentCounts.setLong(4, getBroken(2));  //Placed
    	_ps_storeCurrentCounts.setString(5, playerName);
    	
    	_ps_storeCurrentCounts.execute();
    	
    }
    
}
