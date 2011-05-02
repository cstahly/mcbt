package minecraftwl.MCBT;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class MCBTEntityListener extends EntityListener  {
    private final MCBT plugin;

    public MCBTEntityListener(MCBT instance) {
        plugin = instance;
    }

    public void onEntityExplode(EntityExplodeEvent event)
    {
		try {
			for (int i=0; i<event.blockList().size(); i++) {
				//TODO: get rid of magic number
				plugin.sqlInterface.updateBreaks(1/*explosion*/);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
