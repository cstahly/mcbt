package minecraftwl.MCBT;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockListener;

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
    public void onBlockBurn(BlockBurnEvent event) {

    	try {
        	//TODO: get rid of magic number
    		plugin.sqlInterface.updateBreaks(2/*fire*/);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }

    //put all Block related code here
    public void onBlockBreak(BlockBreakEvent event) {
    	Player p = event.getPlayer();
    	int id = -1;
    	
    	try {
    		id = plugin.sqlInterface.getPlayerID(p.getName());		
    		plugin.sqlInterface.updateBreaks(id);
    		
        	//TODO: get rid of magic number
    		plugin.sqlInterface.updateBreaks(0/*global*/);
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
    		plugin.sqlInterface.updatePlaces(id);
    		
        	//TODO: get rid of magic number
    		plugin.sqlInterface.updatePlaces(0/*global*/);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
}
