package family.salavat.easynpcs.models;

import net.minecraft.ChatFormatting;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class NpcModel implements ConfigurationSerializable {

    public static NpcModel deserialize(Map<String, Object> properties) {
        NpcModel model = new NpcModel();
        model.id = (Integer) properties.get("id");
        model.name = (String) properties.get("name");
        model.listName = (String) properties.get("list-name");
        model.x = (Double) properties.get("x");
        model.y = (Double) properties.get("y");
        model.z = (Double) properties.get("z");
        model.isUnnamed = (Boolean) properties.get("is-unnamed");
        model.isUntabbed = (Boolean) properties.get("is-unnamed");
        model.isHidden = (Boolean) properties.get("is-unnamed");
        model.pose = (String) properties.get("pose");
        model.isRotatable = (Boolean) properties.get("is-unnamed");
        model.worldUuid = (String) properties.get("world-uuid");
        model.npcUuid = (String) properties.get("npc-uuid");
        model.prefix = (String) properties.get("prefix");
        model.suffix = (String) properties.get("suffix");
        model.onCommand = (String) properties.get("on-command");
        model.sender = (String) properties.get("sender");
        model.click = (String) properties.get("click");
        model.skin = (String) properties.get("skin");
        model.signature = (String) properties.get("signature");
        model.yaw = (Double) properties.get("yaw");
        model.pitch = (Double) properties.get("pitch");
        model.equipment = (Equipment) properties.get("equipment");
        model.isCollidable = (Boolean) properties.get("is-collidable");
        model.isGlowing = (Boolean) properties.get("is-glowing");
        model.color = (String) properties.get("color");
        return model;
    }

    private int id;

    private String name, listName;

    private String npcUuid;

    private double x, y, z;

    private String worldUuid;

    private boolean isUnnamed, isUntabbed, isHidden, isRotatable, isGlowing, isCollidable;

    private String pose, prefix, suffix;

    private String onCommand, sender, click;

    private String skin, signature;

    private double yaw, pitch;

    private Equipment equipment;

    private String color;

    public NpcModel(String name, double x, double y, double z, String worldUuid, String npcUuid) {
        this.id = 0;
        this.name = name;
        this.listName = name;
        this.x = x;
        this.y = y;
        this.z = z;
        isUnnamed = false;
        isHidden = false;
        isUntabbed = false;
        pose = "STANDING";
        isRotatable = false;
        this.worldUuid = worldUuid;
        this.npcUuid = npcUuid;
        prefix = "";
        suffix = "";
        onCommand = "";
        sender = "";
        click = "";
        skin = "";
        signature = "";
        yaw = 0.0;
        pitch = 0.0;
        equipment = new Equipment();
        color = ChatFormatting.WHITE.name();
        isGlowing = false;
        isCollidable = true;
    }

    public NpcModel() {

    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", id);
        properties.put("name", name);
        properties.put("list-name", listName);
        properties.put("x", x);
        properties.put("y", y);
        properties.put("z", z);
        properties.put("is-unnamed", isUnnamed);
        properties.put("is-hidden", isHidden);
        properties.put("is-untabbed", isUntabbed);
        properties.put("pose", pose);
        properties.put("is-rotatable", isRotatable);
        properties.put("world-uuid", worldUuid);
        properties.put("npc-uuid", npcUuid);
        properties.put("prefix", prefix);
        properties.put("suffix", suffix);
        properties.put("on-command", onCommand);
        properties.put("sender", sender);
        properties.put("click", click);
        properties.put("skin", skin);
        properties.put("signature", signature);
        properties.put("yaw", yaw);
        properties.put("pitch", pitch);
        properties.put("equipment", equipment);
        properties.put("is-glowing", isGlowing);
        properties.put("is-collidable", isCollidable);
        properties.put("color", color);
        return properties;
    }

    public boolean isGlowing() {
        return isGlowing;
    }

    public void setGlowing(boolean glowing) {
        isGlowing = glowing;
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public void setCollidable(boolean collidable) {
        isCollidable = collidable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getOnCommand() {
        return onCommand;
    }

    public void setOnCommand(String onCommand) {
        this.onCommand = onCommand;
    }

    public String getNpcUuid() {
        return npcUuid;
    }

    public void setNpcUuid(String npcUuid) {
        this.npcUuid = npcUuid;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorldUuid() {
        return worldUuid;
    }

    public void setWorldUuid(String worldUuid) {
        this.worldUuid = worldUuid;
    }

    public boolean isRotatable() {
        return isRotatable;
    }

    public void setRotatable(boolean rotatable) {
        isRotatable = rotatable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isUnnamed() {
        return isUnnamed;
    }

    public void setUnnamed(boolean unnamed) {
        isUnnamed = unnamed;
    }

    public boolean isUntabbed() {
        return isUntabbed;
    }

    public void setUntabbed(boolean untabbed) {
        isUntabbed = untabbed;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getPose() {
        return pose;
    }

    public void setPose(String pose) {
        this.pose = pose;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
