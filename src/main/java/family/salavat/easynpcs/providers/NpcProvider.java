package family.salavat.easynpcs.providers;

import family.salavat.easynpcs.NpcManager;
import family.salavat.easynpcs.models.Equipment;
import family.salavat.easynpcs.services.NpcService;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Pose;
import org.bukkit.Location;


public class NpcProvider implements NpcService {

    private NpcManager manager;

    public NpcProvider(NpcManager manager) {
        this.manager = manager;
    }

    @Override
    public int createNpc(Location npcLocation, String npcName) {
        int id = manager.createNpc(npcLocation, npcName);
        return id;
    }

    @Override
    public int removeAllNpcs() {
        return manager.removeAllNpcs();
    }

    @Override
    public boolean removeNpc(int id) {
        return manager.removeNpc(id);
    }

    @Override
    public boolean hideNpc(int id) {
        return manager.hideNpcFromAllPlayer(id);
    }

    @Override
    public boolean setNpcRotatable(int id, boolean criteria) {
        return manager.setNpcRotatable(id, criteria);
    }

    @Override
    public boolean setNpcUnnamed(int id, boolean criteria) {
        return manager.setNpcUnnamed(id, criteria);
    }

    @Override
    public boolean setNpcTabName(int id, String tabName) {
        return manager.setNpcTabName(id, tabName);
    }

    @Override
    public boolean setNpcUntabbed(int id, boolean criteria) {
        return manager.setNpcUntabbed(id, criteria);
    }

    @Override
    public boolean setNpcName(int id, String newName) {
        return manager.setNpcName(id, newName);
    }

    @Override
    public boolean setNpcPrefix(int id, String prefix) {
        return manager.setNpcPrefix(id, prefix);
    }

    @Override
    public boolean setNpcSuffix(int id, String suffix) {
        return manager.setNpcSuffix(id, suffix);
    }

    @Override
    public boolean setNpcOnClickCommand(int id, ClickType clickType, SenderPermission permission, String commandLineWithoutSlash) {
        String clickString;
        switch (clickType) {
            case LEFT:
                clickString = "left";
                break;
            case RIGHT:
                clickString = "right";
                break;
            case BOTH:
                clickString = "both";
                break;
            default:
                clickString = "both";
        }
        String[] parts = commandLineWithoutSlash.split(" ");
        String[] args = new String[parts.length - 1];
        for (int i = 0; i < args.length; i++) {
            args[i] = parts[i + 1];
        }
        String permissionString;
        if (permission == SenderPermission.BY_PLAYER) {
            permissionString = "player";
        }
        else permissionString = "console";
        return manager.setCommandOnNpc(id, permissionString, clickString, parts[0], args);
    }

    @Override
    public boolean setNpcSkinByNickname(int id, String nicknameOfSkin) {
        return manager.setNpcSkinByNickname(id, nicknameOfSkin);
    }

    @Override
    public boolean setNpcSkinByTextureAndSignature(int id, String texture, String signature) {
        return manager.setNpcSkinByTextureAndSignature(id, texture, signature);
    }

    @Override
    public boolean setNpcPose(int id, NpcPose pose) {
        return manager.setNpcPose(id, Pose.valueOf(pose.name()));
    }

    @Override
    public boolean setNpcRotation(int id, float yaw, float pitch) {
        return manager.setNpcRotation(id, yaw, pitch);
    }

    @Override
    public boolean setNpcEquipment(int id, Equipment equipment) {
        return manager.setNpcEquipment(id, equipment);
    }

    @Override
    public boolean setNpcGlowing(int id, boolean criteria) {
        return setNpcGlowing(id, criteria);
    }

    @Override
    public boolean setNpcTeamColor(int id, String color) {
        return manager.setNpcColor(id, ChatFormatting.valueOf(color.toUpperCase()));
    }

    @Override
    public boolean setNpcCollidable(int id, boolean criteria) {
        return manager.setNpcCollidable(id, criteria);
    }
}
