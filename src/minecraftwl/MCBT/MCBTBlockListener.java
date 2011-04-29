package minecraftwl.MCBT

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import java.sql.*;

/**
 * MCBT block listener
 * @author <yourname>
 */
public class MCBTBlockListener extends BlockListener {
    private final MCBT plugin;

    public MCBTBlockListener(final MCBT plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
    public void onBlockBreak(BlockBreakEvent event) {
    	Player p = event.getPlayer();
    	int id = -1;
    	
    	try {
    		id = getPlayerID(p.getName());		
    		updatePlayerBreaks(id);
    		updateGlobalBreaks();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    
    }
    
    public void onBlockPlace(BlockPlaceEvent event) {
    	Player p = event.getPlayer();
    	int id = -1;
    	
    	try {
    		id = getPlayerID(p.getName());		
    		updatePlayerPlaces(id);
    		updateGlobalPlaces();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
    private int getPlayerID(String player) throws Exception {
    	int id = -1;
    	
    	Class.forName("org.sqlite.JDBC");
        Connection conn =
          DriverManager.getConnection("jdbc:sqlite:mcbt.db");
        
        Statement stat = conn.createStatement();
        
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
        
        conn.close();
        
        return id;
    }
    
    private void updatePlayerBreaks(int id) throws Exception {
    	
    	Class.forName("org.sqlite.JDBC");
        Connection conn =
          DriverManager.getConnection("jdbc:sqlite:mcbt.db");
        
        Statement stat = conn.createStatement();
        
        stat.executeQuery("update playerBreaks set count = count + 1 where ID = '" + id + "';");
        
        conn.close();
    }

    private void updatePlayerPlaces(int id) throws Exception {
    	
    	Class.forName("org.sqlite.JDBC");
        Connection conn =
          DriverManager.getConnection("jdbc:sqlite:mcbt.db");
        
        Statement stat = conn.createStatement();
        
        stat.executeQuery("update places set count = count + 1 where ID = '" + id + "';");
        
        conn.close();
        
    }
    
    private void updateGlobalBreaks() throws Exception {
    	
    	Class.forName("org.sqlite.JDBC");
        Connection conn =
          DriverManager.getConnection("jdbc:sqlite:mcbt.db");
        
        Statement stat = conn.createStatement();
        
        stat.executeQuery("update breaks set count = count + 1 where ID = 0';");
        
        conn.close();
    }
    
    private void updateGlobalPlaces() throws Exception {
    	
    	Class.forName("org.sqlite.JDBC");
        Connection conn =
          DriverManager.getConnection("jdbc:sqlite:mcbt.db");
        
        Statement stat = conn.createStatement();
        
        stat.executeQuery("update places set count = count + 1 where ID = 0';");
        
        conn.close();
    }
}
