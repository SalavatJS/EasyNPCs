package family.salavat.easynpcs.listeners;

import com.comphenix.protocol.wrappers.EnumWrappers;
import family.salavat.easynpcs.models.NpcModel;
import family.salavat.easynpcs.events.PlayerInteractNpcEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class InteractListener implements Listener {

    private JavaPlugin main;

    public InteractListener(JavaPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteractNpc(PlayerInteractNpcEvent event) {
        NpcModel model = event.getModel();
        if (!model.getOnCommand().equals("")) {
            if (!model.getClick().equals("")) {
                String click = model.getClick();
                if (click.equalsIgnoreCase("right")) {
                    if (event.getAction() == EnumWrappers.EntityUseAction.INTERACT) {
                        if (model.getSender().equalsIgnoreCase("player")) {
                            Bukkit.dispatchCommand(event.getWhoInteract(), model.getOnCommand());
                        }
                        if (model.getSender().equalsIgnoreCase("console")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), model.getOnCommand());
                        }
                    }
                }
                else if (click.equalsIgnoreCase("left")) {
                    if (event.getAction() == EnumWrappers.EntityUseAction.INTERACT) {
                        if (model.getSender().equalsIgnoreCase("player")) {
                            Bukkit.dispatchCommand(event.getWhoInteract(), model.getOnCommand());
                        }
                        if (model.getSender().equalsIgnoreCase("console")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), model.getOnCommand());
                        }
                    }
                }
                else if (click.equalsIgnoreCase("both")) {
                    if (model.getSender().equalsIgnoreCase("player")) {
                        Bukkit.dispatchCommand(event.getWhoInteract(), model.getOnCommand());
                    }
                    if (model.getSender().equalsIgnoreCase("console")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), model.getOnCommand());
                    }
                }
            }
        }
    }

}
