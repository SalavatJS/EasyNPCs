package family.salavat.easynpcs.models;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Equipment implements ConfigurationSerializable {

    private ItemStack helmet, chestplate, leggins, boots, mainHand, offHand;

    public Equipment() {
        helmet = null;
        chestplate = null;
        leggins = null;
        boots = null;
        mainHand = null;
        offHand = null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("helmet", helmet);
        properties.put("chestplate", chestplate);
        properties.put("leggins", leggins);
        properties.put("boots", boots);
        properties.put("mainHand", mainHand);
        properties.put("offHand", offHand);
        return properties;
    }

    public static Equipment deserialize(Map<String, Object> properties) {
        Equipment equipment = new Equipment();
        equipment.helmet = (ItemStack) properties.get("helmet");
        equipment.chestplate = (ItemStack) properties.get("chestplate");
        equipment.leggins = (ItemStack) properties.get("leggins");
        equipment.boots = (ItemStack) properties.get("boots");
        equipment.mainHand = (ItemStack) properties.get("mainHand");
        equipment.offHand = (ItemStack) properties.get("offHand");
        return equipment;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    public ItemStack getLeggins() {
        return leggins;
    }

    public void setLeggins(ItemStack leggins) {
        this.leggins = leggins;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public void setMainHand(ItemStack mainHand) {
        this.mainHand = mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public void setOffHand(ItemStack offHand) {
        this.offHand = offHand;
    }
}
