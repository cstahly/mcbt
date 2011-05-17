package minecraftwl.MCBT;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author <yourname>
 */
public class MCBTPlayerListener extends PlayerListener {
	
    private final MCBT plugin;

	public MCBTPlayerListener(final MCBT plugin) {
		this.plugin = plugin;
	}

    public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			
			MCBTCount count = plugin.sqlInterface.getCount(event.getPlayer().getName());
			
			event.getPlayer().sendMessage(ChatColor.AQUA.toString() + "Previously on MineCraft-WL.com: ");
		    event.getPlayer().sendMessage(ChatColor.AQUA.toString() + " - " + count.blocksBroken + " blocks broken");
		    event.getPlayer().sendMessage(ChatColor.AQUA.toString() + " - " + count.blocksBurned + " burned");
		    event.getPlayer().sendMessage(ChatColor.RED.toString() + " - " + count.blocksExploded + " exploded");
		    event.getPlayer().sendMessage(ChatColor.GREEN.toString() + "But #TODO# unique players placed " + count.blocksExploded + " blocks");
		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    // TODO: on player exit, remove ID from hash map
    public void onPlayerQuit(PlayerJoinEvent event) {
    	try {
    		plugin.sqlInterface.storeCurrentCount(event.getPlayer().getName());
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
}
