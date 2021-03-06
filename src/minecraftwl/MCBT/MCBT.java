package minecraftwl.MCBT;

import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * MCBT plugin for Bukkit
 *
 * @author none
 */
public class MCBT extends JavaPlugin {
    private final MCBTPlayerListener playerListener = new MCBTPlayerListener(this);
    private final MCBTBlockListener blockListener = new MCBTBlockListener(this);
    private final MCBTEntityListener entityListener = new MCBTEntityListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    protected final MCBTSqlInterface sqlInterface = new MCBTSqlInterface();
    
    public void onDisable() {
    	try {
			sqlInterface.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

		try {
			sqlInterface.init("mcbt.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BURN,  blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);

        // Register our commands
        //getCommand("pos").setExecutor(new MCBTPosCommand(this));
        //getCommand("debug").setExecutor(new MCBTDebugCommand(this));

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}

