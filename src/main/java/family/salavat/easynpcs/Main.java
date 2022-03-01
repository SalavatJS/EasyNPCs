package family.salavat.easynpcs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import family.salavat.easynpcs.events.PlayerInteractNpcEvent;
import family.salavat.easynpcs.listeners.InteractListener;
import family.salavat.easynpcs.listeners.JoinListener;
import family.salavat.easynpcs.listeners.MoveListener;
import family.salavat.easynpcs.models.Equipment;
import family.salavat.easynpcs.models.NpcModel;
import family.salavat.easynpcs.providers.NpcProvider;
import family.salavat.easynpcs.services.NpcService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    private JavaPlugin main;

    private FileConfiguration npcStorage;

    private File npcFile;

    private NpcRepository repository;

    private NpcManager manager;

    private CommandDispacher dispatcher;

    private Listener onJoin, onMove, onInteract;

    private ProtocolManager protocolManager;

    private NpcService service;

    @Override
    public void onEnable() {
        main = this;
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        npcFile = new File(getDataFolder().getAbsolutePath() + File.separator + "npcs.yml");
        if (!npcFile.exists()) {
            try {
                npcFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConfigurationSerialization.registerClass(NpcModel.class);
        ConfigurationSerialization.registerClass(Equipment.class);
        npcStorage = YamlConfiguration.loadConfiguration(npcFile);
        getLogger().info("From main" + npcStorage.getKeys(false).toString());
        repository = new NpcRepository(npcStorage, npcFile);
        manager = new NpcManager(repository, getServer());
        dispatcher = new CommandDispacher(manager);
        getCommand("npc").setExecutor(dispatcher);
        onJoin = new JoinListener(manager);
        getServer().getPluginManager().registerEvents(onJoin, this);
        onMove = new MoveListener(manager);
        getServer().getPluginManager().registerEvents(onMove, this);
        onInteract = new InteractListener(this);
        getServer().getPluginManager().registerEvents(onInteract, this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                int entityId = packet.getIntegers().read(0);
                EnumWrappers.EntityUseAction action = packet.getEnumEntityUseActions().read(0).getAction();
                NpcModel model;
                if ((model = manager.getModelByEntityId(entityId)) == null) return;

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        EnumWrappers.Hand hand = null;
                        if (action == EnumWrappers.EntityUseAction.INTERACT || action == EnumWrappers.EntityUseAction.INTERACT_AT) {
                            hand = packet.getEnumEntityUseActions().read(0).getHand();
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerInteractNpcEvent(event.getPlayer(), model, action, hand));
                    }

                }.runTask(main);
            }

        });
        service = new NpcProvider(manager);
        getServer().getServicesManager().register(NpcService.class, service, this, ServicePriority.High);
    }

    public JavaPlugin getMain() {
        return main;
    }

}
