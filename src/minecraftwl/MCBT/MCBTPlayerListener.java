package minecraftwl.MCBT;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author <yourname>
 */
public class MCBTPlayerListener extends PlayerListener {

    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	event.getPlayer().sendMessage(ChatColor.AQUA + "[MCBT] Plugin test");
    	// TODO: get player ID, put in hash map.  Perhaps doing the lookup in sql is not too much slower than hash map?
    }
    
    // TODO: on player exit, remove ID from hash map
    
}
