package family.salavat.easynpcs.listeners;

import family.salavat.easynpcs.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private NpcManager manager;

    public JoinListener(NpcManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        manager.showAllNpcsToPlayer(event.getPlayer());
    }

}
