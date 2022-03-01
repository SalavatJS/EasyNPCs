package family.salavat.easynpcs;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import family.salavat.easynpcs.models.Equipment;
import family.salavat.easynpcs.models.NpcModel;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class NpcManager {

    private NpcRepository repository;

    private Server server;

    private Map<Integer, ServerPlayer> npcs;

    private Map<Integer, PlayerTeam> teams;

    private Map<Integer, ArmorStand> npcBodies;

    private class FixTeam extends PlayerTeam {

        private FixTeam(Scoreboard scoreboard, String name) {
            super(scoreboard, name);
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof FixTeam fixTeam) {
                if (fixTeam.getName().equals(this.getName())) return true;
            }
            return false;
        }

    }

    public NpcManager(NpcRepository repository, Server server) {
        teams = new HashMap<>();
        npcBodies = new HashMap<>();
        this.repository = repository;
        this.server = server;
        npcs = new HashMap<>();
        repository.getModels().forEach((id, model) -> {
            ServerPlayer npc = makeNpcFromModel(model);
            npcs.put(id, npc);
        });
    }

    public ServerPlayer makeNpcFromModel(NpcModel model) {
        ServerLevel world = ((CraftWorld) Bukkit.getWorld(UUID.fromString(model.getWorldUuid()))).getHandle();
        MinecraftServer server = ((CraftServer) this.server).getHandle().getServer();
        GameProfile gameProfile = new GameProfile(UUID.fromString(model.getNpcUuid()), model.getName());
        String skin = model.getSkin();
        String signature = model.getSignature();
        if (!skin.isEmpty() && !signature.isEmpty()) {
            gameProfile.getProperties().put("textures", new Property("textures", skin, signature));
        }
        ServerPlayer npc = new ServerPlayer(server, world, gameProfile);
        npc.setPose(Pose.valueOf(model.getPose()));
        npc.setPos(model.getX(), model.getY(), model.getZ());
        npc.getBukkitEntity().setPlayerListName(model.getListName());
        PlayerTeam team = new FixTeam(new Scoreboard(), model.getName());
        team.getPlayers().add(model.getName());
        if (!model.getPrefix().equals("")) team.setPlayerPrefix(CraftChatMessage.fromStringOrNull(model.getPrefix()));
        if (!model.getSuffix().equals("")) team.setPlayerSuffix(CraftChatMessage.fromStringOrNull(model.getSuffix()));
        if (model.isUnnamed()) team.setNameTagVisibility(Team.Visibility.NEVER);
        teams.put(model.getId(), team);
        team.setColor(ChatFormatting.valueOf(model.getColor()));
        return npc;
    }

    public int createNpc(Location location, String name) {
        String npcUuid = UUID.randomUUID().toString();
        NpcModel model = new NpcModel(name, location.getX(), location.getY(), location.getZ(), location.getWorld().getUID().toString(), npcUuid);
        int id = repository.put(model);
        ServerPlayer npc = makeNpcFromModel(model);
        npcs.put(id, npc);
        Bukkit.getOnlinePlayers().forEach(player -> showNpcToPlayer(player, id));
        return id;
    }

    private void sendToPlayerNpcTeamInfo(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        teams.forEach((id, team) -> {
            connection.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
            connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        });
    }

    public List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> makeListFromEquipment(Equipment equipment) {
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> nmsEquipment = new ArrayList<>();
        if (equipment.getHelmet() != null) nmsEquipment.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())));
        if (equipment.getChestplate() != null) nmsEquipment.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())));
        if (equipment.getLeggins() != null) nmsEquipment.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggins())));
        if (equipment.getBoots() != null) nmsEquipment.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())));
        if (equipment.getMainHand() != null) nmsEquipment.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.getMainHand())));
        if (equipment.getOffHand() != null) nmsEquipment.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.getOffHand())));
        return nmsEquipment;
    }

    public boolean showNpcToPlayer(Player player, int id) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
        NpcModel model = repository.get(id);
        connection.send(new ClientboundAddPlayerPacket(npc));
        if (model.isUntabbed()) connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
        sendToPlayerNpcTeamInfo(player);
        if (model.isHidden()) connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
        npc.setPose(Pose.valueOf(model.getPose().toUpperCase()));
        npc.getBukkitEntity().setPlayerListName(model.getListName());
        sendNpcRotationToPlayer(connection, npc, (float) (model.getYaw() % 360 / 360 * 255), (float) (model.getPitch() % 360 / 360 * 255));
        Equipment equipment = model.getEquipment();
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> nmsEquipment = makeListFromEquipment(equipment);
        if (nmsEquipment.size() > 0) connection.send(new ClientboundSetEquipmentPacket(npc.getId(), nmsEquipment));
        if (model.isGlowing()) npc.getEntityData().set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0x40);
        connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
        PlayerTeam team = teams.get(id);
        if (model.isCollidable() == true) team.setCollisionRule(Team.CollisionRule.ALWAYS);
        else team.setCollisionRule(Team.CollisionRule.NEVER);
        sendToPlayerNpcTeamInfo(player);
        return true;
    }

    public void showAllNpcsToPlayer(Player player) {
        npcs.keySet().forEach(id -> showNpcToPlayer(player, id));
    }

    public boolean removeNpc(int id) {
        ServerPlayer npc;
        if ((npc = getNpcById(id)) == null) return false;
        Bukkit.getOnlinePlayers().forEach(player -> {
            hideNpcFromPlayer(player, npc);
        });
        removeNpcFromMap(id);
        removeNpcFromRepository(id);
        removeNpcFromTeams(id);
        return true;
    }

    public boolean hideNpcFromAllPlayer(int id) {
        ServerPlayer npc;
        if ((npc = getNpcById(id)) == null) return false;
        Bukkit.getOnlinePlayers().forEach(player -> hideNpcFromPlayer(player, npc));
        return true;
    }

    public void hideNpcFromPlayer(Player player, ServerPlayer npc) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
        connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
    }

    private ServerPlayer getNpcById(int id) {
        return npcs.get(id);
    }

    private void removeNpcFromTeams(int id) {
        teams.remove(id);
    }

    private ServerPlayer removeNpcFromMap(int id) {
        return npcs.remove(id);
    }

    private void removeNpcFromRepository(int id) {
        repository.remove(id);
    }

    private void clearNpcMap() {
        npcs.clear();
    }

    public int removeAllNpcs() {
        for (Integer id : npcs.keySet()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                hideNpcFromPlayer(player, getNpcById(id));
            });
            removeNpcFromRepository(id);
        }
        int size = npcs.size();
        teams.clear();
        clearNpcMap();
        return size;
    }

    public boolean setNpcRotatable(int id, boolean criteria) {
        if (!npcs.containsKey(id)) return false;
        repository.getModels().get(id).setRotatable(criteria);
        repository.save();
        return true;
    }

    private void sendNpcRotationToPlayer(ServerGamePacketListenerImpl connection, ServerPlayer npc, float yaw, float pitch) {
        connection.send(new ClientboundRotateHeadPacket(npc, (byte) yaw));
        connection.send(new ClientboundMoveEntityPacket.Rot(npc.getId(), (byte) yaw, (byte) pitch, true));
    }

    private void setRotatableNpcLocation(Location npcLocation, Location playerLocation) {
        npcLocation.setDirection(playerLocation.subtract(npcLocation).toVector());
    }

    public void rotateNpcToPlayer(Player player) {
        npcs.forEach((id, npc) -> {
            NpcModel model = repository.getModels().get(id);
            if (model.isHidden() || !model.isRotatable() || !model.getPose().equalsIgnoreCase("STANDING")) return;
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            Location npcLocation = npc.getBukkitEntity().getLocation();
            setRotatableNpcLocation(npcLocation, player.getLocation());
            float yaw = npcLocation.getYaw() % 360 / 360 * 255;
            float pitch = npcLocation.getPitch() % 360 / 360 * 255;
            sendNpcRotationToPlayer(connection, npc, yaw, pitch);
        });
    }

    public boolean setNpcUnnamed(int id, boolean criteria) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        repository.getModels().get(id).setUnnamed(criteria);
        repository.save();
        if (criteria == true) teams.get(id).setNameTagVisibility(Team.Visibility.NEVER);
        else teams.get(id).setNameTagVisibility(Team.Visibility.ALWAYS);
        Bukkit.getOnlinePlayers().forEach(player -> showNpcToPlayer(player, id));
        return true;
    }

    public boolean setNpcTabName(int id, String tabName) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        repository.getModels().get(id).setListName(tabName);
        repository.save();
        npc.getBukkitEntity().setPlayerListName(tabName);
        Bukkit.getOnlinePlayers().forEach(player -> sendToPlayerNpcTeamInfo(player));
        return true;
    }

    private void showNpcToAllPlayer(int id) {
        Bukkit.getOnlinePlayers().forEach(player -> showNpcToPlayer(player, id));
    }

    public boolean setNpcUntabbed(int id, boolean criteria) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        repository.getModels().get(id).setUntabbed(criteria);
        repository.save();
        showNpcToAllPlayer(id);
        return true;
    }

    public boolean setNpcName(int id, String name) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        Bukkit.getOnlinePlayers().forEach(player -> hideNpcFromPlayer(player, npc));
        NpcModel model = repository.getModels().get(id);
        String previousName = model.getName();
        model.setName(name);
        repository.save();
        ServerPlayer newNpc = makeNpcFromModel(model);
        npcs.put(id, newNpc);
        PlayerTeam team = teams.get(id);
        team.getPlayers().remove(previousName);
        team.getPlayers().add(name);
        showNpcToAllPlayer(id);
        return true;
    }

    public boolean setNpcPrefix(int id, String prefix) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        teams.get(id).setPlayerPrefix(CraftChatMessage.fromStringOrNull(prefix));
        repository.getModels().get(id).setPrefix(prefix);
        repository.save();
        Bukkit.getOnlinePlayers().forEach(player -> sendToPlayerNpcTeamInfo(player));
        return true;
    }

    public boolean setNpcSuffix(int id, String suffix) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        teams.get(id).setPlayerSuffix(CraftChatMessage.fromStringOrNull(suffix));
        repository.getModels().get(id).setSuffix(suffix);
        repository.save();
        Bukkit.getOnlinePlayers().forEach(player -> sendToPlayerNpcTeamInfo(player));
        return true;
    }

    private String makeStringFromCommandWithArguments(String label, String[] args) {
        for (String arg : args) label += " " + arg;
        return label;
    }

    public NpcModel getModelByEntityId(int entityId) {
        for (Map.Entry<Integer, ServerPlayer> entry : npcs.entrySet()) {
            if (entry.getValue().getId() == entityId) return repository.get(entry.getKey());
        }
        return null;
    }

    public boolean setCommandOnNpc(int id, String permission, String click, String label, String args[]) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        NpcModel model = repository.getModels().get(id);
        model.setOnCommand(makeStringFromCommandWithArguments(label, args));
        model.setClick(click);
        model.setSender(permission);
        repository.save();
        return true;
    }

    private String convertNicknameIntoUuid(String nickanme) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + nickanme;
        try {
            InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
            return uuid;
        } catch (Throwable throwable) {}
        return "";
    }

    private String[] getSignatureAndTexture(String uuid) {
        JsonParser parser = new JsonParser();
        try {
            InputStreamReader reader = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").openStream());
            JsonObject parsed = parser.parse(reader).getAsJsonObject();
            JsonArray array = parsed.get("properties").getAsJsonArray();
            JsonObject property = array.get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture, signature};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public boolean setNpcSkinByTextureAndSignature(int id, String texture, String signature) {
        NpcModel model;
        if ((model = repository.get(id)) == null) return false;
        model.setSkin(texture);
        model.setSignature(signature);
        repository.save();
        hideNpcFromAllPlayer(id);
        showNpcToAllPlayer(id);
        return true;
    }

    public boolean setNpcSkinByNickname(int id, String nickname) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        String uuid = convertNicknameIntoUuid(nickname);
        if (uuid.isEmpty()) return false;
        String[] textureAndSignature = getSignatureAndTexture(uuid);
        if (textureAndSignature.length == 0) return false;
        String skin = textureAndSignature[0];
        String signature = textureAndSignature[1];
        NpcModel model = repository.get(id);
        model.setSkin(skin);
        model.setSignature(signature);
        repository.save();
        npc.getGameProfile().getProperties().put("textures", new Property("textures", skin, signature));
        Bukkit.getOnlinePlayers().forEach(player -> hideNpcFromPlayer(player, npc));
        showNpcToAllPlayer(id);
        return true;
    }

    public boolean hasNpcWithId(int id) {
        return npcs.containsKey(id);
    }

    public boolean setNpcPose(int id, Pose pose) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        npc.setPose(pose);
        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
        });
        repository.getModels().get(id).setPose(pose.name());
        repository.save();
        return true;
    }

    public boolean setNpcRotation(int id, float yaw, float pitch) {
        ServerPlayer npc;
        if ((npc = npcs.get(id)) == null) return false;
        NpcModel model = repository.getModels().get(id);
        model.setYaw(yaw);
        model.setPitch(pitch);
        repository.save();
        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            sendNpcRotationToPlayer(connection, npc, yaw % 360 / 360 * 255, pitch % 360 / 360 * 255);
        });
        return true;
    }

    public boolean setNpcRotationToPlayer(int id, Player player) {
        ServerPlayer npc = npcs.get(id);
        if (npc == null) return false;
        Location npcLocation = npc.getBukkitEntity().getLocation();
        setRotatableNpcLocation(npcLocation, player.getLocation());
        float yaw = npcLocation.getYaw();
        float pitch = npcLocation.getPitch();
        setNpcRotation(id, yaw, pitch);
        return true;
    }

    public Equipment makeEquipmentFromPlayer(Player player) {
        Equipment equipment = new Equipment();
        ItemStack helmet = player.getEquipment().getHelmet();
        ItemStack chestplate = player.getEquipment().getChestplate();
        ItemStack leggins = player.getEquipment().getLeggings();
        ItemStack boots = player.getEquipment().getBoots();
        ItemStack mainHand = player.getEquipment().getItemInMainHand();
        ItemStack offHand = player.getEquipment().getItemInOffHand();
        equipment.setHelmet(helmet);
        equipment.setChestplate(chestplate);
        equipment.setLeggins(leggins);
        equipment.setBoots(boots);
        equipment.setMainHand(mainHand);
        equipment.setOffHand(offHand);
        return equipment;
    }

    public ClientboundSetEquipmentPacket makeSetEuipmentPacketForPlayer(Player player, int entityId) {
        Equipment equipment = makeEquipmentFromPlayer(player);
        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, Lists.newArrayList(new Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())),
                new Pair(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())),
                new Pair(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggins())),
                new Pair(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())),
                new Pair(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.getMainHand())),
                new Pair(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.getOffHand()))));
        return packet;
    }

    public void sendPacketToPlayer(Player player, Packet<ClientGamePacketListener> packet) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(packet);
    }

    public boolean setNpcEquipment(int id, Equipment equipment) {
        ServerPlayer npc = npcs.get(id);
        if (npc == null) return false;
        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(npc.getId(), Lists.newArrayList(new Pair(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())),
                new Pair(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())),
                new Pair(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggins())),
                new Pair(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())),
                new Pair(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.getMainHand())),
                new Pair(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.getOffHand()))));
        repository.getModels().get(id).setEquipment(equipment);
        repository.save();
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> sendPacketToPlayer(onlinePlayer, packet));
        return true;
    }

    public boolean setNpcEquipmentByPlayer(Player player, int id) {
        ServerPlayer npc = npcs.get(id);
        if (npc == null) return false;
        ClientboundSetEquipmentPacket packet = makeSetEuipmentPacketForPlayer(player, npc.getId());
        Equipment equipment = makeEquipmentFromPlayer(player);
        repository.getModels().get(id).setEquipment(equipment);
        repository.save();
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> sendPacketToPlayer(onlinePlayer, packet));
        return true;
    }

    public boolean setNpcGlowing(int id, boolean criteria) {
        ServerPlayer npc = npcs.get(id);
        if (npc == null) return false;
        repository.getModels().get(id).setGlowing(criteria);
        repository.save();
        if (criteria) npc.getEntityData().set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0x40);
        else npc.getEntityData().set(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), (byte) 0);
        Bukkit.getOnlinePlayers().forEach(player -> sendPacketToPlayer(player, new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true)));
        return true;
    }

    public boolean setNpcColor(int id, ChatFormatting color) {
        PlayerTeam team = teams.get(id);
        if (team == null) return false;
        repository.getModels().get(id).setColor(color.name());
        repository.save();
        team.setColor(color);
        Bukkit.getOnlinePlayers().forEach(player -> sendToPlayerNpcTeamInfo(player));
        return true;
    }

    public boolean setNpcCollidable(int id, boolean criteria) {
        PlayerTeam team = teams.get(id);
        if (team == null) return false;
        repository.getModels().get(id).setCollidable(criteria);
        repository.save();
        if (criteria == true) team.setCollisionRule(Team.CollisionRule.ALWAYS);
        else team.setCollisionRule(Team.CollisionRule.NEVER);
        Bukkit.getOnlinePlayers().forEach(player -> sendToPlayerNpcTeamInfo(player));
        return true;
    }

}
