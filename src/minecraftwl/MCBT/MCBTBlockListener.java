package minecraftwl.MCBT;

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
    		id = plugin.sqlInterface.getPlayerID(p.getName());		
    		plugin.sqlInterface.updatePlayerBreaks(id);
    		plugin.sqlInterface.updateGlobalBreaks();
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
    		id = plugin.sqlInterface.getPlayerID(p.getName());		
    		plugin.sqlInterface.updatePlayerPlaces(id);
    		plugin.sqlInterface.updateGlobalPlaces();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
}
