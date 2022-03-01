package family.salavat.easynpcs;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Pose;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDispacher implements CommandExecutor {

    private NpcManager manager;

    public CommandDispacher(NpcManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player == false) return false;
        Player player = (Player) sender;
        if (!player.isOp()) return false;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("removeall")) {
                player.sendMessage(ChatColor.YELLOW + "" + manager.removeAllNpcs() + " NPCS were removed!");
                return true;
            }
            return false;
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                int id = manager.createNpc(player.getLocation(), args[1]);
                player.sendMessage(ChatColor.GREEN + "You successfully created the NPC! Its ID is: " + id);
                return true;
            }
            else if (args[0].equalsIgnoreCase("rotateme")) {
                int id = Integer.parseInt(args[1]);
                if (manager.setNpcRotationToPlayer(id, player)) player.sendMessage(ChatColor.GREEN + "You successfully rotated the NPC to you");
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + args[1]);
                return true;
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                if (manager.removeNpc(Integer.parseInt(args[1]))) {
                    player.sendMessage(ChatColor.GREEN + "You successfully removed the NPC with ID: " + args[1]);
                }
                else {
                    player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + args[1]);
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("setequipment")) {
                int id = Integer.parseInt(args[1]);
                if (manager.setNpcEquipmentByPlayer(player, id)) player.sendMessage(ChatColor.GREEN + "You successfully set the NPC equipment like your!");
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + args[1]);
                return true;
            }
            return false;
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setrotatable")) {
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException exception) {
                    player.sendMessage(ChatColor.RED + "Insert integer NPC's ID!");
                    return true;
                }
                boolean criteria = Boolean.parseBoolean(args[2]);
                if (manager.setNpcRotatable(id, criteria)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID: " + id + (criteria ? " " : " un") + "rotatable!");
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equalsIgnoreCase("setunnamed")) {
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException exception) {
                    player.sendMessage(ChatColor.RED + "Insert ineger NPC's ID!");
                    return true;
                }
                boolean criteria = Boolean.parseBoolean(args[2]);
                if (manager.setNpcUnnamed(id, criteria)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID: " + id + (criteria ? " un" : " ") + "named!");
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equalsIgnoreCase("settabname")) {
                int id = Integer.parseInt(args[1]);
                String name = args[2];
                if (manager.setNpcTabName(id, name)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " name " + name);
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equalsIgnoreCase("setuntabbed")) {
                int id = Integer.parseInt(args[1]);
                boolean criteria = Boolean.parseBoolean(args[2]);
                if (manager.setNpcUntabbed(id, criteria)) player.sendMessage(ChatColor.GREEN + "You successfully made NPC with ID " + id + (criteria ? " untabbed" : " tabbed"));
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equalsIgnoreCase("setname")) {
                int id = Integer.parseInt(args[1]);
                String name = args[2];
                name = name.replaceAll("_", " ");
                if (manager.setNpcName(id, name)) player.sendMessage(ChatColor.GREEN + "You successfully made NPC with ID " + id + " name " + name);
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equals("setprefix")) {
                int id = Integer.parseInt(args[1]);
                String prefix = args[2];
                if (manager.setNpcPrefix(id, prefix)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " prefix " + prefix);
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equals("setsuffix")) {
                int id = Integer.parseInt(args[1]);
                String suffix = args[2];
                if (manager.setNpcSuffix(id, suffix)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " suffix " + suffix);
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equals("setskin")) {
                int id = Integer.parseInt(args[1]);
                if (!manager.hasNpcWithId(id)) {
                    player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                    return true;
                }
                String nickname = args[2];
                if (manager.setNpcSkinByNickname(id, nickname)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " " + nickname + "'s skin");
                else player.sendMessage(ChatColor.RED + "Nickname " + nickname + " doesn't exist");
                return true;
            }
            else if (args[0].equals("setpose")) {
                int id = Integer.parseInt(args[1]);
                String pose = args[2];
                try {
                    Pose nmsPose = Pose.valueOf(pose.toUpperCase());
                    if (manager.setNpcPose(id, nmsPose)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " pose " + pose);
                    else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                }
                catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "The available poses are only: standing, sleeping, crouching, dying, fall_flying, long_jumping, spin_attack and swimming");
                }
                return true;
            }
            else if (args[0].equals("setglowing")) {
                int id = Integer.parseInt(args[1]);
                Boolean criteria = Boolean.parseBoolean(args[2]);
                if (manager.setNpcGlowing(id, criteria)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + (criteria ? "" : "un") +"glowing");
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            else if (args[0].equals("setcolor")) {
                int id = Integer.parseInt(args[1]);
                try {
                    ChatFormatting color = ChatFormatting.valueOf(args[2].toUpperCase());
                    if (manager.setNpcColor(id, color)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " color: " + color.getName());
                    else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                }
                catch (Throwable throwable) {
                    StringBuilder availableColors = new StringBuilder();
                    for (ChatFormatting color : ChatFormatting.values()) {
                        availableColors.append(color.getName() + ", ");
                    }
                    availableColors.deleteCharAt(availableColors.length() - 1);
                    availableColors.deleteCharAt(availableColors.length() - 1);
                    player.sendMessage(ChatColor.RED + "Available color are only: " + availableColors);
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("setcollidable")) {
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException exception) {
                    player.sendMessage(ChatColor.RED + "Insert ineger NPC's ID!");
                    return true;
                }
                boolean criteria = Boolean.parseBoolean(args[2]);
                if (manager.setNpcCollidable(id, criteria)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID: " + id + (criteria ? "" : "un") + "collidable!");
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            return false;
        }
        else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("setrotation")) {
                int id = Integer.parseInt(args[1]);
                float yaw = Integer.parseInt(args[2]);
                float pitch = Integer.parseInt(args[3]);
                if (manager.setNpcRotation(id, yaw, pitch)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC with ID " + id + " yaw: " + yaw + " pitch: " + pitch);
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            return false;
        }
        else if (args.length >= 5) {
            if (args[0].equalsIgnoreCase("setcommand")) {
                int id = Integer.parseInt(args[1]);
                String permission = args[2];
                String click = args[3];
                String commandLabel = args[4];
                String[] commandArgs = new String[args.length - 5];
                for (int i = 5; i < args.length; i++) {
                    commandArgs[i - 5] = args[i];
                }
                if (manager.setCommandOnNpc(id, permission, click, commandLabel, commandArgs)) player.sendMessage(ChatColor.GREEN + "You successfully set NPC to with ID " + id + " command " + commandLabel);
                else player.sendMessage(ChatColor.RED + "There is no NPC with ID: " + id);
                return true;
            }
            return false;
        }
        return false;
    }
}
