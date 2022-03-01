package family.salavat.easynpcs.events;

import com.comphenix.protocol.wrappers.EnumWrappers;
import family.salavat.easynpcs.models.NpcModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInteractNpcEvent extends Event {

    private static HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private Player whoInteract;

    private NpcModel model;

    private EnumWrappers.Hand hand;

    private EnumWrappers.EntityUseAction action;

    public PlayerInteractNpcEvent(Player whoInteract, NpcModel model, EnumWrappers.EntityUseAction action, EnumWrappers.Hand hand) {
        this.whoInteract = whoInteract;
        this.model = model;
        this.hand = hand;
        this.action = action;
    }

    public Player getWhoInteract() {
        return whoInteract;
    }

    public void setWhoInteract(Player whoInteract) {
        this.whoInteract = whoInteract;
    }

    public NpcModel getModel() {
        return model;
    }

    public void setModel(NpcModel model) {
        this.model = model;
    }

    public EnumWrappers.Hand getHand() {
        return hand;
    }

    public void setHand(EnumWrappers.Hand hand) {
        this.hand = hand;
    }

    public EnumWrappers.EntityUseAction getAction() {
        return action;
    }

    public void setAction(EnumWrappers.EntityUseAction action) {
        this.action = action;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
