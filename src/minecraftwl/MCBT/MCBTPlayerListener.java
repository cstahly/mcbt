package minecraftwl.MCBT;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author <yourname>
 */
public class MCBTPlayerListener extends PlayerListener {
    private final MCBT plugin;

    public MCBTPlayerListener(MCBT instance) {
        plugin = instance;
    }

    public void onPlayerJoin(PlayerEvent event)
    {
    	event.getPlayer().sendMessage(ChatColor.AQUA + "[MCBT] Plugin test");
    	// TODO: get player ID, put in hash map.  Perhaps doing the lookup in sql is not too much slower than hash map?
    }
    
    // TODO: on player exit, remove ID from hash map
    
}
