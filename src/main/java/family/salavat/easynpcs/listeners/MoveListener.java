package family.salavat.easynpcs.listeners;

import family.salavat.easynpcs.NpcManager;
import family.salavat.easynpcs.NpcRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private NpcManager manager;

    public MoveListener(NpcManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        manager.rotateNpcToPlayer(event.getPlayer());
    }

}
