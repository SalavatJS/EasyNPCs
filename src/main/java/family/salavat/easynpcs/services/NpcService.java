package family.salavat.easynpcs.services;

import family.salavat.easynpcs.models.Equipment;
import org.bukkit.Location;

public interface NpcService {

    enum ClickType {
        LEFT, RIGHT, BOTH
    }

    enum SenderPermission {
        BY_PLAYER, BY_CONSOLE
    }

    enum NpcPose {
        STANDING, SLEEPING, CROUCHING, DYING, FALL_FLYING, LONG_JUMPING, SPIN_ATTACK , SWIMMING
    }

    int createNpc(Location npcLocation, String npcName);

    int removeAllNpcs();

    boolean removeNpc(int id);

    boolean hideNpc(int id);

    boolean setNpcRotatable(int id, boolean criteria);

    boolean setNpcUnnamed(int id, boolean criteria);

    boolean setNpcTabName(int id, String tabName);

    boolean setNpcUntabbed(int id, boolean criteria);

    boolean setNpcName(int id, String newName);

    boolean setNpcPrefix(int id, String prefix);

    boolean setNpcSuffix(int id, String suffix);

    boolean setNpcOnClickCommand(int id, ClickType clickType, SenderPermission permission, String commandLineWithoutSlash);

    boolean setNpcSkinByNickname(int id, String nicknameOfSkin);

    boolean setNpcSkinByTextureAndSignature(int id, String texture, String signature);

    boolean setNpcPose(int id, NpcPose pose);

    boolean setNpcRotation(int id, float yaw, float pitch);

    boolean setNpcEquipment(int id, Equipment equipment);

    boolean setNpcGlowing(int id, boolean criteria);

    boolean setNpcTeamColor(int id, String color);

    boolean setNpcCollidable(int id, boolean criteria);

}
